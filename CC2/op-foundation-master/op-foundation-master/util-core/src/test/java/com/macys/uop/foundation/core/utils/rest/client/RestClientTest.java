package com.macys.uop.foundation.core.utils.rest.client;

import static com.macys.uop.foundation.core.utils.Constant.BEAN_ID_DEFAULT_REST_TEMPLATE;
import static com.macys.uop.foundation.core.utils.Constant.CLIENTID_HDR;
import static com.macys.uop.foundation.core.utils.Constant.CORRELATIONID_HDR;
import static com.macys.uop.foundation.core.utils.Constant.MESSAGEID_HDR;
import static com.macys.uop.foundation.core.utils.Constant.ORDERID_HDR;
import static org.mockito.Mockito.mock;

import java.lang.reflect.Field;
import java.util.Objects;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import com.macys.uop.foundation.core.utils.json.JsonUtils;
import com.macys.uop.foundation.core.utils.logging.LogMessageBuilder;
import com.macys.uop.foundation.core.utils.test.TestContextUtil;

@RunWith(SpringRunner.class)
public class RestClientTest implements TestContextUtil
{
	private final String baseUri="http://message-store-service/message-store-service/1.0";

	@MockBean
	private RestTemplate restTemplate;
	
	@MockBean
	private JsonUtils jsonUtils;
	
	@MockBean
	private ApplicationContext applicationContext;
	
	@Before
	public void beforeTest() {
		initContext();
	}

	@After
	public void afterTest() {
		clearContext();
	}
	
