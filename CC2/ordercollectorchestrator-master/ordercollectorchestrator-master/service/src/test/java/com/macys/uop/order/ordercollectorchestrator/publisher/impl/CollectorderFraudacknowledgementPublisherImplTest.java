package com.macys.uop.order.ordercollectorchestrator.publisher.impl;

import static com.macys.uop.foundation.core.utils.Constant.CLIENTID_HDR;
import static com.macys.uop.foundation.core.utils.Constant.CORRELATIONID_HDR;
import static com.macys.uop.foundation.core.utils.Constant.MESSAGEID_HDR;
import static com.macys.uop.foundation.core.utils.Constant.ORDERID_HDR;
import static com.macys.uop.order.ordercollectorchestrator.utils.OrdercollectorchestratorConstants.EVENT_SOURCE;
import static com.macys.uop.order.ordercollectorchestrator.utils.OrdercollectorchestratorConstants.EVENT_TIME_STAMP;
import static com.macys.uop.order.ordercollectorchestrator.utils.OrdercollectorchestratorConstants.EVENT_TYPE;
import static com.macys.uop.order.ordercollectorchestrator.utils.OrdercollectorchestratorConstants.EVENT_TYPE_ORDER_ON_SUCCESS;
import static org.junit.Assert.assertEquals;

import com.macys.uop.foundation.core.utils.Constant;
import com.macys.uop.foundation.core.utils.execution.ServiceContextUtil;
import com.macys.uop.foundation.core.utils.msg.publisher.MessagePublisherClient;
import com.macys.uop.foundation.core.utils.test.TestContextUtil;
import com.macys.uop.order.model.Order;
import com.macys.uop.order.ordercollectorchestrator.publisher.ICollectorderFraudacknowledgementPublisher;
import com.macys.uop.order.ordercollectorchestrator.utils.OrdercollectorchestratorUtil;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.Message;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CollectorderFraudacknowledgementPublisherImpl.class)
public class CollectorderFraudacknowledgementPublisherImplTest
		implements TestContextUtil, ServiceContextUtil, OrdercollectorchestratorUtil {

	@Autowired
	private ICollectorderFraudacknowledgementPublisher fraudack;

	@MockBean
	private MessagePublisherClient publisherClient;

	@Before
	public void beforeTest() {
		initContext();
		getServiceRequestContext().setApplicationName("ordercollectorchestrator");
		getServiceRequestContext().setHeaders(getHeaders());
	}

	@After
	public void afterTest() {
		clearContext();
	}

	@Test
	public void publishTest() throws ExecutionException, InterruptedException {

		Order order = new Order();
		order.setOrderId("123");
		Mockito.when(publisherClient.sendMessage(Mockito.anyString(), Mockito.any(Message.class))).thenReturn("OK");
		String result = fraudack.publishMessage("MessageString", getDefaultMessageHeaders());
		assertEquals("OK", result);
	}

	private HttpHeaders getHeaders() {
		HttpHeaders headers=new HttpHeaders();
		headers.add(Constant.MESSAGEID_HDR, "1");
		headers.add(Constant.ORDERID_HDR, "2");
		headers.add(Constant.CLIENTID_HDR, "3");
		headers.add(Constant.CORRELATIONID_HDR, "4");
		return headers;
	}
}
