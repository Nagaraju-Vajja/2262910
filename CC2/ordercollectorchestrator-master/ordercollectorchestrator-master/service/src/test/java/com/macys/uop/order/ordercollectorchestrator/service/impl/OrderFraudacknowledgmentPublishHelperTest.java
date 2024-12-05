package com.macys.uop.order.ordercollectorchestrator.service.impl;

import static com.macys.uop.foundation.core.utils.Constant.CLIENTID_HDR;
import static com.macys.uop.foundation.core.utils.Constant.CORRELATIONID_HDR;
import static com.macys.uop.foundation.core.utils.Constant.MESSAGEID_HDR;
import static com.macys.uop.foundation.core.utils.Constant.ORDERID_HDR;
import static com.macys.uop.order.ordercollectorchestrator.utils.OrdercollectorchestratorConstants.EVENT_SOURCE;
import static com.macys.uop.order.ordercollectorchestrator.utils.OrdercollectorchestratorConstants.EVENT_TIME_STAMP;
import static com.macys.uop.order.ordercollectorchestrator.utils.OrdercollectorchestratorConstants.EVENT_TYPE;
import static com.macys.uop.order.ordercollectorchestrator.utils.OrdercollectorchestratorConstants.EVENT_TYPE_ORDER_ON_SUCCESS;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

import com.macys.uop.foundation.core.utils.Constant;
import com.macys.uop.foundation.core.utils.execution.ServiceContextUtil;
import com.macys.uop.foundation.core.utils.json.JsonUtils;
import com.macys.uop.foundation.core.utils.test.TestContextUtil;
import com.macys.uop.fraudprocessor.model.FraudServiceResponse;
import com.macys.uop.order.model.Order;
import com.macys.uop.order.model.OrderLine;
import com.macys.uop.order.ordercollectorchestrator.model.ErrorProcessorRequest;
import com.macys.uop.order.ordercollectorchestrator.publisher.ICollectorderFraudacknowledgementPublisher;
import com.macys.uop.order.ordercollectorchestrator.service.IErrorprocessorService;
import com.macys.uop.order.ordercollectorchestrator.service.IOrderFraudacknowledgmentPublishHelper;
import com.macys.uop.order.ordercollectorchestrator.utils.OrdercollectorchestratorUtil;
import com.macys.uop.order.ordercollectorchestrator.utils.TestUtils;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = OrderFraudacknowledgmentPublishHelper.class)
public class OrderFraudacknowledgmentPublishHelperTest implements TestContextUtil, ServiceContextUtil, OrdercollectorchestratorUtil {

	@Autowired
	private IOrderFraudacknowledgmentPublishHelper orderFraudPublishHelper;

	@MockBean
	private ICollectorderFraudacknowledgementPublisher publisher;

	@MockBean
	private IErrorprocessorService errorprocessorService;;

	@MockBean
	private JsonUtils jsonUtils;

	@Before
	public void beforeTest() {
		initContext();
		HttpHeaders headers=new HttpHeaders();
		headers.add(Constant.MESSAGEID_HDR, "1");
		headers.add(Constant.ORDERID_HDR, "2");
		headers.add(Constant.CLIENTID_HDR, "3");
		headers.add(Constant.CORRELATIONID_HDR, "4");
		getServiceRequestContext().setApplicationName("ordercollectorchestrator");
		getServiceRequestContext().setHeaders(headers);
	}

	@After
	public void afterTest() {
		clearContext();
	}

	private Map<String, String> getMessageHeader() {
		Map<String, String> messageHeaders = new HashMap<>();
		messageHeaders.put(EVENT_SOURCE, "appName");
		messageHeaders.put(EVENT_TYPE, EVENT_TYPE_ORDER_ON_SUCCESS);
		messageHeaders.put(ORDERID_HDR, "orderId");
		messageHeaders.put(CORRELATIONID_HDR, "34324");
		messageHeaders.put(EVENT_TIME_STAMP, "2021-05-20 13:00:9090");
		messageHeaders.put(CLIENTID_HDR, "clientId");
		messageHeaders.put(MESSAGEID_HDR, "346536");
		return messageHeaders;
	}

	@Test
	public void publisherTest() throws ExecutionException, InterruptedException {
		Order orderObject = new Order();
		orderObject.setOrderId("2HSGYDK789");
		orderObject.setSourceChannel("MCOM");
		OrderLine orderLine = new OrderLine();
		orderLine.setFulfillmentType("EMAIL");
		OrderLine orderLine1 = new OrderLine();
		orderLine.setFulfillmentType("FACS");
		orderObject.setOrderLines(Arrays.asList(orderLine1, orderLine));
		FraudServiceResponse fraudServiceResponse = new FraudServiceResponse();
		fraudServiceResponse.setOrderId("TEST");
		Mockito.when(publisher.publishMessage(Mockito.any(), Mockito.anyMap())).thenReturn("OK");
		String result = orderFraudPublishHelper.publish(orderObject, getMessageHeader());
		assertEquals("OK", result);
		}

	@Test
	public void publisherExceptionTest() throws ExecutionException, InterruptedException {
		FraudServiceResponse fraudServiceResponse = new FraudServiceResponse();
		fraudServiceResponse.setOrderId("TEST");
		Mockito.when(publisher.publishMessage(Mockito.any(), Mockito.any())).thenThrow(new InterruptedException("TEST"));
		ArgumentCaptor<ErrorProcessorRequest> errorRequest = ArgumentCaptor.forClass(ErrorProcessorRequest.class);
		orderFraudPublishHelper.publish(TestUtils.getOrderReqest(), getMessageHeader());
		verify(errorprocessorService, Mockito.times(1)).retryLater(errorRequest.capture());
	}
}

