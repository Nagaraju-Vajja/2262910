package com.macys.uop.foundation.core.utils.exception;

import static com.macys.uop.foundation.core.utils.Constant.DEFAULT_EXCEPTION_MESSAGE_WHEN_BLANK;

import java.util.concurrent.ExecutionException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.zalando.problem.ThrowableProblem;

public interface ExceptionUtil {
	
	/**
	 * Method that returns exception message.
	 * 
	 * @param throwable
	 * 
	 * @return Exception message
	 */
	public default String getMessage(Throwable throwable) {
		Assert.notNull(throwable, "'throwable' cannot be null");
		return StringUtils.isAllBlank(throwable.getMessage())?DEFAULT_EXCEPTION_MESSAGE_WHEN_BLANK:throwable.getMessage();
	}
	
	/**
	 * Method that returns HTTPStatus Code for given Throwable
	 * 
	 * @param throwable
	 * 
	 * @return int
	 */
	public default int getStatusCode(Throwable throwable) {
		int statusCode = 0;
		if ((throwable instanceof HttpClientErrorException)) {
			statusCode = ((HttpClientErrorException) throwable).getStatusCode().value();
		} else if ((throwable instanceof HttpServerErrorException)) {
			statusCode = ((HttpServerErrorException) throwable).getRawStatusCode();
		} else if (throwable instanceof InterruptedException) {
			statusCode = ((InterruptedException) throwable).hashCode();
		} else if (throwable instanceof ExecutionException) {
			statusCode = ((ExecutionException) throwable).hashCode();
		} else if (throwable instanceof ThrowableProblem) {
			statusCode = ((ThrowableProblem) throwable).getStatus().getStatusCode();
		} else {
			statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
		}
		return statusCode;
	}

}
