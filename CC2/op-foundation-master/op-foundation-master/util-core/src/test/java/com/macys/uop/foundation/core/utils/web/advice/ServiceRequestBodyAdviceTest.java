package com.macys.uop.foundation.core.utils.web.advice;

import static org.mockito.Mockito.mock;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

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
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.macys.uop.foundation.core.utils.execution.ServiceContextUtil;
import com.macys.uop.foundation.core.utils.json.JsonUtils;
import com.macys.uop.foundation.core.utils.logging.LogMessageBuilder;
import com.macys.uop.foundation.core.utils.masking.ApplicationMaskingConfiguration;
import com.macys.uop.foundation.core.utils.masking.IDataMasker;
import com.macys.uop.foundation.core.utils.test.TestContextUtil;
import com.macys.uop.foundation.core.utils.test.model.Employee;
import com.macys.uop.foundation.core.utils.xml.XmlUtils;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringRunner.class)
public class ServiceRequestBodyAdviceTest implements TestContextUtil, ServiceContextUtil {
	
	@MockBean
	private JsonUtils jsonUtils;
	
	@MockBean
	private XmlUtils xmlUtils;
	
	@Before
	public void beforeTest() {
		initContext();
	}

	@After
	public void afterTest() {
		clearContext();
	}
	
	@Test
	public void testAfterBodyRead() {
		
		ServiceRequestBodyAdvice bodyAdvice=mock(ServiceRequestBodyAdvice.class);
		ReflectionTestUtils.setField(bodyAdvice, "isServerRequestLoggingEnabled", Boolean.TRUE);
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
		
		Mockito.doReturn("http://someurl").when(bodyAdvice).getRequestURL();
		Mockito.doReturn("http://someurl").when(bodyAdvice).getUri();
		Mockito.doReturn(Boolean.FALSE).when(bodyAdvice).isURIInExclusionList(ArgumentMatchers.any());
		Mockito.doReturn("POST").when(bodyAdvice).getHttpMethod();
		Mockito.doReturn(new HashMap<>()).when(bodyAdvice).getSingleValueHttpHeaders();
		
		Mockito.doReturn(JSONDATA).when(jsonUtils).convertToJson(employee);
		
		Mockito.doCallRealMethod().when(bodyAdvice).afterBodyRead(employee, null, null, null, null);
		
		Object body=bodyAdvice.afterBodyRead(employee, null, null, null, null);
		
		Assert.assertTrue(body instanceof Employee);
	}
	
	@Test
	public void testAfterBodyReadLoggingDisabled() {
		
		ServiceRequestBodyAdvice bodyAdvice=mock(ServiceRequestBodyAdvice.class);
		ReflectionTestUtils.setField(bodyAdvice, "isServerRequestLoggingEnabled", Boolean.FALSE);
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
		
		Mockito.doReturn("http://someurl").when(bodyAdvice).getRequestURL();
		Mockito.doReturn("http://someurl").when(bodyAdvice).getUri();
		Mockito.doReturn(Boolean.FALSE).when(bodyAdvice).isURIInExclusionList(ArgumentMatchers.any());
		Mockito.doReturn("POST").when(bodyAdvice).getHttpMethod();
		Mockito.doReturn(new HashMap<>()).when(bodyAdvice).getSingleValueHttpHeaders();
		
		Mockito.doReturn(JSONDATA).when(jsonUtils).convertToJson(employee);
		
		Mockito.doCallRealMethod().when(bodyAdvice).afterBodyRead(employee, null, null, null, null);
		
		Object body=bodyAdvice.afterBodyRead(employee, null, null, null, null);
		
		Assert.assertTrue(body instanceof Employee);
	}
	
	@Test
	public void testAfterBodyReadNotValidURI() {
		
		ServiceRequestBodyAdvice bodyAdvice=mock(ServiceRequestBodyAdvice.class);
		ReflectionTestUtils.setField(bodyAdvice, "isServerRequestLoggingEnabled", Boolean.TRUE);
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
		
		Mockito.doReturn("http://someurl").when(bodyAdvice).getRequestURL();
		Mockito.doReturn("http://someurl").when(bodyAdvice).getUri();
		Mockito.doReturn(Boolean.TRUE).when(bodyAdvice).isURIInExclusionList(ArgumentMatchers.any());
		Mockito.doReturn("POST").when(bodyAdvice).getHttpMethod();
		Mockito.doReturn(new HashMap<>()).when(bodyAdvice).getSingleValueHttpHeaders();
		
		Mockito.doReturn(JSONDATA).when(jsonUtils).convertToJson(employee);
		
		Mockito.doCallRealMethod().when(bodyAdvice).afterBodyRead(employee, null, null, null, null);
		
		Object body=bodyAdvice.afterBodyRead(employee, null, null, null, null);
		
		Assert.assertTrue(body instanceof Employee);
	}
	
