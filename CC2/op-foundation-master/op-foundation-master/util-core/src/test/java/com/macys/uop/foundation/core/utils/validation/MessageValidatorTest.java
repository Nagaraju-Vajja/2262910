package com.macys.uop.foundation.core.utils.validation;

import static com.macys.uop.foundation.core.utils.Constant.CLIENTID_HDR;
import static com.macys.uop.foundation.core.utils.Constant.MESSAGEID_HDR;
import static com.macys.uop.foundation.core.utils.Constant.ORDERID_HDR;
import static org.mockito.Mockito.mock;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.gcp.pubsub.support.converter.SimplePubSubMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.google.pubsub.v1.PubsubMessage;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringRunner.class)
@SpringBootTest(classes = { MessageValidatorImpl.class })
public class MessageValidatorTest {
	
	@MockBean
	private IMessageDuplicationCheck messageDuplicationService;
	
	@MockBean
	private IMessageHeaderCheck messageHeaderCheckService;
	
	private final SimplePubSubMessageConverter pubSubMessageConverter = new SimplePubSubMessageConverter();
	
	@Test
	public void testValidateMessage() {
		
		Map<String, String> headers = new HashMap<>();
		headers.put("X-B3-SpanId", "3209983fde7abbfe");
		headers.put("X-B3-ParentSpanId", "255db6c7094583d0");
		headers.put("X-B3-Sampled", "1");
		headers.put("X-B3-TraceId", "6002e9aab2393437255db6c7094583d0");
		headers.put(ORDERID_HDR, "11");
		headers.put(MESSAGEID_HDR, "22");
		headers.put(CLIENTID_HDR, "33");
		PubsubMessage message = pubSubMessageConverter.toPubSubMessage("Some Message", headers);
		
		MessageValidatorImpl msgValidatorService=mock(MessageValidatorImpl.class);
		ReflectionTestUtils.setField(msgValidatorService, "isMessageDuplicationCheckEnabled", Boolean.TRUE);
		ReflectionTestUtils.setField(msgValidatorService, "isDefaultMandatoryHeaderCheckingEnabled", Boolean.TRUE);
		
		try {
			Field fldMessageDuplicationService = msgValidatorService.getClass().getSuperclass().getDeclaredField("messageDuplicationService");
			fldMessageDuplicationService.setAccessible(true);
			fldMessageDuplicationService.set(msgValidatorService, messageDuplicationService);

			Field fieldMessageHeaderCheckService = msgValidatorService.getClass().getSuperclass().getDeclaredField("messageHeaderCheckService");
			fieldMessageHeaderCheckService.setAccessible(true);
			fieldMessageHeaderCheckService.set(msgValidatorService, messageHeaderCheckService);
		} catch (Exception e) {
			e.printStackTrace();
		}
		//messageHeaderCheckService
		Mockito.doNothing().when(messageHeaderCheckService).validateMessageHeaders(message);
		Mockito.doReturn(Boolean.TRUE).when(messageDuplicationService).isMessageDuplicate(message);
		Mockito.doCallRealMethod().when(msgValidatorService).validateMessage(message);
		
		Boolean result=msgValidatorService.validateMessage(message);
		
		Assert.assertEquals(false, result);
	}
	
	@Test
	public void testMessageDuplicationCheckEnabledFalse() {
		
		Map<String, String> headers = new HashMap<>();
		headers.put("X-B3-SpanId", "3209983fde7abbfe");
		headers.put("X-B3-ParentSpanId", "255db6c7094583d0");
		headers.put("X-B3-Sampled", "1");
		headers.put("X-B3-TraceId", "6002e9aab2393437255db6c7094583d0");
		headers.put(ORDERID_HDR, "11");
		headers.put(MESSAGEID_HDR, "22");
		headers.put(CLIENTID_HDR, "33");
		PubsubMessage message = pubSubMessageConverter.toPubSubMessage("Some Message", headers);
		
		MessageValidatorImpl msgValidatorService=mock(MessageValidatorImpl.class);
		ReflectionTestUtils.setField(msgValidatorService, "isMessageDuplicationCheckEnabled", Boolean.FALSE);
		ReflectionTestUtils.setField(msgValidatorService, "isDefaultMandatoryHeaderCheckingEnabled", Boolean.TRUE);
		
		try {
			Field fldMessageDuplicationService = msgValidatorService.getClass().getSuperclass().getDeclaredField("messageDuplicationService");
			fldMessageDuplicationService.setAccessible(true);
			fldMessageDuplicationService.set(msgValidatorService, messageDuplicationService);

			Field fieldMessageHeaderCheckService = msgValidatorService.getClass().getSuperclass().getDeclaredField("messageHeaderCheckService");
			fieldMessageHeaderCheckService.setAccessible(true);
			fieldMessageHeaderCheckService.set(msgValidatorService, messageHeaderCheckService);
		} catch (Exception e) {
			e.printStackTrace();
		}
		//messageHeaderCheckService
		Mockito.doNothing().when(messageHeaderCheckService).validateMessageHeaders(message);
		Mockito.doReturn(Boolean.TRUE).when(messageDuplicationService).isMessageDuplicate(message);
		Mockito.doCallRealMethod().when(msgValidatorService).validateMessage(message);
		
		Boolean result=msgValidatorService.validateMessage(message);
		
		Assert.assertEquals(true, result);
	}
	
