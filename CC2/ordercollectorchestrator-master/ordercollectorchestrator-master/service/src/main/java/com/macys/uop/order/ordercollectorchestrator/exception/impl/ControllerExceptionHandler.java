package com.macys.uop.order.ordercollectorchestrator.exception.impl;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.zalando.problem.Status;
import org.zalando.problem.ThrowableProblem;

import com.macys.uop.foundation.core.utils.CommonStatusCode;
import com.macys.uop.foundation.core.utils.exception.ExceptionHandler;
import com.macys.uop.foundation.core.utils.exception.ProblemUtil;
import com.macys.uop.foundation.core.utils.execution.ServiceContextUtil;
import com.macys.uop.foundation.core.utils.logging.LoggingUtil;

@Slf4j
public class ControllerExceptionHandler
    implements ExceptionHandler, ServiceContextUtil, ProblemUtil, LoggingUtil {

    /**
     * controller exception handler method
     * @param joinPoint
     * @param error
     */
    @Override
    public void handleException(JoinPoint joinPoint, Throwable error) {
      if (error instanceof ThrowableProblem) {
        throw (ThrowableProblem) error;
      } else {
        getErrorLogMessageBuilder(
                CommonStatusCode.INTERNAL_SERVICE_ERROR.getCode(),
                CommonStatusCode.INTERNAL_SERVICE_ERROR.getDescription(),
                error, log)
            .build()
            .logAsError();
        throw createProblem(
            Status.INTERNAL_SERVER_ERROR.getStatusCode(),
            CommonStatusCode.INTERNAL_SERVICE_ERROR.getCode(),
            CommonStatusCode.INTERNAL_SERVICE_ERROR.getDescription(),
            error);
      }
    }
}
