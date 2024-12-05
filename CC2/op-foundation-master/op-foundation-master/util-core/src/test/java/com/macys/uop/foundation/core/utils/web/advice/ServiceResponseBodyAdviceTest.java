package com.macys.uop.foundation.core.utils.web.advice;

import static com.macys.uop.foundation.core.utils.Constant.MSG_PROCESSING_DURATION_TEXT_ORIGIN_REST;
import static org.mockito.Mockito.mock;

import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.macys.uop.foundation.core.utils.json.JsonUtils;
import com.macys.uop.foundation.core.utils.logging.LogMessageBuilder;
import com.macys.uop.foundation.core.utils.masking.ApplicationMaskingConfiguration;
import com.macys.uop.foundation.core.utils.masking.IDataMasker;
import com.macys.uop.foundation.core.utils.test.model.Employee;
import com.macys.uop.foundation.core.utils.xml.XmlUtils;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringRunner.class)
public class ServiceResponseBodyAdviceTest {
	
	@MockBean
	private JsonUtils jsonUtils;
	
	@MockBean
	private XmlUtils xmlUtils;
	
	@Test
	public void testBeforeBodyWrite() throws URISyntaxException {
		
		ServiceResponseBodyAdvice bodyAdvice=mock(ServiceResponseBodyAdvice.class);
		ReflectionTestUtils.setField(bodyAdvice, "appName", "test-service");
		ReflectionTestUtils.setField(bodyAdvice, "appVersion", "1.0");
		ReflectionTestUtils.setField(bodyAdvice, "isServerResponseLoggingEnabled", Boolean.FALSE);
		try {
			Field fieldJsonUtils = bodyAdvice.getClass().getSuperclass().getDeclaredField("jsonUtils");
			fieldJsonUtils.setAccessible(true);
			fieldJsonUtils.set(bodyAdvice, jsonUtils);

			Field fieldXmlUtils = bodyAdvice.getClass().getSuperclass().getDeclaredField("xmlUtils");
			fieldXmlUtils.setAccessible(true);
			fieldXmlUtils.set(bodyAdvice, xmlUtils);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Employee employee=new Employee("test");
		HttpHeaders httpHeaders=new HttpHeaders();
		
		ServerHttpRequest request=mock(ServerHttpRequest.class);
		ServerHttpResponse response=mock(ServerHttpResponse.class);
		Mockito.doReturn(httpHeaders).when(response).getHeaders();
		
		HttpServletResponse servletResponse = mock(HttpServletResponse.class);
		Mockito.doReturn(servletResponse).when(bodyAdvice).getHttpServletResponse(response);
		
		Mockito.doReturn(400).when(servletResponse).getStatus();
		Mockito.doReturn(new URI("http://url")).when(request).getURI();
		
		Mockito.doReturn(Instant.now().toString()).when(bodyAdvice).getReceivedTime();
		Mockito.doReturn("Some Text").when(bodyAdvice).constructMsgProcessingDurationText4Logging(Instant.now(), Instant.now(), MSG_PROCESSING_DURATION_TEXT_ORIGIN_REST);
		
		
		Mockito.doReturn(Boolean.TRUE).when(bodyAdvice).isURIInExclusionList(ArgumentMatchers.any());
		
		Mockito.doCallRealMethod().when(bodyAdvice).beforeBodyWrite(employee, null, null, null, request, response);
		
		Object body=bodyAdvice.beforeBodyWrite(employee, null, null, null, request, response);
		
		Assert.assertTrue(body instanceof Employee);
	}
	
	@Test
	public void testBeforeBodyWriteInvalidStatus() throws URISyntaxException {
		
		ServiceResponseBodyAdvice bodyAdvice=mock(ServiceResponseBodyAdvice.class);
		ReflectionTestUtils.setField(bodyAdvice, "appName", "test-service");
		ReflectionTestUtils.setField(bodyAdvice, "appVersion", "1.0");
		ReflectionTestUtils.setField(bodyAdvice, "isServerResponseLoggingEnabled", Boolean.TRUE);
		try {
			Field fieldJsonUtils = bodyAdvice.getClass().getSuperclass().getDeclaredField("jsonUtils");
			fieldJsonUtils.setAccessible(true);
			fieldJsonUtils.set(bodyAdvice, jsonUtils);

			Field fieldXmlUtils = bodyAdvice.getClass().getSuperclass().getDeclaredField("xmlUtils");
			fieldXmlUtils.setAccessible(true);
			fieldXmlUtils.set(bodyAdvice, xmlUtils);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Employee employee=new Employee("test");
		HttpHeaders httpHeaders=new HttpHeaders();
		
		ServerHttpRequest request=mock(ServerHttpRequest.class);
		ServerHttpResponse response=mock(ServerHttpResponse.class);
		Mockito.doReturn(httpHeaders).when(response).getHeaders();
		
		HttpServletResponse servletResponse = mock(HttpServletResponse.class);
		Mockito.doReturn(servletResponse).when(bodyAdvice).getHttpServletResponse(response);
		
		Mockito.doReturn(400).when(servletResponse).getStatus();
		Mockito.doReturn(new URI("http://url")).when(request).getURI();

		Mockito.doReturn(Instant.now().toString()).when(bodyAdvice).getReceivedTime();
		Mockito.doReturn("Some Text").when(bodyAdvice).constructMsgProcessingDurationText4Logging(Instant.now(), Instant.now(), MSG_PROCESSING_DURATION_TEXT_ORIGIN_REST);
		
		Mockito.doReturn(Boolean.FALSE).when(bodyAdvice).isURIInExclusionList(ArgumentMatchers.any());
		
		Mockito.doCallRealMethod().when(bodyAdvice).beforeBodyWrite(employee, null, null, null, request, response);
		
		Object body=bodyAdvice.beforeBodyWrite(employee, null, null, null, request, response);
		
		Assert.assertTrue(body instanceof Employee);
	}
	
	@Test
	public void testBeforeBodyWriteInvalidURL() throws URISyntaxException {
		
		ServiceResponseBodyAdvice bodyAdvice=mock(ServiceResponseBodyAdvice.class);
		ReflectionTestUtils.setField(bodyAdvice, "appName", "test-service");
		ReflectionTestUtils.setField(bodyAdvice, "appVersion", "1.0");
		ReflectionTestUtils.setField(bodyAdvice, "isServerResponseLoggingEnabled", Boolean.TRUE);
		try {
			Field fieldJsonUtils = bodyAdvice.getClass().getSuperclass().getDeclaredField("jsonUtils");
			fieldJsonUtils.setAccessible(true);
			fieldJsonUtils.set(bodyAdvice, jsonUtils);

			Field fieldXmlUtils = bodyAdvice.getClass().getSuperclass().getDeclaredField("xmlUtils");
			fieldXmlUtils.setAccessible(true);
			fieldXmlUtils.set(bodyAdvice, xmlUtils);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Employee employee=new Employee("test");
		HttpHeaders httpHeaders=new HttpHeaders();
		
		ServerHttpRequest request=mock(ServerHttpRequest.class);
		ServerHttpResponse response=mock(ServerHttpResponse.class);
		Mockito.doReturn(httpHeaders).when(response).getHeaders();
		
		HttpServletResponse servletResponse = mock(HttpServletResponse.class);
		Mockito.doReturn(servletResponse).when(bodyAdvice).getHttpServletResponse(response);
		
		Mockito.doReturn(200).when(servletResponse).getStatus();
		Mockito.doReturn(new URI("http://url")).when(request).getURI();

		Mockito.doReturn(Instant.now().toString()).when(bodyAdvice).getReceivedTime();
		Mockito.doReturn("Some Text").when(bodyAdvice).constructMsgProcessingDurationText4Logging(Instant.now(), Instant.now(), MSG_PROCESSING_DURATION_TEXT_ORIGIN_REST);
		
		Mockito.doReturn(Boolean.TRUE).when(bodyAdvice).isURIInExclusionList(ArgumentMatchers.any());
		
		MediaType selectedContentType=MediaType.APPLICATION_JSON;
		
		Mockito.doCallRealMethod().when(bodyAdvice).beforeBodyWrite(employee, null, selectedContentType, null, request, response);
		
		Object body=bodyAdvice.beforeBodyWrite(employee, null, selectedContentType, null, request, response);
		
		Assert.assertTrue(body instanceof Employee);
	}
	
	@Test
	public void testBeforeBodyWriteLoggingDisabled() throws URISyntaxException {
		
		ServiceResponseBodyAdvice bodyAdvice=mock(ServiceResponseBodyAdvice.class);
		ReflectionTestUtils.setField(bodyAdvice, "appName", "test-service");
		ReflectionTestUtils.setField(bodyAdvice, "appVersion", "1.0");
		ReflectionTestUtils.setField(bodyAdvice, "isServerResponseLoggingEnabled", Boolean.FALSE);
		try {
			Field fieldJsonUtils = bodyAdvice.getClass().getSuperclass().getDeclaredField("jsonUtils");
			fieldJsonUtils.setAccessible(true);
			fieldJsonUtils.set(bodyAdvice, jsonUtils);

			Field fieldXmlUtils = bodyAdvice.getClass().getSuperclass().getDeclaredField("xmlUtils");
			fieldXmlUtils.setAccessible(true);
			fieldXmlUtils.set(bodyAdvice, xmlUtils);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Employee employee=new Employee("test");
		HttpHeaders httpHeaders=new HttpHeaders();
		
		ServerHttpRequest request=mock(ServerHttpRequest.class);
		ServerHttpResponse response=mock(ServerHttpResponse.class);
		Mockito.doReturn(httpHeaders).when(response).getHeaders();
		
		HttpServletResponse servletResponse = mock(HttpServletResponse.class);
		Mockito.doReturn(servletResponse).when(bodyAdvice).getHttpServletResponse(response);
		
		Mockito.doReturn(200).when(servletResponse).getStatus();
		Mockito.doReturn(new URI("http://url")).when(request).getURI();

		Mockito.doReturn(Instant.now().toString()).when(bodyAdvice).getReceivedTime();
		Mockito.doReturn("Some Text").when(bodyAdvice).constructMsgProcessingDurationText4Logging(Instant.now(), Instant.now(), MSG_PROCESSING_DURATION_TEXT_ORIGIN_REST);
		
		Mockito.doReturn(Boolean.FALSE).when(bodyAdvice).isURIInExclusionList(ArgumentMatchers.any());
		
		MediaType selectedContentType=MediaType.APPLICATION_JSON;
		
		Mockito.doCallRealMethod().when(bodyAdvice).beforeBodyWrite(employee, null, selectedContentType, null, request, response);
		
		Object body=bodyAdvice.beforeBodyWrite(employee, null, selectedContentType, null, request, response);
		
		Assert.assertTrue(body instanceof Employee);
	}
	
	@Test
	public void testBeforeBodyWriteJson() throws URISyntaxException {
		
		ServiceResponseBodyAdvice bodyAdvice=mock(ServiceResponseBodyAdvice.class);
		ReflectionTestUtils.setField(bodyAdvice, "appName", "test-service");
		ReflectionTestUtils.setField(bodyAdvice, "appVersion", "1.0");
		ReflectionTestUtils.setField(bodyAdvice, "isServerResponseLoggingEnabled", Boolean.TRUE);
		try {
			Field fieldJsonUtils = bodyAdvice.getClass().getSuperclass().getDeclaredField("jsonUtils");
			fieldJsonUtils.setAccessible(true);
			fieldJsonUtils.set(bodyAdvice, jsonUtils);

			Field fieldXmlUtils = bodyAdvice.getClass().getSuperclass().getDeclaredField("xmlUtils");
			fieldXmlUtils.setAccessible(true);
			fieldXmlUtils.set(bodyAdvice, xmlUtils);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Employee employee=new Employee("test");
		String JSONDATA="{\"name\":\"test\"}";
		HttpHeaders httpHeaders=new HttpHeaders();
		
		ServerHttpRequest request=mock(ServerHttpRequest.class);
		ServerHttpResponse response=mock(ServerHttpResponse.class);
		Mockito.doReturn(httpHeaders).when(response).getHeaders();
		
		HttpServletResponse servletResponse = mock(HttpServletResponse.class);
		Mockito.doReturn(servletResponse).when(bodyAdvice).getHttpServletResponse(response);
		
		Mockito.doReturn(200).when(servletResponse).getStatus();
		Mockito.doReturn(new URI("http://url")).when(request).getURI();

		Mockito.doReturn(Instant.now().toString()).when(bodyAdvice).getReceivedTime();
		Mockito.doReturn("Some Text").when(bodyAdvice).constructMsgProcessingDurationText4Logging(Instant.now(), Instant.now(), MSG_PROCESSING_DURATION_TEXT_ORIGIN_REST);
		
		Mockito.doReturn(Boolean.FALSE).when(bodyAdvice).isURIInExclusionList(ArgumentMatchers.any());
		
		Mockito.doReturn(new LogMessageBuilder()).when(bodyAdvice).getLogMessageBuilder(ArgumentMatchers.any());
		Mockito.doReturn("POST").when(bodyAdvice).getHttpMethod();
		Mockito.doReturn("http://someurl").when(bodyAdvice).getRequestURL();
		Mockito.doReturn(JSONDATA).when(jsonUtils).convertToJson(employee);
		
		MediaType selectedContentType=MediaType.APPLICATION_JSON;
		Mockito.doCallRealMethod().when(bodyAdvice).beforeBodyWrite(employee, null, selectedContentType, null, request, response);
		
		Object body=bodyAdvice.beforeBodyWrite(employee, null, selectedContentType, null, request, response);
		
		Assert.assertTrue(body instanceof Employee);
	}
	
	@Test
	public void testBeforeBodyWriteXml() throws URISyntaxException {
		
		ServiceResponseBodyAdvice bodyAdvice=mock(ServiceResponseBodyAdvice.class);
		ReflectionTestUtils.setField(bodyAdvice, "appName", "test-service");
		ReflectionTestUtils.setField(bodyAdvice, "appVersion", "1.0");
		ReflectionTestUtils.setField(bodyAdvice, "isServerResponseLoggingEnabled", Boolean.TRUE);
		try {
			Field fieldJsonUtils = bodyAdvice.getClass().getSuperclass().getDeclaredField("jsonUtils");
			fieldJsonUtils.setAccessible(true);
			fieldJsonUtils.set(bodyAdvice, jsonUtils);

			Field fieldXmlUtils = bodyAdvice.getClass().getSuperclass().getDeclaredField("xmlUtils");
			fieldXmlUtils.setAccessible(true);
			fieldXmlUtils.set(bodyAdvice, xmlUtils);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Employee employee=new Employee("test");
		String XMLDATA="<name>test</name>";
		HttpHeaders httpHeaders=new HttpHeaders();
		
		ServerHttpRequest request=mock(ServerHttpRequest.class);
		ServerHttpResponse response=mock(ServerHttpResponse.class);
		Mockito.doReturn(httpHeaders).when(response).getHeaders();
		
		HttpServletResponse servletResponse = mock(HttpServletResponse.class);
		Mockito.doReturn(servletResponse).when(bodyAdvice).getHttpServletResponse(response);
		
		Mockito.doReturn(200).when(servletResponse).getStatus();
		Mockito.doReturn(new URI("http://url")).when(request).getURI();

		Mockito.doReturn(Instant.now().toString()).when(bodyAdvice).getReceivedTime();
		Mockito.doReturn("Some Text").when(bodyAdvice).constructMsgProcessingDurationText4Logging(Instant.now(), Instant.now(), MSG_PROCESSING_DURATION_TEXT_ORIGIN_REST);
		
		Map<String,IDataMasker> maskConfigMap=new HashMap<>();
		maskConfigMap.put("ContactNo", new com.macys.uop.foundation.core.utils.masking.CreditCardNumberMasker());
		ApplicationMaskingConfiguration.setXmlMaskConfMap(maskConfigMap);
		
		Mockito.doReturn(new LogMessageBuilder()).when(bodyAdvice).getLogMessageBuilder(ArgumentMatchers.any());
		Mockito.doReturn("POST").when(bodyAdvice).getHttpMethod();
		Mockito.doReturn("http://someurl").when(bodyAdvice).getRequestURL();
		Mockito.doReturn(XMLDATA).when(xmlUtils).convertToXml(employee);
		
		MediaType selectedContentType=MediaType.APPLICATION_XML;
		Mockito.doCallRealMethod().when(bodyAdvice).beforeBodyWrite(employee, null, selectedContentType, null, request, response);
		
		Object body=bodyAdvice.beforeBodyWrite(employee, null, selectedContentType, null, request, response);
		
		Assert.assertTrue(body instanceof Employee);
	}
	
	@Test
	public void testBeforeBodyWriteXmlMaskingOff() throws URISyntaxException {
		
		ServiceResponseBodyAdvice bodyAdvice=mock(ServiceResponseBodyAdvice.class);
		ReflectionTestUtils.setField(bodyAdvice, "appName", "test-service");
		ReflectionTestUtils.setField(bodyAdvice, "appVersion", "1.0");
		ReflectionTestUtils.setField(bodyAdvice, "isServerResponseLoggingEnabled", Boolean.TRUE);
		try {
			Field fieldJsonUtils = bodyAdvice.getClass().getSuperclass().getDeclaredField("jsonUtils");
			fieldJsonUtils.setAccessible(true);
			fieldJsonUtils.set(bodyAdvice, jsonUtils);

			Field fieldXmlUtils = bodyAdvice.getClass().getSuperclass().getDeclaredField("xmlUtils");
			fieldXmlUtils.setAccessible(true);
			fieldXmlUtils.set(bodyAdvice, xmlUtils);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Employee employee=new Employee("test");
		String XMLDATA="<name>test</name>";
		HttpHeaders httpHeaders=new HttpHeaders();
		
		ServerHttpRequest request=mock(ServerHttpRequest.class);
		ServerHttpResponse response=mock(ServerHttpResponse.class);
		Mockito.doReturn(httpHeaders).when(response).getHeaders();
		
		HttpServletResponse servletResponse = mock(HttpServletResponse.class);
		Mockito.doReturn(servletResponse).when(bodyAdvice).getHttpServletResponse(response);
		
		Mockito.doReturn(200).when(servletResponse).getStatus();
		Mockito.doReturn(new URI("http://url")).when(request).getURI();

		Mockito.doReturn(Instant.now().toString()).when(bodyAdvice).getReceivedTime();
		Mockito.doReturn("Some Text").when(bodyAdvice).constructMsgProcessingDurationText4Logging(Instant.now(), Instant.now(), MSG_PROCESSING_DURATION_TEXT_ORIGIN_REST);
		
		ApplicationMaskingConfiguration.setXmlMaskConfMap(null);
		
		Mockito.doReturn(new LogMessageBuilder()).when(bodyAdvice).getLogMessageBuilder(ArgumentMatchers.any());
		Mockito.doReturn("POST").when(bodyAdvice).getHttpMethod();
		Mockito.doReturn("http://someurl").when(bodyAdvice).getRequestURL();
		Mockito.doReturn(XMLDATA).when(xmlUtils).convertToXml(employee);
		
		MediaType selectedContentType=MediaType.APPLICATION_XML;
		Mockito.doCallRealMethod().when(bodyAdvice).beforeBodyWrite(employee, null, selectedContentType, null, request, response);
		
		Object body=bodyAdvice.beforeBodyWrite(employee, null, selectedContentType, null, request, response);
		
		Assert.assertTrue(body instanceof Employee);
	}
	
	
}
