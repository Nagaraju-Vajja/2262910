package com.macys.uop.foundation.core.utils.web.interceptor;

import static org.mockito.Mockito.mock;

import java.lang.reflect.Field;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import brave.SpanCustomizer;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringRunner.class)
public class SpanCustomizerHandlerInterceptorTest {
	
	@MockBean
	private SpanCustomizer spanCustomizer;
	
	@Test
	public void testPreHandle() throws Exception {
		
		SpanCustomizerHandlerInterceptor interceptor=mock(SpanCustomizerHandlerInterceptor.class);
		ReflectionTestUtils.setField(interceptor, "spanTagHeaderNames", "header1,header2");
		try {
			Field fieldJsonUtils = interceptor.getClass().getSuperclass().getDeclaredField("spanCustomizer");
			fieldJsonUtils.setAccessible(true);
			fieldJsonUtils.set(interceptor, spanCustomizer);
		} catch (Exception e) {
			e.printStackTrace();
		}
		HttpServletRequest request=mock(HttpServletRequest.class);
		HttpServletResponse response=mock(HttpServletResponse.class);
		
		Mockito.doReturn("http://someurl").when(request).getRequestURI();
		Mockito.doReturn(Boolean.FALSE).when(interceptor).isURIInExclusionList(ArgumentMatchers.any());
		Mockito.doReturn("headervalue").when(interceptor).getSingleValueHeaderParam(ArgumentMatchers.any());
		Mockito.doReturn(spanCustomizer).when(spanCustomizer).tag(ArgumentMatchers.any(), ArgumentMatchers.any());
		
		Mockito.doCallRealMethod().when(interceptor).preHandle(request, response, "");
		
		boolean result=interceptor.preHandle(request, response, "");
		
		Assert.assertTrue(result);
	}
	
	@Test
	public void testPreHandleInvalidURL() throws Exception {
		
		SpanCustomizerHandlerInterceptor interceptor=mock(SpanCustomizerHandlerInterceptor.class);
		ReflectionTestUtils.setField(interceptor, "spanTagHeaderNames", "header1,header2");
		try {
			Field fieldJsonUtils = interceptor.getClass().getSuperclass().getDeclaredField("spanCustomizer");
			fieldJsonUtils.setAccessible(true);
			fieldJsonUtils.set(interceptor, spanCustomizer);
		} catch (Exception e) {
			e.printStackTrace();
		}
		HttpServletRequest request=mock(HttpServletRequest.class);
		HttpServletResponse response=mock(HttpServletResponse.class);
		
		Mockito.doReturn("http://someurl").when(request).getRequestURI();
		Mockito.doReturn(Boolean.TRUE).when(interceptor).isURIInExclusionList(ArgumentMatchers.any());
		Mockito.doReturn("headervalue").when(interceptor).getSingleValueHeaderParam(ArgumentMatchers.any());
		Mockito.doReturn(spanCustomizer).when(spanCustomizer).tag(ArgumentMatchers.any(), ArgumentMatchers.any());
		
		Mockito.doCallRealMethod().when(interceptor).preHandle(request, response, "");
		
		boolean result=interceptor.preHandle(request, response, "");
		
		Assert.assertTrue(result);
	}
	
	@Test
	public void testPreHandleNoSpanHeaders() throws Exception {
		
		SpanCustomizerHandlerInterceptor interceptor=mock(SpanCustomizerHandlerInterceptor.class);
		ReflectionTestUtils.setField(interceptor, "spanTagHeaderNames", "");
		try {
			Field fieldJsonUtils = interceptor.getClass().getSuperclass().getDeclaredField("spanCustomizer");
			fieldJsonUtils.setAccessible(true);
			fieldJsonUtils.set(interceptor, spanCustomizer);
		} catch (Exception e) {
			e.printStackTrace();
		}
		HttpServletRequest request=mock(HttpServletRequest.class);
		HttpServletResponse response=mock(HttpServletResponse.class);
		
		Mockito.doReturn("http://someurl").when(request).getRequestURI();
		Mockito.doReturn(Boolean.FALSE).when(interceptor).isURIInExclusionList(ArgumentMatchers.any());
		Mockito.doReturn("headervalue").when(interceptor).getSingleValueHeaderParam(ArgumentMatchers.any());
		Mockito.doReturn(spanCustomizer).when(spanCustomizer).tag(ArgumentMatchers.any(), ArgumentMatchers.any());
		
		Mockito.doCallRealMethod().when(interceptor).preHandle(request, response, "");
		
		boolean result=interceptor.preHandle(request, response, "");
		
		Assert.assertTrue(result);
	}
	
	@Test
	public void testPreHandleSpanTagValueNull() throws Exception {
		
		SpanCustomizerHandlerInterceptor interceptor=mock(SpanCustomizerHandlerInterceptor.class);
		ReflectionTestUtils.setField(interceptor, "spanTagHeaderNames", "header1,header2");
		try {
			Field fieldJsonUtils = interceptor.getClass().getSuperclass().getDeclaredField("spanCustomizer");
			fieldJsonUtils.setAccessible(true);
			fieldJsonUtils.set(interceptor, spanCustomizer);
		} catch (Exception e) {
			e.printStackTrace();
		}
		HttpServletRequest request=mock(HttpServletRequest.class);
		HttpServletResponse response=mock(HttpServletResponse.class);
		
		Mockito.doReturn("http://someurl").when(request).getRequestURI();
		Mockito.doReturn(Boolean.FALSE).when(interceptor).isURIInExclusionList(ArgumentMatchers.any());
		Mockito.doReturn(null).when(interceptor).getSingleValueHeaderParam(ArgumentMatchers.any());
		Mockito.doReturn(spanCustomizer).when(spanCustomizer).tag(ArgumentMatchers.any(), ArgumentMatchers.any());
		
		Mockito.doCallRealMethod().when(interceptor).preHandle(request, response, "");
		
		boolean result=interceptor.preHandle(request, response, "");
		
		Assert.assertTrue(result);
	}
	
	@Test
	public void testPreHandleSpanTagValueEmpty() throws Exception {
		
		SpanCustomizerHandlerInterceptor interceptor=mock(SpanCustomizerHandlerInterceptor.class);
		ReflectionTestUtils.setField(interceptor, "spanTagHeaderNames", "header1,header2");
		try {
			Field fieldJsonUtils = interceptor.getClass().getSuperclass().getDeclaredField("spanCustomizer");
			fieldJsonUtils.setAccessible(true);
			fieldJsonUtils.set(interceptor, spanCustomizer);
		} catch (Exception e) {
			e.printStackTrace();
		}
		HttpServletRequest request=mock(HttpServletRequest.class);
		HttpServletResponse response=mock(HttpServletResponse.class);
		
		Mockito.doReturn("http://someurl").when(request).getRequestURI();
		Mockito.doReturn(Boolean.FALSE).when(interceptor).isURIInExclusionList(ArgumentMatchers.any());
		Mockito.doReturn("").when(interceptor).getSingleValueHeaderParam(ArgumentMatchers.any());
		Mockito.doReturn(spanCustomizer).when(spanCustomizer).tag(ArgumentMatchers.any(), ArgumentMatchers.any());
		
		Mockito.doCallRealMethod().when(interceptor).preHandle(request, response, "");
		
		boolean result=interceptor.preHandle(request, response, "");
		
		Assert.assertTrue(result);
	}

}
