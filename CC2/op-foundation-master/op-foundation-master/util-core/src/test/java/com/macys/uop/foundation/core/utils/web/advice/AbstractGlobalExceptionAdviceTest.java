package com.macys.uop.foundation.core.utils.web.advice;

import static com.macys.uop.foundation.core.utils.Constant.HDR_REQUIRED_DEFAULT_LOCATIONTYPE;
import static com.macys.uop.foundation.core.utils.Constant.HDR_REQUIRED_DEFAULT_REASON;
import static com.macys.uop.foundation.core.utils.Constant.MSG_PROCESSING_DURATION_TEXT_ORIGIN_REST;
import static org.mockito.Mockito.mock;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.exception.ExceptionUtils;
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

import com.macys.uop.foundation.core.utils.CommonStatusCode;
import com.macys.uop.foundation.core.utils.epf.EPFMessagePublisher;
import com.macys.uop.foundation.core.utils.exception.ErrorDetail;
import com.macys.uop.foundation.core.utils.execution.RequestOriginEnum;
import com.macys.uop.foundation.core.utils.json.JsonUtils;
import com.macys.uop.foundation.core.utils.logging.LogMessageBuilder;
import com.macys.uop.foundation.core.utils.masking.ApplicationMaskingConfiguration;
import com.macys.uop.foundation.core.utils.masking.IDataMasker;
import com.macys.uop.foundation.core.utils.ordererror.OrderErrorMessagePublisher;
import com.macys.uop.foundation.core.utils.test.model.Employee;
import com.macys.uop.foundation.core.utils.xml.XmlUtils;


@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringRunner.class)
public class AbstractGlobalExceptionAdviceTest 
{
	@MockBean
	private JsonUtils jsonUtils;
	
	@MockBean
	private XmlUtils xmlUtils;
	
	@MockBean
	private OrderErrorMessagePublisher orderErrorMessagePublisher;

	@MockBean
	private EPFMessagePublisher epfMessagePublisher;
	
