package com.macys.uop.order.ordercollectorchestrator.stream;

import brave.Tracer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.pubsub.v1.PubsubMessage;
import com.macys.uop.common.omconfig.api.ErrorDetails;
import com.macys.uop.foundation.core.utils.json.JsonUtils;
import com.macys.uop.foundation.core.utils.msg.subscriber.AbstractStreamSubscriber;
import com.macys.uop.foundation.core.utils.ordererror.OrderErrorMessagePublisher;
import com.macys.uop.foundation.core.utils.validation.MessageValidator;
import com.macys.uop.foundation.messagestore.IMessageStoreRecordManager;
import com.macys.uop.order.model.Order;
import com.macys.uop.order.ordercollectorchestrator.model.OrderError;
import com.macys.uop.order.ordercollectorchestrator.service.ICollectorderResponsePublishService;
import com.macys.uop.order.ordercollectorchestrator.service.IEventLogService;
import com.macys.uop.order.ordercollectorchestrator.service.IOrdercollectorchestratorService;
import com.macys.uop.order.ordercollectorchestrator.utils.OrderValidationErrorCodes;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gcp.pubsub.core.PubSubTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.*;
import java.util.concurrent.ExecutionException;

import static com.macys.uop.foundation.core.utils.Constant.*;
import static com.macys.uop.foundation.core.utils.Constant.CLIENTID_HDR;
import static com.macys.uop.order.ordercollectorchestrator.utils.OrderTransaction.ORDER_ACKNOWLEDGE;
import static com.macys.uop.order.ordercollectorchestrator.utils.OrdercollectorchestratorConstants.*;
import static com.macys.uop.order.ordercollectorchestrator.utils.OrdercollectorchestratorConstants.SERVICENAME;

@Component
@Slf4j
public class MCOMDigitalCheckOutStream extends AbstractStreamSubscriber {
    @Value("${topic.subscription.digitalcheckout.mcom.name}")
    private String subscriptionName;
    @Value("${custom.pubsub.listener.shutdown.waittime}")
    private int shutdownWaitTime;
    private final JsonUtils jsonUtils;
    private final IOrdercollectorchestratorService ordercollectorchestratorService;
    private final MessageValidator messageValidator;
    private final IMessageStoreRecordManager messageStoreRecordManager;
    private final ICollectorderResponsePublishService collectorderResponsePublishService;

    public MCOMDigitalCheckOutStream(PubSubTemplate pubSubTemplate, Tracer tracer,
                                     OrderErrorMessagePublisher orderErrorMessagePublisher, JsonUtils jsonUtils,
                                     MessageValidator messageValidator,
                                     IOrdercollectorchestratorService ordercollectorchestratorService,
                                     IMessageStoreRecordManager messageStoreRecordManager, ICollectorderResponsePublishService collectorderResponsePublishService) {
        super(pubSubTemplate, tracer, orderErrorMessagePublisher);
        this.jsonUtils = jsonUtils;
        this.messageValidator = messageValidator;
        this.ordercollectorchestratorService = ordercollectorchestratorService;
        this.messageStoreRecordManager = messageStoreRecordManager;
        this.collectorderResponsePublishService = collectorderResponsePublishService;
    }

    @SneakyThrows
    @Override
    protected void invokeService(PubsubMessage message) {
        if (messageValidator.validateMessage(message)) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                Order orderSource = mapper.readValue(getPayload(), Order.class);
                orderSource.setSourceSystem(MCHECKOUT_SOURCE_SYSTEM);
                ordercollectorchestratorService.collectOrder(orderSource);
            } catch (Exception e) {
                if(e instanceof JsonProcessingException){
                    log.error("Exception while parsing MCOM order json {} messageId -> {} input payload -> {}",e.getMessage(),message.getMessageId(),message.getData().toStringUtf8());
                    publishAckIfError(e,message);
                }
                if (isCircuitBreakerError(e)) {
                    messageStoreRecordManager.deleteMessageStoreRecord(message.getMessageId(), getOrderId(), getClientId());
                }
                throw e;
            }
        }
    }

    /**
     * processHeaders
     *
     * @param message
     * @return Map
     */
    @Override
    protected Map<String, String> processHeaders(PubsubMessage message) {
        Map<String, String> headers = new HashMap<>();
        message.getAttributesMap().entrySet().stream().forEach(entry -> {
            String key = entry.getKey();
            String value = entry.getValue();
            headers.put(key, value);
        });
        if (!headers.containsKey(ORDERID_HDR)) {
            headers.put(ORDERID_HDR, ORDERID_HDR_DEFAULT_VALUE);
        }
        if (!headers.containsKey(CORRELATIONID_HDR)) {
            headers.put(CORRELATIONID_HDR, UUID.randomUUID().toString());
        }
        headers.put(MESSAGEID_HDR, message.getMessageId());
        headers.put(CLIENTID_HDR, applicationName);
        return headers;
    }

    /**
     * get Subscription
     *
     * @return String
     */
    @Override
    protected String getTopicSubscription() {
        return subscriptionName;
    }

    private void publishAckIfError(Exception e,PubsubMessage message) throws ExecutionException, InterruptedException {
        Order order = new Order();
        order.setSourceSystem(MCHECKOUT_SOURCE_SYSTEM);
        order.setSourceChannel(SOURCE_CHANNEL);
        OrderError orderError = new OrderError();
        List<ErrorDetails> detailsList = new ArrayList<ErrorDetails>();
        ErrorDetails details = new ErrorDetails();
        String additionalInfo = ExceptionUtils.getStackTrace(e);
        details.setErrorDescription(additionalInfo);
        details.setErrorCode(Integer.valueOf(OrderValidationErrorCodes.PARSE_ERROR.getCode()));
        detailsList.add(details);
        orderError.setApplicationName(SERVICENAME);
        com.macys.uop.order.ordercollectorchestrator.model.Error err = new com.macys.uop.order.ordercollectorchestrator.model.Error();
        err.setErrorDetailsList(detailsList);
        orderError.setError(err);
        collectorderResponsePublishService.publish(order, orderError);
    }

    @PreDestroy
    public void shutdown() throws InterruptedException {
        stopSubscriberAsync();
        Thread.sleep(shutdownWaitTime);
    }
}
