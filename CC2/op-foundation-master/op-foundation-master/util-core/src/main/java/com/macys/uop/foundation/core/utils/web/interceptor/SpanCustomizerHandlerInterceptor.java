package com.macys.uop.foundation.core.utils.web.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.macys.uop.foundation.core.utils.common.URIUtil;
import com.macys.uop.foundation.core.utils.execution.ServiceContextUtil;

import brave.SpanCustomizer;
import lombok.RequiredArgsConstructor;

/**
 * Interceptor to add Tags to Current Span. 
 * <br>
 * Current implementation looks for qualified header names in <b>spantag.header.names</b> environment variable value.
 * <br> 
 * Based on the names provided, Span tags are added as key=[header name] and value=[incoming request header value]
 */
@Component
@RequiredArgsConstructor
public class SpanCustomizerHandlerInterceptor implements HandlerInterceptor, ServiceContextUtil, URIUtil {

	@Value("${spantag.header.names:#{null}}")
	private String spanTagHeaderNames;

	private final SpanCustomizer spanCustomizer;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		String requestURI = request.getRequestURI();
		if (!isURIInExclusionList(requestURI) && !StringUtils.isAllBlank(spanTagHeaderNames)) {
			String[] spanTagHeaderNamesArray = spanTagHeaderNames.split(",");
			for (int i = 0; i < spanTagHeaderNamesArray.length; i++) {
				String spanTagHeaderName = spanTagHeaderNamesArray[i];
				String spanTagHeaderNameValue = getSingleValueHeaderParam(spanTagHeaderName);
				if (spanTagHeaderNameValue != null && !spanTagHeaderNameValue.isEmpty()) {
					spanCustomizer.tag(spanTagHeaderName, spanTagHeaderNameValue);
				}
			}
		}
		return HandlerInterceptor.super.preHandle(request, response, handler);
	}
}
