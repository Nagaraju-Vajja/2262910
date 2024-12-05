package com.macys.uop.foundation.core.utils.web.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.macys.uop.foundation.core.utils.execution.ServiceContextUtil;
import com.macys.uop.foundation.core.utils.validation.IHeaderCheck;

import lombok.RequiredArgsConstructor;

/**
 * Interceptor to validate mandatory header parameters based on <b>default.mandatory.header.checking.enabled</b> flag.
 * <br>
 * Mandatory header checking is made pluggable. 
 * <br>
 * In order to overwrite the default implementation 
 * <br>
 * Developer should write his/her own implementation implementing {@link IHeaderCheck}.
 * <br>
 * Developer should make the implementation as primary bean through @Primary annotation.
 */
@Component
@RequiredArgsConstructor
public class ServiceRequestHeaderHandlerInterceptor implements HandlerInterceptor, ServiceContextUtil {
	
	private final IHeaderCheck headerCheckService;
	
	@Value("${default.mandatory.header.checking.enabled:true}")
	private boolean isDefaultMandatoryHeaderCheckingEnabled;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		if(isDefaultMandatoryHeaderCheckingEnabled) {
			headerCheckService.validateHeaders(request);
		}	
		return HandlerInterceptor.super.preHandle(request, response, handler);
	}
}
