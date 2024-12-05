package com.macys.uop.foundation.core.utils.exception;

import static com.macys.uop.foundation.core.utils.Constant.PROBLEM_ERROR_KEY;

import java.util.Map;

import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import org.zalando.problem.ThrowableProblem;

import com.macys.uop.foundation.core.utils.CommonStatusCode;
import com.macys.uop.foundation.core.utils.Constant;

/**
 * The purpose of this interface is to :
 * <br>
 * <ul>
 * <li>provide various overloaded helper methods for creating ThrowableProblem</li>
 * <li>hide the internal implementation : directly dealing with Problem Object</li>
 * <li>create Error object and set it under key {@value Constant#PROBLEM_ERROR_KEY} in Problem.  </li>
 * </ul> 
 * <br>
 * Method input parameters are self explanatory. Documentation at method level is omitted.  
 *
 */
public interface ProblemUtil {
	public default ThrowableProblem createProblem(com.macys.uop.foundation.core.utils.exception.Error error) {
		return Problem.builder().with(PROBLEM_ERROR_KEY, error).build();
	}

	public default ThrowableProblem createProblem(int httpStatusCode,
			com.macys.uop.foundation.core.utils.exception.Error error) {
		return Problem.builder().withStatus(Status.valueOf(httpStatusCode)).with(PROBLEM_ERROR_KEY, error).build();
	}
	
	public default ThrowableProblem createProblem(String appCode, String appMessage) {
		com.macys.uop.foundation.core.utils.exception.Error error = Error.builder().withCode(appCode)
				.withMessage(appMessage).build();
		return Problem.builder().with(PROBLEM_ERROR_KEY, error).build();
	}

	public default ThrowableProblem createProblem(int httpStatusCode, String appCode, String appMessage) {
		com.macys.uop.foundation.core.utils.exception.Error error = Error.builder().withCode(appCode)
				.withMessage(appMessage).build();
		return Problem.builder().withStatus(Status.valueOf(httpStatusCode)).with(PROBLEM_ERROR_KEY, error).build();
	}

	public default ThrowableProblem createProblem(String appCode, String appMessage, Throwable throwable) {
		com.macys.uop.foundation.core.utils.exception.Error error = Error.builder().withCode(appCode)
				.withMessage(appMessage).build();
		return createProblem(error, throwable);
	}
	
	public default ThrowableProblem createProblem(int httpStatusCode, String appCode, String appMessage, Throwable throwable) {
		com.macys.uop.foundation.core.utils.exception.Error error = Error.builder().withCode(appCode)
				.withMessage(appMessage).build();
		return createProblem(httpStatusCode, error, throwable);
	}
	
	public default ThrowableProblem createProblem(int httpStatusCode, String appCode, String appMessage, String key, String value, Throwable throwable) {
		com.macys.uop.foundation.core.utils.exception.Error error = Error.builder().withCode(appCode)
				.withMessage(appMessage).build();
		return createProblem(httpStatusCode, error, key, value, throwable);
	}
	
	public default ThrowableProblem createProblem(com.macys.uop.foundation.core.utils.exception.Error error, Throwable throwable) {
		final ThrowableProblem problem = Problem.builder().with(PROBLEM_ERROR_KEY, error).build();
		final StackTraceElement[] stackTrace = throwable.getStackTrace();
		problem.setStackTrace(stackTrace);
		return problem;
	}
	
	public default ThrowableProblem createProblem(int httpStatusCode, com.macys.uop.foundation.core.utils.exception.Error error, Throwable throwable) {
		final ThrowableProblem problem = Problem.builder().withStatus(Status.valueOf(httpStatusCode)).with(PROBLEM_ERROR_KEY, error).build();
		final StackTraceElement[] stackTrace = throwable.getStackTrace();
		problem.setStackTrace(stackTrace);
		return problem;
	}
	
	public default ThrowableProblem createProblem(int httpStatusCode, com.macys.uop.foundation.core.utils.exception.Error error, String key, String value, Throwable throwable) {
		final ThrowableProblem problem = Problem.builder()
				.withStatus(Status.valueOf(httpStatusCode))
				.with(PROBLEM_ERROR_KEY, error)
				.with(key, value)
				.build();
		final StackTraceElement[] stackTrace = throwable.getStackTrace();
		problem.setStackTrace(stackTrace);
		return problem;
	}
	
	public default ThrowableProblem createProblem(int httpStatusCode, String appCode, String appMessage, ThrowableProblem problem) {
		com.macys.uop.foundation.core.utils.exception.Error error = Error.builder().withCode(appCode)
				.withMessage(appMessage).build();
		return Problem.builder().withStatus(Status.valueOf(httpStatusCode)).withCause(problem).with(PROBLEM_ERROR_KEY, error).build();
	}
	
	public default com.macys.uop.foundation.core.utils.exception.Error createError(String code, String message,
			String detailDomain, String detailReason, String detailMessage)
	{
		return createErrorBuilder(code, message, detailDomain, detailReason, detailMessage).build();
	}
	
	public default ErrorBuilder createErrorBuilder(String code, String message,
			String detailDomain, String detailReason, String detailMessage)
	{
		return Error.builder()
				.withCode(code)
				.withMessage(message)
				.withErrorDetail(ErrorDetail.builder()
						.withDomain(detailDomain)
						.withReason(detailReason)
						.withMessage(detailMessage)
						.build());
	}
	
	public default ThrowableProblem createProblem(String channelName, String payload, Map<String, String> headers, String applicationName, Throwable throwable)
	{
		com.macys.uop.foundation.core.utils.exception.Error error = com.macys.uop.foundation.core.utils.exception.Error
				.builder()
				.withCode(CommonStatusCode.APPLICATION_ERROR.getCode())
				.withMessage(CommonStatusCode.APPLICATION_ERROR.getDescription())
				.withErrorDetail(ErrorDetail.builder()
						.withDomain("publish")
						.withReason("Invalid Resource")
						.withMessage(throwable.getMessage())
						.withLocation(applicationName)
						.build())
				.build();
		return createProblem(Status.INTERNAL_SERVER_ERROR.getStatusCode(), error, throwable);
	}
	
	/**
	 * Extract Error information from ThrowableProblem. Internally, Error information is stored against key {@value Constant#PROBLEM_ERROR_KEY} in ThrowableProblem.
	 *  
	 * @param problem ThrowableProblem
	 * 
	 * @return ThrowableProblem else null if Error information is not found against key {@value Constant#PROBLEM_ERROR_KEY} "error"
	 */
	public default com.macys.uop.foundation.core.utils.exception.Error extractError(ThrowableProblem problem)
	{
		com.macys.uop.foundation.core.utils.exception.Error error=null;
		Map<String, Object> parameters=problem.getParameters();
		if(parameters!=null && !parameters.isEmpty() && parameters.containsKey(PROBLEM_ERROR_KEY)) {
			error = (com.macys.uop.foundation.core.utils.exception.Error) problem.getParameters().get(PROBLEM_ERROR_KEY);
		}
		return error;
	}
}
