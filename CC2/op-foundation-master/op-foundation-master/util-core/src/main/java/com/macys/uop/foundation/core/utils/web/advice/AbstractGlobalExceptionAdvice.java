package com.macys.uop.foundation.core.utils.web.advice;

import static com.macys.uop.foundation.core.utils.Constant.CLIENTID_HDR;
import static com.macys.uop.foundation.core.utils.Constant.CONTEXT_SERVER_ERROR_LOGGING;
import static com.macys.uop.foundation.core.utils.Constant.CORRELATIONID_HDR;
import static com.macys.uop.foundation.core.utils.Constant.EPF_CONTENT_TYPE_KEY;
import static com.macys.uop.foundation.core.utils.Constant.EPF_HTTP_METHOD_KEY;
import static com.macys.uop.foundation.core.utils.Constant.EPF_ORIGIN_KEY;
import static com.macys.uop.foundation.core.utils.Constant.EPF_PAYLOAD_KEY;
import static com.macys.uop.foundation.core.utils.Constant.EPF_REQUEST_URL_KEY;
import static com.macys.uop.foundation.core.utils.Constant.MESSAGEID_HDR;
import static com.macys.uop.foundation.core.utils.Constant.MSG_PROCESSING_DURATION_TEXT_ORIGIN_REST;
import static com.macys.uop.foundation.core.utils.Constant.ORDERID_HDR;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.macys.uop.foundation.core.utils.CommonStatusCode;
import com.macys.uop.foundation.core.utils.common.StringUtil;
import com.macys.uop.foundation.core.utils.epf.EPFMessagePublisher;
import com.macys.uop.foundation.core.utils.exception.ErrorDetail;
import com.macys.uop.foundation.core.utils.json.JsonUtils;
import com.macys.uop.foundation.core.utils.logging.LogMessageBuilder;
import com.macys.uop.foundation.core.utils.logging.LogTypeEnum;
import com.macys.uop.foundation.core.utils.logging.LoggingUtil;
import com.macys.uop.foundation.core.utils.masking.ApplicationMaskingConfiguration;
import com.macys.uop.foundation.core.utils.masking.IDataMasker;
import com.macys.uop.foundation.core.utils.ordererror.OrderErrorMessage;
import com.macys.uop.foundation.core.utils.ordererror.OrderErrorMessageBuilder;
import com.macys.uop.foundation.core.utils.ordererror.OrderErrorMessagePublisher;
import com.macys.uop.foundation.core.utils.xml.XmlUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Abstract class to capture common functionalities which are used by {@link GlobalExceptionAdvice} methods.
 *
 */
@RequiredArgsConstructor
@Slf4j
public abstract class AbstractGlobalExceptionAdvice extends ResponseEntityExceptionHandler implements LoggingUtil, StringUtil {
	
	private final JsonUtils jsonUtils;
	private final XmlUtils xmlUtils;
	private final OrderErrorMessagePublisher orderErrorMessagePublisher;
	private final EPFMessagePublisher epfMessagePublisher;
	
	@Value("${ordererror.event.publishing.enabled:true}")
	private boolean isOrderErrorEventPublishingEnabled;
	
	@Value("${epf.event.publishing.enabled:true}")
	private boolean isEPFEventPublishingEnabled;
	
	@Value("${rest.epf.event.publishing.valid.errorcodes.list:#{null}}")
	private String epfEventPublishingValidErrorCodesList;
	
	/**
	 * Utility method to convert {@link Object} to Json string using {@link JsonUtils#convertToJson(Object)} 
	 * 
	 * @param body Input payload
	 * @return Json string
	 */
	protected String convertJsonBodyToString(Object body) {
		if(body==null) {
			return null;
		}
		return jsonUtils.convertToJson(body);
	}
	
	/**
	 * Utility method to convert {@link Object} to Xml string using {@link XmlUtils#convertToXml(Object)} 
	 * 
	 * @param body Input payload
	 * @return Xml String
	 */
	protected String convertXmlBodyToString(Object body) {
		if(body==null) {
			return null;
		}
		return xmlUtils.convertToXml(body);
	}
	
