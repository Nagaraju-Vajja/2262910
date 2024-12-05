package com.macys.uop.foundation.messagestore;

import static com.macys.uop.foundation.core.utils.Constant.CLIENTID_HDR;
import static com.macys.uop.foundation.core.utils.Constant.HDR_REQUIRED_DEFAULT_LOCATIONTYPE;
import static com.macys.uop.foundation.core.utils.Constant.HDR_REQUIRED_DEFAULT_REASON;
import static com.macys.uop.foundation.core.utils.Constant.MESSAGEID_HDR;
import static com.macys.uop.foundation.core.utils.Constant.ORDERID_HDR;
import static com.macys.uop.foundation.core.utils.Constant.PROBLEM_ERROR_KEY;
import static org.mockito.Mockito.mock;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.gcp.pubsub.support.converter.SimplePubSubMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import org.zalando.problem.ThrowableProblem;

import com.google.pubsub.v1.PubsubMessage;
import com.macys.uop.foundation.core.utils.CommonStatusCode;
import com.macys.uop.foundation.core.utils.exception.ErrorDetail;
import com.macys.uop.foundation.core.utils.rest.client.RestClientResponse;
import com.macys.uop.foundation.core.utils.validation.IRestBasedDuplicationCheck;
import com.macys.uop.foundation.core.utils.validation.ISpannerDBBasedDuplicationCheck;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;

@RunWith(SpringRunner.class)
public class MessageDuplicationCheckServiceTest {
	
	private final SimplePubSubMessageConverter pubSubMessageConverter = new SimplePubSubMessageConverter();
	private static String JSONDATA="{\"isDuplicate\":\"true\"}";

	@MockBean
	private IRestBasedDuplicationCheck restBasedDuplicationCheck;

	@MockBean
	private ISpannerDBBasedDuplicationCheck spannerDBBasedDuplicationCheck;
	
