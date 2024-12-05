package com.macys.uop.order.ordercollectorchestrator.service.impl;

import static com.macys.uop.abstraction.commonlookupmapper.audit.AuditTransactionEnum.*;
import static com.macys.uop.abstraction.commonlookupmapper.audit.AuditTransactionEnum.ORDER_ENRICHMENT;
import static com.macys.uop.abstraction.commonlookupmapper.enums.FulfillmentTypeEnum.STH;
import static com.macys.uop.foundation.core.utils.Constant.*;
import static com.macys.uop.foundation.core.utils.task.support.EventStateConstants.ORDER_ID;
import static com.macys.uop.foundation.core.utils.task.support.EventStateConstants.PAYLOAD;
import static com.macys.uop.foundation.core.utils.task.support.EventStateConstants.PROCESS;
import static com.macys.uop.foundation.core.utils.task.support.EventStateConstants.TRANSACTION_ID;
import static com.macys.uop.order.ordercollectorchestrator.utils.OrderErrorCodes.*;
import static com.macys.uop.order.ordercollectorchestrator.utils.OrderTransaction.*;
import static com.macys.uop.order.ordercollectorchestrator.utils.OrdercollectorchestratorConstants.*;

import com.macys.uop.common.commonlib.Status;
import com.macys.uop.foundation.core.utils.exception.Error;
import com.macys.uop.foundation.core.utils.json.JsonUtils;
import com.macys.uop.foundation.core.utils.task.*;
import com.macys.uop.foundation.core.utils.task.support.EventState;
import com.macys.uop.foundation.core.utils.task.support.EventStateService;
import com.macys.uop.order.model.Order;
import com.macys.uop.order.model.OrderLine;
import com.macys.uop.order.model.OrderLineStatus;
import com.macys.uop.order.ordercollectorchestrator.model.ErrorProcessorRequest;
import com.macys.uop.order.ordercollectorchestrator.model.OrderError;
import com.macys.uop.order.ordercollectorchestrator.service.*;
import com.macys.uop.order.ordercollectorchestrator.utils.*;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.micrometer.core.annotation.Timed;