	@Test
	public void testAfterBodyReadContentTypeJson() {
		
		ServiceRequestBodyAdvice bodyAdvice=mock(ServiceRequestBodyAdvice.class);
		ReflectionTestUtils.setField(bodyAdvice, "isServerRequestLoggingEnabled", Boolean.TRUE);
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
		
		Mockito.doReturn("http://someurl").when(bodyAdvice).getRequestURL();
		Mockito.doReturn("http://someurl").when(bodyAdvice).getUri();
		Mockito.doReturn(Boolean.FALSE).when(bodyAdvice).isURIInExclusionList(ArgumentMatchers.any());
		Mockito.doReturn("POST").when(bodyAdvice).getHttpMethod();
		Mockito.doReturn(new HashMap<>()).when(bodyAdvice).getSingleValueHttpHeaders();
		Mockito.doReturn(Boolean.TRUE).when(bodyAdvice).isContentTypeApplicationJson();
		Mockito.doCallRealMethod().doReturn(new LogMessageBuilder()).when(bodyAdvice).getLogMessageBuilder(ArgumentMatchers.any());
		
		Mockito.doReturn(JSONDATA).when(jsonUtils).convertToJson(employee);
		
		Mockito.doCallRealMethod().when(bodyAdvice).afterBodyRead(employee, null, null, null, null);
		
		Object body=bodyAdvice.afterBodyRead(employee, null, null, null, null);
		
		Assert.assertTrue(body instanceof Employee);
	}
	
	@Test
	public void testAfterBodyReadContentTypeXml() {
		
		ServiceRequestBodyAdvice bodyAdvice=mock(ServiceRequestBodyAdvice.class);
		ReflectionTestUtils.setField(bodyAdvice, "isServerRequestLoggingEnabled", Boolean.TRUE);
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
		
		Map<String,IDataMasker> maskConfigMap=new HashMap<>();
		maskConfigMap.put("ContactNo", new com.macys.uop.foundation.core.utils.masking.CreditCardNumberMasker());
		ApplicationMaskingConfiguration.setXmlMaskConfMap(maskConfigMap);
		
		Employee employee=new Employee("test");
		String XMLDATA="<name>test</name>";
		
		Mockito.doReturn("http://someurl").when(bodyAdvice).getRequestURL();
		Mockito.doReturn("http://someurl").when(bodyAdvice).getUri();
		Mockito.doReturn(Boolean.FALSE).when(bodyAdvice).isURIInExclusionList(ArgumentMatchers.any());
		Mockito.doReturn("POST").when(bodyAdvice).getHttpMethod();
		Mockito.doReturn(new HashMap<>()).when(bodyAdvice).getSingleValueHttpHeaders();
		Mockito.doReturn(Boolean.FALSE).when(bodyAdvice).isContentTypeApplicationJson();
		Mockito.doReturn(Boolean.TRUE).when(bodyAdvice).isContentTypeApplicationXml();
		Mockito.doCallRealMethod().doReturn(new LogMessageBuilder()).when(bodyAdvice).getLogMessageBuilder(ArgumentMatchers.any());
		
		Mockito.doReturn(XMLDATA).when(xmlUtils).convertToXml(employee);
		
		Mockito.doCallRealMethod().when(bodyAdvice).afterBodyRead(employee, null, null, null, null);
		
		Object body=bodyAdvice.afterBodyRead(employee, null, null, null, null);
		
		Assert.assertTrue(body instanceof Employee);
	}
	
	@Test
	public void testAfterBodyReadContentTypeXmlWithoutMasking() {
		
		ServiceRequestBodyAdvice bodyAdvice=mock(ServiceRequestBodyAdvice.class);
		ReflectionTestUtils.setField(bodyAdvice, "isServerRequestLoggingEnabled", Boolean.TRUE);
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
		
		Mockito.doReturn("http://someurl").when(bodyAdvice).getRequestURL();
		Mockito.doReturn("http://someurl").when(bodyAdvice).getUri();
		Mockito.doReturn(Boolean.FALSE).when(bodyAdvice).isURIInExclusionList(ArgumentMatchers.any());
		Mockito.doReturn("POST").when(bodyAdvice).getHttpMethod();
		Mockito.doReturn(new HashMap<>()).when(bodyAdvice).getSingleValueHttpHeaders();
		Mockito.doReturn(Boolean.FALSE).when(bodyAdvice).isContentTypeApplicationJson();
		Mockito.doReturn(Boolean.TRUE).when(bodyAdvice).isContentTypeApplicationXml();
		Mockito.doCallRealMethod().doReturn(new LogMessageBuilder()).when(bodyAdvice).getLogMessageBuilder(ArgumentMatchers.any());
		
		Mockito.doReturn(XMLDATA).when(xmlUtils).convertToXml(employee);
		
		Mockito.doCallRealMethod().when(bodyAdvice).afterBodyRead(employee, null, null, null, null);
		
		Object body=bodyAdvice.afterBodyRead(employee, null, null, null, null);
		
		Assert.assertTrue(body instanceof Employee);
	}
	
	@Test
	public void testSupports() {
		
		ServiceRequestBodyAdvice bodyAdvice=mock(ServiceRequestBodyAdvice.class);
		ReflectionTestUtils.setField(bodyAdvice, "isServerRequestLoggingEnabled", Boolean.TRUE);
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
		
		Mockito.doCallRealMethod().when(bodyAdvice).supports(null, null,	null);
		
		boolean result=bodyAdvice.supports(null, null,	null);
		
		Assert.assertTrue(result);
	}
}