	@Test 
	public void testExecuteWithBody()
	{
		RestClientRequest<String> clientRequest=RestClientRequest
				.<String>builder().withUrl(baseUri+"/orders/check")
				.withBody("{\"name\":\"bob\"}")
				.withMethod(HttpMethod.POST.name())
				.withHeader(CLIENTID_HDR, "12")
				.withHeader(CORRELATIONID_HDR, "1234")
				.withHeader(MESSAGEID_HDR, "12345")
				.withHeader(ORDERID_HDR, "123456")
				.withRestTemplate(restTemplate)
				.build();
		
		@SuppressWarnings("unchecked")
		RestClientImpl<String,Object> restClient=(RestClientImpl<String,Object>) mock(RestClientImpl.class);
		ReflectionTestUtils.setField(restClient, "isClientRequestLoggingEnabled", true);
		ReflectionTestUtils.setField(restClient, "isClientResponseLoggingEnabled", true);
		ReflectionTestUtils.setField(restClient, "isClientRequestHeaderAndPayloadLoggingEnabled", true);
		ReflectionTestUtils.setField(restClient, "isClientResponseHeaderAndPayloadLoggingEnabled", true);

		try {
			Field fieldTracer = restClient.getClass().getSuperclass().getDeclaredField("applicationContext");
			fieldTracer.setAccessible(true);
			fieldTracer.set(restClient, applicationContext);

			Field fieldSpanCustomizer = restClient.getClass().getSuperclass().getDeclaredField("jsonUtils");
			fieldSpanCustomizer.setAccessible(true);
			fieldSpanCustomizer.set(restClient, jsonUtils);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Mockito.doReturn(restTemplate).when(applicationContext).getBean(BEAN_ID_DEFAULT_REST_TEMPLATE, RestTemplate.class);
		ResponseEntity<String> responseEntity = new ResponseEntity<>("", HttpStatus.OK);
		Mockito.when(restTemplate.exchange(ArgumentMatchers.any(), ArgumentMatchers.<Class<String>>any())).thenReturn(responseEntity);
		Mockito.doCallRealMethod().doReturn(new LogMessageBuilder()).when(restClient).getLogMessageBuilder(ArgumentMatchers.any());
		
		RestClientResponse<Object> clientResponse=new RestClientResponse<Object>();
		clientResponse.setBody("some value");
		clientResponse.setStatus(HttpStatus.OK);
				
		Mockito.doCallRealMethod().when(restClient).execute(clientRequest, Object.class);
		
		RestClientResponse<Object> result=restClient.execute(clientRequest, Object.class);
		
		Assert.assertEquals(HttpStatus.OK, result.getStatus());
	}
	
	@Test 
	public void testExecuteWithoutBody()
	{
		RestClientRequest<String> clientRequest=RestClientRequest
				.<String>builder().withUrl(baseUri+"/orders/check")
				.withMethod(HttpMethod.POST.name())
				.withHeader(CLIENTID_HDR, "12")
				.withHeader(CORRELATIONID_HDR, "1234")
				.withHeader(MESSAGEID_HDR, "12345")
				.withHeader(ORDERID_HDR, "123456")
				.withRestTemplate(restTemplate)
				.build();
		
		@SuppressWarnings("unchecked")
		RestClientImpl<String,Object> restClient=(RestClientImpl<String,Object>) mock(RestClientImpl.class);
		ReflectionTestUtils.setField(restClient, "isClientRequestLoggingEnabled", true);
		ReflectionTestUtils.setField(restClient, "isClientResponseLoggingEnabled", true);
		
		try {
			Field fieldTracer = restClient.getClass().getSuperclass().getDeclaredField("applicationContext");
			fieldTracer.setAccessible(true);
			fieldTracer.set(restClient, applicationContext);

			Field fieldSpanCustomizer = restClient.getClass().getSuperclass().getDeclaredField("jsonUtils");
			fieldSpanCustomizer.setAccessible(true);
			fieldSpanCustomizer.set(restClient, jsonUtils);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Mockito.doReturn(restTemplate).when(applicationContext).getBean(BEAN_ID_DEFAULT_REST_TEMPLATE, RestTemplate.class);
		ResponseEntity<String> responseEntity = new ResponseEntity<>("", HttpStatus.OK);
		Mockito.when(restTemplate.exchange(ArgumentMatchers.any(), ArgumentMatchers.<Class<String>>any())).thenReturn(responseEntity);
		Mockito.doCallRealMethod().doReturn(new LogMessageBuilder()).when(restClient).getLogMessageBuilder(ArgumentMatchers.any());
		
		RestClientResponse<Object> clientResponse=new RestClientResponse<Object>();
		clientResponse.setBody("some value");
		clientResponse.setStatus(HttpStatus.OK);
				
		Mockito.doCallRealMethod().when(restClient).execute(clientRequest, Object.class);
		
		RestClientResponse<Object> result=restClient.execute(clientRequest, Object.class);
		
		Assert.assertEquals(HttpStatus.OK, result.getStatus());
	}
	
	
	
	@Test 
	public void testExecuteWithoutHeaders()
	{
		RestClientRequest<String> clientRequest=RestClientRequest
				.<String>builder().withUrl(baseUri+"/orders/check")
				.withMethod(HttpMethod.POST.name())
				.withRestTemplate(restTemplate)
				.build();
		
		@SuppressWarnings("unchecked")
		RestClientImpl<String,Object> restClient=(RestClientImpl<String,Object>) mock(RestClientImpl.class);
		ReflectionTestUtils.setField(restClient, "isClientRequestLoggingEnabled", true);
		ReflectionTestUtils.setField(restClient, "isClientResponseLoggingEnabled", true);
		try {
			Field fieldTracer = restClient.getClass().getSuperclass().getDeclaredField("applicationContext");
			fieldTracer.setAccessible(true);
			fieldTracer.set(restClient, applicationContext);

			Field fieldSpanCustomizer = restClient.getClass().getSuperclass().getDeclaredField("jsonUtils");
			fieldSpanCustomizer.setAccessible(true);
			fieldSpanCustomizer.set(restClient, jsonUtils);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Mockito.doReturn(restTemplate).when(applicationContext).getBean(BEAN_ID_DEFAULT_REST_TEMPLATE, RestTemplate.class);
		ResponseEntity<String> responseEntity = new ResponseEntity<>("", HttpStatus.OK);
		Mockito.when(restTemplate.exchange(ArgumentMatchers.any(), ArgumentMatchers.<Class<String>>any())).thenReturn(responseEntity);
		Mockito.doCallRealMethod().doReturn(new LogMessageBuilder()).when(restClient).getLogMessageBuilder(ArgumentMatchers.any());
		
		RestClientResponse<Object> clientResponse=new RestClientResponse<Object>();
		clientResponse.setBody("some value");
		clientResponse.setStatus(HttpStatus.OK);
				
		Mockito.doCallRealMethod().when(restClient).execute(clientRequest, Object.class);
		
		RestClientResponse<Object> result=restClient.execute(clientRequest, Object.class);
		
		Assert.assertEquals(HttpStatus.OK, result.getStatus());
	}
	
	@Test 
	public void testExecuteWithoutRestTemplate()
	{
		RestClientRequest<String> clientRequest=RestClientRequest
				.<String>builder().withUrl(baseUri+"/orders/check")
				.withMethod(HttpMethod.POST.name())
				.build();
		
		@SuppressWarnings("unchecked")
		RestClientImpl<String,Object> restClient=(RestClientImpl<String,Object>) mock(RestClientImpl.class);
		ReflectionTestUtils.setField(restClient, "isClientRequestLoggingEnabled", true);
		ReflectionTestUtils.setField(restClient, "isClientResponseLoggingEnabled", true);
		try {
			Field fieldTracer = restClient.getClass().getSuperclass().getDeclaredField("applicationContext");
			fieldTracer.setAccessible(true);
			fieldTracer.set(restClient, applicationContext);

			Field fieldSpanCustomizer = restClient.getClass().getSuperclass().getDeclaredField("jsonUtils");
			fieldSpanCustomizer.setAccessible(true);
			fieldSpanCustomizer.set(restClient, jsonUtils);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Mockito.doReturn(restTemplate).when(applicationContext).getBean(BEAN_ID_DEFAULT_REST_TEMPLATE, RestTemplate.class);
		ResponseEntity<String> responseEntity = new ResponseEntity<>("", HttpStatus.OK);
		Mockito.when(restTemplate.exchange(ArgumentMatchers.any(), ArgumentMatchers.<Class<String>>any())).thenReturn(responseEntity);
		Mockito.doCallRealMethod().doReturn(new LogMessageBuilder()).when(restClient).getLogMessageBuilder(ArgumentMatchers.any());
		
		RestClientResponse<Object> clientResponse=new RestClientResponse<Object>();
		clientResponse.setBody("some value");
		clientResponse.setStatus(HttpStatus.OK);
				
		Mockito.doCallRealMethod().when(restClient).execute(clientRequest, Object.class);
		
		RestClientResponse<Object> result=restClient.execute(clientRequest, Object.class);
		
		Assert.assertEquals(HttpStatus.OK, result.getStatus());
	}
	
	@Test 
	public void testExecuteRequestLoggingDisabled()
	{
		RestClientRequest<String> clientRequest=RestClientRequest
				.<String>builder().withUrl(baseUri+"/orders/check")
				.withBody("{\"name\":\"bob\"}")
				.withMethod(HttpMethod.POST.name())
				.withHeader(CLIENTID_HDR, "12")
				.withHeader(CORRELATIONID_HDR, "1234")
				.withHeader(MESSAGEID_HDR, "12345")
				.withHeader(ORDERID_HDR, "123456")
				.withRestTemplate(restTemplate)
				.withRequestLoggingEnabled(false)
				.build();
		
		@SuppressWarnings("unchecked")
		RestClientImpl<String,Object> restClient=(RestClientImpl<String,Object>) mock(RestClientImpl.class);
		ReflectionTestUtils.setField(restClient, "isClientRequestLoggingEnabled", true);
		ReflectionTestUtils.setField(restClient, "isClientResponseLoggingEnabled", true);
		try {
			Field fieldTracer = restClient.getClass().getSuperclass().getDeclaredField("applicationContext");
			fieldTracer.setAccessible(true);
			fieldTracer.set(restClient, applicationContext);

			Field fieldSpanCustomizer = restClient.getClass().getSuperclass().getDeclaredField("jsonUtils");
			fieldSpanCustomizer.setAccessible(true);
			fieldSpanCustomizer.set(restClient, jsonUtils);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Mockito.doReturn(restTemplate).when(applicationContext).getBean(BEAN_ID_DEFAULT_REST_TEMPLATE, RestTemplate.class);
		ResponseEntity<String> responseEntity = new ResponseEntity<>("", HttpStatus.OK);
		Mockito.when(restTemplate.exchange(ArgumentMatchers.any(), ArgumentMatchers.<Class<String>>any())).thenReturn(responseEntity);
		Mockito.doCallRealMethod().doReturn(new LogMessageBuilder()).when(restClient).getLogMessageBuilder(ArgumentMatchers.any());
		
		RestClientResponse<Object> clientResponse=new RestClientResponse<Object>();
		clientResponse.setBody("some value");
		clientResponse.setStatus(HttpStatus.OK);
				
		Mockito.doCallRealMethod().when(restClient).execute(clientRequest, Object.class);
		
		RestClientResponse<Object> result=restClient.execute(clientRequest, Object.class);
		
		Assert.assertEquals(HttpStatus.OK, result.getStatus());
	}
	
	@Test 
	public void testExecuteResponseLoggingDisabled()
	{
		RestClientRequest<String> clientRequest=RestClientRequest
				.<String>builder().withUrl(baseUri+"/orders/check")
				.withBody("{\"name\":\"bob\"}")
				.withMethod(HttpMethod.POST.name())
				.withHeader(CLIENTID_HDR, "12")
				.withHeader(CORRELATIONID_HDR, "1234")
				.withHeader(MESSAGEID_HDR, "12345")
				.withHeader(ORDERID_HDR, "123456")
				.withRestTemplate(restTemplate)
				.withResponseLoggingEnabled(false)
				.build();
		
		@SuppressWarnings("unchecked")
		RestClientImpl<String,Object> restClient=(RestClientImpl<String,Object>) mock(RestClientImpl.class);
		ReflectionTestUtils.setField(restClient, "isClientRequestLoggingEnabled", true);
		ReflectionTestUtils.setField(restClient, "isClientResponseLoggingEnabled", true);
		try {
			Field fieldTracer = restClient.getClass().getSuperclass().getDeclaredField("applicationContext");
			fieldTracer.setAccessible(true);
			fieldTracer.set(restClient, applicationContext);

			Field fieldSpanCustomizer = restClient.getClass().getSuperclass().getDeclaredField("jsonUtils");
			fieldSpanCustomizer.setAccessible(true);
			fieldSpanCustomizer.set(restClient, jsonUtils);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Mockito.doReturn(restTemplate).when(applicationContext).getBean(BEAN_ID_DEFAULT_REST_TEMPLATE, RestTemplate.class);
		ResponseEntity<String> responseEntity = new ResponseEntity<>("", HttpStatus.OK);
		Mockito.when(restTemplate.exchange(ArgumentMatchers.any(), ArgumentMatchers.<Class<String>>any())).thenReturn(responseEntity);
		Mockito.doCallRealMethod().doReturn(new LogMessageBuilder()).when(restClient).getLogMessageBuilder(ArgumentMatchers.any());
		
		RestClientResponse<Object> clientResponse=new RestClientResponse<Object>();
		clientResponse.setBody("some value");
		clientResponse.setStatus(HttpStatus.OK);
				
		Mockito.doCallRealMethod().when(restClient).execute(clientRequest, Object.class);
		
		RestClientResponse<Object> result=restClient.execute(clientRequest, Object.class);
		
		Assert.assertEquals(HttpStatus.OK, result.getStatus());
	}
	
	@Test 
	public void testExecuteRequestResponseLoggingDisabled()
	{
		RestClientRequest<String> clientRequest=RestClientRequest
				.<String>builder().withUrl(baseUri+"/orders/check")
				.withBody("{\"name\":\"bob\"}")
				.withMethod(HttpMethod.POST.name())
				.withHeader(CLIENTID_HDR, "12")
				.withHeader(CORRELATIONID_HDR, "1234")
				.withHeader(MESSAGEID_HDR, "12345")
				.withHeader(ORDERID_HDR, "123456")
				.withRestTemplate(restTemplate)
				.withRequestLoggingEnabled(false)
				.withResponseLoggingEnabled(false)
				.build();
		
		@SuppressWarnings("unchecked")
		RestClientImpl<String,Object> restClient=(RestClientImpl<String,Object>) mock(RestClientImpl.class);
		ReflectionTestUtils.setField(restClient, "isClientRequestLoggingEnabled", true);
		ReflectionTestUtils.setField(restClient, "isClientResponseLoggingEnabled", true);
		try {
			Field fieldTracer = restClient.getClass().getSuperclass().getDeclaredField("applicationContext");
			fieldTracer.setAccessible(true);
			fieldTracer.set(restClient, applicationContext);

			Field fieldSpanCustomizer = restClient.getClass().getSuperclass().getDeclaredField("jsonUtils");
			fieldSpanCustomizer.setAccessible(true);
			fieldSpanCustomizer.set(restClient, jsonUtils);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Mockito.doReturn(restTemplate).when(applicationContext).getBean(BEAN_ID_DEFAULT_REST_TEMPLATE, RestTemplate.class);
		ResponseEntity<String> responseEntity = new ResponseEntity<>("", HttpStatus.OK);
		Mockito.when(restTemplate.exchange(ArgumentMatchers.any(), ArgumentMatchers.<Class<String>>any())).thenReturn(responseEntity);
		Mockito.doCallRealMethod().doReturn(new LogMessageBuilder()).when(restClient).getLogMessageBuilder(ArgumentMatchers.any());
		
		RestClientResponse<Object> clientResponse=new RestClientResponse<Object>();
		clientResponse.setBody("some value");
		clientResponse.setStatus(HttpStatus.OK);
				
		Mockito.doCallRealMethod().when(restClient).execute(clientRequest, Object.class);
		
		RestClientResponse<Object> result=restClient.execute(clientRequest, Object.class);
		
		Assert.assertEquals(HttpStatus.OK, result.getStatus());
	}
	
	@Test 
	public void testExecuteAllLoggingDisabled()
	{
		RestClientRequest<String> clientRequest=RestClientRequest
				.<String>builder().withUrl(baseUri+"/orders/check")
				.withBody("{\"name\":\"bob\"}")
				.withMethod(HttpMethod.POST.name())
				.withHeader(CLIENTID_HDR, "12")
				.withHeader(CORRELATIONID_HDR, "1234")
				.withHeader(MESSAGEID_HDR, "12345")
				.withHeader(ORDERID_HDR, "123456")
				.withRestTemplate(restTemplate)
				.withRequestLoggingEnabled(false)
				.withResponseLoggingEnabled(false)
				.build();
		
		@SuppressWarnings("unchecked")
		RestClientImpl<String,Object> restClient=(RestClientImpl<String,Object>) mock(RestClientImpl.class);
		ReflectionTestUtils.setField(restClient, "isClientRequestLoggingEnabled", true);
		ReflectionTestUtils.setField(restClient, "isClientResponseLoggingEnabled", true);
		try {
			Field fieldTracer = restClient.getClass().getSuperclass().getDeclaredField("applicationContext");
			fieldTracer.setAccessible(true);
			fieldTracer.set(restClient, applicationContext);

			Field fieldSpanCustomizer = restClient.getClass().getSuperclass().getDeclaredField("jsonUtils");
			fieldSpanCustomizer.setAccessible(true);
			fieldSpanCustomizer.set(restClient, jsonUtils);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Mockito.doReturn(restTemplate).when(applicationContext).getBean(BEAN_ID_DEFAULT_REST_TEMPLATE, RestTemplate.class);
		ResponseEntity<String> responseEntity = new ResponseEntity<>("", HttpStatus.OK);
		Mockito.when(restTemplate.exchange(ArgumentMatchers.any(), ArgumentMatchers.<Class<String>>any())).thenReturn(responseEntity);
		Mockito.doCallRealMethod().doReturn(new LogMessageBuilder()).when(restClient).getLogMessageBuilder(ArgumentMatchers.any());
		
		RestClientResponse<Object> clientResponse=new RestClientResponse<Object>();
		clientResponse.setBody("some value");
		clientResponse.setStatus(HttpStatus.OK);
				
		Mockito.doCallRealMethod().when(restClient).execute(clientRequest, Object.class);
		
		RestClientResponse<Object> result=restClient.execute(clientRequest, Object.class);
		
		Assert.assertEquals(HttpStatus.OK, result.getStatus());
	}



}
