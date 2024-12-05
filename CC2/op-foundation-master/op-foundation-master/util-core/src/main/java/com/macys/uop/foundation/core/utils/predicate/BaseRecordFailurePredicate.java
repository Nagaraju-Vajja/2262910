package com.macys.uop.foundation.core.utils.predicate;

import java.util.Arrays;
import java.util.function.Predicate;

import org.zalando.problem.ThrowableProblem;

import com.macys.uop.foundation.core.utils.exception.Error;
import com.macys.uop.foundation.core.utils.logging.LoggingUtil;
import com.macys.uop.foundation.core.utils.spring.SpringContextBridge;

public abstract class BaseRecordFailurePredicate implements Predicate<Throwable>, LoggingUtil {

	/**
	 * Will be populated by the extended class
	 */
	protected abstract String getCBName();

	/**
	 * Default Ignore exception predicate behavior. Will extract error code and
	 * compare against property value
	 * resilience4j.circuitbreaker.instances.{cb-name}.ignoreExceptionCodes
	 * 
	 * @param e
	 * @return boolean
	 */
	public boolean test(Throwable e) {
		boolean isFailure = false;
		Error error = null;

		String circuitBreakerExclusionErrorCodes = SpringContextBridge
				.getProperty("resilience4j.circuitbreaker.instances." + getCBName() + ".recordFailureReasonCodes");

		if (e instanceof ThrowableProblem) {
			ThrowableProblem problem = (ThrowableProblem) e;
			if (problem.getParameters().get("error") != null) {
				error = (Error) problem.getParameters().get("error");
				String reason = null;

				if (error.getErrorDetails() != null && error.getErrorDetails().size() > 0) {
					reason = error.getErrorDetails().get(0).getReason();
				}
				
				if (reason != null && circuitBreakerExclusionErrorCodes != null) {
					String[] errorCodesArr = circuitBreakerExclusionErrorCodes.split(",");
					if (reason != null && errorCodesArr != null && errorCodesArr.length > 0) {
						isFailure = Arrays.asList(errorCodesArr).contains(reason);
					}
				}

			}
		}
		return isFailure;
	}
	
}