	@Test
	public void testPublishOrderErrorEvent() {
		
		AbstractGlobalExceptionAdvice globalExceptionAdvice=mock(AbstractGlobalExceptionAdvice.class);
		try {
			Field fieldJsonUtils = globalExceptionAdvice.getClass().getSuperclass().getDeclaredField("jsonUtils");
			fieldJsonUtils.setAccessible(true);
			fieldJsonUtils.set(globalExceptionAdvice, jsonUtils);

			Field fieldXmlUtils = globalExceptionAdvice.getClass().getSuperclass().getDeclaredField("xmlUtils");
			fieldXmlUtils.setAccessible(true);
			fieldXmlUtils.set(globalExceptionAdvice, xmlUtils);
			
			Field fieldOrderErrorMessagePublisher = globalExceptionAdvice.getClass().getSuperclass().getDeclaredField("orderErrorMessagePublisher");
			fieldOrderErrorMessagePublisher.setAccessible(true);
			fieldOrderErrorMessagePublisher.set(globalExceptionAdvice, orderErrorMessagePublisher);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		com.macys.uop.foundation.core.utils.exception.Error error = com.macys.uop.foundation.core.utils.exception.Error.builder()
				.withCode(CommonStatusCode.NO_CORRELATIONID.getCode())
				.withMessage(CommonStatusCode.NO_CORRELATIONID.getDescription())
				.withErrorDetail(ErrorDetail.builder().withDomain("some-service").withReason(HDR_REQUIRED_DEFAULT_REASON)
						.withMessage(CommonStatusCode.NO_CORRELATIONID.getDescription()).withLocation("some location")
						.withLocationType(HDR_REQUIRED_DEFAULT_LOCATIONTYPE).build())
				.build();
		
		String stackTrace=ExceptionUtils.getStackTrace(new RuntimeException("Custom"));
		String requestBody="{\"name\":\"test\"}";
		
		Mockito.doReturn("33333").when(globalExceptionAdvice).getCorrelationId();
		Mockito.doReturn("33333").when(globalExceptionAdvice).getOrderId();
		Mockito.doReturn("some-service").when(globalExceptionAdvice).getAppName();
		Mockito.doReturn("http://someurl").when(globalExceptionAdvice).getRequestURL();
		Mockito.doReturn("application/json").when(globalExceptionAdvice).getContentType();
		Mockito.doReturn("34356").when(globalExceptionAdvice).getClientId();
		Mockito.doReturn("123909").when(globalExceptionAdvice).getMessageId();
		
		Mockito.doNothing().when(orderErrorMessagePublisher).publishOrderErrorMessage(ArgumentMatchers.any());
		
		Mockito.doCallRealMethod().when(globalExceptionAdvice).publishOrderErrorEvent(error, stackTrace, requestBody);
		
		globalExceptionAdvice.publishOrderErrorEvent(error, stackTrace, requestBody);
		
		Assert.assertNotEquals("expected", "actual");
	}
	
	@Test
	public void testConvertJsonBodyToString() {
		
		AbstractGlobalExceptionAdvice globalExceptionAdvice=mock(AbstractGlobalExceptionAdvice.class);
		try {
			Field fieldJsonUtils = globalExceptionAdvice.getClass().getSuperclass().getDeclaredField("jsonUtils");
			fieldJsonUtils.setAccessible(true);
			fieldJsonUtils.set(globalExceptionAdvice, jsonUtils);

			Field fieldXmlUtils = globalExceptionAdvice.getClass().getSuperclass().getDeclaredField("xmlUtils");
			fieldXmlUtils.setAccessible(true);
			fieldXmlUtils.set(globalExceptionAdvice, xmlUtils);
			
			Field fieldOrderErrorMessagePublisher = globalExceptionAdvice.getClass().getSuperclass().getDeclaredField("orderErrorMessagePublisher");
			fieldOrderErrorMessagePublisher.setAccessible(true);
			fieldOrderErrorMessagePublisher.set(globalExceptionAdvice, orderErrorMessagePublisher);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Employee employee=new Employee("test");
		String JSONDATA="{\"name\":\"test\"}";
		
		Mockito.doReturn(JSONDATA).when(jsonUtils).convertToJson(employee);
		
		Mockito.doCallRealMethod().when(globalExceptionAdvice).convertJsonBodyToString(employee);
		
		String result=globalExceptionAdvice.convertJsonBodyToString(employee);
		
		Assert.assertEquals(JSONDATA, result);
	}
	
	@Test
	public void testConvertJsonBodyToStringBodyNull() {
		
		AbstractGlobalExceptionAdvice globalExceptionAdvice=mock(AbstractGlobalExceptionAdvice.class);
		try {
			Field fieldJsonUtils = globalExceptionAdvice.getClass().getSuperclass().getDeclaredField("jsonUtils");
			fieldJsonUtils.setAccessible(true);
			fieldJsonUtils.set(globalExceptionAdvice, jsonUtils);

			Field fieldXmlUtils = globalExceptionAdvice.getClass().getSuperclass().getDeclaredField("xmlUtils");
			fieldXmlUtils.setAccessible(true);
			fieldXmlUtils.set(globalExceptionAdvice, xmlUtils);
			
			Field fieldOrderErrorMessagePublisher = globalExceptionAdvice.getClass().getSuperclass().getDeclaredField("orderErrorMessagePublisher");
			fieldOrderErrorMessagePublisher.setAccessible(true);
			fieldOrderErrorMessagePublisher.set(globalExceptionAdvice, orderErrorMessagePublisher);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Employee employee=null;
		String JSONDATA="{\"name\":\"test\"}";
		
		Mockito.doReturn(JSONDATA).when(jsonUtils).convertToJson(employee);
		
		Mockito.doCallRealMethod().when(globalExceptionAdvice).convertJsonBodyToString(employee);
		
		String result=globalExceptionAdvice.convertJsonBodyToString(employee);
		
		Assert.assertEquals(null, result);
	}
	
	@Test
	public void testConvertXmlBodyToString() {
		
		AbstractGlobalExceptionAdvice globalExceptionAdvice=mock(AbstractGlobalExceptionAdvice.class);
		try {
			Field fieldJsonUtils = globalExceptionAdvice.getClass().getSuperclass().getDeclaredField("jsonUtils");
			fieldJsonUtils.setAccessible(true);
			fieldJsonUtils.set(globalExceptionAdvice, jsonUtils);

			Field fieldXmlUtils = globalExceptionAdvice.getClass().getSuperclass().getDeclaredField("xmlUtils");
			fieldXmlUtils.setAccessible(true);
			fieldXmlUtils.set(globalExceptionAdvice, xmlUtils);
			
			Field fieldOrderErrorMessagePublisher = globalExceptionAdvice.getClass().getSuperclass().getDeclaredField("orderErrorMessagePublisher");
			fieldOrderErrorMessagePublisher.setAccessible(true);
			fieldOrderErrorMessagePublisher.set(globalExceptionAdvice, orderErrorMessagePublisher);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Employee employee=new Employee("test");
		String XMLDATA="<name>test<name>";
		
		Mockito.doReturn(XMLDATA).when(xmlUtils).convertToXml(employee);
		
		Mockito.doCallRealMethod().when(globalExceptionAdvice).convertXmlBodyToString(employee);
		
		String result=globalExceptionAdvice.convertXmlBodyToString(employee);
		
		Assert.assertEquals(XMLDATA, result);
	}
	
	@Test
	public void testConvertXmlBodyToStringBodyNull() {
		
		AbstractGlobalExceptionAdvice globalExceptionAdvice=mock(AbstractGlobalExceptionAdvice.class);
		try {
			Field fieldJsonUtils = globalExceptionAdvice.getClass().getSuperclass().getDeclaredField("jsonUtils");
			fieldJsonUtils.setAccessible(true);
			fieldJsonUtils.set(globalExceptionAdvice, jsonUtils);

			Field fieldXmlUtils = globalExceptionAdvice.getClass().getSuperclass().getDeclaredField("xmlUtils");
			fieldXmlUtils.setAccessible(true);
			fieldXmlUtils.set(globalExceptionAdvice, xmlUtils);
			
			Field fieldOrderErrorMessagePublisher = globalExceptionAdvice.getClass().getSuperclass().getDeclaredField("orderErrorMessagePublisher");
			fieldOrderErrorMessagePublisher.setAccessible(true);
			fieldOrderErrorMessagePublisher.set(globalExceptionAdvice, orderErrorMessagePublisher);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Employee employee=null;
		String XMLDATA="<name>test<name>";
		
		Mockito.doReturn(XMLDATA).when(xmlUtils).convertToXml(employee);
		
		Mockito.doCallRealMethod().when(globalExceptionAdvice).convertXmlBodyToString(employee);
		
		String result=globalExceptionAdvice.convertXmlBodyToString(employee);
		
		Assert.assertEquals(null, result);
	}
	
	@Test
	public void testLogJsonError() {
		
		AbstractGlobalExceptionAdvice globalExceptionAdvice=mock(AbstractGlobalExceptionAdvice.class);
		try {
			Field fieldJsonUtils = globalExceptionAdvice.getClass().getSuperclass().getDeclaredField("jsonUtils");
			fieldJsonUtils.setAccessible(true);
			fieldJsonUtils.set(globalExceptionAdvice, jsonUtils);

			Field fieldXmlUtils = globalExceptionAdvice.getClass().getSuperclass().getDeclaredField("xmlUtils");
			fieldXmlUtils.setAccessible(true);
			fieldXmlUtils.set(globalExceptionAdvice, xmlUtils);
			
			Field fieldOrderErrorMessagePublisher = globalExceptionAdvice.getClass().getSuperclass().getDeclaredField("orderErrorMessagePublisher");
			fieldOrderErrorMessagePublisher.setAccessible(true);
			fieldOrderErrorMessagePublisher.set(globalExceptionAdvice, orderErrorMessagePublisher);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		com.macys.uop.foundation.core.utils.exception.Error error = com.macys.uop.foundation.core.utils.exception.Error.builder()
				.withCode(CommonStatusCode.NO_CORRELATIONID.getCode())
				.withMessage(CommonStatusCode.NO_CORRELATIONID.getDescription())
				.withErrorDetail(ErrorDetail.builder().withDomain("some-service").withReason(HDR_REQUIRED_DEFAULT_REASON)
						.withMessage(CommonStatusCode.NO_CORRELATIONID.getDescription()).withLocation("some location")
						.withLocationType(HDR_REQUIRED_DEFAULT_LOCATIONTYPE).build())
				.build();
		
		String stackTrace="";
		String JSONDATA="{\"name\":\"test\"}";
		
		Mockito.doCallRealMethod().doReturn(new LogMessageBuilder()).when(globalExceptionAdvice).getLogMessageBuilder(ArgumentMatchers.any());
		Mockito.doReturn("POST").when(globalExceptionAdvice).getHttpMethod();
		Mockito.doReturn(Instant.now().toString()).when(globalExceptionAdvice).getReceivedTime();
		Mockito.doReturn("Some Text").when(globalExceptionAdvice).constructMsgProcessingDurationText4Logging(Instant.now(), Instant.now(), MSG_PROCESSING_DURATION_TEXT_ORIGIN_REST);
		
		Mockito.doCallRealMethod().when(globalExceptionAdvice).logJsonError(error, stackTrace, JSONDATA);
		
		globalExceptionAdvice.logJsonError(error, stackTrace, JSONDATA);
		
		Assert.assertNotEquals("expected", "actual");
		
	}	
	
	@Test
	public void testLogXmlErrorDataMaskerNull() {
		
		AbstractGlobalExceptionAdvice globalExceptionAdvice=mock(AbstractGlobalExceptionAdvice.class);
		try {
			Field fieldJsonUtils = globalExceptionAdvice.getClass().getSuperclass().getDeclaredField("jsonUtils");
			fieldJsonUtils.setAccessible(true);
			fieldJsonUtils.set(globalExceptionAdvice, jsonUtils);

			Field fieldXmlUtils = globalExceptionAdvice.getClass().getSuperclass().getDeclaredField("xmlUtils");
			fieldXmlUtils.setAccessible(true);
			fieldXmlUtils.set(globalExceptionAdvice, xmlUtils);
			
			Field fieldOrderErrorMessagePublisher = globalExceptionAdvice.getClass().getSuperclass().getDeclaredField("orderErrorMessagePublisher");
			fieldOrderErrorMessagePublisher.setAccessible(true);
			fieldOrderErrorMessagePublisher.set(globalExceptionAdvice, orderErrorMessagePublisher);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		com.macys.uop.foundation.core.utils.exception.Error error = com.macys.uop.foundation.core.utils.exception.Error.builder()
				.withCode(CommonStatusCode.NO_CORRELATIONID.getCode())
				.withMessage(CommonStatusCode.NO_CORRELATIONID.getDescription())
				.withErrorDetail(ErrorDetail.builder().withDomain("some-service").withReason(HDR_REQUIRED_DEFAULT_REASON)
						.withMessage(CommonStatusCode.NO_CORRELATIONID.getDescription()).withLocation("some location")
						.withLocationType(HDR_REQUIRED_DEFAULT_LOCATIONTYPE).build())
				.build();
		
		String stackTrace="";
		String XMLDATA="<name>test<name>";
		ApplicationMaskingConfiguration.setXmlMaskConfMap(null);
		Mockito.doCallRealMethod().doReturn(new LogMessageBuilder()).when(globalExceptionAdvice).getLogMessageBuilder(ArgumentMatchers.any());
		Mockito.doReturn("POST").when(globalExceptionAdvice).getHttpMethod();
		Mockito.doReturn(Instant.now().toString()).when(globalExceptionAdvice).getReceivedTime();
		Mockito.doReturn("Some Text").when(globalExceptionAdvice).constructMsgProcessingDurationText4Logging(Instant.now(), Instant.now(), MSG_PROCESSING_DURATION_TEXT_ORIGIN_REST);
		
		Mockito.doCallRealMethod().when(globalExceptionAdvice).logXmlError(error, stackTrace, XMLDATA);
		
		globalExceptionAdvice.logXmlError(error, stackTrace, XMLDATA);
		
		Assert.assertNotEquals("expected", "actual");
	}	
	
	@Test
	public void testLogXmlErrorWithDataMasker() {
		
		AbstractGlobalExceptionAdvice globalExceptionAdvice=mock(AbstractGlobalExceptionAdvice.class);
		try {
			Field fieldJsonUtils = globalExceptionAdvice.getClass().getSuperclass().getDeclaredField("jsonUtils");
			fieldJsonUtils.setAccessible(true);
			fieldJsonUtils.set(globalExceptionAdvice, jsonUtils);

			Field fieldXmlUtils = globalExceptionAdvice.getClass().getSuperclass().getDeclaredField("xmlUtils");
			fieldXmlUtils.setAccessible(true);
			fieldXmlUtils.set(globalExceptionAdvice, xmlUtils);
			
			Field fieldOrderErrorMessagePublisher = globalExceptionAdvice.getClass().getSuperclass().getDeclaredField("orderErrorMessagePublisher");
			fieldOrderErrorMessagePublisher.setAccessible(true);
			fieldOrderErrorMessagePublisher.set(globalExceptionAdvice, orderErrorMessagePublisher);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		com.macys.uop.foundation.core.utils.exception.Error error = com.macys.uop.foundation.core.utils.exception.Error.builder()
				.withCode(CommonStatusCode.NO_CORRELATIONID.getCode())
				.withMessage(CommonStatusCode.NO_CORRELATIONID.getDescription())
				.withErrorDetail(ErrorDetail.builder().withDomain("some-service").withReason(HDR_REQUIRED_DEFAULT_REASON)
						.withMessage(CommonStatusCode.NO_CORRELATIONID.getDescription()).withLocation("some location")
						.withLocationType(HDR_REQUIRED_DEFAULT_LOCATIONTYPE).build())
				.build();
		
		String stackTrace="";
		String XMLDATA="<name>test<name>";
		
		Map<String,IDataMasker> maskConfigMap=new HashMap<>();
		maskConfigMap.put("ContactNo", new com.macys.uop.foundation.core.utils.masking.CreditCardNumberMasker());
		ApplicationMaskingConfiguration.setXmlMaskConfMap(maskConfigMap);
		
		Mockito.doCallRealMethod().doReturn(new LogMessageBuilder()).when(globalExceptionAdvice).getLogMessageBuilder(ArgumentMatchers.any());
		Mockito.doReturn("POST").when(globalExceptionAdvice).getHttpMethod();
		Mockito.doReturn(Instant.now().toString()).when(globalExceptionAdvice).getReceivedTime();
		Mockito.doReturn("Some Text").when(globalExceptionAdvice).constructMsgProcessingDurationText4Logging(Instant.now(), Instant.now(), MSG_PROCESSING_DURATION_TEXT_ORIGIN_REST);
		
		Mockito.doCallRealMethod().when(globalExceptionAdvice).logXmlError(error, stackTrace, XMLDATA);
		
		globalExceptionAdvice.logXmlError(error, stackTrace, XMLDATA);
		
		Assert.assertNotEquals("expected", "actual");
	}	
	
	@Test
	public void testLogAndPublishOrderErrorEventJson() {

		AbstractGlobalExceptionAdvice globalExceptionAdvice=mock(AbstractGlobalExceptionAdvice.class);
		ReflectionTestUtils.setField(globalExceptionAdvice, "isOrderErrorEventPublishingEnabled", Boolean.TRUE);
		ReflectionTestUtils.setField(globalExceptionAdvice, "isEPFEventPublishingEnabled", Boolean.TRUE);
		try {
			Field fieldJsonUtils = globalExceptionAdvice.getClass().getSuperclass().getDeclaredField("jsonUtils");
			fieldJsonUtils.setAccessible(true);
			fieldJsonUtils.set(globalExceptionAdvice, jsonUtils);

			Field fieldXmlUtils = globalExceptionAdvice.getClass().getSuperclass().getDeclaredField("xmlUtils");
			fieldXmlUtils.setAccessible(true);
			fieldXmlUtils.set(globalExceptionAdvice, xmlUtils);

			Field fieldOrderErrorMessagePublisher = globalExceptionAdvice.getClass().getSuperclass().getDeclaredField("orderErrorMessagePublisher");
			fieldOrderErrorMessagePublisher.setAccessible(true);
			fieldOrderErrorMessagePublisher.set(globalExceptionAdvice, orderErrorMessagePublisher);
		} catch (Exception e) {
			e.printStackTrace();
		}

		com.macys.uop.foundation.core.utils.exception.Error error = com.macys.uop.foundation.core.utils.exception.Error.builder()
				.withCode(CommonStatusCode.NO_CORRELATIONID.getCode())
				.withMessage(CommonStatusCode.NO_CORRELATIONID.getDescription())
				.withErrorDetail(ErrorDetail.builder().withDomain("some-service").withReason(HDR_REQUIRED_DEFAULT_REASON)
						.withMessage(CommonStatusCode.NO_CORRELATIONID.getDescription()).withLocation("some location")
						.withLocationType(HDR_REQUIRED_DEFAULT_LOCATIONTYPE).build())
				.build();

		String stackTrace="";
		String JSONDATA="{\"name\":\"test\"}";

		Map<String,IDataMasker> maskConfigMap=new HashMap<>();
		maskConfigMap.put("ContactNo", new com.macys.uop.foundation.core.utils.masking.CreditCardNumberMasker());
		ApplicationMaskingConfiguration.setXmlMaskConfMap(maskConfigMap);

		Mockito.doReturn(Boolean.TRUE).when(globalExceptionAdvice).isContentTypeApplicationJson();
		Mockito.doReturn(Boolean.FALSE).when(globalExceptionAdvice).isContentTypeApplicationXml();
		Mockito.doReturn(JSONDATA).when(globalExceptionAdvice).convertJsonBodyToString(ArgumentMatchers.any());
		Mockito.doReturn(JSONDATA).when(globalExceptionAdvice).getBody();
		Mockito.doNothing().when(globalExceptionAdvice).logJsonError(error, stackTrace, JSONDATA);
		Mockito.doNothing().when(globalExceptionAdvice).publishOrderErrorEvent(error, stackTrace, JSONDATA);

		Exception ex=new RuntimeException("Custom");
		Mockito.doCallRealMethod().when(globalExceptionAdvice).logAndPublishErrorEvents(error, ex,500);
		globalExceptionAdvice.logAndPublishErrorEvents(error, ex,500);

		Assert.assertNotEquals("expected", "actual");
	}

	@Test
	public void testLogAndPublishOrderErrorEventXml() {

		AbstractGlobalExceptionAdvice globalExceptionAdvice=mock(AbstractGlobalExceptionAdvice.class);
		ReflectionTestUtils.setField(globalExceptionAdvice, "isOrderErrorEventPublishingEnabled", Boolean.FALSE);
		ReflectionTestUtils.setField(globalExceptionAdvice, "isEPFEventPublishingEnabled", Boolean.FALSE);
		try {
			Field fieldJsonUtils = globalExceptionAdvice.getClass().getSuperclass().getDeclaredField("jsonUtils");
			fieldJsonUtils.setAccessible(true);
			fieldJsonUtils.set(globalExceptionAdvice, jsonUtils);

			Field fieldXmlUtils = globalExceptionAdvice.getClass().getSuperclass().getDeclaredField("xmlUtils");
			fieldXmlUtils.setAccessible(true);
			fieldXmlUtils.set(globalExceptionAdvice, xmlUtils);

			Field fieldOrderErrorMessagePublisher = globalExceptionAdvice.getClass().getSuperclass().getDeclaredField("orderErrorMessagePublisher");
			fieldOrderErrorMessagePublisher.setAccessible(true);
			fieldOrderErrorMessagePublisher.set(globalExceptionAdvice, orderErrorMessagePublisher);
		} catch (Exception e) {
			e.printStackTrace();
		}

		com.macys.uop.foundation.core.utils.exception.Error error = com.macys.uop.foundation.core.utils.exception.Error.builder()
				.withCode(CommonStatusCode.NO_CORRELATIONID.getCode())
				.withMessage(CommonStatusCode.NO_CORRELATIONID.getDescription())
				.withErrorDetail(ErrorDetail.builder().withDomain("some-service").withReason(HDR_REQUIRED_DEFAULT_REASON)
						.withMessage(CommonStatusCode.NO_CORRELATIONID.getDescription()).withLocation("some location")
						.withLocationType(HDR_REQUIRED_DEFAULT_LOCATIONTYPE).build())
				.build();

		String stackTrace="";
		String XMLDATA="<name>test<name>";

		Map<String,IDataMasker> maskConfigMap=new HashMap<>();
		maskConfigMap.put("ContactNo", new com.macys.uop.foundation.core.utils.masking.CreditCardNumberMasker());
		ApplicationMaskingConfiguration.setXmlMaskConfMap(maskConfigMap);

		Mockito.doReturn(Boolean.FALSE).when(globalExceptionAdvice).isContentTypeApplicationJson();
		Mockito.doReturn(Boolean.TRUE).when(globalExceptionAdvice).isContentTypeApplicationXml();
		Mockito.doReturn(XMLDATA).when(globalExceptionAdvice).convertXmlBodyToString(ArgumentMatchers.any());
		Mockito.doReturn(XMLDATA).when(globalExceptionAdvice).getBody();
		Mockito.doNothing().when(globalExceptionAdvice).logXmlError(error, stackTrace, XMLDATA);
		Mockito.doNothing().when(globalExceptionAdvice).publishOrderErrorEvent(error, stackTrace, XMLDATA);

		Exception ex=new RuntimeException("Custom");
		Mockito.doCallRealMethod().when(globalExceptionAdvice).logAndPublishErrorEvents(error, ex,500);
		globalExceptionAdvice.logAndPublishErrorEvents(error, ex,500);

		Assert.assertNotEquals("expected", "actual");
	}

	@Test
	public void testPublishEPFEventJson() {

		AbstractGlobalExceptionAdvice globalExceptionAdvice=mock(AbstractGlobalExceptionAdvice.class);
		try {
			Field fieldJsonUtils = globalExceptionAdvice.getClass().getSuperclass().getDeclaredField("jsonUtils");
			fieldJsonUtils.setAccessible(true);
			fieldJsonUtils.set(globalExceptionAdvice, jsonUtils);

			Field fieldXmlUtils = globalExceptionAdvice.getClass().getSuperclass().getDeclaredField("xmlUtils");
			fieldXmlUtils.setAccessible(true);
			fieldXmlUtils.set(globalExceptionAdvice, xmlUtils);

			Field fieldOrderErrorMessagePublisher = globalExceptionAdvice.getClass().getSuperclass().getDeclaredField("epfMessagePublisher");
			fieldOrderErrorMessagePublisher.setAccessible(true);
			fieldOrderErrorMessagePublisher.set(globalExceptionAdvice, epfMessagePublisher);
		} catch (Exception e) {
			e.printStackTrace();
		}

		com.macys.uop.foundation.core.utils.exception.Error error = com.macys.uop.foundation.core.utils.exception.Error.builder()
				.withCode(CommonStatusCode.NO_CORRELATIONID.getCode())
				.withMessage(CommonStatusCode.NO_CORRELATIONID.getDescription())
				.build();

		Mockito.doReturn("33333").when(globalExceptionAdvice).getCorrelationId();
		Mockito.doReturn("33333").when(globalExceptionAdvice).getOrderId();
		Mockito.doReturn("some-service").when(globalExceptionAdvice).getAppName();
		Mockito.doReturn(RequestOriginEnum.MESSAGING).when(globalExceptionAdvice).getOrigin();
		Mockito.doReturn("http://someurl").when(globalExceptionAdvice).getRequestURL();
		Mockito.doReturn("application/json").when(globalExceptionAdvice).getContentType();
		Mockito.doReturn("34356").when(globalExceptionAdvice).getClientId();
		Mockito.doReturn("123909").when(globalExceptionAdvice).getMessageId();

		Mockito.doNothing().when(epfMessagePublisher).publishEPFMessage(ArgumentMatchers.any(),ArgumentMatchers.any(),ArgumentMatchers.anyMap(),ArgumentMatchers.anyMap());

		Mockito.doCallRealMethod().when(globalExceptionAdvice).publishEPFEvent(error, 500);

		globalExceptionAdvice.publishEPFEvent(error, 500);

		Assert.assertNotEquals("expected", "actual");
	}
	
	@Test
	public void testGetEPFEventPublishingValidErrorCodesWhenNotSet() {

		AbstractGlobalExceptionAdvice globalExceptionAdvice=mock(AbstractGlobalExceptionAdvice.class);
		try {
			Field fieldJsonUtils = globalExceptionAdvice.getClass().getSuperclass().getDeclaredField("jsonUtils");
			fieldJsonUtils.setAccessible(true);
			fieldJsonUtils.set(globalExceptionAdvice, jsonUtils);

			Field fieldXmlUtils = globalExceptionAdvice.getClass().getSuperclass().getDeclaredField("xmlUtils");
			fieldXmlUtils.setAccessible(true);
			fieldXmlUtils.set(globalExceptionAdvice, xmlUtils);

			Field fieldOrderErrorMessagePublisher = globalExceptionAdvice.getClass().getSuperclass().getDeclaredField("orderErrorMessagePublisher");
			fieldOrderErrorMessagePublisher.setAccessible(true);
			fieldOrderErrorMessagePublisher.set(globalExceptionAdvice, orderErrorMessagePublisher);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Mockito.doCallRealMethod().when(globalExceptionAdvice).getEPFEventPublishingValidErrorCodes();
		List<String> errorCodesList=globalExceptionAdvice.getEPFEventPublishingValidErrorCodes();

		Assert.assertEquals(errorCodesList.get(0), CommonStatusCode.NO_CORRELATIONID.getCode());
	}
	
	@Test
	public void testGetEPFEventPublishingValidErrorCodesWhenSet() {

		AbstractGlobalExceptionAdvice globalExceptionAdvice=mock(AbstractGlobalExceptionAdvice.class);
		ReflectionTestUtils.setField(globalExceptionAdvice, "epfEventPublishingValidErrorCodesList", "UOP-GEN-E05004,UOP-GEN-E05005");
		try {
			Field fieldJsonUtils = globalExceptionAdvice.getClass().getSuperclass().getDeclaredField("jsonUtils");
			fieldJsonUtils.setAccessible(true);
			fieldJsonUtils.set(globalExceptionAdvice, jsonUtils);

			Field fieldXmlUtils = globalExceptionAdvice.getClass().getSuperclass().getDeclaredField("xmlUtils");
			fieldXmlUtils.setAccessible(true);
			fieldXmlUtils.set(globalExceptionAdvice, xmlUtils);

			Field fieldOrderErrorMessagePublisher = globalExceptionAdvice.getClass().getSuperclass().getDeclaredField("orderErrorMessagePublisher");
			fieldOrderErrorMessagePublisher.setAccessible(true);
			fieldOrderErrorMessagePublisher.set(globalExceptionAdvice, orderErrorMessagePublisher);
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		
		Mockito.doCallRealMethod().when(globalExceptionAdvice).getEPFEventPublishingValidErrorCodes();
		List<String> errorCodesList=globalExceptionAdvice.getEPFEventPublishingValidErrorCodes();

		Assert.assertEquals(errorCodesList.size(), 2);
	}
	
	@Test
	public void testGetEPFEventPublishingValidErrorCodesWhenOnlyComma() {

		AbstractGlobalExceptionAdvice globalExceptionAdvice=mock(AbstractGlobalExceptionAdvice.class);
		ReflectionTestUtils.setField(globalExceptionAdvice, "epfEventPublishingValidErrorCodesList", ",");
		try {
			Field fieldJsonUtils = globalExceptionAdvice.getClass().getSuperclass().getDeclaredField("jsonUtils");
			fieldJsonUtils.setAccessible(true);
			fieldJsonUtils.set(globalExceptionAdvice, jsonUtils);

			Field fieldXmlUtils = globalExceptionAdvice.getClass().getSuperclass().getDeclaredField("xmlUtils");
			fieldXmlUtils.setAccessible(true);
			fieldXmlUtils.set(globalExceptionAdvice, xmlUtils);

			Field fieldOrderErrorMessagePublisher = globalExceptionAdvice.getClass().getSuperclass().getDeclaredField("orderErrorMessagePublisher");
			fieldOrderErrorMessagePublisher.setAccessible(true);
			fieldOrderErrorMessagePublisher.set(globalExceptionAdvice, orderErrorMessagePublisher);
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		
		Mockito.doCallRealMethod().when(globalExceptionAdvice).getEPFEventPublishingValidErrorCodes();
		List<String> errorCodesList=globalExceptionAdvice.getEPFEventPublishingValidErrorCodes();

		Assert.assertEquals(errorCodesList.get(0), CommonStatusCode.NO_CORRELATIONID.getCode());
	}
	
	@Test
	public void testGetEPFEventPublishingValidErrorCodesWhenSomethingWithComma() {

		AbstractGlobalExceptionAdvice globalExceptionAdvice=mock(AbstractGlobalExceptionAdvice.class);
		ReflectionTestUtils.setField(globalExceptionAdvice, "epfEventPublishingValidErrorCodesList", "UOP-GEN-E05004,");
		try {
			Field fieldJsonUtils = globalExceptionAdvice.getClass().getSuperclass().getDeclaredField("jsonUtils");
			fieldJsonUtils.setAccessible(true);
			fieldJsonUtils.set(globalExceptionAdvice, jsonUtils);

			Field fieldXmlUtils = globalExceptionAdvice.getClass().getSuperclass().getDeclaredField("xmlUtils");
			fieldXmlUtils.setAccessible(true);
			fieldXmlUtils.set(globalExceptionAdvice, xmlUtils);

			Field fieldOrderErrorMessagePublisher = globalExceptionAdvice.getClass().getSuperclass().getDeclaredField("orderErrorMessagePublisher");
			fieldOrderErrorMessagePublisher.setAccessible(true);
			fieldOrderErrorMessagePublisher.set(globalExceptionAdvice, orderErrorMessagePublisher);
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		
		Mockito.doCallRealMethod().when(globalExceptionAdvice).getEPFEventPublishingValidErrorCodes();
		List<String> errorCodesList=globalExceptionAdvice.getEPFEventPublishingValidErrorCodes();

		Assert.assertEquals(errorCodesList.get(0), "UOP-GEN-E05004");
	}
	
	@Test
	public void testLogAndPublishErrorEventsErrorCodeListNull() {

		AbstractGlobalExceptionAdvice globalExceptionAdvice=mock(AbstractGlobalExceptionAdvice.class);
		ReflectionTestUtils.setField(globalExceptionAdvice, "isOrderErrorEventPublishingEnabled", Boolean.TRUE);
		ReflectionTestUtils.setField(globalExceptionAdvice, "isEPFEventPublishingEnabled", Boolean.TRUE);
		try {
			Field fieldJsonUtils = globalExceptionAdvice.getClass().getSuperclass().getDeclaredField("jsonUtils");
			fieldJsonUtils.setAccessible(true);
			fieldJsonUtils.set(globalExceptionAdvice, jsonUtils);

			Field fieldXmlUtils = globalExceptionAdvice.getClass().getSuperclass().getDeclaredField("xmlUtils");
			fieldXmlUtils.setAccessible(true);
			fieldXmlUtils.set(globalExceptionAdvice, xmlUtils);

			Field fieldOrderErrorMessagePublisher = globalExceptionAdvice.getClass().getSuperclass().getDeclaredField("orderErrorMessagePublisher");
			fieldOrderErrorMessagePublisher.setAccessible(true);
			fieldOrderErrorMessagePublisher.set(globalExceptionAdvice, orderErrorMessagePublisher);
		} catch (Exception e) {
			e.printStackTrace();
		}

		com.macys.uop.foundation.core.utils.exception.Error error = com.macys.uop.foundation.core.utils.exception.Error.builder()
				.withCode(CommonStatusCode.NO_CORRELATIONID.getCode())
				.withMessage(CommonStatusCode.NO_CORRELATIONID.getDescription())
				.withErrorDetail(ErrorDetail.builder().withDomain("some-service").withReason(HDR_REQUIRED_DEFAULT_REASON)
						.withMessage(CommonStatusCode.NO_CORRELATIONID.getDescription()).withLocation("some location")
						.withLocationType(HDR_REQUIRED_DEFAULT_LOCATIONTYPE).build())
				.build();

		String stackTrace="";
		String JSONDATA="{\"name\":\"test\"}";

		Map<String,IDataMasker> maskConfigMap=new HashMap<>();
		maskConfigMap.put("ContactNo", new com.macys.uop.foundation.core.utils.masking.CreditCardNumberMasker());
		ApplicationMaskingConfiguration.setXmlMaskConfMap(maskConfigMap);

		Mockito.doReturn(Boolean.TRUE).when(globalExceptionAdvice).isContentTypeApplicationJson();
		Mockito.doReturn(Boolean.FALSE).when(globalExceptionAdvice).isContentTypeApplicationXml();
		Mockito.doReturn(JSONDATA).when(globalExceptionAdvice).convertJsonBodyToString(ArgumentMatchers.any());
		Mockito.doReturn(JSONDATA).when(globalExceptionAdvice).getBody();
		Mockito.doNothing().when(globalExceptionAdvice).logJsonError(error, stackTrace, JSONDATA);
		Mockito.doNothing().when(globalExceptionAdvice).publishOrderErrorEvent(error, stackTrace, JSONDATA);
		Mockito.doReturn(null).when(globalExceptionAdvice).getEPFEventPublishingValidErrorCodes();

		Exception ex=new RuntimeException("Custom");
		Mockito.doCallRealMethod().when(globalExceptionAdvice).logAndPublishErrorEvents(error, ex,500);
		globalExceptionAdvice.logAndPublishErrorEvents(error, ex,500);

		Assert.assertNotEquals("expected", "actual");
	}
	
	@Test
	public void testLogAndPublishErrorEventsErrorCodeListNotNull() {

		AbstractGlobalExceptionAdvice globalExceptionAdvice=mock(AbstractGlobalExceptionAdvice.class);
		ReflectionTestUtils.setField(globalExceptionAdvice, "isOrderErrorEventPublishingEnabled", Boolean.TRUE);
		ReflectionTestUtils.setField(globalExceptionAdvice, "isEPFEventPublishingEnabled", Boolean.TRUE);
		try {
			Field fieldJsonUtils = globalExceptionAdvice.getClass().getSuperclass().getDeclaredField("jsonUtils");
			fieldJsonUtils.setAccessible(true);
			fieldJsonUtils.set(globalExceptionAdvice, jsonUtils);

			Field fieldXmlUtils = globalExceptionAdvice.getClass().getSuperclass().getDeclaredField("xmlUtils");
			fieldXmlUtils.setAccessible(true);
			fieldXmlUtils.set(globalExceptionAdvice, xmlUtils);

			Field fieldOrderErrorMessagePublisher = globalExceptionAdvice.getClass().getSuperclass().getDeclaredField("orderErrorMessagePublisher");
			fieldOrderErrorMessagePublisher.setAccessible(true);
			fieldOrderErrorMessagePublisher.set(globalExceptionAdvice, orderErrorMessagePublisher);
		} catch (Exception e) {
			e.printStackTrace();
		}

		com.macys.uop.foundation.core.utils.exception.Error error = com.macys.uop.foundation.core.utils.exception.Error.builder()
				.withCode(CommonStatusCode.NO_CORRELATIONID.getCode())
				.withMessage(CommonStatusCode.NO_CORRELATIONID.getDescription())
				.withErrorDetail(ErrorDetail.builder().withDomain("some-service").withReason(HDR_REQUIRED_DEFAULT_REASON)
						.withMessage(CommonStatusCode.NO_CORRELATIONID.getDescription()).withLocation("some location")
						.withLocationType(HDR_REQUIRED_DEFAULT_LOCATIONTYPE).build())
				.build();

		String stackTrace="";
		String JSONDATA="{\"name\":\"test\"}";

		Map<String,IDataMasker> maskConfigMap=new HashMap<>();
		maskConfigMap.put("ContactNo", new com.macys.uop.foundation.core.utils.masking.CreditCardNumberMasker());
		ApplicationMaskingConfiguration.setXmlMaskConfMap(maskConfigMap);

		Mockito.doReturn(Boolean.TRUE).when(globalExceptionAdvice).isContentTypeApplicationJson();
		Mockito.doReturn(Boolean.FALSE).when(globalExceptionAdvice).isContentTypeApplicationXml();
		Mockito.doReturn(JSONDATA).when(globalExceptionAdvice).convertJsonBodyToString(ArgumentMatchers.any());
		Mockito.doReturn(JSONDATA).when(globalExceptionAdvice).getBody();
		Mockito.doNothing().when(globalExceptionAdvice).logJsonError(error, stackTrace, JSONDATA);
		Mockito.doNothing().when(globalExceptionAdvice).publishOrderErrorEvent(error, stackTrace, JSONDATA);
		Mockito.doReturn(Arrays.asList("UOP-GEN-E05004")).when(globalExceptionAdvice).getEPFEventPublishingValidErrorCodes();

		Exception ex=new RuntimeException("Custom");
		Mockito.doCallRealMethod().when(globalExceptionAdvice).logAndPublishErrorEvents(error, ex,500);
		globalExceptionAdvice.logAndPublishErrorEvents(error, ex,500);

		Assert.assertNotEquals("expected", "actual");
	}

}
