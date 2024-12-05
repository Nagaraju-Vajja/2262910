package com.macys.uop.foundation.core.utils.validation;

import static com.macys.uop.foundation.core.utils.Constant.CLIENTID_HDR;
import static com.macys.uop.foundation.core.utils.Constant.MESSAGEID_HDR;
import static com.macys.uop.foundation.core.utils.Constant.ORDERID_HDR;
import static org.mockito.Mockito.mock;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.cloud.gcp.pubsub.support.converter.SimplePubSubMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;

import com.google.pubsub.v1.PubsubMessage;

@RunWith(SpringRunner.class)
public class MessageDuplicationCheckServiceTest {
	
	private final SimplePubSubMessageConverter pubSubMessageConverter = new SimplePubSubMessageConverter();
	
	@Test
	public void testIsMessageDuplicate() {
		
		DefaultMessageDuplicationCheckServiceImpl msgDupCheckService=mock(DefaultMessageDuplicationCheckServiceImpl.class);
		
		Map<String, String> headers = new HashMap<>();
		headers.put("X-B3-SpanId", "3209983fde7abbfe");
		headers.put("X-B3-ParentSpanId", "255db6c7094583d0");
		headers.put("X-B3-Sampled", "1");
		headers.put("X-B3-TraceId", "6002e9aab2393437255db6c7094583d0");
		headers.put(ORDERID_HDR, "11");
		headers.put(MESSAGEID_HDR, "22");
		headers.put(CLIENTID_HDR, "33");
		PubsubMessage message = pubSubMessageConverter.toPubSubMessage("Some Message", headers);
		
		Mockito.doCallRealMethod().when(msgDupCheckService).isMessageDuplicate(message);
		
		try {
			msgDupCheckService.isMessageDuplicate(message);
		} catch(UnsupportedOperationException uoe) {
			Assert.assertEquals(uoe.getMessage(), "Concrete implementation for IMessageDuplicationCheck#isMessageDuplicate not found!");
		}
	}
}
