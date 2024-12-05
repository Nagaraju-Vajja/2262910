package com.macys.uop.foundation.core.utils.trace;

import java.util.Map;

import org.slf4j.MDC;
import org.springframework.core.task.TaskDecorator;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

/**
 * {@link TaskDecorator} which helps making available the following information to supplied {@link Runnable}
 * <ul>
 * <li>{@link RequestContextHolder} attributes</li>
 * <li>{@link MDC} context map</li>
 * </ul> 
 * 
 */
public class ContextCopyingDecorator implements TaskDecorator {
	@Override
	public Runnable decorate(Runnable runnable) {
		RequestAttributes context = RequestContextHolder.currentRequestAttributes();
		Map<String, String> contextMap = MDC.getCopyOfContextMap();
		return () -> {
			try {
				RequestContextHolder.setRequestAttributes(context);
				MDC.setContextMap(contextMap);
				runnable.run();
			} finally {
				MDC.clear();
				RequestContextHolder.resetRequestAttributes();
			}
		};
	}
}
