package com.macys.uop.foundation.core.utils.test;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import com.macys.uop.foundation.core.utils.Constant;
import com.macys.uop.foundation.core.utils.execution.CustomRequestAttributes;
import com.macys.uop.foundation.core.utils.execution.ServiceRequestContext;

/**
 * The purpose of this class is to initialize and clear the context in test environment.
 */
public interface TestContextUtil 
{
	/**
	 * To be used in JUnit before method.
	 * <br>
	 * New instance of {@link ServiceRequestContext} put in {@link RequestContextHolder} with request scope.
	 */
	public default void initContext()
	{
		RequestAttributes attributes = new CustomRequestAttributes();
		RequestContextHolder.setRequestAttributes(attributes);
		ServiceRequestContext context = new ServiceRequestContext();
		RequestContextHolder.getRequestAttributes().setAttribute(Constant.SERVICE_REQUEST_CONTEXT, context,
				RequestAttributes.SCOPE_REQUEST);
	}
	
	/**
	 * To be used in JUnit after method. Clears {@link RequestContextHolder}
	 */
	public default void clearContext() {
		RequestContextHolder.resetRequestAttributes();
	}
}
