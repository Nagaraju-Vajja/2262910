package com.macys.uop.order.ordercollectorchestrator.service.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

import com.macys.uop.common.eventlog.model.EventLogRequest;
import com.macys.uop.foundation.core.utils.Constant;
import com.macys.uop.foundation.core.utils.execution.ServiceContextUtil;
import com.macys.uop.foundation.core.utils.json.JsonUtils;
import com.macys.uop.foundation.core.utils.test.TestContextUtil;
import com.macys.uop.order.model.Order;
import com.macys.uop.order.ordercollectorchestrator.model.ErrorProcessorRequest;
import com.macys.uop.order.ordercollectorchestrator.publisher.IEventOnsuccessPublisher;
import com.macys.uop.order.ordercollectorchestrator.service.IErrorprocessorService;
import com.macys.uop.order.ordercollectorchestrator.service.IEventLogService;
import com.macys.uop.order.ordercollectorchestrator.utils.OrderTransaction;

import java.util.ArrayList;
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
@SpringBootTest(classes = EventLogServiceImpl.class)
public class EventlogServiceImplTest implements TestContextUtil, ServiceContextUtil {

	@Autowired
	private IEventLogService eventLogService;

	@MockBean
	private IEventOnsuccessPublisher eventLogPublisher;

	@MockBean
	private IErrorprocessorService errorprocessorService;

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

	@Test
	public void testcreateEventLogForEvent() throws Exception {
		Order order = new Order();
		order.setOrderId("12");
		order.setOrderLines(new ArrayList<>());
		EventLogRequest eventLogRequest = new EventLogRequest();
		eventLogRequest.setTransactionTime("10:22");
		eventLogRequest.setChannelName("DCR_URL");
		eventLogRequest.setChannelType("INBOUND");
		eventLogRequest.setRequestPayload("Request_Payload");
		Mockito.when(jsonUtils.convertToJson(Mockito.any())).thenReturn("String");
		Mockito.when(eventLogPublisher.publishMessage(Mockito.any(), Mockito.any())).thenReturn("OK");
		String response = eventLogService
			.createEventLog(order, "channel", "OUTBOUND", OrderTransaction.CREATED, "orderId", "1",
				getMessageHeaders(),"SITE");
		assertEquals("OK", response);
	}

	@Test
	public void testcreateEventLogForEventNegative1() throws Exception {
		Order order = new Order();
		order.setOrderId("12");
		order.setOrderLines(new ArrayList<>());
		EventLogRequest eventLogRequest = new EventLogRequest();
		eventLogRequest.setTransactionTime("10:22");
		eventLogRequest.setChannelName("DCR_URL");
		eventLogRequest.setChannelType("INBOUND");
		eventLogRequest.setRequestPayload("Request_Payload");
		Mockito.when(jsonUtils.convertToJson(Mockito.any())).thenReturn("String");
		Mockito.when(eventLogPublisher.publishMessage(Mockito.any(), Mockito.any()))
			.thenThrow(new InterruptedException("TEST"));
		ArgumentCaptor<ErrorProcessorRequest> errorRequest = ArgumentCaptor.forClass(ErrorProcessorRequest.class);
		eventLogService
				.createEventLog(order,
					"channel", "OUTBOUND", OrderTransaction.CREATED, "orderId",
					"124",
					getMessageHeaders(),"SITE");
		verify(errorprocessorService, Mockito.times(1)).retryLater(errorRequest.capture());

	}

	@Test
	public void testcreateEventLogForEvent1() throws Exception {
		Order order = new Order();
		order.setOrderId("12");
		order.setOrderLines(new ArrayList<>());
		EventLogRequest eventLogRequest = new EventLogRequest();
		eventLogRequest.setTransactionTime("10:22");
		eventLogRequest.setChannelName("DCR_URL");
		eventLogRequest.setChannelType("INBOUND");
		eventLogRequest.setRequestPayload("Request_Payload");
		Mockito.when(jsonUtils.convertToJson(Mockito.any())).thenReturn("String");
		Mockito.when(eventLogPublisher.publishMessage(Mockito.any(), Mockito.any()))
			.thenThrow(new InterruptedException("Interrupted"));
		ArgumentCaptor<ErrorProcessorRequest> errorRequest = ArgumentCaptor.forClass(ErrorProcessorRequest.class);
		eventLogService
			.createEventLog(order, "channel", "OUTBOUND", OrderTransaction.ORDER_REQ,
				"orderId", "1",
				getMessageHeaders(),"SITE");
		verify(errorprocessorService, Mockito.atLeastOnce()).retryLater(errorRequest.capture());
	}

	@Test
	public void testcreateEventLogForEventNegative2() throws Exception {
		Order order = new Order();
		order.setOrderId("12");
		order.setOrderLines(new ArrayList<>());
		EventLogRequest eventLogRequest = new EventLogRequest();
		eventLogRequest.setTransactionTime("10:22");
		eventLogRequest.setChannelName("DCR_URL");
		eventLogRequest.setChannelType("INBOUND");
		eventLogRequest.setRequestPayload("Request_Payload");
		Mockito.when(jsonUtils.convertToJson(Mockito.any())).thenReturn("String");
		Mockito.when(eventLogPublisher.publishMessage(Mockito.any(), Mockito.any()))
			.thenThrow(new InterruptedException("Interrupted"));
		ArgumentCaptor<ErrorProcessorRequest> reqArgument = ArgumentCaptor.forClass(ErrorProcessorRequest.class);
		eventLogService
				.createEventLog(order,
					"channel", "OUTBOUND", OrderTransaction.CREATED, "orderId",
					"124",
					getMessageHeaders(),"SITE");

		verify(errorprocessorService, Mockito.atLeastOnce()).retryLater(reqArgument.capture());
	}