import java.util.*;
import java.util.concurrent.ExecutionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
@Slf4j
public class OrdercollectorchestratorServiceImpl
    implements IOrdercollectorchestratorService, OrdercollectorchestratorUtil, TaskUtil {

    private final ValidatorUtil validatorUtil;

    private final ProfileEvaluatorUtil profileEvaluatorUtil;

    private final IOrdercollectService ordercollectService;

    private final ILockmanagerService lockmanagerService;

    private final IOrderenrichmentService orderenrichmentService;

    private final JsonUtils jsonUtils;

    private final IOrderCreationSuccessPublishHelper orderCreationOnSuccessPublisher;

    private final IOrderFraudacknowledgmentPublishHelper fraudackPublishHelper;

    private final IEventLogService eventLogService;

    private final IErrorprocessorService errorprocessorService;

    private final EventStateService eventStateService;
    private final  CommonOrderValidations orderValidations;

    private final IPickUpOrderEnhanceServiceImpl pickUpOrderEnhanceServiceImpl;

    @Value("${ordercollectorchestrator.input.channel.name}")
    private String orchestratorInputChannel;

    @Value("${ordercreation_onsuccess.channel_name}")
    private String orderCreateSuccessChannel;

    @Value("${collectorder_fraudacknowledgment.channel_name}")
    private String fraudAckChannel;

    @Value("${command.collectorder.steps}")
    private String stepsConfig;

    @Value("${command.checkfraudresponse.steps}")
    private String fraudSteps;

    @Value("#{'${errorList.400error}'.split(',')}")
    private List<String> errorList;

    @Value("${taskexecutor.taskexecutor.logsummary.enabled:true}")
    private boolean doLogTaskExecutorSummary;

    @Value("${kill-switch-r3}")
    private Boolean killSwitchR3;


    /**
     * Placeholder Endpoint for collectorder stream Stamps, persist, lock-for NonZola and Publish
     * the received Input Order to OrderSourcing
     *
     * @param orderRequest
     * @return Status
     */
    @Override
    @CircuitBreaker(name = "ordercollectorchestrator-cb", fallbackMethod = "collectOrderFallback")
    @Timed(value = "messaging_requests_total", histogram = true)
    public Object collectOrder(Order orderRequest) {

        boolean isRetry = Boolean.parseBoolean(getSingleValueHeaderParam(RETRY));
        String forcedFailedStep = getSingleValueHeaderParam("forcedFailedStep");
        //EventState eventState = isRetry ? getEventState(isRetry, ORDER_CREATED.getTransactionId()) : null;
        String failedState = getFailedStep(isRetry);
        Map<String, String> referenceMap = getReferenceMap(orderRequest);
        String referenceType = referenceMap.keySet().stream().findFirst().orElse(null);
        String subClientId= StringUtils.isNotEmpty(orderRequest.getSourceSystem()) && NEW_SOURCE_SYSTEMS.contains(orderRequest.getSourceSystem()) ? orderRequest.getSourceSystem() :orderRequest.getSourceChannel();
        String referenceId = ObjectUtils.isNotEmpty(referenceType) ? referenceMap.get(referenceType) : null;
        String stepsToBeExecuted = defineSteps(isRetry, failedState);

        SimpleTaskBuilder taskBuilder =
            new SimpleTaskBuilder(COLLECT_ORDER).withCSVFlow(stepsToBeExecuted);

        //Recovery State
        taskBuilder.addStep(new AbstractStep(ORDER_RECOVERYSTATE) {
            public void handle(StepExecution stepExecution) {
//                Order order = (ObjectUtils.isNotEmpty(eventState) && StringUtils.isNotEmpty(
//                        eventState.getPayload())) ? jsonUtils.convertFromJson(
//                        eventState.getPayload(), Order.class) : orderRequest;
//                stepExecution.getTaskExecution().getExecutionContext().put(PAYLOAD, order);
                stepExecution.getTaskExecution().getExecutionContext().put(PAYLOAD, orderRequest);
            }
        });


        //Event Log
        taskBuilder.addStep(new AbstractStep(ORDER_LOGEVENT) {
            public void handle(StepExecution stepExecution)
                throws InterruptedException, ExecutionException {
                if(StringUtils.isNotEmpty(orderRequest.getSourceSystem()) && NEW_SOURCE_SYSTEMS.contains(orderRequest.getSourceSystem())) {
                    eventLogService.createEventLog(orderRequest, orchestratorInputChannel, INBOUND,
                            ORDER_RECEIVED, referenceType, referenceId, getDefaultMessageHeadersForCheckout(getSingleValueHttpHeaders(),referenceId),subClientId);
                } else{
                    eventLogService.createEventLog(orderRequest, orchestratorInputChannel, INBOUND,
                            ORDER_REQ, referenceType, referenceId, getDefaultMessageHeaders(),subClientId);
                }
            }
        });

        // Validate Order
        taskBuilder.addStep(new AbstractStep(ORDER_VALIDATE) {
            public void handle(StepExecution stepExecution) throws ExecutionException, InterruptedException {
                if(StringUtils.isNotEmpty(orderRequest.getSourceSystem()) && NEW_SOURCE_SYSTEMS.contains(orderRequest.getSourceSystem())) {
                    validatorUtil.setAssociateDetails(orderRequest);
                    orderValidations.validate(orderRequest);
                    orderValidations.validateFieldValues(orderRequest);
                    validatorUtil.setCustomerPreferences(orderRequest);
                }else {
                    validatorUtil.validateMandatoryParams(orderRequest);
                }
            }
        });

        // Stamp Profile
        taskBuilder.addStep(new AbstractStep(ORDER_STAMPPROFILE) {
            public void handle(StepExecution stepExecution) {
                Order profiledOrder = new Order();
                if(!killSwitchR3) {
                    profiledOrder =
                            profileEvaluatorUtil.evaluateProfileV2(ORDER, orderRequest,
                                    orderRequest.getOrderId());
                }
                stepExecution.getTaskExecution().getExecutionContext().put(PAYLOAD, profiledOrder);
           }
        });

        taskBuilder.addStep(new AbstractStep(ORDER_ENHANCEBOSSPLUS) {
            public void handle(StepExecution stepExecution) {
                Order profiledOrder = getContextMapValue(stepExecution.getTaskExecution(), PAYLOAD, Order.class, jsonUtils);
                //To enrich the pickup order for BOSS Plus flow
                pickUpOrderEnhanceServiceImpl.enrichPickUpOrder(profiledOrder);
                stepExecution.getTaskExecution().getExecutionContext().put(PAYLOAD, profiledOrder);
            }
        });

        // Collect Order
        taskBuilder.addStep(new AbstractStep(ORDER_CREATE) {
            public void handle(StepExecution stepExecution) {
                Order profiledOrder =
                    getContextMapValue(stepExecution.getTaskExecution(), PAYLOAD, Order.class,
                        jsonUtils);
                HttpHeaders headers =
                    (isRetry && ORDER_CREATE.equalsIgnoreCase(failedState)) ? setCommonHeaders(
                            getDefaultHttpHeaders()) : getDefaultHttpHeaders();
                if (StringUtils.isEmpty(profiledOrder.getOrderPurpose())){
                    profiledOrder.setOrderPurpose(SALE);
                }
                profiledOrder.getOrderLines().forEach(orderLine -> {
                    if(orderLine.getOrderLineStatuses() != null && null != orderLine.getLineId()){
                        orderLine.getOrderLineStatuses().forEach(orderLineStatus -> {
                                    if (StringUtils.isEmpty(orderLineStatus.getLineRefNumber())) {
                                        orderLineStatus.setLineRefNumber(String.valueOf(orderLine.getLineId()));
                                    }
                                }
                        );
                    }
                });
                if (ZOLA.equalsIgnoreCase(profiledOrder.getProfileId()))
                {
                    profiledOrder.setPartnerOrderId(profiledOrder.getSellerOrderId());
                    profiledOrder.getOrderLines().forEach(orderLine -> {
                        if(StringUtils.isEmpty(orderLine.getFulfillmentType()))
                            orderLine.setFulfillmentType(STH.getFulfillmentTypeDesc());
                        if(orderLine.getOrderLineStatuses() != null){
                            orderLine.getOrderLineStatuses().forEach(orderLineStatus -> {
                                        if (StringUtils.isEmpty(orderLineStatus.getRefShipmentId())) {
                                            orderLineStatus.setRefShipmentId(DEFAULT_REF_SHIPMENT_ID);
                                        }
                                    }
                            );
                        }else{
                            OrderLineStatus orderLineStatus = new OrderLineStatus();
                            orderLineStatus.setStatusQuantity(ObjectUtils.isNotEmpty(orderLine) && ObjectUtils.isNotEmpty(orderLine.getOrderedQty())?orderLine.getOrderedQty():null);
                            orderLineStatus.setRefShipmentId(DEFAULT_REF_SHIPMENT_ID);
                            orderLine.setOrderLineStatuses(Arrays.asList(orderLineStatus));
                        }
                    });
                }
                Order collectOrder = ordercollectService.collectOrder(profiledOrder, headers);

                stepExecution.getTaskExecution().getExecutionContext().put(PAYLOAD, collectOrder);
            }
        });

        // Lock Order
        taskBuilder.addStep(new AbstractStep(ORDER_LOCK) {
            public void handle(StepExecution stepExecution) {
                Order collectOrder =
                    getContextMapValue(stepExecution.getTaskExecution(), PAYLOAD, Order.class,
                        jsonUtils);
                HttpHeaders headers = getDefaultHeaders(collectOrder.getOrderId());
                headers.add(EXECUTION_ID, stepExecution.getExecutionId());
                lockmanagerService.lockOrder(collectOrder, headers);
                stepExecution.getTaskExecution().getExecutionContext().put(PAYLOAD, collectOrder);
            }
        });
        
        // Marketing partner id Lock
        taskBuilder.addStep(new AbstractStep(PARTNER_FULFILLMENT_ID_LOCK) {
            public void handle(StepExecution stepExecution) {
                Order collectOrder =
                    getContextMapValue(stepExecution.getTaskExecution(), PAYLOAD, Order.class,
                        jsonUtils);
                if(ObjectUtils.isNotEmpty(collectOrder) && ObjectUtils.isNotEmpty(collectOrder.getOrderLines())) {
                	String marketingPartnerId = collectOrder.getOrderLines().get(0).getMarketingPartnerId();
                	if(StringUtils.isNotBlank(marketingPartnerId) && MARKETING_PARTNER_ID_MIRAKL.equalsIgnoreCase(marketingPartnerId)) {
                		HttpHeaders headers = getDefaultHeaders(collectOrder.getOrderId());
                		headers = addCustomMessageHeader(headers, collectOrder);
                		headers.add(EXECUTION_ID, stepExecution.getExecutionId());
                		lockmanagerService.lockOrderByPartnerFulfillmentId(collectOrder, headers);
                	}
                }
            }
        });

        // Enrich Order
        taskBuilder.addStep(new AbstractStep(ORDER_ENRICH) {
            public void handle(StepExecution stepExecution) {
                Order collectOrder =
                    getContextMapValue(stepExecution.getTaskExecution(), PAYLOAD, Order.class,
                        jsonUtils);
                HttpHeaders headers =
                    (isRetry && ORDER_ENRICH.equalsIgnoreCase(failedState))
                        ? setCommonHeaders(getDefaultHeaders(collectOrder.getOrderId()))
                        : getDefaultHeaders(collectOrder.getOrderId());
                headers.add(EXECUTION_ID, stepExecution.getExecutionId());
                if(!StringUtils.isEmpty(forcedFailedStep)){
                    headers.add("forcedFailedStep",forcedFailedStep);
                }
                if(validatorUtil.getBusinessConfigForOrder(collectOrder.getOrderPurpose(), CATALOG_ENRICH)) {
                    Order enrichedOrder = orderenrichmentService.enrichOrder(collectOrder, headers);
                    stepExecution.getTaskExecution().getExecutionContext()
                            .put(PAYLOAD, enrichedOrder);
                }
            }
        });

        // Publish to ordercreation_onsuccess Pubsub Topic
        taskBuilder.addStep(new AbstractStep(ORDER_PUBLISHCREATESUCCESS) {
            public void handle(StepExecution stepExecution) throws Exception {
                Order order =
                    getContextMapValue(stepExecution.getTaskExecution(), PAYLOAD, Order.class,
                        jsonUtils);
        		Map<String, String> headers = getDefaultMessageHeaders();
        		headers = addCustomMessageHeader(headers, order);

                orderCreationOnSuccessPublisher.publish(order, headers);

                eventLogService.createEventLog(order, orderCreateSuccessChannel, INBOUND,
                        OrderTransaction.getNameByStatusCode(String.valueOf(
                                (null == order.getMaxStatusCode() || StringUtils.isEmpty(order.getMaxStatusCode())) ? "1000" : order.getMaxStatusCode()
                        )),
                        ORDERID_HDR, order.getOrderId(), headers, subClientId);
            }
        });

        return executeTask(taskBuilder, referenceId, referenceType, SUCCESS_STATUS_MESSAGE,
            orderRequest, isRetry, COLLECT_ORDER);
    }


    /**
     * Method to Process Fraud Response
     *
     * @param fraudResponse
     * @return Status
     */
    @Override
    @CircuitBreaker(name = "ordercollectorchestratorackmchub-cb", fallbackMethod = "collectOrderFallback")
    @Timed(value = "messaging_requests_total", histogram = true)
    public Object checkResponse(Order fraudResponse) {

        boolean isRetry = Boolean.parseBoolean(getSingleValueHeaderParam(RETRY));

        SimpleTaskBuilder taskBuilder =
            new SimpleTaskBuilder(ACK_FRAUD_RESPONSE).withCSVFlow(fraudSteps);
        Map<String, String> messageHeaders = getMcHubAckHeaders(fraudResponse);
        // Send Acknowledgement to McHub
        taskBuilder.addStep(new AbstractStep(ORDER_PUBLISHMCHUBACK) {
            public void handle(StepExecution stepExecution) throws Exception {
                fraudackPublishHelper.publish(fraudResponse, messageHeaders);
                eventLogService.createEventLog(fraudResponse, fraudAckChannel, OUTBOUND,
                    OrderTransaction.MCHUB_ACK, ORDERID_HDR, fraudResponse.getOrderId(),
                    messageHeaders,fraudResponse.getSourceChannel());

            }
        });
        return executeTask(taskBuilder, fraudResponse.getOrderId(), ORDERID_HDR,
            FRAUD_STATUS_MESSAGE, fraudResponse, isRetry, ACK_FRAUD_RESPONSE);
    }


    /**
     * Method to execute task steps defined
     *
     * @param taskBuilder
     * @param referenceId
     * @param referenceType
     * @param statusMessage
     * @param initialPayload
     * @return Status
     */
    private Object executeTask(SimpleTaskBuilder taskBuilder, String referenceId,
        String referenceType, String statusMessage, Object initialPayload, boolean isRetry, String process) {

        SimpleTask task = taskBuilder.addListener(eventStateService.getTaskListener()).build();
        task.getSteps().forEach(step -> ((AbstractStep) step).registerStepExecutionListener(
            eventStateService.getStepListener()));
        SimpleTaskLauncher launcher = new SimpleTaskLauncher();
        launcher.setTaskRepository(new SimpleTaskRepository(new MapTaskExecutionDao()));
        TaskExecution execution = launcher.run(task, getTaskParameters(referenceId, process));

        // Log Summary
        if (doLogTaskExecutorSummary) {
            log.info(getExecutionSummary(execution));
        }

        if (execution.getStatus().equals(TaskStatus.FAILURE.name())) {
            Order orderObject =
                    (ObjectUtils.isNotEmpty(execution.getExecutionContext().get(PAYLOAD)))
                            ? (Order) execution.getExecutionContext().get(PAYLOAD)
                            : (Order) initialPayload;
            String failedStep = getFailedStep(execution);
            if(failedStep.equalsIgnoreCase(ORDER_CREATE)){
                EventState eventState = getUpdatedEventState(ORDER_CREATED.getTransactionId(),"ordercollect","collectOrder",ORDER_NA_REF_ID);
                if(null != eventState && null !=eventState.getPayload()){
                    orderObject = jsonUtils.convertFromJson(eventState.getPayload(), Order.class);
                }
            }
            if(failedStep.equalsIgnoreCase(ORDER_ENRICH)){
                String orderId = orderObject.getOrderId();
                EventState eventState = getUpdatedEventState(
                        ORDER_ENRICHMENT.getTransactionId(),"orderenrichment","enrichOrder",orderId);
                if(null != eventState && null != eventState.getPayload()){
                    orderObject = jsonUtils.convertFromJson(eventState.getPayload(), Order.class);
                }
            }

            Object reservationId = ObjectUtils.isNotEmpty(orderObject.getOrderLines())? orderObject.getOrderLines().stream().
                    filter(o1-> ObjectUtils.isNotEmpty(o1.getReservationId())).map(OrderLine::getReservationId).findFirst().orElse(null)
                    :null;

            String refId = StringUtils.isNotEmpty(orderObject.getOrderId())
                    ? orderObject.getOrderId()
                    : ObjectUtils.isNotEmpty(reservationId)? String.valueOf(reservationId) : ORDER_NA_REF_ID;
            String refType = StringUtils.isNotEmpty(orderObject.getOrderId())? ORDERID_HDR:
                    ObjectUtils.isNotEmpty(reservationId)? RESERVATION_ID: ORDERID_HDR;

            int statusCode = getStatusCode(execution.getError());
            Error errorObject = getError(execution.getError(), failedStep);
            String errorType = (FOUR_HUNDRED == statusCode) ? EL : ET;
            String payload = jsonUtils.convertToJson(orderObject);

            String error = null;
            try {
                if (!errorList.contains(errorObject.getCode()) && !validatorUtil.restrictEpfForExchange
                        (orderObject.getOrderPurpose(),failedStep,errorObject )) {
                    ErrorProcessorRequest request =
                        ErrorProcessorRequest.builder().error(errorObject)
                            .statusCode(statusCode)
                            .failedState(failedStep)
                            .payload(payload)
                            .referenceType(refType)
                            .referenceId(refId)
                            .errorType(errorType)
                            .headers(getErrorProcessorMessageHeaders(getSingleValueHeaderParam(RETRY))).build();
                    error = errorprocessorService.retryLater(request);
                }
            } catch (Exception e) {
                handleError(e, referenceId, ERROR_PROCESSOR, getDefaultHttpHeaders(),
                        ERROR_PROCESSOR_ERROR);
          }

            buildErrorProcessorResponse(execution, errorObject, error, statusCode,
                    getDefaultHttpHeaders());
        }
        Order orderObject =
                (ObjectUtils.isNotEmpty(execution.getExecutionContext().get(PAYLOAD)))
                        ? (Order) execution.getExecutionContext().get(PAYLOAD)
                        : (Order) initialPayload;

        if (validatorUtil.getBusinessConfigForOrder(orderObject.getOrderPurpose(),ORDER_RESPONSE)){
            return (Order) execution.getExecutionContext().get(PAYLOAD);
        }else{
            return getStatus(HttpStatus.OK, statusMessage);
        }
        }

    /**
     * Method to get Task Parameters
     *
     * @param referenceId
     * @param process
     * @return TaskParameters
     */
    private TaskParameters getTaskParameters(String referenceId, String process) {
        String transactionId = null;
        String orderId = null;
        if (process.equalsIgnoreCase(COLLECT_ORDER)) {
            transactionId = ORDER_CREATED.getTransactionId();
            orderId = ORDER_NA_REF_ID;
        } else {
            transactionId = ORDER_FRAUDRESPONSE.getTransactionId();
            orderId = referenceId;
        }

        // @formatter:off
        return new TaskParametersBuilder()
                .addString(EXECUTION_ID, getExecutionId())
                .addString(APP_NAME, getAppName())
                .addString(ORDER_ID, orderId)
                .addString(TRANSACTION_ID, transactionId)
                .addString(PROCESS, process)
                .build();
        // @formatter:on
    }

    /**
     * Circuit Breaker Fallback method
     *
     * @param e
     * @return Status
     */
    private Status collectOrderFallback(Throwable e) {
    handleServiceFailure(e);
        return null;
    }

    /**
     * Method to define steps
     * @param isRetry
     * @param failedState
     */
    private String defineSteps(boolean isRetry, String failedState) {
        if (isRetry && !StringUtils.isBlank(failedState)) {
            return validatorUtil.getProperty(RECOVERY_INITIAL_STEPS) + COMMA + StringUtils.substring(
                            stepsConfig, stepsConfig.indexOf(failedState));
        } else {
            return validatorUtil.getProperty(INITIAL_STEPS);
        }
    }

    /**
     * Method to Get EventState
     *
     * @param isRetry
     * @param transactionId
     * @return EventState
     */
    private EventState getEventState(boolean isRetry, String transactionId) {
        if (isRetry) {
            List<EventState>  eventStateList = eventStateService.fetch(getCorrelationId(), getAppName(), COLLECT_ORDER,
                ORDER_NA_REF_ID, null,transactionId);
            if (!eventStateList.isEmpty()) {
                return eventStateList.stream().findFirst().orElse(null);
            }
        }
        return null;
    }
    private EventState getUpdatedEventState(String transactionId,String serviceName, String process,String orderId) {
            List<EventState>  eventStateList = eventStateService.fetch(getCorrelationId(), serviceName, process,
                    orderId, null,transactionId);
            if (!eventStateList.isEmpty()) {
                return eventStateList.stream().findFirst().orElse(null);
            }
        return null;
    }

    /**
     * Get Failed Step
     * @param isRetry
     * @return String
     */
    private String getFailedStep(boolean isRetry) {
        if(isRetry) {
            return getSingleValueHeaderParam(FAILED_STATE);
        }
        return null;
    }
}



