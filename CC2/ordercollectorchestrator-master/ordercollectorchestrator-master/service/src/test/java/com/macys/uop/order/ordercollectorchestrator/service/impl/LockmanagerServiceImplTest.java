package com.macys.uop.order.ordercollectorchestrator.service.impl;

import static com.macys.uop.order.ordercollectorchestrator.utils.OrdercollectorchestratorConstants.*;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

import com.macys.uop.common.lock.model.LockManagerRequest;
import com.macys.uop.common.lock.model.LockManagerResponse;
import com.macys.uop.foundation.core.utils.Constant;
import com.macys.uop.foundation.core.utils.execution.ServiceContextUtil;
import com.macys.uop.foundation.core.utils.json.JsonUtils;
import com.macys.uop.foundation.core.utils.test.TestContextUtil;
import com.macys.uop.order.model.Order;
import com.macys.uop.order.model.OrderLine;
import com.macys.uop.order.ordercollectorchestrator.proxy.ILockmanagerProxy;
import com.macys.uop.order.ordercollectorchestrator.utils.BusinessConfigManagerUtil;
import com.macys.uop.order.ordercollectorchestrator.utils.OrdercollectorchestratorUtil;
import com.macys.uop.order.ordercollectorchestrator.utils.ValidatorUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = LockmanagerServiceImpl.class)
public class LockmanagerServiceImplTest implements TestContextUtil, ServiceContextUtil , OrdercollectorchestratorUtil{

	@Autowired
	private LockmanagerServiceImpl lockmanagerService;
	
	@MockBean
	private JsonUtils jsonUtils;

	@MockBean
	private ILockmanagerProxy lockmanagerProxy;

	@InjectMocks
	private LockManagerResponse lock;

	@MockBean
	private ValidatorUtil validatorUtil;

	@MockBean
	private BusinessConfigManagerUtil businessConfigManagerUtil;

	@Before
	public void beforeTest() {
		initContext();

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add(Constant.MESSAGEID_HDR, "1");
		httpHeaders.add(Constant.ORDERID_HDR, "2");
		httpHeaders.add(Constant.CORRELATIONID_HDR, "3");
		httpHeaders.add(Constant.CLIENTID_HDR, "4");
		getServiceRequestContext().setApplicationName("ordercollectorchestrator");
		getServiceRequestContext().setHeaders(httpHeaders);
	}

	@After
	public void afterTest() {
		clearContext();
	}
	
	@Test
	public void testCreateLock() {

		Mockito.when(validatorUtil.isProfileFeatureActive(Mockito.any(), Mockito.anyString())).thenReturn(true);

		Mockito.when(validatorUtil.isProfileFeatureActive(Mockito.any(),Mockito.anyString())).thenReturn(true);
		Order order = new Order();
		order.setOrderId("2");
		order.setSourceSystem("MCHECKOUT");
		LockManagerRequest lockManagerRequest = new LockManagerRequest();
		lockManagerRequest.setOrderId("123");
		lockManagerRequest.setLockId("12");
		order.setOriginalSaleOrderId("12");
		OrderLine orderline = new OrderLine();
		orderline.setReservationId("1234");
		ArrayList<OrderLine> orderLines = new ArrayList<>();
		orderLines.add(orderline);
		order.setOrderLines(orderLines);
		ArgumentCaptor<LockManagerRequest> reqArgument = ArgumentCaptor.forClass(LockManagerRequest.class);
		ArgumentCaptor<MultiValueMap> headerArgument = ArgumentCaptor.forClass(MultiValueMap.class);

		Mockito.when(lockmanagerProxy.createLockTransaction(Mockito.any(), Mockito.any()))
				.thenReturn(new LockManagerRequest());
		lockmanagerService.lockOrder(order, getHttpHeaders());
		verify(lockmanagerProxy, atLeastOnce()).createLockTransaction(reqArgument.capture(), headerArgument.capture());
	}
	
	
	@Test
	public void testCreateLock_False() {
		Mockito.when(validatorUtil.isProfileFeatureActive(Mockito.any(),Mockito.anyString())).thenReturn(false);
		Order order = new Order();
		order.setOrderId("2");
		order.setSourceSystem("MCHECKOUT");
		lockmanagerService.lockOrder(order, getHttpHeaders());
		verify(validatorUtil, atLeastOnce()).isProfileFeatureActive(order, FRAUD_VALIDATION);
	}

	@Test
	public void testCreateLockPayment(){

		Mockito.when(validatorUtil.isProfileFeatureActive(Mockito.any(), Mockito.anyString())).thenReturn(false);
		Map<String, Object> bizConfigMap = new HashMap<String, Object>();
		bizConfigMap.put("paymentLock", Boolean.TRUE);
		bizConfigMap.put("orderResponse", Boolean.TRUE);
		Order order = new Order();
		order.setOrderId("2");
		order.setOrderPurpose("EXCHANGE");
		order.setOriginalSaleOrderId("12");
		OrderLine orderline = new OrderLine();
		orderline.setReservationId("1234");
		ArrayList<OrderLine> orderLines = new ArrayList<>();
		orderLines.add(orderline);
		order.setOrderLines(orderLines);
		LockManagerRequest lockManagerRequest = new LockManagerRequest();
		lockManagerRequest.setOrderId("123");
		lockManagerRequest.setLockId("12");
		lockManagerRequest.setLockedEntity(PAYMENT);
		order.setSourceSystem("MCHECKOUT");
		ArgumentCaptor<LockManagerRequest> reqArgument = ArgumentCaptor.forClass(LockManagerRequest.class);
		ArgumentCaptor<MultiValueMap> headerArgument = ArgumentCaptor.forClass(MultiValueMap.class);

		Mockito.when(validatorUtil.getBusinessConfigForOrder(Mockito.any(),Mockito.any())).thenReturn(true);
		Mockito.when(lockmanagerProxy.createLockTransaction(Mockito.any(), Mockito.any()))
				.thenReturn(new LockManagerRequest());
		lockmanagerService.lockOrder(order, getHttpHeaders());
		verify(lockmanagerProxy, atLeastOnce()).createLockTransaction(reqArgument.capture(), headerArgument.capture());
	}
}
