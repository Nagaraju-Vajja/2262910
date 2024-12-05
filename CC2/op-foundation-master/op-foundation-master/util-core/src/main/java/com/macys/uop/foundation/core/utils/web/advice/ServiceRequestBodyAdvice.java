package com.macys.uop.foundation.core.utils.web.advice;

import static com.macys.uop.foundation.core.utils.Constant.CONTEXT_SERVER_REQUEST_LOGGING;

import java.lang.reflect.Type;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;

import com.macys.uop.foundation.core.utils.Constant;
import com.macys.uop.foundation.core.utils.common.URIUtil;
import com.macys.uop.foundation.core.utils.execution.ServiceRequestContext;
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
 * The purpose of this Controller Advice is to 
 * <br>
 * <ul>
 * <li> Set request payload in ServiceRequestContext body</li>
 * <li>Log request payload based on (a) valid request URI (b) content type(application/json, application.xml) and (c) <b>server.request.logging.enabled</b> flag</li>
 * <li>During logging if masking is enabled then mask payload data using platform configured default maskers</li>
 * </ul>
 */
@ControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class ServiceRequestBodyAdvice extends RequestBodyAdviceAdapter implements LoggingUtil, URIUtil {

	@Value("${server.request.logging.enabled:true}")
	private boolean isServerRequestLoggingEnabled;
	
	private final JsonUtils jsonUtils;
	private final XmlUtils xmlUtils;

	@Override
	public Object afterBodyRead(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType,
			Class<? extends HttpMessageConverter<?>> converterType) {
		ServiceRequestContext serviceRequestContext = (ServiceRequestContext) RequestContextHolder.getRequestAttributes()
				.getAttribute(Constant.SERVICE_REQUEST_CONTEXT, RequestAttributes.SCOPE_REQUEST);
		
		boolean isValid = !isURIInExclusionList(getUri()) && isServerRequestLoggingEnabled;
		
		if(isValid) {
			serviceRequestContext.setBody(body);
		}	
		
		if(isValid && isContentTypeApplicationJson()) {
			getLogMessageBuilder(log)
			.withContext(CONTEXT_SERVER_REQUEST_LOGGING)
			.withLogType(LogTypeEnum.MSG)
			.withOperationType(getHttpMethod())
			.withEndpointUrl(getRequestURL())
			.withHeaderAttribute(getSingleValueHttpHeaders())
			.withRequestBody(jsonUtils.convertToJson(body))
			.buildDisableChecking()
			.logAsInfo();
		}
		
		if(isValid && isContentTypeApplicationXml()) {
			IDataMasker masker=ApplicationMaskingConfiguration.getDefaultXmlMaskerInstance();
			
			LogMessageBuilder logMessageBuilder=getLogMessageBuilder(log)
			.withContext(CONTEXT_SERVER_REQUEST_LOGGING)
			.withLogType(LogTypeEnum.MSG)
			.withOperationType(getHttpMethod())
			.withEndpointUrl(getRequestURL())
			.withHeaderAttribute(getSingleValueHttpHeaders())
			.withRequestBody(xmlUtils.convertToXml(body));
			
			if(masker!=null) {
				logMessageBuilder.withRequestBodyDataMasker(masker);
			} else {
				logMessageBuilder.withMaskingEnabled(false);
			}

			logMessageBuilder.buildDisableChecking().logAsInfo();
		}
		
		return body;
	}

	@Override
	public boolean supports(MethodParameter methodParameter, Type targetType,
			Class<? extends HttpMessageConverter<?>> converterType) {
		return true;
	}
}
