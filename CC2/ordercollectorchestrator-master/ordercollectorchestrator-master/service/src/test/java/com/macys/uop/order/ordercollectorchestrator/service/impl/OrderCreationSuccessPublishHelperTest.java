package com.macys.uop.order.ordercollectorchestrator.service.impl;

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
import com.macys.uop.order.ordercollectorchestrator.model.ErrorProcessorRequest;
import com.macys.uop.order.ordercollectorchestrator.publisher.IOrdercreationOnsuccessPublisher;
import com.macys.uop.order.ordercollectorchestrator.service.IErrorprocessorService;
import com.macys.uop.order.ordercollectorchestrator.service.IOrderCreationSuccessPublishHelper;
import com.macys.uop.order.ordercollectorchestrator.utils.OrdercollectorchestratorUtil;
import com.macys.uop.order.ordercollectorchestrator.utils.TestUtils;
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
@SpringBootTest(classes = OrderCreationSuccessPublishHelper.class)
public class OrderCreationSuccessPublishHelperTest implements TestContextUtil, ServiceContextUtil,
    OrdercollectorchestratorUtil {

	@Autowired
	private IOrderCreationSuccessPublishHelper orderCreationSuccessPublishHelper;

	@MockBean
	private IOrdercreationOnsuccessPublisher publisher;
	
	@MockBean
	private IErrorprocessorService errorprocessorService;

	@MockBean
	private JsonUtils jsonUtils;

	@Before
	public void beforeTest() {
		initContext();

		HttpHeaders headers = new HttpHeaders();
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
	public void publisherTest() throws ExecutionException, InterruptedException {
		Order order = new Order();
		order.setOrderId("123");
		Mockito.when(publisher.publishMessage(Mockito.any(), Mockito.anyMap())).thenReturn("OK");
		String result = orderCreationSuccessPublishHelper.publish(TestUtils.getOrderReqest(), getDefaultMessageHeaders());
		assertEquals("OK", result);
	}

	@Test
	public void publisherExceptionTest() throws ExecutionException, InterruptedException {
		FraudServiceResponse fraudServiceResponse = new FraudServiceResponse();
		fraudServiceResponse.setOrderId("TEST");
		Mockito.when(publisher.publishMessage(Mockito.any(), Mockito.any())).thenThrow(new InterruptedException("TEST"));
		ArgumentCaptor<ErrorProcessorRequest> errorRequest = ArgumentCaptor.forClass(ErrorProcessorRequest.class);
		orderCreationSuccessPublishHelper.publish(TestUtils.getOrderReqest(), getDefaultMessageHeaders());
		verify(errorprocessorService, Mockito.times(1)).retryLater(errorRequest.capture());
	}

}
