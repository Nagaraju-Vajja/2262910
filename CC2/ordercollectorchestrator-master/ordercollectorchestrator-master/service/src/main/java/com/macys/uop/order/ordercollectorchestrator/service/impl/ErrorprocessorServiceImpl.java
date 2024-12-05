package com.macys.uop.order.ordercollectorchestrator.service.impl;

import static com.macys.uop.order.ordercollectorchestrator.utils.OrdercollectorchestratorConstants.*;

import com.macys.commons.errorprocessor.model.ErrorSourceInfo;
import com.macys.commons.errorprocessor.model.HttpRequest;
import com.macys.commons.errorprocessor.model.PubSubRequestInfo;
import com.macys.commons.errorprocessor.model.ResponseInfo;
import com.macys.commons.errorprocessor.model.RetryCallbackInfo;
import com.macys.uop.foundation.core.utils.exception.ErrorDetail;
import com.macys.uop.foundation.core.utils.json.JsonUtils;
import com.macys.uop.order.ordercollectorchestrator.model.ErrorProcessorRequest;
import com.macys.uop.order.ordercollectorchestrator.publisher.IErrorprocessorErrorrequestPublisher;
import com.macys.uop.order.ordercollectorchestrator.service.IErrorprocessorService;
import com.macys.uop.order.ordercollectorchestrator.utils.OrdercollectorchestratorUtil;
import com.macys.uop.order.ordercollectorchestrator.utils.RetryEnum;
import com.macys.uop.order.ordercollectorchestrator.utils.ValidatorUtil;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;


@Component
@RequiredArgsConstructor
@Slf4j
public class ErrorprocessorServiceImpl
    implements IErrorprocessorService, OrdercollectorchestratorUtil {

    private final JsonUtils jsonUtils;
    private final IErrorprocessorErrorrequestPublisher errorrequestPublisher;
    private final ValidatorUtil validatorUtil;

    @Value("${errorprocessor.requesttype}")
    private String requestType;

    @Value("${spring.cloud.gcp.spanner.project-id}")
    private String projectId;

    @Value("${errorprocessor.dispatchdeadline}")
    private String dispatchDeadline;

    /**
     * Error Processor Object Construction and call to EPF Publisher
     * @param errorRequest
     * @return String
     */
    @SneakyThrows
    @Override
    public String retryLater(ErrorProcessorRequest errorRequest) {
        com.macys.commons.errorprocessor.model.Error errorInfo = new com.macys.commons.errorprocessor.model.Error();

        RetryEnum retryEnum = RetryEnum.getRetryInfoByStep(errorRequest.getFailedState());
        ErrorDetail errorDetail = errorRequest.getError().getErrorDetails().stream().findFirst().orElse(null);
        assert errorDetail != null;
        Map<String, String> headers = errorRequest.getHeaders();

        RetryCallbackInfo retryInfo = new RetryCallbackInfo();
        Integer retryCount = ObjectUtils.isEmpty(getSingleValueHeaderParam(RETRY_COUNT)) ? 0 : Integer.parseInt(getSingleValueHeaderParam(RETRY_COUNT)) ;
        if (headers.containsKey(RETRY) &&
        (!(retryCount <= Integer.parseInt(validatorUtil.getProperty(errorRequest.getErrorType() + MAXCOUNT))))){
            retryInfo.setMaxRetryCount(ZERO);
        } else{
            headers.put(RETRY_COUNT, String.valueOf(retryCount+1));
            retryInfo.setMaxRetryCount(
                    Integer.parseInt(validatorUtil.getProperty(errorRequest.getErrorType() + MAXCOUNT)));
        }

        //Set Headers
        headers.put(FAILED_STATE, errorRequest.getFailedState());
        headers.put(REFERENCE_ID, errorRequest.getReferenceId());
        headers.put(RETRY, TRUE);
        if(StringUtils.isNotEmpty(errorDetail.getLocationType())) {
            headers.put(FAILED_SUB_STATE, errorDetail.getLocationType());
        }

        headers.put(EVENT_TIME_STAMP, Instant.now().toString());

        // For Message queue Retry
        PubSubRequestInfo pubSubInfo = new PubSubRequestInfo();
        pubSubInfo.setMessage(errorRequest.getPayload());
        pubSubInfo.setAttributesMap(headers);
        pubSubInfo.setTopicName(
            getFullyQualifiedTopicName(validatorUtil.getProperty(retryEnum.getCallBackSource()),
                projectId));

        //Error Source Info
        ErrorSourceInfo sourceInfo = new ErrorSourceInfo();

        if (retryEnum.getErrorSource().equalsIgnoreCase(HTTP)) {
            sourceInfo.setRequestInterface(HTTP);
            HttpRequest httpRequest = new HttpRequest();
            httpRequest.setUrl(validatorUtil.getProperty(retryEnum.getErrorSourceSearchKey()));
            httpRequest.setMethod(retryEnum.getHttpMethod());
            httpRequest.setHeadersMap(headers);
            sourceInfo.setHttpRequest(httpRequest);
        } else {
            sourceInfo.setRequestInterface(PUBSUB);
            PubSubRequestInfo pubSubErrorInfo = new PubSubRequestInfo();
            pubSubErrorInfo.setMessage(errorRequest.getPayload());
            pubSubErrorInfo.setAttributesMap(headers);
            pubSubErrorInfo.setTopicName(getFullyQualifiedTopicName(
                validatorUtil.getProperty(retryEnum.getErrorSourceSearchKey()), projectId));
            sourceInfo.setPubSubRequestInfo(pubSubErrorInfo);
        }

        //Error Source Response Info
        ResponseInfo responseInfo = new ResponseInfo();
        responseInfo.setMessage(errorRequest.getError().getMessage());
        responseInfo.setStatusCode(errorRequest.getStatusCode());
        sourceInfo.setResponseInfo(responseInfo);

        retryInfo.setPubSubRequestInfo(pubSubInfo);
        retryInfo.setRetryRequestInterface(PUBSUB);
        retryInfo.setRetryScheduleTimestamp(new Timestamp(
            System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(
                Long.parseLong(validatorUtil.getProperty(errorRequest.getErrorType() + DURATION)))).toInstant()
            .toString());
        retryInfo.setDispatchDeadline(dispatchDeadline);


        errorInfo.setRetryInfo(retryInfo);
        errorInfo.setCode(errorRequest.getError().getCode());
        errorInfo.setDescription(errorRequest.getError().getMessage());
        errorInfo.setErrorSourceInfo(sourceInfo);
        errorInfo.setErrorTimestamp(Instant.now().toString());
        errorInfo.setReferenceType(errorRequest.getReferenceType());
        errorInfo.setReferenceId(errorRequest.getReferenceId());
        errorInfo.setRequestId(UUID.randomUUID().toString());
        errorInfo.setServiceName(getAppName());
        errorInfo.setSourceName(errorRequest.getFailedState());
        errorInfo.setRequestType(requestType);

        return errorrequestPublisher.publishMessage(jsonUtils.convertToJson(errorInfo), headers);
    }

}
