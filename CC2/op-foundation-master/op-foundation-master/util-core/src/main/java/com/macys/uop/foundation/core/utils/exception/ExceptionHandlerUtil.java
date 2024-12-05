package com.macys.uop.foundation.core.utils.exception;

import static org.slf4j.LoggerFactory.getLogger;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.zalando.problem.ThrowableProblem;

import com.macys.uop.foundation.core.utils.CommonStatusCode;
import com.macys.uop.foundation.core.utils.json.JsonUtils;
import com.macys.uop.foundation.core.utils.logging.LoggingUtil;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;

public interface ExceptionHandlerUtil extends LoggingUtil, ProblemUtil, ExceptionUtil {

	public static final JsonUtils jsonUtils = null;
	
    /**
     * Overloaded method
     * 
     * @param error
     */
    public default void handleServiceFailure(Throwable error) {
    	handleServiceFailure(error, CommonStatusCode.INTERNAL_SERVICE_ERROR.getCode(), CommonStatusCode.INTERNAL_SERVICE_ERROR.getDescription());
    }

    /**
     * Overloaded method
     * 
     * @param error
     * @param code
     * @param desc
     */
    public default void handleServiceFailure(Throwable error, String code, String desc) {
    	handleServiceFailure(error, code, desc, null);
    }
    
	/**
     * If the error is a network error, converts it to problem based on the raw status code
     * 404 - Not found, 408 - Timed out, 502 - Bad Gateway, 503 - Service Unavailable, 504 - Gateway Timed Out
     * Above exceptions are handled
     * 
     * @param error
     */
    public default void handleServiceFailure(Throwable error, String code, String desc, String location) {

    	if (error instanceof ThrowableProblem) {
            throw (ThrowableProblem) error;
        }
    	

    	String reasonCode = getReasonCode(error);
    	String message = getMessage(error);
    	
    	if (CommonStatusCode.CIRCUIT_BREAKER_OPEN.getCode().equals(reasonCode)) {
    		if (message.indexOf("'") != -1) {
    			location = message.substring(message.indexOf("'") + 1, message.lastIndexOf("'"));
    		} else {
    			location = "default";
    		}
    	}
		
        // If all these errors are not handled, throw a default problem
        
        String additionalInfo = "Error occurred while processing, reason: " + reasonCode + ", Exception: " + message;

        getErrorLogMessageBuilder(code,
        		desc, error, additionalInfo, LogHolder.LOGGER)
        		.withStackTrace(ExceptionUtils.getStackTrace(error))
                .build()
                .logAsError();

        Error genericError = Error.builder()
                .withCode(code)
                .withMessage(desc)
                .withErrorDetail(ErrorDetail.builder()
                        .withDomain(getAppName())
                        .withReason(reasonCode)
                        .withMessage(additionalInfo)
                        .withLocation(location)
                        .build())
                .build();

        int rawStatusCode = getStatusCode(error);
        throw createProblem(rawStatusCode != 0? rawStatusCode: org.zalando.problem.Status.INTERNAL_SERVER_ERROR.getStatusCode(), genericError);
        
    }

	public default String getReasonCode(Throwable error) {
		String reasonCode = "unknown";
		String message = getMessage(error);
    	int httpStatusCode = 0;
    	if (error instanceof HttpClientErrorException || error instanceof HttpServerErrorException) {
    		httpStatusCode = getStatusCode(error);
    		
    		if (httpStatusCode == 404 || httpStatusCode == 502 ) { 
    			reasonCode = CommonStatusCode.NOT_FOUND.getCode();
    		} else if (httpStatusCode == 408) {
    			reasonCode = CommonStatusCode.CLIENT_TIMEOUT.getCode();
    		} else if (httpStatusCode == 504)  {
    			reasonCode = CommonStatusCode.SERVICE_TIMEOUT.getCode();
    		} else if (httpStatusCode == 503)  {
    			reasonCode = CommonStatusCode.SERVICE_NOT_REACHABLE.getCode();
    		}

    		if (message != null && message.contains(CommonStatusCode.CIRCUIT_BREAKER_OPEN.getCode())
    				&& message.contains(CommonStatusCode.CIRCUIT_BREAKER_OPEN.getDescription())) {
    			reasonCode = CommonStatusCode.PROXY_CALL_CIRCUIT_BREAKER_OPEN.getCode();
    		}
    		
    	} else if (error instanceof ResourceAccessException) {
			reasonCode = CommonStatusCode.RESOURCE_ACCESS_EXCEPTION.getCode();
			
			if (message != null && message.contains(CommonStatusCode.CIRCUIT_BREAKER_OPEN.getCode())
    				&& message.contains(CommonStatusCode.CIRCUIT_BREAKER_OPEN.getDescription())) {
    			reasonCode = CommonStatusCode.PROXY_CALL_CIRCUIT_BREAKER_OPEN.getCode();
    		}
			
    	} else if (error instanceof CallNotPermittedException) { 
			reasonCode = CommonStatusCode.CIRCUIT_BREAKER_OPEN.getCode();
    	}
		return reasonCode;
	}

	static final class LogHolder {
        private static final Logger LOGGER = getLogger(ExceptionHandlerUtil.class);

        private LogHolder() {
        }
    }

}
