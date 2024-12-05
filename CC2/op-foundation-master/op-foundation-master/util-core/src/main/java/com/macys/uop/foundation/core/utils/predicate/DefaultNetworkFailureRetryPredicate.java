package com.macys.uop.foundation.core.utils.predicate;

import static com.macys.uop.foundation.core.utils.Constant.CONTEXT_SUBSCRIBER_ERROR_MESSAGE_LOGGING;

import java.util.function.Predicate;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import com.macys.uop.foundation.core.utils.CommonStatusCode;
import com.macys.uop.foundation.core.utils.execution.ServiceContextUtil;
import com.macys.uop.foundation.core.utils.logging.LogMessageBuilder;
import com.macys.uop.foundation.core.utils.logging.LogTypeEnum;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DefaultNetworkFailureRetryPredicate implements Predicate<Throwable>, ServiceContextUtil {

	/**
	 * Default retry predicate behavior. Will extract error and compare against HTTP Error Codes
	 *  404 - Not found, 408 - Timed out, 502 - Bad Gateway, 503 - Service Unavailable, 504 - Gateway Timed Out
	 * If true, then there will be a retry
	 * 
	 * @param e
	 * @return boolean
	 */
	public boolean test(Throwable e) {
		boolean shouldRetry = false;
		
		shouldRetry = isNetworkError(e, shouldRetry);
		
		return shouldRetry;
	}

	protected boolean isNetworkError(Throwable e, boolean shouldRetry) {
		int httpStatusCode = 0;
    	if (e instanceof HttpClientErrorException) {
    		HttpClientErrorException error = (HttpClientErrorException) e;
    		httpStatusCode = error.getRawStatusCode();
    	} else if (e instanceof HttpServerErrorException) {
    		HttpServerErrorException error = (HttpServerErrorException) e;
    		httpStatusCode = error.getRawStatusCode();
    	} else if (e instanceof ResourceAccessException) {
    		shouldRetry = true;
    	}

    	if (httpStatusCode == 404 || httpStatusCode == 502  || httpStatusCode == 408 || httpStatusCode == 504 || httpStatusCode == 503)  {
    		shouldRetry = true;
		}
		return shouldRetry;
	}
	
}
