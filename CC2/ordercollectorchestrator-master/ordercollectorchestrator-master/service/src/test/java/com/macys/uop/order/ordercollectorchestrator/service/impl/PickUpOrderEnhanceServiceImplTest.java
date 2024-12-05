package com.macys.uop.order.ordercollectorchestrator.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.macys.uop.foundation.core.utils.Constant;
import com.macys.uop.foundation.core.utils.execution.ServiceContextUtil;
import com.macys.uop.foundation.core.utils.json.JsonUtils;
import com.macys.uop.foundation.core.utils.test.TestContextUtil;
import com.macys.uop.order.model.Order;
import com.macys.uop.order.model.OrderLine;
import com.macys.uop.order.model.OrderLineStatus;
import com.macys.uop.order.model.PersonInfo;
import com.macys.uop.order.ordercollectorchestrator.utils.BusinessConfigManagerUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.HttpClientErrorException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.eq;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=PickUpOrderEnhanceServiceImpl.class)
public class PickUpOrderEnhanceServiceImplTest implements TestContextUtil, ServiceContextUtil {

	@Autowired
	private PickUpOrderEnhanceServiceImpl pickUpOrderEnhanceServiceImpl;

	@MockBean
	private BusinessConfigManagerUtil businessConfigManagerUtil;
	@MockBean
	private JsonUtils jsonUtils;

