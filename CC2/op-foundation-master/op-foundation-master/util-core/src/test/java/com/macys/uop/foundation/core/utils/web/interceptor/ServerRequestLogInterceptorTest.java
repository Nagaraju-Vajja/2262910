package com.macys.uop.foundation.core.utils.web.interceptor;

import static org.mockito.Mockito.mock;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.DispatcherType;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.macys.uop.foundation.core.utils.logging.LogMessageBuilder;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringRunner.class)
public class ServerRequestLogInterceptorTest {
	
	@Test
	public void testPreHandle() {
		
		ServerRequestLogInterceptor logInterceptor=mock(ServerRequestLogInterceptor.class);
		ReflectionTestUtils.setField(logInterceptor, "isServerRequestLoggingEnabled", Boolean.TRUE);
		
		HttpServletRequest request=mock(HttpServletRequest.class);
		HttpServletResponse response=mock(HttpServletResponse.class);
		Map<String,String> headers=new HashMap<>();
		
		Mockito.doReturn("http://someurl").when(request).getRequestURI();
		Mockito.doReturn(DispatcherType.REQUEST).when(request).getDispatcherType();
		Mockito.doReturn("GET").when(request).getMethod();
		Mockito.doReturn(Boolean.FALSE).when(logInterceptor).isURIInExclusionList(ArgumentMatchers.any());
		Mockito.doReturn(new LogMessageBuilder()).when(logInterceptor).getLogMessageBuilder(ArgumentMatchers.any());
		Mockito.doReturn("GET").when(logInterceptor).getHttpMethod();
		Mockito.doReturn("http://someurl").when(logInterceptor).getRequestURL();
		Mockito.doReturn(headers).when(logInterceptor).getSingleValueHttpHeaders();
		
		Mockito.doCallRealMethod().when(logInterceptor).preHandle(request, response, "");
		
		boolean result=logInterceptor.preHandle(request, response, "");
		
		Assert.assertTrue(result);
	}
	
	@Test
	public void testPreHandleDiffDispatcher() {
		
		ServerRequestLogInterceptor logInterceptor=mock(ServerRequestLogInterceptor.class);
		ReflectionTestUtils.setField(logInterceptor, "isServerRequestLoggingEnabled", Boolean.TRUE);
		
		HttpServletRequest request=mock(HttpServletRequest.class);
		HttpServletResponse response=mock(HttpServletResponse.class);
		Map<String,String> headers=new HashMap<>();
		
		Mockito.doReturn("http://someurl").when(request).getRequestURI();
		Mockito.doReturn(DispatcherType.FORWARD).when(request).getDispatcherType();
		Mockito.doReturn("GET").when(request).getMethod();
		Mockito.doReturn(Boolean.FALSE).when(logInterceptor).isURIInExclusionList(ArgumentMatchers.any());
		Mockito.doReturn(new LogMessageBuilder()).when(logInterceptor).getLogMessageBuilder(ArgumentMatchers.any());
		Mockito.doReturn("GET").when(logInterceptor).getHttpMethod();
		Mockito.doReturn("http://someurl").when(logInterceptor).getRequestURL();
		Mockito.doReturn(headers).when(logInterceptor).getSingleValueHttpHeaders();
		
		Mockito.doCallRealMethod().when(logInterceptor).preHandle(request, response, "");
		
		boolean result=logInterceptor.preHandle(request, response, "");
		
		Assert.assertTrue(result);
	}
	
