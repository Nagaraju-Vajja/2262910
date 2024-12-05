package com.macys.uop.order.ordercollectorchestrator.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import com.macys.uop.foundation.core.utils.Constant;
import com.macys.uop.foundation.core.utils.execution.ServiceContextUtil;
import com.macys.uop.foundation.core.utils.json.JsonUtils;
import com.macys.uop.foundation.core.utils.test.TestContextUtil;
import com.macys.uop.order.model.Order;
import com.macys.uop.order.ordercollectorchestrator.proxy.IOrderenrichmentProxy;
import com.macys.uop.order.ordercollectorchestrator.service.IOrderenrichmentService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.HttpServerErrorException;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=OrderenrichmentServiceImpl.class)
public class OrderenrichmentServiceImplTest implements TestContextUtil, ServiceContextUtil {

	@Autowired
	private IOrderenrichmentService orderenrichmentService;

	@MockBean
	public JsonUtils jsonUtils;

	@MockBean
	public IOrderenrichmentProxy orderenrichmentProxy;

	@Before
	public void beforeTest() {
		initContext();
		getServiceRequestContext().setApplicationName("ordercollectorchestrator");
		getServiceRequestContext().setHeaders(getDefaultHttpHeaders());
	}

	@After
	public void afterTest() {
		clearContext();
	}


	private Order defaultOrder() {
		Order order = new Order();
		order.setOrderId("OrderId");
		return order;
	}

	@Test
	public void testEnrichOrder() {
		Order order = new Order();
		order.setOrderId("OrderId");
		Mockito.when(orderenrichmentProxy.enrichOrder(defaultOrder(),getDefaultHttpHeaders())).thenReturn(order);
		Order result = orderenrichmentService.enrichOrder(defaultOrder(),getDefaultHttpHeaders());
		assertEquals("OrderId", result.getOrderId());
	}

	@Test
	public void testEnrichOrderException() {
		Order order = new Order();
		order.setOrderId("OrderId");
		Mockito.when(orderenrichmentProxy.enrichOrder(defaultOrder(),getDefaultHttpHeaders())).thenThrow(new HttpServerErrorException(
			HttpStatus.INTERNAL_SERVER_ERROR));
		assertThrows(Exception.class, () -> orderenrichmentService.enrichOrder(defaultOrder(),getDefaultHttpHeaders()));
	}
}