	@Test public void testLogEventOnPriceCheckForEvent() throws Exception {
		Order order = new Order();
		order.setOrderId("12");
		order.setOrderLines(new ArrayList<>());
		EventLogRequest eventLogRequest = new EventLogRequest();
		eventLogRequest.setTransactionTime("10:22");
		eventLogRequest.setChannelName("DCR_URL");
		eventLogRequest.setChannelType("INBOUND");
		eventLogRequest.setRequestPayload("Request_Payload");
		Mockito.when(jsonUtils.convertToJson(Mockito.any())).thenReturn("String");
		Mockito.when(eventLogPublisher.publishMessage(Mockito.any(), Mockito.any())).thenReturn("OK");
		String response = eventLogService
			.createEventLog(order,
				"channel", "OUTBOUND", OrderTransaction.CREATED, "orderId",
				"124",
				getMessageHeaders(),"SITE");
		assertEquals("OK", response);
	}

	@Test public void testLogEventOnPriceCheckForEventNegative1() throws Exception {
		Order order = new Order();
		order.setOrderId("12");
		order.setOrderLines(new ArrayList<>());
		EventLogRequest eventLogRequest = new EventLogRequest();
		eventLogRequest.setTransactionTime("10:22");
		eventLogRequest.setChannelName("DCR_URL");
		eventLogRequest.setChannelType("INBOUND");
		eventLogRequest.setRequestPayload("Request_Payload");
		Mockito.when(jsonUtils.convertToJson(Mockito.any())).thenReturn("String");
		Mockito.when(eventLogPublisher.publishMessage(Mockito.any(), Mockito.any()))
			.thenThrow(new InterruptedException("TEST"));
		ArgumentCaptor<ErrorProcessorRequest> reqArgument = ArgumentCaptor.forClass(ErrorProcessorRequest.class);
		eventLogService
				.createEventLog(order,
					"channel", "OUTBOUND", OrderTransaction.CREATED, "orderId",
					"124",
					getMessageHeaders(),"SITE");
		verify(errorprocessorService, Mockito.atLeastOnce())
			.retryLater(reqArgument.capture());

	}

	@Test
	public void testLogEventOnPriceCheckForEventNegative2() throws Exception {
		Order order = new Order();
		order.setOrderId("12");
		order.setOrderLines(new ArrayList<>());
		EventLogRequest eventLogRequest = new EventLogRequest();
		eventLogRequest.setTransactionTime("10:22");
		eventLogRequest.setChannelName("DCR_URL");
		eventLogRequest.setChannelType("INBOUND");
		eventLogRequest.setRequestPayload("Request_Payload");
		Mockito.when(jsonUtils.convertToJson(Mockito.any())).thenReturn("String");
		Mockito.when(eventLogPublisher.publishMessage(Mockito.any(), Mockito.any()))
			.thenThrow(new ExecutionException(new Throwable("Exception")));
		ArgumentCaptor<ErrorProcessorRequest> reqArgument = ArgumentCaptor.forClass(ErrorProcessorRequest.class);
		eventLogService
				.createEventLog(order,
					"channel", "OUTBOUND", OrderTransaction.CREATED, "orderId",
					"124",
					getMessageHeaders(),"SITE");
		verify(errorprocessorService, Mockito.times(1))
			.retryLater(reqArgument.capture());

	}

	@Test
	public void testLogEventOnPriceCheckForEventNegative3() throws Exception {
		Order order = new Order();
		order.setOrderId("12");
		order.setOrderLines(new ArrayList<>());
		EventLogRequest eventLogRequest = new EventLogRequest();
		eventLogRequest.setTransactionTime("10:22");
		eventLogRequest.setChannelName("DCR_URL");
		eventLogRequest.setChannelType("INBOUND");
		eventLogRequest.setRequestPayload("Request_Payload");
		Mockito.when(jsonUtils.convertToJson(Mockito.any())).thenReturn("String");
		Mockito.when(eventLogPublisher.publishMessage(Mockito.any(), Mockito.any()))
			.thenThrow(new ExecutionException(new Throwable("TEST")));
		ArgumentCaptor<ErrorProcessorRequest> reqArgument = ArgumentCaptor.forClass(ErrorProcessorRequest.class);
		eventLogService
			.createEventLog(order,
				"channel", "OUTBOUND", OrderTransaction.MCHUB_ACK, "orderId",
				"124",
				getMessageHeaders(),"SITE");
		verify(errorprocessorService, Mockito.atLeastOnce())
			.retryLater(reqArgument.capture());
	}

	private Map<String, String> getMessageHeaders() {
		Map<String, String> messageHeaders = new HashMap<>();
		messageHeaders.put(Constant.ORDERID_HDR, getOrderId());
		messageHeaders.put(Constant.CLIENTID_HDR, getAppName());
		messageHeaders.put(Constant.CORRELATIONID_HDR, getCorrelationId());
		messageHeaders.put(Constant.MESSAGEID_HDR, getMessageId());
		return messageHeaders;
	}
}
