package com.macys.uop.foundation.core.utils.web.advice;

import static com.macys.uop.foundation.core.utils.Constant.APPNAME_HDR;
import static com.macys.uop.foundation.core.utils.Constant.APPVERSION_HDR;
import static com.macys.uop.foundation.core.utils.Constant.CONTEXT_SERVER_RESPONSE_LOGGING;
import static com.macys.uop.foundation.core.utils.Constant.MSG_PROCESSING_DURATION_TEXT_ORIGIN_REST;

import java.time.Instant;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.macys.uop.foundation.core.utils.common.StringUtil;
import com.macys.uop.foundation.core.utils.common.URIUtil;
import com.macys.uop.foundation.core.utils.json.JsonUtils;
import com.macys.uop.foundation.core.utils.logging.LogMessageBuilder;
import com.macys.uop.foundation.core.utils.logging.LogTypeEnum;
import com.macys.uop.foundation.core.utils.logging.LoggingUtil;
import com.macys.uop.foundation.core.utils.masking.ApplicationMaskingConfiguration;
import com.macys.uop.foundation.core.utils.masking.IDataMasker;
import com.macys.uop.foundation.core.utils.xml.XmlUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * The purpose of this Controller Advice is to :
 * <br>
 * <ul>
 * <li>Log response payload based on (a) valid request URI (b) content type(application/json, application.xml) (c) <b>server.response.logging.enabled</b> flag (d) Http Status 200 successful </li>
 * <li>During logging if masking is enabled then mask payload data using platform configured default maskers</li>
 * <li>Sends application name and version as response headers</li>
 * </ul>
 */
@ControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class ServiceResponseBodyAdvice implements ResponseBodyAdvice<Object>, LoggingUtil, URIUtil, StringUtil {
	
	@Value("${info.app.name:not-assigned}")
	private String appName;

	@Value("${info.app.version:not-assigned}")
	private String appVersion;
	
	@Value("${server.response.logging.enabled:true}")
	private boolean isServerResponseLoggingEnabled;
	
	private final JsonUtils jsonUtils;
	private final XmlUtils xmlUtils;

	@Override
	public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
		return true;
	}
	
	@Override
	public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
			Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request,
			ServerHttpResponse response) {
		
		response.getHeaders().add(APPNAME_HDR, appName);
		response.getHeaders().add(APPVERSION_HDR, appVersion);
		
		HttpServletResponse servletResponse = getHttpServletResponse(response);
		int status = servletResponse.getStatus();
		String requestURI = request.getURI().toString();
		boolean isValid = HttpStatus.valueOf(status).is2xxSuccessful() && !isURIInExclusionList(requestURI) && isServerResponseLoggingEnabled;
		
		if(isValid && MediaType.APPLICATION_JSON_VALUE.equals(selectedContentType.toString())) {
			Instant receivedTime=Instant.parse(getReceivedTime());

			getLogMessageBuilder(log)
			.withContext(CONTEXT_SERVER_RESPONSE_LOGGING)
			.withLogType(LogTypeEnum.MSG)
			.withOperationType(getHttpMethod())
			.withEndpointUrl(getRequestURL())
			.withHeaderAttribute(response.getHeaders().toSingleValueMap())
			.withStatusCode(String.valueOf(servletResponse.getStatus()))
			.withStatusMessage(HttpStatus.valueOf(servletResponse.getStatus()).name())
			.withResponseBody(jsonUtils.convertToJson(body))
			.withAdditionalInfo(constructMsgProcessingDurationText4Logging(receivedTime, Instant.now(), MSG_PROCESSING_DURATION_TEXT_ORIGIN_REST))
			.buildDisableChecking()
			.logAsInfo();
		}
		
		if(isValid&& MediaType.APPLICATION_XML_VALUE.equals(selectedContentType.toString())) {
			Instant receivedTime=Instant.parse(getReceivedTime());
			IDataMasker masker=ApplicationMaskingConfiguration.getDefaultXmlMaskerInstance();
			
			LogMessageBuilder logMessageBuilder = getLogMessageBuilder(log)
			.withContext(CONTEXT_SERVER_RESPONSE_LOGGING)
			.withLogType(LogTypeEnum.MSG)
			.withOperationType(getHttpMethod())
			.withEndpointUrl(getRequestURL())
			.withHeaderAttribute(response.getHeaders().toSingleValueMap())
			.withStatusCode(String.valueOf(servletResponse.getStatus()))
			.withStatusMessage(HttpStatus.valueOf(servletResponse.getStatus()).name())
			.withResponseBody(xmlUtils.convertToXml(body))
			.withAdditionalInfo(constructMsgProcessingDurationText4Logging(receivedTime, Instant.now(), MSG_PROCESSING_DURATION_TEXT_ORIGIN_REST));
			
			if(masker!=null) {
				logMessageBuilder.withResponseBodyDataMasker(masker);
			} else {
				logMessageBuilder.withMaskingEnabled(false);
			}
			
			logMessageBuilder.buildDisableChecking().logAsInfo();
		}
		
		return body;
	}
	
	/**
	 * Utility method added for convenient conversion.
	 * 
	 * @param response ServerHttpResponse
	 * 
	 * @return HttpServletResponse
	 */
	public HttpServletResponse getHttpServletResponse(ServerHttpResponse response) {
		return ((ServletServerHttpResponse) response).getServletResponse();
	}
}