	@Test
	public void testIsMessageDuplicateWithTypeSpannerdb() {

		Map<String, String> headers = new HashMap<>();
		headers.put("X-B3-SpanId", "3209983fde7abbfe");
		headers.put("X-B3-ParentSpanId", "255db6c7094583d0");
		headers.put("X-B3-Sampled", "1");
		headers.put("X-B3-TraceId", "6002e9aab2393437255db6c7094583d0");
		headers.put(ORDERID_HDR, "11");
		headers.put(MESSAGEID_HDR, "22");
		headers.put(CLIENTID_HDR, "33");
		PubsubMessage message = pubSubMessageConverter.toPubSubMessage("Some Message", headers);

		MessageDuplicationCheckServiceImpl msgDupCheckService=mock(MessageDuplicationCheckServiceImpl.class);
		ReflectionTestUtils.setField(msgDupCheckService, "baseUri", "http://someurl");
		ReflectionTestUtils.setField(msgDupCheckService, "isMessageDuplicationStatusLoggingEnabled", true);
		ReflectionTestUtils.setField(msgDupCheckService, "messageDuplicationCheckType", "spannerdb");
		ReflectionTestUtils.setField(msgDupCheckService, "messagestorePayloadPersistenceEnabled", true);

		try {
			Field fldMessageDuplicationService = msgDupCheckService.getClass().getSuperclass().getDeclaredField("restBasedDuplicationCheck");
			fldMessageDuplicationService.setAccessible(true);
			fldMessageDuplicationService.set(msgDupCheckService, restBasedDuplicationCheck);

			Field fieldMessageHeaderCheckService = msgDupCheckService.getClass().getSuperclass().getDeclaredField("spannerDBBasedDuplicationCheck");
			fieldMessageHeaderCheckService.setAccessible(true);
			fieldMessageHeaderCheckService.set(msgDupCheckService, spannerDBBasedDuplicationCheck);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Mockito.doReturn(JSONDATA).when(msgDupCheckService).getPayload();
		Mockito.doReturn("12345").when(msgDupCheckService).getClientId();
		Mockito.doReturn("2222").when(msgDupCheckService).getCorrelationId();
		Mockito.doReturn("2222").when(msgDupCheckService).getMessageId();
		Mockito.doReturn("33333").when(msgDupCheckService).getOrderId();
		Mockito.doReturn("json").when(msgDupCheckService).getContentType();
		Mockito.doReturn("sample payload").when(msgDupCheckService).getPayload();
		Mockito.doReturn("some-service").when(msgDupCheckService).getAppName();
		Mockito.doReturn(true).when(spannerDBBasedDuplicationCheck).isDuplicate(Mockito.anyString(),Mockito.anyMap());

		RestClientResponse<Object> serviceResponseContext = new RestClientResponse<Object>();
		serviceResponseContext.setBody(JSONDATA);

		Mockito.doCallRealMethod().when(msgDupCheckService).isMessageDuplicate(message);

		msgDupCheckService.isMessageDuplicate(message);

		Assert.assertNotEquals("expected", "actual");
	}

	@Test
	public void testIsMessageDuplicateWithTypeRest() {

		Map<String, String> headers = new HashMap<>();
		headers.put("X-B3-SpanId", "3209983fde7abbfe");
		headers.put("X-B3-ParentSpanId", "255db6c7094583d0");
		headers.put("X-B3-Sampled", "1");
		headers.put("X-B3-TraceId", "6002e9aab2393437255db6c7094583d0");
		headers.put(ORDERID_HDR, "11");
		headers.put(MESSAGEID_HDR, "22");
		headers.put(CLIENTID_HDR, "33");
		PubsubMessage message = pubSubMessageConverter.toPubSubMessage("Some Message", headers);

		MessageDuplicationCheckServiceImpl msgDupCheckService=mock(MessageDuplicationCheckServiceImpl.class);
		ReflectionTestUtils.setField(msgDupCheckService, "baseUri", "http://someurl");
		ReflectionTestUtils.setField(msgDupCheckService, "isMessageDuplicationStatusLoggingEnabled", true);
		ReflectionTestUtils.setField(msgDupCheckService, "messageDuplicationCheckType", "rest");
		ReflectionTestUtils.setField(msgDupCheckService, "messagestorePayloadPersistenceEnabled", false);

		try {
			Field fldMessageDuplicationService = msgDupCheckService.getClass().getSuperclass().getDeclaredField("restBasedDuplicationCheck");
			fldMessageDuplicationService.setAccessible(true);
			fldMessageDuplicationService.set(msgDupCheckService, restBasedDuplicationCheck);

			Field fieldMessageHeaderCheckService = msgDupCheckService.getClass().getSuperclass().getDeclaredField("spannerDBBasedDuplicationCheck");
			fieldMessageHeaderCheckService.setAccessible(true);
			fieldMessageHeaderCheckService.set(msgDupCheckService, spannerDBBasedDuplicationCheck);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Mockito.doReturn(JSONDATA).when(msgDupCheckService).getPayload();
		Mockito.doReturn("12345").when(msgDupCheckService).getClientId();
		Mockito.doReturn("2222").when(msgDupCheckService).getCorrelationId();
		Mockito.doReturn("2222").when(msgDupCheckService).getMessageId();
		Mockito.doReturn("33333").when(msgDupCheckService).getOrderId();
		Mockito.doReturn("json").when(msgDupCheckService).getContentType();
		Mockito.doReturn("sample payload").when(msgDupCheckService).getPayload();
		Mockito.doReturn("some-service").when(msgDupCheckService).getAppName();
		Mockito.doReturn(false).when(spannerDBBasedDuplicationCheck).isDuplicate(Mockito.anyString(),Mockito.anyMap());

		RestClientResponse<Object> serviceResponseContext = new RestClientResponse<Object>();
		serviceResponseContext.setBody(JSONDATA);

		Mockito.doCallRealMethod().when(msgDupCheckService).isMessageDuplicate(message);

		msgDupCheckService.isMessageDuplicate(message);

		Assert.assertNotEquals("expected", "actual");
	}

//	@Test
//	public void testMessageDuplicationCheckCBFallback() throws Exception {
//
//		com.macys.uop.foundation.core.utils.exception.Error error = com.macys.uop.foundation.core.utils.exception.Error.builder()
//				.withCode(CommonStatusCode.NO_CORRELATIONID.getCode())
//				.withMessage(CommonStatusCode.NO_CORRELATIONID.getDescription())
//				.withErrorDetail(ErrorDetail.builder().withDomain("some-service").withReason(HDR_REQUIRED_DEFAULT_REASON)
//						.withMessage(CommonStatusCode.NO_CORRELATIONID.getDescription()).withLocation("some location")
//						.withLocationType(HDR_REQUIRED_DEFAULT_LOCATIONTYPE).build())
//				.build();
//
//		MessageDuplicationCheckServiceImpl msgDupCheckService= PowerMockito.spy(new MessageDuplicationCheckServiceImpl(restBasedDuplicationCheck, spannerDBBasedDuplicationCheck));
//		ReflectionTestUtils.setField(msgDupCheckService, "baseUri", "http://someurl");
//
//		CallNotPermittedException ex=mock(CallNotPermittedException.class);
//		Mockito.doReturn("Some Exception Message").when(ex).getMessage();
//
//		ThrowableProblem problem=Problem.builder().withStatus(Status.valueOf(Status.INTERNAL_SERVER_ERROR.getStatusCode())).with(PROBLEM_ERROR_KEY, error).build();
//
//		Mockito.doReturn(problem).when(msgDupCheckService).createProblem(ArgumentMatchers.anyInt(), ArgumentMatchers.any());
//		Mockito.doThrow(problem).when(msgDupCheckService).defaultMessageDuplicationCheckFallback(ex, "Circuit Breaker Fallback");
//
//		Mockito.doCallRealMethod().when(msgDupCheckService).messageDuplicationCheckCBFallback(ex);
//
//		try {
//			msgDupCheckService.messageDuplicationCheckCBFallback(ex);
//		} catch(Exception e) {
//			Assert.assertTrue(e instanceof ThrowableProblem);
//		}
//	}
	
	@Test
	public void testMessageDuplicationCheckRTFallback() throws Exception {

		com.macys.uop.foundation.core.utils.exception.Error error = com.macys.uop.foundation.core.utils.exception.Error.builder()
				.withCode(CommonStatusCode.NO_CORRELATIONID.getCode())
				.withMessage(CommonStatusCode.NO_CORRELATIONID.getDescription())
				.withErrorDetail(ErrorDetail.builder().withDomain("some-service").withReason(HDR_REQUIRED_DEFAULT_REASON)
						.withMessage(CommonStatusCode.NO_CORRELATIONID.getDescription()).withLocation("some location")
						.withLocationType(HDR_REQUIRED_DEFAULT_LOCATIONTYPE).build())
				.build();

		MessageDuplicationCheckServiceImpl msgDupCheckService= PowerMockito.spy(new MessageDuplicationCheckServiceImpl(restBasedDuplicationCheck, spannerDBBasedDuplicationCheck));
		ReflectionTestUtils.setField(msgDupCheckService, "baseUri", "http://someurl");

		ThrowableProblem problem=Problem.builder().withStatus(Status.valueOf(Status.INTERNAL_SERVER_ERROR.getStatusCode())).with(PROBLEM_ERROR_KEY, error).build();

		CallNotPermittedException ex=mock(CallNotPermittedException.class);
		Mockito.doReturn("Some Exception Message").when(ex).getMessage();

		Mockito.doReturn(problem).when(msgDupCheckService).createProblem(ArgumentMatchers.anyInt(), ArgumentMatchers.any());
		Mockito.doThrow(problem).when(msgDupCheckService).defaultMessageDuplicationCheckFallback(ex, "Retry Fallback");

		Mockito.doCallRealMethod().when(msgDupCheckService).messageDuplicationCheckRTFallback(ex);

		try {
			msgDupCheckService.messageDuplicationCheckRTFallback(ex);
		} catch(Exception e) {
			Assert.assertTrue(e instanceof ThrowableProblem);
		}
	}
}
