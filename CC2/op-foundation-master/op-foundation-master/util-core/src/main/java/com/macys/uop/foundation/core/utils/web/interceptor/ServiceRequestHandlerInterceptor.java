package com.macys.uop.foundation.core.utils.web.interceptor;

import java.time.Instant;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import com.macys.uop.foundation.core.utils.Constant;
import com.macys.uop.foundation.core.utils.execution.ServiceRequestContext;

import lombok.RequiredArgsConstructor;

/**
 * Interceptor which populates {@link ServiceRequestContext} and set it in {@link RequestContextHolder} under {@link RequestAttributes#SCOPE_REQUEST} Scope
 *
 */
@Component
@RequiredArgsConstructor
public class ServiceRequestHandlerInterceptor implements HandlerInterceptor {
	
	@Value("${spring.application.name:default}")
	private String applicationName;
	
	private final IRequestProcessor requestProcessor;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

		// 1 Set Request Received Time 
		String receivedTime=Instant.now().toString();

		// 2 Set Request URL
		String url = request.getRequestURL().toString();
		
		// 3 Set Request URI
		String uri = request.getRequestURI();

		// 4 Set Http Method
		String method = request.getMethod();

		// 5 Set Headers. 
		HttpHeaders headers = requestProcessor.processHeaders(request);
		
		// 6 Preserve Original CLIENTID_HDR in callerId
		String callerId=requestProcessor.getCallerId(request);
		
		// 7 Set Path Parameters
		@SuppressWarnings("unchecked")
		Map<String, String> pathParams = (Map<String, String>) request
				.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

		// 8 Set query Parameters
		MultiValueMap<String, String> queryParams = requestProcessor.extractQueryParameters(request);
		
		// 9 Set Content Type
		String contentType=request.getContentType();
		
		RequestContextHolder.getRequestAttributes().setAttribute(
				Constant.SERVICE_REQUEST_CONTEXT, ServiceRequestContext.builder()
						.headers(headers)
						.pathParams(pathParams)
						.queryParams(queryParams)
						.url(url)
						.uri(uri)
						.method(method)
						.applicationName(applicationName)
						.receivedTime(receivedTime)
						.callerId(callerId)
						.contentType(contentType)
						.build(),
				RequestAttributes.SCOPE_REQUEST);

		return HandlerInterceptor.super.preHandle(request, response, handler);
	}
}
