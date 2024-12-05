package com.macys.uop.foundation.core.utils.web.interceptor;

import static org.mockito.Mockito.mock;

import java.lang.reflect.Field;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.macys.uop.foundation.core.utils.validation.IHeaderCheck;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringRunner.class)
public class ServiceRequestHeaderHandlerInterceptorTest {
	
	@MockBean
	private IHeaderCheck headerCheckService;
	
	@Test
	public void testPreHandle() throws Exception {
		
		ServiceRequestHeaderHandlerInterceptor interceptor=mock(ServiceRequestHeaderHandlerInterceptor.class);
		ReflectionTestUtils.setField(interceptor, "isDefaultMandatoryHeaderCheckingEnabled", Boolean.TRUE);
		try {
			Field fieldJsonUtils = interceptor.getClass().getSuperclass().getDeclaredField("headerCheckService");
			fieldJsonUtils.setAccessible(true);
			fieldJsonUtils.set(interceptor, headerCheckService);
		} catch (Exception e) {
			e.printStackTrace();
		}
		HttpServletRequest request=mock(HttpServletRequest.class);
		HttpServletResponse response=mock(HttpServletResponse.class);
		
		Mockito.doNothing().when(headerCheckService).validateHeaders(request);
		
		Mockito.doCallRealMethod().when(interceptor).preHandle(request, response, "");
		
		boolean result=interceptor.preHandle(request, response, "");
		
		Assert.assertTrue(result);
	}
	
	@Test
	public void testPreHandleHeaderCheckingDisabled() throws Exception {
		
		ServiceRequestHeaderHandlerInterceptor interceptor=mock(ServiceRequestHeaderHandlerInterceptor.class);
		ReflectionTestUtils.setField(interceptor, "isDefaultMandatoryHeaderCheckingEnabled", Boolean.FALSE);
		try {
			Field fieldJsonUtils = interceptor.getClass().getSuperclass().getDeclaredField("headerCheckService");
			fieldJsonUtils.setAccessible(true);
			fieldJsonUtils.set(interceptor, headerCheckService);
		} catch (Exception e) {
			e.printStackTrace();
		}
		HttpServletRequest request=mock(HttpServletRequest.class);
		HttpServletResponse response=mock(HttpServletResponse.class);
		
		Mockito.doNothing().when(headerCheckService).validateHeaders(request);
		
		Mockito.doCallRealMethod().when(interceptor).preHandle(request, response, "");
		
		boolean result=interceptor.preHandle(request, response, "");
		
		Assert.assertTrue(result);
	}
}
