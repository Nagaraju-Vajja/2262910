package com.macys.uop.foundation.core.utils.web.interceptor;

import static com.macys.uop.foundation.core.utils.Constant.CONTEXT_SERVER_REQUEST_LOGGING;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.DispatcherType;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.macys.uop.foundation.core.utils.common.URIUtil;
import com.macys.uop.foundation.core.utils.logging.LogTypeEnum;
import com.macys.uop.foundation.core.utils.logging.LoggingUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Interceptor to log based on (a) valid request URI (b) Http GET method only and (c) <b>server.request.logging.enabled</b> flag
 *
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ServerRequestLogInterceptor implements HandlerInterceptor, LoggingUtil, URIUtil {

	@Value("${server.request.logging.enabled:true}")
	private boolean isServerRequestLoggingEnabled;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		String requestURI = request.getRequestURI();
		if (DispatcherType.REQUEST.name().equals(request.getDispatcherType().name())
				&& request.getMethod().equals(HttpMethod.GET.name()) && !isURIInExclusionList(requestURI)
				&& isServerRequestLoggingEnabled) {
			getLogMessageBuilder(log)
			.withContext(CONTEXT_SERVER_REQUEST_LOGGING)
			.withLogType(LogTypeEnum.MSG)
			.withOperationType(getHttpMethod())
			.withEndpointUrl(getRequestURL())
			.withHeaderAttribute(getSingleValueHttpHeaders())
			.buildDisableChecking()
			.logAsInfo();
		}
		return true;
	}
}