	/**
	 * Logs error information with default Json data masker {@link ApplicationMaskingConfiguration#getDefaultJsonMaskerInstance()}
	 * 
	 * @param error {@link com.macys.uop.foundation.core.utils.exception.Error}
	 * @param stackTrace Stack Trace
	 * @param requestBody Request payload
	 */
	protected void logJsonError(com.macys.uop.foundation.core.utils.exception.Error error, String stackTrace, String requestBody) {
		Instant receivedTime=Instant.parse(getReceivedTime());
		getLogMessageBuilder(log)
		.withLogType(LogTypeEnum.ERROR)
		.withOperationType(getHttpMethod())
		.withContext(CONTEXT_SERVER_ERROR_LOGGING)
		.withErrorCode(error.getCode())
		.withErrorMessage(error.toString())
		.withStackTrace(stackTrace)
		.withRequestBody(requestBody)
		.withAdditionalInfo(constructMsgProcessingDurationText4Logging(receivedTime, Instant.now(), MSG_PROCESSING_DURATION_TEXT_ORIGIN_REST))
		.buildDisableChecking()
		.logAsError();
	}
	
	/**
	 * Logs error information with default Xml data masker {@link ApplicationMaskingConfiguration#getDefaultXmlMaskerInstance()}
	 * 
	 * @param error {@link com.macys.uop.foundation.core.utils.exception.Error}
	 * @param stackTrace Stack Trace
	 * @param requestBody Request payload
	 */
	protected void logXmlError(com.macys.uop.foundation.core.utils.exception.Error error, String stackTrace, String requestBody) {
		IDataMasker masker=ApplicationMaskingConfiguration.getDefaultXmlMaskerInstance();
		Instant receivedTime=Instant.parse(getReceivedTime());
		
		LogMessageBuilder logMessageBuilder=getLogMessageBuilder(log)
		.withLogType(LogTypeEnum.ERROR)
		.withOperationType(getHttpMethod())
		.withContext(CONTEXT_SERVER_ERROR_LOGGING)
		.withErrorCode(error.getCode())
		.withErrorMessage(error.toString())
		.withStackTrace(stackTrace)
		.withRequestBody(requestBody)
		.withAdditionalInfo(constructMsgProcessingDurationText4Logging(receivedTime, Instant.now(), MSG_PROCESSING_DURATION_TEXT_ORIGIN_REST));
		
		if(masker!=null) {
			logMessageBuilder.withRequestBodyDataMasker(masker);
		} else {
			logMessageBuilder.withMaskingEnabled(false);
		}

		logMessageBuilder.buildDisableChecking().logAsInfo();
	}
	
	/**
	 * Utility method which logs error and publish Order Error and EPF message.
	 * 
	 * @param error {@link com.macys.uop.foundation.core.utils.exception.Error}
	 * @param ex Exception
	 * @param status 
	 */
	protected void logAndPublishErrorEvents(com.macys.uop.foundation.core.utils.exception.Error error, Exception ex, int status) {
		String requestBody=null;
		if(isContentTypeApplicationJson()) {
			requestBody=convertJsonBodyToString(getBody());
		}
		if(isContentTypeApplicationXml()) {
			requestBody=convertXmlBodyToString(getBody());
		}
		String stackTrace = ExceptionUtils.getStackTrace(ex);
		
		if(isContentTypeApplicationJson()) {
			logJsonError(error, stackTrace, requestBody);
		}
		if(isContentTypeApplicationXml()) {
			logXmlError(error, stackTrace, requestBody);
		}
		
		if(isOrderErrorEventPublishingEnabled) {
			publishOrderErrorEvent(error, stackTrace, requestBody);
		}
		
		List<String> errorCodesList = getEPFEventPublishingValidErrorCodes(); 
		
		// Only valid error codes will be sent to EPF
		if(isEPFEventPublishingEnabled && errorCodesList!=null && errorCodesList.contains(error.getCode())) {
			publishEPFEvent(error, status);
		}
	}
	
