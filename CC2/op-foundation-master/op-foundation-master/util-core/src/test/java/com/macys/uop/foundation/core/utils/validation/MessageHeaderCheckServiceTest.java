package com.macys.uop.foundation.core.utils.validation;

import static com.macys.uop.foundation.core.utils.Constant.CLIENTID_HDR;
import static com.macys.uop.foundation.core.utils.Constant.HDR_REQUIRED_DEFAULT_LOCATIONTYPE;
import static com.macys.uop.foundation.core.utils.Constant.HDR_REQUIRED_DEFAULT_REASON;
import static com.macys.uop.foundation.core.utils.Constant.MESSAGEID_HDR;
import static com.macys.uop.foundation.core.utils.Constant.ORDERID_HDR;
import static com.macys.uop.foundation.core.utils.Constant.PROBLEM_ERROR_KEY;
import static org.mockito.Mockito.mock;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.gcp.pubsub.support.converter.SimplePubSubMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.zalando.problem.Problem;
import org.zalando.problem.ThrowableProblem;

import com.google.pubsub.v1.PubsubMessage;
import com.macys.uop.foundation.core.utils.CommonStatusCode;
import com.macys.uop.foundation.core.utils.exception.ErrorDetail;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringRunner.class)
@SpringBootTest(classes = { MessageHeaderCheckServiceImpl.class })
public class MessageHeaderCheckServiceTest {
	
	private final SimplePubSubMessageConverter pubSubMessageConverter = new SimplePubSubMessageConverter();
	private static String JSONDATA="{\"isDuplicate\":\"true\"}";
	
	@Test
	public void testValidateMessageHeaders() {
		
		Map<String, String> headers = new HashMap<>();
		headers.put("X-B3-SpanId", "3209983fde7abbfe");
		headers.put("X-B3-ParentSpanId", "255db6c7094583d0");
		headers.put("X-B3-Sampled", "1");
		headers.put("X-B3-TraceId", "6002e9aab2393437255db6c7094583d0");
		headers.put(ORDERID_HDR, "11");
		headers.put(MESSAGEID_HDR, "22");
		headers.put(CLIENTID_HDR, "33");
		PubsubMessage message = pubSubMessageConverter.toPubSubMessage("Some Message", headers);
		
		MessageHeaderCheckServiceImpl messageHeaderCheckService=mock(MessageHeaderCheckServiceImpl.class);
		
		Mockito.doReturn("2222").when(messageHeaderCheckService).getCorrelationId();
		Mockito.doReturn("pubsub:abc").when(messageHeaderCheckService).getUrl();
		
		Mockito.doCallRealMethod().when(messageHeaderCheckService).validateMessageHeaders(message);
		
		messageHeaderCheckService.validateMessageHeaders(message);
	}
	
	@Test
	public void testMessageHeadersValidationFailure() {
		
		com.macys.uop.foundation.core.utils.exception.Error error = com.macys.uop.foundation.core.utils.exception.Error.builder()
				.withCode(CommonStatusCode.NO_CORRELATIONID.getCode())
				.withMessage(CommonStatusCode.NO_CORRELATIONID.getDescription())
				.withErrorDetail(ErrorDetail.builder().withDomain("some-service").withReason(HDR_REQUIRED_DEFAULT_REASON)
						.withMessage(CommonStatusCode.NO_CORRELATIONID.getDescription()).withLocation("some location")
						.withLocationType(HDR_REQUIRED_DEFAULT_LOCATIONTYPE).build())
				.build();
		
		Map<String, String> headers = new HashMap<>();
		headers.put("X-B3-SpanId", "3209983fde7abbfe");
		headers.put("X-B3-ParentSpanId", "255db6c7094583d0");
		headers.put("X-B3-Sampled", "1");
		headers.put("X-B3-TraceId", "6002e9aab2393437255db6c7094583d0");
		headers.put(ORDERID_HDR, "11");
		headers.put(MESSAGEID_HDR, "22");
		headers.put(CLIENTID_HDR, "33");
		PubsubMessage message = pubSubMessageConverter.toPubSubMessage("Some Message", headers);
		
		MessageHeaderCheckServiceImpl messageHeaderCheckService=mock(MessageHeaderCheckServiceImpl.class);
		
		Mockito.doReturn(JSONDATA).when(messageHeaderCheckService).getPayload();
		Mockito.doReturn("12345").when(messageHeaderCheckService).getClientId();
		Mockito.doReturn("2222").when(messageHeaderCheckService).getMessageId();
		Mockito.doReturn("33333").when(messageHeaderCheckService).getOrderId();
		Mockito.doReturn("some-service").when(messageHeaderCheckService).getAppName();
		Mockito.doReturn("pubsub:abc").when(messageHeaderCheckService).getUrl();
		
		Mockito.doReturn(Problem.builder().with(PROBLEM_ERROR_KEY, error).build()).when(messageHeaderCheckService).createProblem(ArgumentMatchers.anyInt(), ArgumentMatchers.any());
		Mockito.doCallRealMethod().when(messageHeaderCheckService).validateMessageHeaders(message);
		
		try {
			messageHeaderCheckService.validateMessageHeaders(message);
		} catch(Exception e) {
			Assert.assertEquals(true, e instanceof ThrowableProblem);
		}	
	}
}