	@Before
	public void beforeTest() {
		initContext();
		HttpHeaders headers = new HttpHeaders();
		headers.add(Constant.MESSAGEID_HDR, "1");
		headers.add(Constant.ORDERID_HDR, "RSV172463");
		headers.add(Constant.CLIENTID_HDR, "3");
		headers.add(Constant.CORRELATIONID_HDR, "4");
		getServiceRequestContext().setApplicationName("PickUpOrderEnhanceServiceImpl");
		getServiceRequestContext().setHeaders(headers);
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
	public void testEnhancePickUpOrderAndProcessBOSS() throws ExecutionException, InterruptedException, JsonProcessingException {
		Map<Integer, Map<Object, Object>> bizConfig = new HashMap<>();
		Map<Integer, Map<Object, Object>> bizConfigForBossPlus = new HashMap<>();
		Map<Object, Object> bizConfigDetails = new HashMap<>();
		bizConfigDetails.put("enableBossPlus", "true");
		bizConfig.put(1, bizConfigDetails);

		Map<Object, Object> bizConfigDetailsForBossPlus = new HashMap<>();
		bizConfigDetailsForBossPlus.put("allowBossPlus", "true");
		bizConfigForBossPlus.put(1, bizConfigDetailsForBossPlus);

		Order order = new Order();
		order.setPartnerOrderId("123");
		List<OrderLine> orderLines = new ArrayList<>();
		OrderLine orderLine = new OrderLine();
		orderLine.setLineId(1);
		orderLine.setFulfillmentType("BOSS");
		List<OrderLineStatus> orderLineStatuses = new ArrayList<>();
		OrderLineStatus orderLineStatus = new OrderLineStatus();
		orderLineStatus.setPickupLocation("12345");
		orderLineStatus.setFulfillmentLocation("12345");
		orderLineStatus.setFulfillmentStore("734");
		orderLineStatuses.add(orderLineStatus);
		orderLine.setOrderLineStatuses(orderLineStatuses);
		orderLines.add(orderLine);
		order.setOrderLines(orderLines);
		Mockito.when(businessConfigManagerUtil.getConfigByName(eq("order.bossplus"), Mockito.any(Order.class))).thenReturn(bizConfig);
		Mockito.when(businessConfigManagerUtil.getConfigByName(eq("order.bossplus.enablefulfillmentstore"), Mockito.any(Order.class))).thenReturn(bizConfigForBossPlus);
		pickUpOrderEnhanceServiceImpl.enrichPickUpOrder(order);
		assertEquals("BOPS", CollectionUtils.firstElement(order.getOrderLines()).getFulfillmentType());
	}

	@Test
	public void testEnhancePickUpOrderAndProcessBOSS_1() throws ExecutionException, InterruptedException, JsonProcessingException {
		Map<Integer, Map<Object, Object>> bizConfig = new HashMap<>();
		Map<Object, Object> bizConfigDetails = new HashMap<>();
		bizConfigDetails.put("enableBossPlus", "true");
		bizConfig.put(1, bizConfigDetails);
		Order order = new Order();
		order.setPartnerOrderId("123");
		List<OrderLine> orderLines = new ArrayList<>();
		OrderLine orderLine = new OrderLine();
		orderLine.setLineId(1);
		orderLine.setFulfillmentType("BOSS");
		List<OrderLineStatus> orderLineStatuses = new ArrayList<>();
		OrderLineStatus orderLineStatus = new OrderLineStatus();
		orderLineStatus.setPickupLocation("12345");
		orderLineStatus.setFulfillmentLocation("12345");
		orderLineStatus.setFulfillmentStore("734");
		orderLineStatuses.add(orderLineStatus);
		orderLine.setOrderLineStatuses(orderLineStatuses);
		orderLines.add(orderLine);
		order.setOrderLines(orderLines);
		//Mockito.when(businessConfigManagerUtil.getConfigByName(eq("order.bossplus"), Mockito.any(Order.class))).thenReturn(bizConfig);
		Mockito.when(businessConfigManagerUtil.getConfigByName(eq("order.bossplus.enablefulfillmentstore"), Mockito.any(Order.class))).thenThrow(HttpClientErrorException.class);
		Assert.assertThrows("Business Configuration for Boss Plus not available",Exception.class,
				() -> pickUpOrderEnhanceServiceImpl.enrichPickUpOrder(order));
	}

	@Test
	public void testEnhancePickUpOrderAndProcessBOPS() throws ExecutionException, InterruptedException, JsonProcessingException {
		Map<Integer, Map<Object, Object>> bizConfig = new HashMap<>();
		Map<Object, Object> bizConfigDetails = new HashMap<>();
		bizConfigDetails.put("enableBossPlus", "true");
		bizConfigDetails.put("preferredFulfillmentType", "STH");
		bizConfig.put(1, bizConfigDetails);
		Order order = new Order();
		order.setOrderId("OrderId");
		List<OrderLine> orderLines = new ArrayList<>();
		OrderLine orderLine = new OrderLine();
		orderLine.setLineId(1);
		orderLine.setFulfillmentType("BOPS");
		List<OrderLineStatus> orderLineStatuses = new ArrayList<>();
		OrderLineStatus orderLineStatus = new OrderLineStatus();
		orderLineStatus.setPickupLocation("12345");
		orderLineStatus.setFulfillmentLocation("12345");
		orderLineStatuses.add(orderLineStatus);
		orderLine.setOrderLineStatuses(orderLineStatuses);
		orderLines.add(orderLine);
		order.setOrderLines(orderLines);
		Mockito.when(businessConfigManagerUtil.getConfigByName(Mockito.anyString(), Mockito.any())).thenReturn(bizConfig);
		pickUpOrderEnhanceServiceImpl.enrichPickUpOrder(order);
		assertEquals("STH", CollectionUtils.firstElement(order.getOrderLines()).getPreferredFulfillmentType());
	}

	@Test
	public void testEnhancePickUpOrderAndProcessSTH() throws ExecutionException, InterruptedException, JsonProcessingException {
		Map<Integer, Map<Object, Object>> bizConfig = new HashMap<>();
		Map<Object, Object> bizConfigDetails = new HashMap<>();
		bizConfigDetails.put("enableBossPlus", "true");
		bizConfig.put(1, bizConfigDetails);
		Order order = new Order();
		order.setOrderId("OrderId");
		List<OrderLine> orderLines = new ArrayList<>();
		OrderLine orderLine = new OrderLine();
		orderLine.setLineId(1);
		orderLine.setFulfillmentType("STH");
		List<OrderLineStatus> orderLineStatuses = new ArrayList<>();
		OrderLineStatus orderLineStatus = new OrderLineStatus();
		orderLineStatus.setPickupLocation("12345");
		orderLineStatus.setFulfillmentLocation("12345");
		orderLineStatuses.add(orderLineStatus);
		orderLine.setOrderLineStatuses(orderLineStatuses);
		orderLines.add(orderLine);
		order.setOrderLines(orderLines);
		Mockito.when(businessConfigManagerUtil.getConfigByName(Mockito.anyString(), Mockito.any())).thenReturn(bizConfig);
		pickUpOrderEnhanceServiceImpl.enrichPickUpOrder(order);
		assertNull( CollectionUtils.firstElement(order.getOrderLines()).getPreferredFulfillmentType());
	}

	@Test
	public void testEnhancePickUpOrderAndProcessBOSS_2() {
		Map<Integer, Map<Object, Object>> bizConfigForBossPlus = new HashMap<>();
		Map<Object, Object> bizConfigDetailsForBossPlus = new HashMap<>();
		bizConfigDetailsForBossPlus.put("allowBossPlus", "true");
		bizConfigDetailsForBossPlus.put("eligibleBossPlusEmailId", "[abc123@macys.com, xyz456@macys.com]");
		bizConfigForBossPlus.put(1, bizConfigDetailsForBossPlus);

		Order order = new Order();
		PersonInfo personInfo = new PersonInfo();
		personInfo.setEmailId("abc123@macys.com");
		order.setBillingInfo(personInfo);
		order.setPartnerOrderId("123");
		List<OrderLine> orderLines = new ArrayList<>();
		OrderLine orderLine = new OrderLine();
		orderLine.setLineId(1);
		orderLine.setFulfillmentType("BOSS");
		List<OrderLineStatus> orderLineStatuses = new ArrayList<>();
		OrderLineStatus orderLineStatus = new OrderLineStatus();
		orderLineStatus.setPickupLocation("12345");
		orderLineStatus.setFulfillmentLocation("12345");
		orderLineStatus.setFulfillmentStore("734");
		orderLineStatuses.add(orderLineStatus);
		orderLine.setOrderLineStatuses(orderLineStatuses);
		orderLines.add(orderLine);
		order.setOrderLines(orderLines);
		Mockito.when(businessConfigManagerUtil.getConfigByName(eq("order.bossplus.enablefulfillmentstore"), Mockito.any(Order.class))).thenReturn(bizConfigForBossPlus);
		pickUpOrderEnhanceServiceImpl.enrichPickUpOrder(order);
		assertEquals("BOPS", CollectionUtils.firstElement(order.getOrderLines()).getFulfillmentType());
	}

	@Test
	public void testEnhancePickUpOrderAndProcessBOSS_3() {
		Map<Integer, Map<Object, Object>> bizConfigForBossPlus = new HashMap<>();
		Map<Object, Object> bizConfigDetailsForBossPlus = new HashMap<>();
		bizConfigDetailsForBossPlus.put("allowBossPlus", "true");
		bizConfigDetailsForBossPlus.put("eligibleBossPlusEmailId", "[abc123@macys.com, xyz456@macys.com]");
		bizConfigForBossPlus.put(1, bizConfigDetailsForBossPlus);

		Order order = new Order();
		PersonInfo personInfo = new PersonInfo();
		personInfo.setEmailId("abc1234@macys.com");
		order.setBillingInfo(personInfo);
		order.setPartnerOrderId("123");
		List<OrderLine> orderLines = new ArrayList<>();
		OrderLine orderLine = new OrderLine();
		orderLine.setLineId(1);
		orderLine.setFulfillmentType("BOSS");
		List<OrderLineStatus> orderLineStatuses = new ArrayList<>();
		OrderLineStatus orderLineStatus = new OrderLineStatus();
		orderLineStatus.setPickupLocation("12345");
		orderLineStatus.setFulfillmentLocation("12345");
		orderLineStatus.setFulfillmentStore("734");
		orderLineStatuses.add(orderLineStatus);
		orderLine.setOrderLineStatuses(orderLineStatuses);
		orderLines.add(orderLine);
		order.setOrderLines(orderLines);
		Mockito.when(businessConfigManagerUtil.getConfigByName(eq("order.bossplus.enablefulfillmentstore"), Mockito.any(Order.class))).thenReturn(bizConfigForBossPlus);
		pickUpOrderEnhanceServiceImpl.enrichPickUpOrder(order);
		assertEquals("BOSS", CollectionUtils.firstElement(order.getOrderLines()).getFulfillmentType());
	}


	@Test
	public void testEnhancePickUpOrderAndProcessBOSS_4() {
		Map<Integer, Map<Object, Object>> bizConfigForBossPlus = new HashMap<>();
		Map<Object, Object> bizConfigDetailsForBossPlus = new HashMap<>();
		bizConfigDetailsForBossPlus.put("allowBossPlus", "true");
		bizConfigForBossPlus.put(1, bizConfigDetailsForBossPlus);

		Order order = new Order();
		PersonInfo personInfo = new PersonInfo();
		personInfo.setEmailId("abc1234@macys.com");
		order.setBillingInfo(personInfo);
		order.setPartnerOrderId("123");
		List<OrderLine> orderLines = new ArrayList<>();
		OrderLine orderLine = new OrderLine();
		orderLine.setLineId(1);
		orderLine.setFulfillmentType("BOSS");
		List<OrderLineStatus> orderLineStatuses = new ArrayList<>();
		OrderLineStatus orderLineStatus = new OrderLineStatus();
		orderLineStatus.setPickupLocation("12345");
		orderLineStatus.setFulfillmentLocation("12345");
		orderLineStatus.setFulfillmentStore("734");
		orderLineStatuses.add(orderLineStatus);
		orderLine.setOrderLineStatuses(orderLineStatuses);
		orderLines.add(orderLine);
		order.setOrderLines(orderLines);
		Mockito.when(businessConfigManagerUtil.getConfigByName(eq("order.bossplus.enablefulfillmentstore"), Mockito.any(Order.class))).thenReturn(bizConfigForBossPlus);
		pickUpOrderEnhanceServiceImpl.enrichPickUpOrder(order);
		assertEquals("BOPS", CollectionUtils.firstElement(order.getOrderLines()).getFulfillmentType());
	}
}