	/**
	 * Utility method for publishing EPF message through {@link EPFMessagePublisher}
	 * 
	 * @param error {@link com.macys.uop.foundation.core.utils.exception.Error} information
	 * @param status EPF ResponseInfo status code
	 */
	protected void publishEPFEvent(com.macys.uop.foundation.core.utils.exception.Error error, int status) {
		
		Map<String,String> headers=new HashMap<>();
		headers.putAll(getSingleValueHttpHeaders());
		
		Map<String,String> additionalEPFInfo=new HashMap<>();
		additionalEPFInfo.put(EPF_PAYLOAD_KEY, jsonUtils.convertToJson(getBody()));
		additionalEPFInfo.put(EPF_CONTENT_TYPE_KEY, getContentType());
		additionalEPFInfo.put(EPF_ORIGIN_KEY, getOrigin().toString());
		additionalEPFInfo.put(EPF_REQUEST_URL_KEY, getRequestURL());
		additionalEPFInfo.put(EPF_HTTP_METHOD_KEY, getHttpMethod());
		
		// If ErrorDetail information not available then populate default value.
		if(CollectionUtils.isEmpty(error.getErrorDetails())) {
			ErrorDetail errorDetail=ErrorDetail.builder()
					.withDomain(getAppName())
					.withReason(error.getCode())
					.withMessage(error.getMessage())
					.build();
			List<ErrorDetail> errorDetails = new ArrayList<>();
			errorDetails.add(errorDetail);
			error.setErrorDetails(errorDetails);
		} 
		
		epfMessagePublisher.publishEPFMessage(error, status, headers, additionalEPFInfo);
		
	}
	
	/**
	 * This utility method does the following :
	 * <p>
	 * <ul>
	 * <li>Populates {@link OrderErrorMessage} information through {@link OrderErrorMessageBuilder}</li>
	 * <li>Publishes {@link OrderErrorMessage} information through {@link OrderErrorMessagePublisher}</li>
	 * </ul> 
	 * 
	 * @param error {@link com.macys.uop.foundation.core.utils.exception.Error} information
	 * @param stackTrace Stack Trace
	 * @param requestBody Request payload
	 */
	protected void publishOrderErrorEvent(com.macys.uop.foundation.core.utils.exception.Error error, String stackTrace, String requestBody)
	{
		OrderErrorMessageBuilder builder=OrderErrorMessage.builder()
				.withCorrelationId(getCorrelationId())
				.withOrderId(getOrderId())
				.withCreatedBy(getAppName())
				.withCreatedTs(Instant.now().toString())
				.withLastUpdatedBy(getAppName())
				.withLastUpdatedTs(Instant.now().toString())
				.withMessage(requestBody)
				.withErrorCode(error.getCode())
				.withErrorDesc(error.toString())
				.withServiceName(getAppName())
				.withStackTrace(stackTrace)
				.withEntityRefId(getOrderId())
				.withEntityRefType("Order")
				.withUrl(getRequestURL())
				.withMessageContentType(getContentType());
		
		if(getClientId()!=null) {
			builder.withMessageHeader(CLIENTID_HDR, getClientId());
		}
		if(getCorrelationId()!=null) {
			builder.withMessageHeader(CORRELATIONID_HDR, getCorrelationId());
		}
		if(getMessageId()!=null) {
			builder.withMessageHeader(MESSAGEID_HDR, getMessageId());
		}
		if(getOrderId()!=null) {
			builder.withMessageHeader(ORDERID_HDR, getOrderId());
		}
		
		OrderErrorMessage orderErrorMessage=builder.build();
		
		orderErrorMessagePublisher.publishOrderErrorMessage(orderErrorMessage);
	}
	
	/**
	 * Populate list of Error Codes for which EPF event will be published.
	 * By default valid list will contain {@link ommonStatusCode.NO_CORRELATIONID} error code.
	 * 
	 * @return List of Error Codes.
	 */
	protected List<String> getEPFEventPublishingValidErrorCodes() {
		List<String> errorCodesList = null;
		if(StringUtils.isAllBlank(epfEventPublishingValidErrorCodesList)) {
			errorCodesList=new ArrayList<>();
			errorCodesList.add(CommonStatusCode.NO_CORRELATIONID.getCode());
		} else {
			String[] errorCodesArr=epfEventPublishingValidErrorCodesList.split(","); 
			if(errorCodesArr!=null && errorCodesArr.length>0) {
				errorCodesList=Arrays.asList(errorCodesArr);
			} else {
				errorCodesList=new ArrayList<>();
				errorCodesList.add(CommonStatusCode.NO_CORRELATIONID.getCode());
			}
		}
		return errorCodesList;
	}
}
