package com.macys.uop.foundation.core.utils.web.interceptor;

import static com.macys.uop.foundation.core.utils.Constant.CALLERID_DEFAULT_VALUE;
import static com.macys.uop.foundation.core.utils.Constant.CLIENTID_HDR;
import static com.macys.uop.foundation.core.utils.Constant.MESSAGEID_HDR;
import static com.macys.uop.foundation.core.utils.Constant.ORDERID_HDR;
import static com.macys.uop.foundation.core.utils.Constant.ORDERID_HDR_DEFAULT_VALUE;
import static org.mockito.Mockito.mock;

import java.util.Arrays;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.MultiValueMap;

import com.macys.uop.foundation.core.utils.test.model.KeyEnumeration;
import com.macys.uop.foundation.core.utils.test.model.ValueEnumeration;



@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringRunner.class)
public class RequestProcessorTest {
	
	@Test
	public void testExtractHeaders() {
		
		RequestProcessorImpl requestProcessor=mock(RequestProcessorImpl.class);
		HttpServletRequest request=mock(HttpServletRequest.class);
		
		Enumeration<String> keyEnumeration=new KeyEnumeration();
		Enumeration<String> valueEnumeration=new ValueEnumeration();
		
		Mockito.doReturn(keyEnumeration).when(request).getHeaderNames();
		Mockito.doReturn(valueEnumeration).when(request).getHeaders(ArgumentMatchers.any());
		
		Mockito.doCallRealMethod().when(requestProcessor).extractHeaders(request);
		
		HttpHeaders httpHeaders=requestProcessor.extractHeaders(request);
		
		Assert.assertNotNull(httpHeaders);
	}
	
	@Test
	public void testExtractQueryParameters() {
		
		RequestProcessorImpl requestProcessor=mock(RequestProcessorImpl.class);
		HttpServletRequest request=mock(HttpServletRequest.class);
		
		Enumeration<String> keyEnumeration=new KeyEnumeration();
		String[] values={"V1","V2","V3"};
		
		Mockito.doReturn(keyEnumeration).when(request).getParameterNames();
		Mockito.doReturn(values).when(request).getParameterValues(ArgumentMatchers.any());
		
		Mockito.doCallRealMethod().when(requestProcessor).extractQueryParameters(request);
		
		MultiValueMap<String, String> queryParameters=requestProcessor.extractQueryParameters(request);
		
		Assert.assertNotNull(queryParameters);
	}
	
	@Test
	public void testGetCallerId() {
		
		RequestProcessorImpl requestProcessor=mock(RequestProcessorImpl.class);
		HttpServletRequest request=mock(HttpServletRequest.class);
		
		Mockito.doReturn("somevalue").when(request).getHeader(CLIENTID_HDR);
		Mockito.doCallRealMethod().when(requestProcessor).getCallerId(request);
		
		String result=requestProcessor.getCallerId(request);
		
		Assert.assertEquals("somevalue", result);
	}
	
	@Test
	public void testGetCallerIdDefault() {
		
		RequestProcessorImpl requestProcessor=mock(RequestProcessorImpl.class);
		HttpServletRequest request=mock(HttpServletRequest.class);
		
		Mockito.doReturn(null).when(request).getHeader(CLIENTID_HDR);
		Mockito.doCallRealMethod().when(requestProcessor).getCallerId(request);
		
		String result=requestProcessor.getCallerId(request);
		
		Assert.assertEquals(CALLERID_DEFAULT_VALUE, result);
	}
	
	@Test
	public void testProcessHeaders() {
		
		RequestProcessorImpl requestProcessor=mock(RequestProcessorImpl.class);
		ReflectionTestUtils.setField(requestProcessor, "applicationName", "test-service");
		
		HttpHeaders headers =new HttpHeaders();
		headers.put(ORDERID_HDR, Arrays.asList("1111"));
		headers.put(MESSAGEID_HDR, Arrays.asList("2222"));
		headers.put(CLIENTID_HDR, Arrays.asList("3333"));
		
		HttpServletRequest request=mock(HttpServletRequest.class);
		Mockito.doReturn(headers).when(requestProcessor).extractHeaders(request);
		
		Mockito.doCallRealMethod().when(requestProcessor).processHeaders(request);
		
		HttpHeaders result=requestProcessor.processHeaders(request);
		
		Assert.assertEquals("test-service", result.get(CLIENTID_HDR).get(0));
	}
	
	@Test
	public void testProcessHeadersDefault() {
		
		RequestProcessorImpl requestProcessor=mock(RequestProcessorImpl.class);
		ReflectionTestUtils.setField(requestProcessor, "applicationName", "test-service");
		
		HttpHeaders headers =new HttpHeaders();
				
		HttpServletRequest request=mock(HttpServletRequest.class);
		Mockito.doReturn(headers).when(requestProcessor).extractHeaders(request);
		
		Mockito.doCallRealMethod().when(requestProcessor).processHeaders(request);
		
		HttpHeaders result=requestProcessor.processHeaders(request);
		
		Assert.assertEquals(ORDERID_HDR_DEFAULT_VALUE, result.get(ORDERID_HDR).get(0));
	}
}
