package com.macys.uop.foundation.messagestore;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.zalando.problem.Status;
import org.zalando.problem.ThrowableProblem;

import com.macys.uop.foundation.core.utils.CommonStatusCode;
import com.macys.uop.foundation.core.utils.exception.ErrorDetail;
import com.macys.uop.foundation.core.utils.exception.ProblemUtil;
import com.macys.uop.foundation.core.utils.execution.StreamContextUtil;
import com.macys.uop.foundation.core.utils.logging.LogMessageBuilder;
import com.macys.uop.foundation.core.utils.logging.LogTypeEnum;

/**
 * Abstract class to capture common functionality related to {@link MessageDuplicationCheckService} implementation.
 *
 */
public abstract class AbstractMessageDuplicationCheckService implements StreamContextUtil, ProblemUtil {
	
	protected static final String CONTEXT_MESSAGE_DUPLICATION_CHECK="MESSAGE DUPLICATION CHECK";
	
	/**
	 * Default fallback method. This method 
	 * <ul>
	 * <li>Populates {@link com.macys.uop.foundation.core.utils.exception.Error} information</li>
	 * <li>Logs error</li>
	 * <li>Returns {@link org.zalando.problem.ThrowableProblem}</li>
	 * </ul> 
	 * 
	 * @param e
	 * 
	 * @param fallbackType
	 * 
	 * @return ThrowableProblem
	 */
	protected ThrowableProblem defaultMessageDuplicationCheckFallback(Throwable e, String fallbackType) {
			
			CommonStatusCode error = CommonStatusCode.MSSAGE_DUPLICATION_CHECK_ERROR;

			if (e instanceof IllegalStateException && e.getMessage() != null
					&& e.getMessage().contains("Pool has been closed")) {
				error = CommonStatusCode.DB_POOL_CLOSED_ERROR;
			}
			
			new LogMessageBuilder()
			.withClientId(getClientId())
			.withMessageId(getMessageId())
			.withOrderId(getOrderId())
			.withCorrelationId(getCorrelationId())
			.withAppName(getAppName())
			.withCallerId(getCallerId())
			.withLogType(LogTypeEnum.ERROR)
			.withErrorCode(error.getCode())
			.withErrorMessage(error.getDescription())
			.withStackTrace(ExceptionUtils.getStackTrace(e))
			.withAdditionalInfo(fallbackType + " triggered during message duplication check.")
			.withContext(CONTEXT_MESSAGE_DUPLICATION_CHECK)
			.buildDisableChecking()
			.logAsError();
			
			com.macys.uop.foundation.core.utils.exception.Error errorInfo = com.macys.uop.foundation.core.utils.exception.Error
					.builder()
					.withCode(error.getCode())
					.withMessage(error.getDescription())
					.withErrorDetail(ErrorDetail.builder()
							.withDomain(getAppName())
							.withReason("Message Duplication Check Error")
							.withMessage(e.getMessage())
							.build())
					.build();
			
			return createProblem(Status.INTERNAL_SERVER_ERROR.getStatusCode(), errorInfo);
	}
}
