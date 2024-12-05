package com.macys.uop.order.ordercollectorchestrator.service.impl;

import static org.junit.Assert.assertEquals;

import com.macys.uop.foundation.core.utils.Constant;
import com.macys.uop.foundation.core.utils.execution.ServiceContextUtil;
import com.macys.uop.foundation.core.utils.test.TestContextUtil;
import com.macys.uop.order.model.Order;
import com.macys.uop.order.ordercollectorchestrator.proxy.IOrdercollectProxy;
import com.macys.uop.order.ordercollectorchestrator.service.IOrdercollectService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=OrdercollectServiceImpl.class)
public class OrdercollectServiceImplTest implements TestContextUtil, ServiceContextUtil {

	@Autowired
	private IOrdercollectService ordercollectService;

	@MockBean
	public IOrdercollectProxy ordercollectProxy;

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
	public void testCollectOrder() {
		Order order = new Order();
		order.setOrderId("OrderId");
		Mockito.when(ordercollectProxy.collectOrder(defaultOrder(),getDefaultHttpHeaders())).thenReturn(order);
		Order result = ordercollectService.collectOrder(defaultOrder(),getDefaultHttpHeaders());
		assertEquals("OrderId", result.getOrderId());
	}
}
