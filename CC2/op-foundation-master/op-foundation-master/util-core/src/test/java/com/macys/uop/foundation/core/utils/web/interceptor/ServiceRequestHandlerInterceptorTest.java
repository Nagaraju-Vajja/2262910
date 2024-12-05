package com.macys.uop.foundation.core.utils.web.interceptor;

import static org.mockito.Mockito.mock;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.macys.uop.foundation.core.utils.test.TestContextUtil;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringRunner.class)
public class ServiceRequestHandlerInterceptorTest implements TestContextUtil {
	
	@Before
	public void beforeTest() {
		initContext();
	}

	@After
	public void afterTest() {
		clearContext();
	}
	
	@MockBean
	private IRequestProcessor requestProcessor;
	
	@Test
	public void testPreHandle() throws Exception {
		
		ServiceRequestHandlerInterceptor interceptor=mock(ServiceRequestHandlerInterceptor.class);
		ReflectionTestUtils.setField(interceptor, "applicationName", "test-service");
		try {
			Field fieldJsonUtils = interceptor.getClass().getSuperclass().getDeclaredField("requestProcessor");
			fieldJsonUtils.setAccessible(true);
			fieldJsonUtils.set(interceptor, requestProcessor);
		} catch (Exception e) {
			e.printStackTrace();
		}
		HttpServletRequest request=mock(HttpServletRequest.class);
		HttpServletResponse response=mock(HttpServletResponse.class);
		HttpHeaders headers = new HttpHeaders();
		Map<String, String> pathParams = new HashMap<>();
		MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
		StringBuffer buffer=new StringBuffer();
		buffer.append("http://someurl");
		
		Mockito.doReturn(buffer).when(request).getRequestURL();
		Mockito.doReturn(headers).when(requestProcessor).processHeaders(request);
		Mockito.doReturn("12345").when(requestProcessor).getCallerId(request);
		Mockito.doReturn(pathParams).when(request).getAttribute(ArgumentMatchers.any());
		Mockito.doReturn(queryParams).when(requestProcessor).extractQueryParameters(request);
		
		Mockito.doCallRealMethod().when(interceptor).preHandle(request, response, "");
		
		boolean result=interceptor.preHandle(request, response, "");
		
		Assert.assertTrue(result);
	}
}
