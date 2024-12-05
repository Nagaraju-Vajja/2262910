package com.macys.uop.foundation.messagestore;

import static com.macys.uop.foundation.core.utils.Constant.CLIENTID_HDR;
import static com.macys.uop.foundation.core.utils.Constant.CORRELATIONID_HDR;
import static com.macys.uop.foundation.core.utils.Constant.MESSAGEID_HDR;
import static com.macys.uop.foundation.core.utils.Constant.MSG_DUP_CHK_TYPE_REST;
import static com.macys.uop.foundation.core.utils.Constant.MSG_DUP_CHK_TYPE_SPANNERDB;
import static com.macys.uop.foundation.core.utils.Constant.ORDERID_HDR;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.zalando.problem.ThrowableProblem;

import com.google.pubsub.v1.PubsubMessage;
import com.macys.uop.foundation.core.utils.logging.LogMessageBuilder;
import com.macys.uop.foundation.core.utils.logging.LogTypeEnum;
import com.macys.uop.foundation.core.utils.masking.IDataMasker;
import com.macys.uop.foundation.core.utils.masking.MaskingUtil;
import com.macys.uop.foundation.core.utils.validation.IMessageDuplicationCheck;
import com.macys.uop.foundation.core.utils.validation.IRestBasedDuplicationCheck;
import com.macys.uop.foundation.core.utils.validation.ISpannerDBBasedDuplicationCheck;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.annotation.Recover;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;

/**
 * This is the concrete implementation provided by foundation to check message duplication.
 *
 * @see IRestBasedDuplicationCheck
 * @see ISpannerDBBasedDuplicationCheck 
 */
@Service
@Primary
@RequiredArgsConstructor
public class MessageDuplicationCheckServiceImpl extends AbstractMessageDuplicationCheckService implements IMessageDuplicationCheck, MaskingUtil {
	
	@Value("${message.duplicationcheck.service.base_uri:#{null}}")
	private String baseUri;

	@Value("${message.duplication.status.logging.enabled:true}")
	private boolean isMessageDuplicationStatusLoggingEnabled;
	
	@Value("${message.duplication.check.type:spannerdb}")
	private String messageDuplicationCheckType;
	
	@Value("${messagestore.payload.persistence.enabled:false}")
	private boolean messagestorePayloadPersistenceEnabled;

	private final IRestBasedDuplicationCheck restBasedDuplicationCheck;
	private final ISpannerDBBasedDuplicationCheck spannerDBBasedDuplicationCheck;
	
	private static final String SEPARATOR=" , ";
	
	/**
	 * Default message duplication check implementation provided by foundation.
	 * 
	 * <ul>
	 * <li>This method is wrapped by resilience4j Retry and CircuitBreaker annotations.</li>
	 * <li>Depending upon the flag value of {@link MessageDuplicationCheckServiceImpl#messageDuplicationCheckType},<br>
	 * when 'rest' {@link IRestBasedDuplicationCheck#isDuplicate(String, String, Map)} implementation is called, <br>
	 * when 'spannerdb' {@link ISpannerDBBasedDuplicationCheck#isDuplicate(String, Map)} implementation is called
	 * </li>
	 * <li>if {@link MessageDuplicationCheckServiceImpl#isMessageDuplicationStatusLoggingEnabled} is set to true,<br>
	 * log message get printed with duplicate status check result along with invocation start and end time
	 * </li>
	 * <li>if {@link MessageDuplicationCheckServiceImpl#messagestorePayloadPersistenceEnabled} is set to true,<br>
	 * payload gets masked and sent to implementation for persistence. By default this value is false.
	 * </li>
	 * <li>In case of any exception fallback methods related to Retry and CircuitBreaker kicks in.</li>
	 * <li>If all attempts are exhausted then {@link ThrowableProblem} is created and thrown </li>
	 * </ul>
	 * 
	 */
	@Override
	@Retryable(value = Exception.class)
//	@CircuitBreaker(name = "messageDuplicationCheck-cb", fallbackMethod = "messageDuplicationCheckCBFallback")
	public boolean isMessageDuplicate(PubsubMessage message) {
		
		Boolean isDuplicate=false;
		String payload=null;
		if(messagestorePayloadPersistenceEnabled) {
			IDataMasker dataMasker=getMaskerInstance(getContentType());
			if(dataMasker!=null) {
				payload=dataMasker.maskData(getPayload());
			} else {
				payload=getPayload();
			}
		}
		
		Map<String,String> headers=new HashMap<>();
		headers.put(CLIENTID_HDR, getClientId());
		headers.put(CORRELATIONID_HDR, getCorrelationId());
		headers.put(MESSAGEID_HDR, message.getMessageId());
		headers.put(ORDERID_HDR, getOrderId());
		
		Instant invocationStartTime=Instant.now();

		if(messageDuplicationCheckType.contentEquals(MSG_DUP_CHK_TYPE_REST)) {
			isDuplicate=restBasedDuplicationCheck.isDuplicate(baseUri, payload, headers);
		} else if(messageDuplicationCheckType.contentEquals(MSG_DUP_CHK_TYPE_SPANNERDB)) {
			isDuplicate=spannerDBBasedDuplicationCheck.isDuplicate(payload, headers);
		}
		
		Instant invocationEndTime=Instant.now();
		long invocationDuration= Duration.between(invocationStartTime, invocationEndTime).toMillis();
		
		// Log status of duplicate check
		if(isMessageDuplicationStatusLoggingEnabled) {
			StringBuilder additionalInfoBuilder=new StringBuilder()
					.append("Message Duplication Check Status isDuplicate :").append(isDuplicate).append(SEPARATOR)
					.append("Invocation Start Time :").append(invocationStartTime.toString()).append(SEPARATOR)
					.append("Invocation End Time :").append(invocationEndTime.toString()).append(SEPARATOR)
					.append("Duration In Millis :"+ invocationDuration);
			
			new LogMessageBuilder()
					.withClientId(getClientId())
					.withMessageId(getMessageId())
					.withOrderId(getOrderId())
					.withCorrelationId(getCorrelationId())
					.withAppName(getAppName())
					.withCallerId(getCallerId())
					.withContext(CONTEXT_MESSAGE_DUPLICATION_CHECK)
					.withAdditionalInfo(additionalInfoBuilder.toString())
					.withLogType(LogTypeEnum.LOG)
					.build()
					.logAsInfo();
		}
		
		return isDuplicate;
	}
	
	/**
	 * Fallback method related to Circuit Breaker annotation defined at {@link this#isMessageDuplicate(PubsubMessage)}
	 * 
	 * @param e Throwable thrown from Circuit Breaker annotated method
	 * 
	 * @return throw exception
	 */
//	public boolean messageDuplicationCheckCBFallback(CallNotPermittedException e) {
//		throw defaultMessageDuplicationCheckFallback(e, "Circuit Breaker Fallback");
//	}
	
	/**
	 * Fallback method related to Retry annotation defined at {@link this#isMessageDuplicate(PubsubMessage)}
	 * 
	 * @param e Throwable thrown from Retry annotated method
	 * 
	 * @return throw exception
	 */
	@Recover
	public boolean messageDuplicationCheckRTFallback(Throwable e) {
		throw defaultMessageDuplicationCheckFallback(e, "Retry Fallback");
	}
}
