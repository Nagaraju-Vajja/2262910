package com.macys.uop.order.ordercollectorchestrator.proxy.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;

import com.macys.uop.common.lock.model.LockManagerRequest;
import com.macys.uop.foundation.core.utils.Constant;
import com.macys.uop.foundation.core.utils.execution.ServiceContextUtil;
import com.macys.uop.foundation.core.utils.rest.client.RestClient;
import com.macys.uop.foundation.core.utils.rest.client.RestClientResponse;
import com.macys.uop.foundation.core.utils.test.TestContextUtil;
import com.macys.uop.order.ordercollectorchestrator.proxy.ILockmanagerProxy;
import com.macys.uop.order.ordercollectorchestrator.utils.TestUtils;
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
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = LockmanagerProxyImpl.class)
public class LockmanagerProxyImplTest implements TestContextUtil, ServiceContextUtil {

	@Autowired
	private ILockmanagerProxy lockmanagerProxy;

	@MockBean
	private RestClient<LockManagerRequest, LockManagerRequest> restClient;


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
	public void testCreateLockTransaction() {
		RestClientResponse<LockManagerRequest> responseContext = new RestClientResponse<>();
		responseContext.setBody(TestUtils.getCreateLockReqest());

		Mockito.when(restClient.execute(any(), any()))
			.thenReturn(responseContext);
		LockManagerRequest
			response = lockmanagerProxy.createLockTransaction(new LockManagerRequest(), getHeaders());
		assertEquals("2RJEKSHG7E02L", response.getLockedEntityId());
		assertEquals("FRAUD_LOCK", response.getLockType());
		assertEquals("LOCK_CREATE", response.getTransactionId());
	}

	@Test
	public void testCreateLockTransactionException() {
		Mockito.doThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST))
			.when(restClient).execute(any(), any());
		Assert.assertThrows(Exception.class,
			() -> lockmanagerProxy.createLockTransaction(null, getHeaders()));
	}

	@Test
	public void testCreateLockTransactionFallback() {
		LockManagerRequest request = new LockManagerRequest();
		request.setOrderId("23425");
		Assert.assertThrows(Exception.class,
			() -> lockmanagerProxy.createLockTransactionFallback(request, getHeaders(),
				new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR)));
	}

	@Test
	public void testCreateLockTransactionFallback2() {
		LockManagerRequest request = new LockManagerRequest();
		request.setOrderId("23425");
		Assert.assertThrows(Exception.class,
			() -> lockmanagerProxy.createLockTransactionFallback(request, getHeaders(),
				new HttpServerErrorException(HttpStatus.SERVICE_UNAVAILABLE)));
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



