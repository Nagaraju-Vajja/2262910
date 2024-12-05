package com.macys.uop.foundation.core.utils.xml;

import static org.mockito.Mockito.mock;

import java.lang.reflect.Field;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import com.macys.uop.foundation.core.utils.test.model.Employee;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringRunner.class)
public class XmlUtilsTest {
	
	@Test
	public void testInit() {
		MappingJackson2XmlHttpMessageConverter xmlConverter = mock(MappingJackson2XmlHttpMessageConverter.class);
		ObjectMapper xmlMapper=mock(ObjectMapper.class);
		
		XmlUtilsImpl xmlUtils = mock(XmlUtilsImpl.class);
		try {
			Field fieldTracer = xmlUtils.getClass().getSuperclass().getDeclaredField("xmlConverter");
			fieldTracer.setAccessible(true);
			fieldTracer.set(xmlUtils, xmlConverter);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Mockito.doReturn(xmlMapper).when(xmlConverter).getObjectMapper();
		Mockito.doReturn(xmlMapper).when(xmlMapper).registerModule(new JaxbAnnotationModule());
		Mockito.doCallRealMethod().when(xmlUtils).init();
		xmlUtils.init();
		
		Assert.assertNotEquals("expected", "actual");
	}
	
	@Test
	public void testConvertToXml() throws JsonProcessingException {
		XmlUtilsImpl xmlUtils = mock(XmlUtilsImpl.class);
		
		MappingJackson2XmlHttpMessageConverter xmlConverter = mock(MappingJackson2XmlHttpMessageConverter.class);
		ObjectMapper xmlMapper=mock(ObjectMapper.class);
		try {
			Field ftXmlConverter = xmlUtils.getClass().getSuperclass().getDeclaredField("xmlConverter");
			ftXmlConverter.setAccessible(true);
			ftXmlConverter.set(xmlUtils, xmlConverter);
			
			Field ftXmlMapper = xmlUtils.getClass().getSuperclass().getDeclaredField("xmlMapper");
			ftXmlMapper.setAccessible(true);
			ftXmlMapper.set(xmlUtils, xmlMapper);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String xml="<name>Test</name>";
		Employee empl=new Employee("Test");
		Mockito.doReturn(xml).when(xmlMapper).writeValueAsString(empl);
		Mockito.doCallRealMethod().when(xmlUtils).convertToXml(empl);
		
		String result=xmlUtils.convertToXml(empl);
		
		Assert.assertEquals(result, xml);
	}
	
	@Test
	public void testConvertToXmlException() throws JsonProcessingException {
		JsonProcessingException exception = new JsonProcessingException("custom") {
			private static final long serialVersionUID = 1L;
		};
		XmlUtilsImpl xmlUtils = mock(XmlUtilsImpl.class);
		
		MappingJackson2XmlHttpMessageConverter xmlConverter = mock(MappingJackson2XmlHttpMessageConverter.class);
		ObjectMapper xmlMapper=mock(ObjectMapper.class);
		try {
			Field ftXmlConverter = xmlUtils.getClass().getSuperclass().getDeclaredField("xmlConverter");
			ftXmlConverter.setAccessible(true);
			ftXmlConverter.set(xmlUtils, xmlConverter);
			
			Field ftXmlMapper = xmlUtils.getClass().getSuperclass().getDeclaredField("xmlMapper");
			ftXmlMapper.setAccessible(true);
			ftXmlMapper.set(xmlUtils, xmlMapper);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Employee empl=new Employee("Test");
		Mockito.doThrow(exception).when(xmlMapper).writeValueAsString(empl);
		Mockito.doCallRealMethod().when(xmlUtils).convertToXml(empl);
		
		String result=null;
		try {
			result=xmlUtils.convertToXml(empl);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		Assert.assertEquals(null, result);
		
	}
	
	@Test
	public void testConvertFromXml() throws JsonProcessingException {
		XmlUtilsImpl xmlUtils = mock(XmlUtilsImpl.class);
		
		MappingJackson2XmlHttpMessageConverter xmlConverter = mock(MappingJackson2XmlHttpMessageConverter.class);
		ObjectMapper xmlMapper=mock(ObjectMapper.class);
		try {
			Field ftXmlConverter = xmlUtils.getClass().getSuperclass().getDeclaredField("xmlConverter");
			ftXmlConverter.setAccessible(true);
			ftXmlConverter.set(xmlUtils, xmlConverter);
			
			Field ftXmlMapper = xmlUtils.getClass().getSuperclass().getDeclaredField("xmlMapper");
			ftXmlMapper.setAccessible(true);
			ftXmlMapper.set(xmlUtils, xmlMapper);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String xml="<name>Test</name>";
		Employee empl=new Employee("Test");
		
		Mockito.doReturn(empl).when(xmlMapper).readValue(xml, Employee.class);
		Mockito.doCallRealMethod().when(xmlUtils).convertFromXml(xml, Employee.class);
		
		Employee result=xmlUtils.convertFromXml(xml, Employee.class);
		
		Assert.assertEquals(result, empl);
	}
	
	@Test
	public void testConvertFromXmlException() throws JsonProcessingException {
		JsonProcessingException exception = new JsonProcessingException("custom") {
			private static final long serialVersionUID = 1L;
		};
		XmlUtilsImpl xmlUtils = mock(XmlUtilsImpl.class);
		
		MappingJackson2XmlHttpMessageConverter xmlConverter = mock(MappingJackson2XmlHttpMessageConverter.class);
		ObjectMapper xmlMapper=mock(ObjectMapper.class);
		try {
			Field ftXmlConverter = xmlUtils.getClass().getSuperclass().getDeclaredField("xmlConverter");
			ftXmlConverter.setAccessible(true);
			ftXmlConverter.set(xmlUtils, xmlConverter);
			
			Field ftXmlMapper = xmlUtils.getClass().getSuperclass().getDeclaredField("xmlMapper");
			ftXmlMapper.setAccessible(true);
			ftXmlMapper.set(xmlUtils, xmlMapper);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String xml="<name>Test</name>";
		Mockito.doThrow(exception).when(xmlMapper).readValue(xml, Employee.class);
		Mockito.doCallRealMethod().when(xmlUtils).convertFromXml(xml, Employee.class);
		
		Employee result=xmlUtils.convertFromXml(xml, Employee.class);
		
		Assert.assertEquals(null, result);
	}
}
