package com.macys.uop.foundation.core.utils.web.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import lombok.RequiredArgsConstructor;

/**
 * Spring configuration class to register interceptor handlers. Interceptor ordering is very important here.
 * <br>
 * <ul>
 * <li>Purpose of {@link ServiceRequestHandlerInterceptor} is to populate {@link ServiceRequestContext}</li>
 * <li>Purpose of {@link SpanCustomizerHandlerInterceptor} is to add span tags</li>
 * <li>Purpose of {@link ServerRequestLogInterceptor} is to log Http GET type request</li>
 * <li>Purpose of {@link ServiceRequestHeaderHandlerInterceptor} is to validate mandatory headers</li>
 * </ul> 
 */
@Configuration
@RequiredArgsConstructor
public class WebMvcConfiguration implements WebMvcConfigurer {
	private final HandlerInterceptor serviceRequestHandlerInterceptor;
	private final HandlerInterceptor spanCustomizerHandlerInterceptor;
	private final HandlerInterceptor serverRequestLogInterceptor;
	private final HandlerInterceptor serviceRequestHeaderHandlerInterceptor;
	
	/**
	 * Add interceptors to registry
	 */
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(serviceRequestHandlerInterceptor);
		registry.addInterceptor(spanCustomizerHandlerInterceptor);
		registry.addInterceptor(serverRequestLogInterceptor);
		registry.addInterceptor(serviceRequestHeaderHandlerInterceptor);
	}
}
