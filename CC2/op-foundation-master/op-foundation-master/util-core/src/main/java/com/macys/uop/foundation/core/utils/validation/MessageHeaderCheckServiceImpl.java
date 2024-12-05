package com.macys.uop.foundation.core.utils.validation;

import static com.macys.uop.foundation.core.utils.Constant.HDR_REQUIRED_DEFAULT_LOCATIONTYPE;
import static com.macys.uop.foundation.core.utils.Constant.HDR_REQUIRED_DEFAULT_REASON;
import static com.macys.uop.foundation.core.utils.Constant.CONTEXT_MESSAGE_HEADER_VALIDATION_LOGGING;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.zalando.problem.Status;

import com.google.pubsub.v1.PubsubMessage;
import com.macys.uop.foundation.core.utils.CommonStatusCode;
import com.macys.uop.foundation.core.utils.Constant;
import com.macys.uop.foundation.core.utils.exception.ErrorDetail;
import com.macys.uop.foundation.core.utils.exception.ProblemUtil;
import com.macys.uop.foundation.core.utils.execution.StreamContextUtil;
import com.macys.uop.foundation.core.utils.logging.LogMessageBuilder;
import com.macys.uop.foundation.core.utils.logging.LogTypeEnum;

import lombok.extern.slf4j.Slf4j;

/**
 * This is the default implementation of {@link IMessageHeaderCheck} provided by foundation which validates only {@link Constant#CORRELATIONID_HDR} header.
 * <p>
 * @see <a href="https://confluence.federated.fds/display/OCOM/UOP+Request+-+Headers+handlers+and+validations">Header Validation Rules</a>
 */
@Service
@Slf4j
public class MessageHeaderCheckServiceImpl implements IMessageHeaderCheck, StreamContextUtil, ProblemUtil {

	@Override
	public void validateMessageHeaders(PubsubMessage message) {
		
		String url=getUrl();
		String applicationName=getAppName();
		
		com.macys.uop.foundation.core.utils.exception.Error err = null;
		
		if (StringUtils.isAllBlank(getCorrelationId())) {
			err = com.macys.uop.foundation.core.utils.exception.Error.builder()
					.withCode(CommonStatusCode.NO_CORRELATIONID.getCode())
					.withMessage(CommonStatusCode.NO_CORRELATIONID.getDescription())
					.withErrorDetail(ErrorDetail.builder()
							.withDomain(applicationName)
							.withReason(HDR_REQUIRED_DEFAULT_REASON)
							.withMessage(CommonStatusCode.NO_CORRELATIONID.getDescription())
							.withLocation(url)
							.withLocationType(HDR_REQUIRED_DEFAULT_LOCATIONTYPE).build())
					.build();

			new LogMessageBuilder()
			.withClientId(getClientId())
			.withMessageId(getMessageId())
			.withOrderId(getOrderId())
			.withCorrelationId(getCorrelationId())
			.withAppName(getAppName())
			.withCallerId(getCallerId())
			.withLogType(LogTypeEnum.ERROR)
			.withErrorCode(CommonStatusCode.NO_CORRELATIONID.getCode())
			.withErrorMessage(CommonStatusCode.NO_CORRELATIONID.getDescription())
			.withContext(CONTEXT_MESSAGE_HEADER_VALIDATION_LOGGING)
			.withPubsubMessage(getPayload())
			.withHeaderAttribute(getMessageHeaders())
			.withLogger(log)
			.buildDisableChecking()
			.logAsError();
			
			throw createProblem(Status.BAD_REQUEST.getStatusCode(), err);
		} 
	}
	
	

}
