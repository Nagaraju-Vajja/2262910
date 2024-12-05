package com.macys.uop.foundation.core.utils.exception;

import org.aspectj.lang.JoinPoint;

/**
 * Interface implemented by DataExceptionHandler, ServiceExceptionHandler and ControllerExceptionHandler.
 * 
 * Refer to core and composite service AOP driven exception handling implementation classes.
 * Also refer to core and composite service resources/app-config.xml for the pointcuts
 * 
 */
public interface ExceptionHandler {
	/**
	 * Method to be implemented by Exception implementation clsses.
	 * 
	 * @param joinPoint AOP JoinPoint
	 * @param error Throwable
	 */
	void handleException(JoinPoint joinPoint, Throwable error);
}