	@Test
	public void testDefaultMandatoryHeaderCheckingEnabledFalse() {
		
		Map<String, String> headers = new HashMap<>();
		headers.put("X-B3-SpanId", "3209983fde7abbfe");
		headers.put("X-B3-ParentSpanId", "255db6c7094583d0");
		headers.put("X-B3-Sampled", "1");
		headers.put("X-B3-TraceId", "6002e9aab2393437255db6c7094583d0");
		headers.put(ORDERID_HDR, "11");
		headers.put(MESSAGEID_HDR, "22");
		headers.put(CLIENTID_HDR, "33");
		PubsubMessage message = pubSubMessageConverter.toPubSubMessage("Some Message", headers);
		
		MessageValidatorImpl msgValidatorService=mock(MessageValidatorImpl.class);
		ReflectionTestUtils.setField(msgValidatorService, "isMessageDuplicationCheckEnabled", Boolean.TRUE);
		ReflectionTestUtils.setField(msgValidatorService, "isDefaultMandatoryHeaderCheckingEnabled", Boolean.FALSE);
		
		try {
			Field fldMessageDuplicationService = msgValidatorService.getClass().getSuperclass().getDeclaredField("messageDuplicationService");
			fldMessageDuplicationService.setAccessible(true);
			fldMessageDuplicationService.set(msgValidatorService, messageDuplicationService);

			Field fieldMessageHeaderCheckService = msgValidatorService.getClass().getSuperclass().getDeclaredField("messageHeaderCheckService");
			fieldMessageHeaderCheckService.setAccessible(true);
			fieldMessageHeaderCheckService.set(msgValidatorService, messageHeaderCheckService);
		} catch (Exception e) {
			e.printStackTrace();
		}
		//messageHeaderCheckService
		Mockito.doNothing().when(messageHeaderCheckService).validateMessageHeaders(message);
		Mockito.doReturn(Boolean.TRUE).when(messageDuplicationService).isMessageDuplicate(message);
		Mockito.doCallRealMethod().when(msgValidatorService).validateMessage(message);
		
		Boolean result=msgValidatorService.validateMessage(message);
		
		Assert.assertEquals(false, result);
	}
	
	@Test
	public void testMessageDuplicateFalse() {
		
		Map<String, String> headers = new HashMap<>();
		headers.put("X-B3-SpanId", "3209983fde7abbfe");
		headers.put("X-B3-ParentSpanId", "255db6c7094583d0");
		headers.put("X-B3-Sampled", "1");
		headers.put("X-B3-TraceId", "6002e9aab2393437255db6c7094583d0");
		headers.put(ORDERID_HDR, "11");
		headers.put(MESSAGEID_HDR, "22");
		headers.put(CLIENTID_HDR, "33");
		PubsubMessage message = pubSubMessageConverter.toPubSubMessage("Some Message", headers);
		
		MessageValidatorImpl msgValidatorService=mock(MessageValidatorImpl.class);
		ReflectionTestUtils.setField(msgValidatorService, "isMessageDuplicationCheckEnabled", Boolean.TRUE);
		ReflectionTestUtils.setField(msgValidatorService, "isDefaultMandatoryHeaderCheckingEnabled", Boolean.TRUE);
		
		try {
			Field fldMessageDuplicationService = msgValidatorService.getClass().getSuperclass().getDeclaredField("messageDuplicationService");
			fldMessageDuplicationService.setAccessible(true);
			fldMessageDuplicationService.set(msgValidatorService, messageDuplicationService);

			Field fieldMessageHeaderCheckService = msgValidatorService.getClass().getSuperclass().getDeclaredField("messageHeaderCheckService");
			fieldMessageHeaderCheckService.setAccessible(true);
			fieldMessageHeaderCheckService.set(msgValidatorService, messageHeaderCheckService);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Mockito.doNothing().when(messageHeaderCheckService).validateMessageHeaders(message);
		Mockito.doReturn(Boolean.FALSE).when(messageDuplicationService).isMessageDuplicate(message);
		Mockito.doCallRealMethod().when(msgValidatorService).validateMessage(message);
		
		Boolean result=msgValidatorService.validateMessage(message);
		
		Assert.assertEquals(true, result);
	}

}