	@Test
	public void testPreHandleDiffMethod() {
		
		ServerRequestLogInterceptor logInterceptor=mock(ServerRequestLogInterceptor.class);
		ReflectionTestUtils.setField(logInterceptor, "isServerRequestLoggingEnabled", Boolean.TRUE);
		
		HttpServletRequest request=mock(HttpServletRequest.class);
		HttpServletResponse response=mock(HttpServletResponse.class);
		Map<String,String> headers=new HashMap<>();
		
		Mockito.doReturn("http://someurl").when(request).getRequestURI();
		Mockito.doReturn(DispatcherType.REQUEST).when(request).getDispatcherType();
		Mockito.doReturn("POST").when(request).getMethod();
		Mockito.doReturn(Boolean.FALSE).when(logInterceptor).isURIInExclusionList(ArgumentMatchers.any());
		Mockito.doReturn(new LogMessageBuilder()).when(logInterceptor).getLogMessageBuilder(ArgumentMatchers.any());
		Mockito.doReturn("POST").when(logInterceptor).getHttpMethod();
		Mockito.doReturn("http://someurl").when(logInterceptor).getRequestURL();
		Mockito.doReturn(headers).when(logInterceptor).getSingleValueHttpHeaders();
		
		Mockito.doCallRealMethod().when(logInterceptor).preHandle(request, response, "");
		
		boolean result=logInterceptor.preHandle(request, response, "");
		
		Assert.assertTrue(result);
	}
	
	@Test
	public void testPreHandleInvalidURL() {
		
		ServerRequestLogInterceptor logInterceptor=mock(ServerRequestLogInterceptor.class);
		ReflectionTestUtils.setField(logInterceptor, "isServerRequestLoggingEnabled", Boolean.TRUE);
		
		HttpServletRequest request=mock(HttpServletRequest.class);
		HttpServletResponse response=mock(HttpServletResponse.class);
		Map<String,String> headers=new HashMap<>();
		
		Mockito.doReturn("http://someurl").when(request).getRequestURI();
		Mockito.doReturn(DispatcherType.REQUEST).when(request).getDispatcherType();
		Mockito.doReturn("GET").when(request).getMethod();
		Mockito.doReturn(Boolean.TRUE).when(logInterceptor).isURIInExclusionList(ArgumentMatchers.any());
		Mockito.doReturn(new LogMessageBuilder()).when(logInterceptor).getLogMessageBuilder(ArgumentMatchers.any());
		Mockito.doReturn("GET").when(logInterceptor).getHttpMethod();
		Mockito.doReturn("http://someurl").when(logInterceptor).getRequestURL();
		Mockito.doReturn(headers).when(logInterceptor).getSingleValueHttpHeaders();
		
		Mockito.doCallRealMethod().when(logInterceptor).preHandle(request, response, "");
		
		boolean result=logInterceptor.preHandle(request, response, "");
		
		Assert.assertTrue(result);
	}
	
	@Test
	public void testPreHandleLoggingDisabled() {
		
		ServerRequestLogInterceptor logInterceptor=mock(ServerRequestLogInterceptor.class);
		ReflectionTestUtils.setField(logInterceptor, "isServerRequestLoggingEnabled", Boolean.FALSE);
		
		HttpServletRequest request=mock(HttpServletRequest.class);
		HttpServletResponse response=mock(HttpServletResponse.class);
		Map<String,String> headers=new HashMap<>();
		
		Mockito.doReturn("http://someurl").when(request).getRequestURI();
		Mockito.doReturn(DispatcherType.REQUEST).when(request).getDispatcherType();
		Mockito.doReturn("GET").when(request).getMethod();
		Mockito.doReturn(Boolean.FALSE).when(logInterceptor).isURIInExclusionList(ArgumentMatchers.any());
		Mockito.doReturn(new LogMessageBuilder()).when(logInterceptor).getLogMessageBuilder(ArgumentMatchers.any());
		Mockito.doReturn("GET").when(logInterceptor).getHttpMethod();
		Mockito.doReturn("http://someurl").when(logInterceptor).getRequestURL();
		Mockito.doReturn(headers).when(logInterceptor).getSingleValueHttpHeaders();
		
		Mockito.doCallRealMethod().when(logInterceptor).preHandle(request, response, "");
		
		boolean result=logInterceptor.preHandle(request, response, "");
		
		Assert.assertTrue(result);
	}
}
