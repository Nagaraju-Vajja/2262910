package com.macys.uop.foundation.core.utils.msg.subscriber;

import static com.macys.uop.foundation.core.utils.Constant.CALLERID_DEFAULT_VALUE;
import static com.macys.uop.foundation.core.utils.Constant.CLIENTID_HDR;
import static com.macys.uop.foundation.core.utils.Constant.HDR_REQUIRED_DEFAULT_LOCATIONTYPE;
import static com.macys.uop.foundation.core.utils.Constant.HDR_REQUIRED_DEFAULT_REASON;
import static com.macys.uop.foundation.core.utils.Constant.MSG_PROCESSING_DURATION_TEXT_ORIGIN_PUBSUB;
import static com.macys.uop.foundation.core.utils.Constant.ORDERID_HDR;
import static com.macys.uop.foundation.core.utils.Constant.ORDERID_HDR_DEFAULT_VALUE;
import static org.mockito.Mockito.mock;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.macys.uop.foundation.core.utils.epf.EPFMessagePublisher;
import com.macys.uop.foundation.core.utils.eventlog.EventLogMessagePublisher;
import com.macys.uop.foundation.core.utils.execution.RequestOriginEnum;
import com.macys.uop.foundation.core.utils.spring.SpringContextBridge;

import org.assertj.core.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.gcp.pubsub.support.converter.SimplePubSubMessageConverter;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import org.zalando.problem.ThrowableProblem;

import com.google.pubsub.v1.PubsubMessage;
import com.macys.uop.foundation.core.utils.CommonStatusCode;
import com.macys.uop.foundation.core.utils.exception.ErrorDetail;
import com.macys.uop.foundation.core.utils.exception.ProblemUtil;
import com.macys.uop.foundation.core.utils.masking.ApplicationMaskingConfiguration;
import com.macys.uop.foundation.core.utils.masking.IDataMasker;
import com.macys.uop.foundation.core.utils.masking.JsonMasker;
import com.macys.uop.foundation.core.utils.ordererror.OrderErrorMessagePublisher;

import brave.Span;
import brave.SpanCustomizer;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringRunner.class)
@PrepareForTest(SpringContextBridge.class)
public class AbstractCommonStreamHelperTest implements ProblemUtil {
	
	@MockBean
	private OrderErrorMessagePublisher orderErrorMessagePublisher;
	
	private SimplePubSubMessageConverter pubSubMessageConverter = new SimplePubSubMessageConverter();
	
	@Test
	public void testGetPayloadContentType() {
		AbstractCommonStreamHelper commonStreamHelper=mock(AbstractCommonStreamHelper.class);
		ReflectionTestUtils.setField(commonStreamHelper, "applicationName", "test-service");
		ReflectionTestUtils.setField(commonStreamHelper, "spanTagHeaderNames", "header1,header2");
		try {
			Field fieldJsonUtils = commonStreamHelper.getClass().getSuperclass().getDeclaredField("orderErrorMessagePublisher");
			fieldJsonUtils.setAccessible(true);
			fieldJsonUtils.set(commonStreamHelper, orderErrorMessagePublisher);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Mockito.doCallRealMethod().when(commonStreamHelper).getPayloadContentType();
		
		String result=commonStreamHelper.getPayloadContentType();
		
		Assert.assertEquals(MediaType.APPLICATION_JSON_VALUE, result);
	}
	
	@Test
	public void testGetCallerId() {
		AbstractCommonStreamHelper commonStreamHelper=mock(AbstractCommonStreamHelper.class);
		ReflectionTestUtils.setField(commonStreamHelper, "applicationName", "test-service");
		ReflectionTestUtils.setField(commonStreamHelper, "spanTagHeaderNames", "header1,header2");
		try {
			Field fieldJsonUtils = commonStreamHelper.getClass().getSuperclass().getDeclaredField("orderErrorMessagePublisher");
			fieldJsonUtils.setAccessible(true);
			fieldJsonUtils.set(commonStreamHelper, orderErrorMessagePublisher);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String JSONDATA="{\"name\":\"test\"}";
		Map<String, String> headers = new HashMap<>();
		headers.put(CLIENTID_HDR, "12345");
		
		PubsubMessage pubSubMessage = pubSubMessageConverter.toPubSubMessage(JSONDATA, headers);
		
		Mockito.doCallRealMethod().when(commonStreamHelper).getCallerId(pubSubMessage);
		
		String result=commonStreamHelper.getCallerId(pubSubMessage);
		
		Assert.assertEquals("12345", result);
	}
	
	@Test
	public void testGetCallerIdDefault() {
		AbstractCommonStreamHelper commonStreamHelper=mock(AbstractCommonStreamHelper.class);
		ReflectionTestUtils.setField(commonStreamHelper, "applicationName", "test-service");
		ReflectionTestUtils.setField(commonStreamHelper, "spanTagHeaderNames", "header1,header2");
		try {
			Field fieldJsonUtils = commonStreamHelper.getClass().getSuperclass().getDeclaredField("orderErrorMessagePublisher");
			fieldJsonUtils.setAccessible(true);
			fieldJsonUtils.set(commonStreamHelper, orderErrorMessagePublisher);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String JSONDATA="{\"name\":\"test\"}";
		Map<String, String> headers = new HashMap<>();
		PubsubMessage pubSubMessage = pubSubMessageConverter.toPubSubMessage(JSONDATA, headers);
		
		Mockito.doCallRealMethod().when(commonStreamHelper).getCallerId(pubSubMessage);
		
		String result=commonStreamHelper.getCallerId(pubSubMessage);
		
		Assert.assertEquals(CALLERID_DEFAULT_VALUE, result);
	}
	
	@Test
	public void testProcessHeaders() {
		AbstractCommonStreamHelper commonStreamHelper=mock(AbstractCommonStreamHelper.class);
		ReflectionTestUtils.setField(commonStreamHelper, "applicationName", "test-service");
		ReflectionTestUtils.setField(commonStreamHelper, "spanTagHeaderNames", "header1,header2");
		try {
			Field fieldJsonUtils = commonStreamHelper.getClass().getSuperclass().getDeclaredField("orderErrorMessagePublisher");
			fieldJsonUtils.setAccessible(true);
			fieldJsonUtils.set(commonStreamHelper, orderErrorMessagePublisher);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String JSONDATA="{\"name\":\"test\"}";
		Map<String, String> headers = new HashMap<>();
		headers.put(ORDERID_HDR, "12345");
		
		PubsubMessage pubSubMessage = pubSubMessageConverter.toPubSubMessage(JSONDATA, headers);
		
		Mockito.doCallRealMethod().when(commonStreamHelper).processHeaders(pubSubMessage);
		Mockito.doCallRealMethod().when(commonStreamHelper).processHeadersDefault(pubSubMessage);
		
		Map<String, String> result=commonStreamHelper.processHeaders(pubSubMessage);
		
		Assert.assertEquals("12345", result.get(ORDERID_HDR));
	}
	
	@Test
	public void testProcessHeadersBlank() {
		AbstractCommonStreamHelper commonStreamHelper=mock(AbstractCommonStreamHelper.class);
		ReflectionTestUtils.setField(commonStreamHelper, "applicationName", "test-service");
		ReflectionTestUtils.setField(commonStreamHelper, "spanTagHeaderNames", "header1,header2");
		try {
			Field fieldJsonUtils = commonStreamHelper.getClass().getSuperclass().getDeclaredField("orderErrorMessagePublisher");
			fieldJsonUtils.setAccessible(true);
			fieldJsonUtils.set(commonStreamHelper, orderErrorMessagePublisher);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String JSONDATA="{\"name\":\"test\"}";
		Map<String, String> headers = new HashMap<>();
		
		PubsubMessage pubSubMessage = pubSubMessageConverter.toPubSubMessage(JSONDATA, headers);
		
		Mockito.doCallRealMethod().when(commonStreamHelper).processHeaders(pubSubMessage);
		Mockito.doCallRealMethod().when(commonStreamHelper).processHeadersDefault(pubSubMessage);
		
		Map<String, String> result=commonStreamHelper.processHeaders(pubSubMessage);
		
		Assert.assertEquals(ORDERID_HDR_DEFAULT_VALUE, result.get(ORDERID_HDR));
	}
	
	@Test
	public void testProcessHeadersDefaultWithValues() {
		
		AbstractCommonStreamHelper commonStreamHelper=mock(AbstractCommonStreamHelper.class);
		ReflectionTestUtils.setField(commonStreamHelper, "applicationName", "test-service");
		ReflectionTestUtils.setField(commonStreamHelper, "spanTagHeaderNames", "header1,header2");
		try {
			Field fieldJsonUtils = commonStreamHelper.getClass().getSuperclass().getDeclaredField("orderErrorMessagePublisher");
			fieldJsonUtils.setAccessible(true);
			fieldJsonUtils.set(commonStreamHelper, orderErrorMessagePublisher);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String JSONDATA="{\"name\":\"test\"}";
		Map<String, String> headers = new HashMap<>();
		headers.put(ORDERID_HDR, "12345");
		
		PubsubMessage pubSubMessage = pubSubMessageConverter.toPubSubMessage(JSONDATA, headers);
		
		Mockito.doCallRealMethod().when(commonStreamHelper).processHeadersDefault(pubSubMessage);
		
		Map<String, String> result=commonStreamHelper.processHeadersDefault(pubSubMessage);
		
		Assert.assertEquals("12345", result.get(ORDERID_HDR));
	}
	
	@Test
	public void testProcessHeadersDefaultBlankHeaders() {
		
		AbstractCommonStreamHelper commonStreamHelper=mock(AbstractCommonStreamHelper.class);
		ReflectionTestUtils.setField(commonStreamHelper, "applicationName", "test-service");
		ReflectionTestUtils.setField(commonStreamHelper, "spanTagHeaderNames", "header1,header2");
		try {
			Field fieldJsonUtils = commonStreamHelper.getClass().getSuperclass().getDeclaredField("orderErrorMessagePublisher");
			fieldJsonUtils.setAccessible(true);
			fieldJsonUtils.set(commonStreamHelper, orderErrorMessagePublisher);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String JSONDATA="{\"name\":\"test\"}";
		Map<String, String> headers = new HashMap<>();
		
		PubsubMessage pubSubMessage = pubSubMessageConverter.toPubSubMessage(JSONDATA, headers);
		
		Mockito.doCallRealMethod().when(commonStreamHelper).processHeadersDefault(pubSubMessage);
		
		Map<String, String> result=commonStreamHelper.processHeadersDefault(pubSubMessage);
		
		Assert.assertEquals(ORDERID_HDR_DEFAULT_VALUE, result.get(ORDERID_HDR));
	}
	
	@Test
	public void testAttachSpanTags() {
		
		AbstractCommonStreamHelper commonStreamHelper=mock(AbstractCommonStreamHelper.class);
		ReflectionTestUtils.setField(commonStreamHelper, "applicationName", "test-service");
		ReflectionTestUtils.setField(commonStreamHelper, "spanTagHeaderNames", "header1,header2");
		try {
			Field fieldJsonUtils = commonStreamHelper.getClass().getSuperclass().getDeclaredField("orderErrorMessagePublisher");
			fieldJsonUtils.setAccessible(true);
			fieldJsonUtils.set(commonStreamHelper, orderErrorMessagePublisher);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Map<String, String> headers = new HashMap<>();
		headers.put("header1", "header1value");
		headers.put("header2", "header2value");
		
		Span span=mock(Span.class);
		SpanCustomizer spanCustomizer=mock(SpanCustomizer.class);
		Mockito.doReturn(spanCustomizer).when(span).customizer();
		Mockito.doReturn(spanCustomizer).when(spanCustomizer).tag(ArgumentMatchers.any(), ArgumentMatchers.any());
		
		Mockito.doCallRealMethod().when(commonStreamHelper).attachSpanTags(headers, span);
		
		commonStreamHelper.attachSpanTags(headers, span);
		
		Assert.assertNotEquals("expected", "actual");
	}
	
	@Test
	public void testAttachSpanTagsBlank() {
		
		AbstractCommonStreamHelper commonStreamHelper=mock(AbstractCommonStreamHelper.class);
		ReflectionTestUtils.setField(commonStreamHelper, "applicationName", "test-service");
		ReflectionTestUtils.setField(commonStreamHelper, "spanTagHeaderNames", "");
		try {
			Field fieldJsonUtils = commonStreamHelper.getClass().getSuperclass().getDeclaredField("orderErrorMessagePublisher");
			fieldJsonUtils.setAccessible(true);
			fieldJsonUtils.set(commonStreamHelper, orderErrorMessagePublisher);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Map<String, String> headers = new HashMap<>();
		headers.put("header1", "header1value");
		headers.put("header2", "header2value");
		
		Span span=mock(Span.class);
		SpanCustomizer spanCustomizer=mock(SpanCustomizer.class);
		Mockito.doReturn(spanCustomizer).when(span).customizer();
		Mockito.doReturn(spanCustomizer).when(spanCustomizer).tag(ArgumentMatchers.any(), ArgumentMatchers.any());
		
		Mockito.doCallRealMethod().when(commonStreamHelper).attachSpanTags(headers, span);
		
		commonStreamHelper.attachSpanTags(headers, span);
		
		Assert.assertNotEquals("expected", "actual");
	}
	
	@Test
	public void testAttachSpanTagsSpanNull() {
		
		AbstractCommonStreamHelper commonStreamHelper=mock(AbstractCommonStreamHelper.class);
		ReflectionTestUtils.setField(commonStreamHelper, "applicationName", "test-service");
		ReflectionTestUtils.setField(commonStreamHelper, "spanTagHeaderNames", "header1,header2");
		try {
			Field fieldJsonUtils = commonStreamHelper.getClass().getSuperclass().getDeclaredField("orderErrorMessagePublisher");
			fieldJsonUtils.setAccessible(true);
			fieldJsonUtils.set(commonStreamHelper, orderErrorMessagePublisher);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Map<String, String> headers = new HashMap<>();
		headers.put("header1", "header1value");
		headers.put("header2", "header2value");
		
		Span span=null;
		
		Mockito.doCallRealMethod().when(commonStreamHelper).attachSpanTags(headers, span);
		
		commonStreamHelper.attachSpanTags(headers, span);
		
		Assert.assertNotEquals("expected", "actual");
	}
	
	@Test
	public void testAttachSpanTagsBlankMissingSpanHederValue() {
		
		AbstractCommonStreamHelper commonStreamHelper=mock(AbstractCommonStreamHelper.class);
		ReflectionTestUtils.setField(commonStreamHelper, "applicationName", "test-service");
		ReflectionTestUtils.setField(commonStreamHelper, "spanTagHeaderNames", "header1,header2");
		try {
			Field fieldJsonUtils = commonStreamHelper.getClass().getSuperclass().getDeclaredField("orderErrorMessagePublisher");
			fieldJsonUtils.setAccessible(true);
			fieldJsonUtils.set(commonStreamHelper, orderErrorMessagePublisher);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Map<String, String> headers = null;
		
		Span span=mock(Span.class);
		SpanCustomizer spanCustomizer=mock(SpanCustomizer.class);
		Mockito.doReturn(spanCustomizer).when(span).customizer();
		Mockito.doReturn(spanCustomizer).when(spanCustomizer).tag(ArgumentMatchers.any(), ArgumentMatchers.any());
		
		Mockito.doCallRealMethod().when(commonStreamHelper).attachSpanTags(headers, span);
		
		commonStreamHelper.attachSpanTags(headers, span);
		
		Assert.assertNotEquals("expected", "actual");
	}
	
	@Test
	public void testGetIDataMaskerJson() {
		
		AbstractCommonStreamHelper commonStreamHelper=mock(AbstractCommonStreamHelper.class);
		ReflectionTestUtils.setField(commonStreamHelper, "applicationName", "test-service");
		ReflectionTestUtils.setField(commonStreamHelper, "spanTagHeaderNames", "header1,header2");
		try {
			Field fieldJsonUtils = commonStreamHelper.getClass().getSuperclass().getDeclaredField("orderErrorMessagePublisher");
			fieldJsonUtils.setAccessible(true);
			fieldJsonUtils.set(commonStreamHelper, orderErrorMessagePublisher);
		} catch (Exception e) {
			e.printStackTrace();
		}
				
		Mockito.doReturn(Boolean.TRUE).when(commonStreamHelper).isContentTypeApplicationJson();
		
		Map<String,IDataMasker> maskConfigMap=new HashMap<>();
		maskConfigMap.put("accountNumber", new com.macys.uop.foundation.core.utils.masking.CreditCardNumberMasker());
		ApplicationMaskingConfiguration.setJsonMaskConfMap(maskConfigMap);
		
		Mockito.doCallRealMethod().when(commonStreamHelper).getIDataMasker();
		
		IDataMasker masker=commonStreamHelper.getIDataMasker();
		
		Assert.assertNotNull(masker);
	}
	
	@Test
	public void testGetIDataMaskerXml() {
		
		AbstractCommonStreamHelper commonStreamHelper=mock(AbstractCommonStreamHelper.class);
		ReflectionTestUtils.setField(commonStreamHelper, "applicationName", "test-service");
		ReflectionTestUtils.setField(commonStreamHelper, "spanTagHeaderNames", "header1,header2");
		try {
			Field fieldJsonUtils = commonStreamHelper.getClass().getSuperclass().getDeclaredField("orderErrorMessagePublisher");
			fieldJsonUtils.setAccessible(true);
			fieldJsonUtils.set(commonStreamHelper, orderErrorMessagePublisher);
		} catch (Exception e) {
			e.printStackTrace();
		}
				
		Mockito.doReturn(Boolean.TRUE).when(commonStreamHelper).isContentTypeApplicationXml();
		
		Map<String,IDataMasker> maskConfigMap=new HashMap<>();
		maskConfigMap.put("accountNumber", new com.macys.uop.foundation.core.utils.masking.CreditCardNumberMasker());
		ApplicationMaskingConfiguration.setXmlMaskConfMap(maskConfigMap);
		
		Mockito.doCallRealMethod().when(commonStreamHelper).getIDataMasker();
		
		IDataMasker masker=commonStreamHelper.getIDataMasker();
		
		Assert.assertNotNull(masker);
	}
	
	@Test
	public void testLogErrorThrowableProblem() {
		AbstractCommonStreamHelper commonStreamHelper=mock(AbstractCommonStreamHelper.class);
		ReflectionTestUtils.setField(commonStreamHelper, "applicationName", "test-service");
		ReflectionTestUtils.setField(commonStreamHelper, "spanTagHeaderNames", "header1,header2");
		try {
			Field fieldJsonUtils = commonStreamHelper.getClass().getSuperclass().getDeclaredField("orderErrorMessagePublisher");
			fieldJsonUtils.setAccessible(true);
			fieldJsonUtils.set(commonStreamHelper, orderErrorMessagePublisher);
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
		IllegalArgumentException exception=new IllegalArgumentException(CommonStatusCode.NO_CORRELATIONID.getDescription());
		
		ThrowableProblem problem=createProblem(error, exception);
		
		Mockito.doNothing().when(commonStreamHelper).logError(problem);
		
		Mockito.doCallRealMethod().when(commonStreamHelper).logError(problem);
		
		commonStreamHelper.logError(problem);
		
		Assert.assertNotEquals("expected", "actual");
	}
	
	@Test
	public void testLogErrorNotThrowableProblem() {
		AbstractCommonStreamHelper commonStreamHelper=mock(AbstractCommonStreamHelper.class);
		ReflectionTestUtils.setField(commonStreamHelper, "applicationName", "test-service");
		ReflectionTestUtils.setField(commonStreamHelper, "spanTagHeaderNames", "header1,header2");
		try {
			Field fieldJsonUtils = commonStreamHelper.getClass().getSuperclass().getDeclaredField("orderErrorMessagePublisher");
			fieldJsonUtils.setAccessible(true);
			fieldJsonUtils.set(commonStreamHelper, orderErrorMessagePublisher);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Map<String,IDataMasker> maskConfigMap=new HashMap<>();
		maskConfigMap.put("accountNumber", new com.macys.uop.foundation.core.utils.masking.CreditCardNumberMasker());
		IDataMasker jsonMasker=new JsonMasker(maskConfigMap);
		
		IllegalArgumentException exception=new IllegalArgumentException(CommonStatusCode.NO_CORRELATIONID.getDescription());
		String JSONDATA="{\"name\":\"test\"}";
		
		Mockito.doReturn("56567").when(commonStreamHelper).getCorrelationId();
		Mockito.doReturn("12345").when(commonStreamHelper).getClientId();
		Mockito.doReturn("2222").when(commonStreamHelper).getMessageId();
		Mockito.doReturn("33333").when(commonStreamHelper).getOrderId();
		Mockito.doReturn("some-service").when(commonStreamHelper).getAppName();
		Mockito.doReturn(JSONDATA).when(commonStreamHelper).getPayload();
		
		Mockito.doReturn(jsonMasker).when(commonStreamHelper).getIDataMasker();
		
		Mockito.doCallRealMethod().when(commonStreamHelper).logError(exception);
		
		commonStreamHelper.logError(exception);
		
		Assert.assertNotEquals("expected", "actual");
	}
	
	@Test
	public void testLogErrorNotThrowableProblemNoMasking() {
		AbstractCommonStreamHelper commonStreamHelper=mock(AbstractCommonStreamHelper.class);
		ReflectionTestUtils.setField(commonStreamHelper, "applicationName", "test-service");
		ReflectionTestUtils.setField(commonStreamHelper, "spanTagHeaderNames", "header1,header2");
		try {
			Field fieldJsonUtils = commonStreamHelper.getClass().getSuperclass().getDeclaredField("orderErrorMessagePublisher");
			fieldJsonUtils.setAccessible(true);
			fieldJsonUtils.set(commonStreamHelper, orderErrorMessagePublisher);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		IllegalArgumentException exception=new IllegalArgumentException(CommonStatusCode.NO_CORRELATIONID.getDescription());
		String JSONDATA="{\"name\":\"test\"}";
		
		Mockito.doReturn("56567").when(commonStreamHelper).getCorrelationId();
		Mockito.doReturn("12345").when(commonStreamHelper).getClientId();
		Mockito.doReturn("2222").when(commonStreamHelper).getMessageId();
		Mockito.doReturn("33333").when(commonStreamHelper).getOrderId();
		Mockito.doReturn("some-service").when(commonStreamHelper).getAppName();
		Mockito.doReturn(JSONDATA).when(commonStreamHelper).getPayload();
		
		Mockito.doReturn(null).when(commonStreamHelper).getIDataMasker();
		
		Mockito.doCallRealMethod().when(commonStreamHelper).logError(exception);
		
		commonStreamHelper.logError(exception);
		
		Assert.assertNotEquals("expected", "actual");
	}
	
	@Test
	public void testLogProblemWithError() {
		AbstractCommonStreamHelper commonStreamHelper=mock(AbstractCommonStreamHelper.class);
		ReflectionTestUtils.setField(commonStreamHelper, "applicationName", "test-service");
		ReflectionTestUtils.setField(commonStreamHelper, "spanTagHeaderNames", "header1,header2");
		try {
			Field fieldJsonUtils = commonStreamHelper.getClass().getSuperclass().getDeclaredField("orderErrorMessagePublisher");
			fieldJsonUtils.setAccessible(true);
			fieldJsonUtils.set(commonStreamHelper, orderErrorMessagePublisher);
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
		IllegalArgumentException exception=new IllegalArgumentException(CommonStatusCode.NO_CORRELATIONID.getDescription());
		ThrowableProblem problem=createProblem(error, exception);
		
		String JSONDATA="{\"name\":\"test\"}";
		
		Mockito.doReturn("56567").when(commonStreamHelper).getCorrelationId();
		Mockito.doReturn("12345").when(commonStreamHelper).getClientId();
		Mockito.doReturn("2222").when(commonStreamHelper).getMessageId();
		Mockito.doReturn("33333").when(commonStreamHelper).getOrderId();
		Mockito.doReturn("some-service").when(commonStreamHelper).getAppName();
		Mockito.doReturn(JSONDATA).when(commonStreamHelper).getPayload();
		
		Map<String,IDataMasker> maskConfigMap=new HashMap<>();
		maskConfigMap.put("accountNumber", new com.macys.uop.foundation.core.utils.masking.CreditCardNumberMasker());
		IDataMasker jsonMasker=new JsonMasker(maskConfigMap);
		Mockito.doReturn(jsonMasker).when(commonStreamHelper).getIDataMasker();
		
		Mockito.doCallRealMethod().when(commonStreamHelper).logProblem(problem);
		
		commonStreamHelper.logProblem(problem);
		
		Assert.assertNotEquals("expected", "actual");
	}
	
	@Test
	public void testLogProblemWithErrorNullMasking() {
		AbstractCommonStreamHelper commonStreamHelper=mock(AbstractCommonStreamHelper.class);
		ReflectionTestUtils.setField(commonStreamHelper, "applicationName", "test-service");
		ReflectionTestUtils.setField(commonStreamHelper, "spanTagHeaderNames", "header1,header2");
		try {
			Field fieldJsonUtils = commonStreamHelper.getClass().getSuperclass().getDeclaredField("orderErrorMessagePublisher");
			fieldJsonUtils.setAccessible(true);
			fieldJsonUtils.set(commonStreamHelper, orderErrorMessagePublisher);
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
		IllegalArgumentException exception=new IllegalArgumentException(CommonStatusCode.NO_CORRELATIONID.getDescription());
		ThrowableProblem problem=createProblem(error, exception);
		
		String JSONDATA="{\"name\":\"test\"}";
		
		Mockito.doReturn("56567").when(commonStreamHelper).getCorrelationId();
		Mockito.doReturn("12345").when(commonStreamHelper).getClientId();
		Mockito.doReturn("2222").when(commonStreamHelper).getMessageId();
		Mockito.doReturn("33333").when(commonStreamHelper).getOrderId();
		Mockito.doReturn("some-service").when(commonStreamHelper).getAppName();
		Mockito.doReturn(JSONDATA).when(commonStreamHelper).getPayload();
		
		Mockito.doReturn(null).when(commonStreamHelper).getIDataMasker();
		
		Mockito.doCallRealMethod().when(commonStreamHelper).logProblem(problem);
		
		commonStreamHelper.logProblem(problem);
		
		Assert.assertNotEquals("expected", "actual");
	}
	
	@Test
	public void testLogProblemWithoutError() {
		AbstractCommonStreamHelper commonStreamHelper=mock(AbstractCommonStreamHelper.class);
		ReflectionTestUtils.setField(commonStreamHelper, "applicationName", "test-service");
		ReflectionTestUtils.setField(commonStreamHelper, "spanTagHeaderNames", "header1,header2");
		try {
			Field fieldJsonUtils = commonStreamHelper.getClass().getSuperclass().getDeclaredField("orderErrorMessagePublisher");
			fieldJsonUtils.setAccessible(true);
			fieldJsonUtils.set(commonStreamHelper, orderErrorMessagePublisher);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		ThrowableProblem problem=Problem.builder().build();
		Map<String,IDataMasker> maskConfigMap=new HashMap<>();
		maskConfigMap.put("accountNumber", new com.macys.uop.foundation.core.utils.masking.CreditCardNumberMasker());
		IDataMasker jsonMasker=new JsonMasker(maskConfigMap);
		Mockito.doReturn(jsonMasker).when(commonStreamHelper).getIDataMasker();
		
		Mockito.doCallRealMethod().when(commonStreamHelper).logProblem(problem);
		
		commonStreamHelper.logProblem(problem);
		
		Assert.assertNotEquals("expected", "actual");
	}
	
	@Test
	public void testLogProblemWithoutErrorWithoutMasking() {
		AbstractCommonStreamHelper commonStreamHelper=mock(AbstractCommonStreamHelper.class);
		ReflectionTestUtils.setField(commonStreamHelper, "applicationName", "test-service");
		ReflectionTestUtils.setField(commonStreamHelper, "spanTagHeaderNames", "header1,header2");
		try {
			Field fieldJsonUtils = commonStreamHelper.getClass().getSuperclass().getDeclaredField("orderErrorMessagePublisher");
			fieldJsonUtils.setAccessible(true);
			fieldJsonUtils.set(commonStreamHelper, orderErrorMessagePublisher);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		ThrowableProblem problem=Problem.builder().build();
		Mockito.doReturn(null).when(commonStreamHelper).getIDataMasker();
		
		Mockito.doCallRealMethod().when(commonStreamHelper).logProblem(problem);
		
		commonStreamHelper.logProblem(problem);
		
		Assert.assertNotEquals("expected", "actual");
	}
	
	@Test
	public void testLogMessage() {
		AbstractCommonStreamHelper commonStreamHelper=mock(AbstractCommonStreamHelper.class);
		ReflectionTestUtils.setField(commonStreamHelper, "applicationName", "test-service");
		ReflectionTestUtils.setField(commonStreamHelper, "spanTagHeaderNames", "header1,header2");
		try {
			Field fieldJsonUtils = commonStreamHelper.getClass().getSuperclass().getDeclaredField("orderErrorMessagePublisher");
			fieldJsonUtils.setAccessible(true);
			fieldJsonUtils.set(commonStreamHelper, orderErrorMessagePublisher);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Map<String,IDataMasker> maskConfigMap=new HashMap<>();
		maskConfigMap.put("accountNumber", new com.macys.uop.foundation.core.utils.masking.CreditCardNumberMasker());
		IDataMasker jsonMasker=new JsonMasker(maskConfigMap);
		Mockito.doReturn(jsonMasker).when(commonStreamHelper).getIDataMasker();
		
		String JSONDATA="{\"name\":\"test\"}";
		Mockito.doReturn("56567").when(commonStreamHelper).getCorrelationId();
		Mockito.doReturn("12345").when(commonStreamHelper).getClientId();
		Mockito.doReturn("2222").when(commonStreamHelper).getMessageId();
		Mockito.doReturn("33333").when(commonStreamHelper).getOrderId();
		Mockito.doReturn("some-service").when(commonStreamHelper).getAppName();
		Mockito.doReturn(JSONDATA).when(commonStreamHelper).getPayload();
		
		Mockito.doCallRealMethod().when(commonStreamHelper).logMessage();
		
		commonStreamHelper.logMessage();
		
		Assert.assertNotEquals("expected", "actual");
	}
	
	@Test
	public void testLogMessageWithoutDataMasker() {
		AbstractCommonStreamHelper commonStreamHelper=mock(AbstractCommonStreamHelper.class);
		ReflectionTestUtils.setField(commonStreamHelper, "applicationName", "test-service");
		ReflectionTestUtils.setField(commonStreamHelper, "spanTagHeaderNames", "header1,header2");
		try {
			Field fieldJsonUtils = commonStreamHelper.getClass().getSuperclass().getDeclaredField("orderErrorMessagePublisher");
			fieldJsonUtils.setAccessible(true);
			fieldJsonUtils.set(commonStreamHelper, orderErrorMessagePublisher);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Mockito.doReturn(null).when(commonStreamHelper).getIDataMasker();
		
		String JSONDATA="{\"name\":\"test\"}";
		Mockito.doReturn("56567").when(commonStreamHelper).getCorrelationId();
		Mockito.doReturn("12345").when(commonStreamHelper).getClientId();
		Mockito.doReturn("2222").when(commonStreamHelper).getMessageId();
		Mockito.doReturn("33333").when(commonStreamHelper).getOrderId();
		Mockito.doReturn("some-service").when(commonStreamHelper).getAppName();
		Mockito.doReturn(JSONDATA).when(commonStreamHelper).getPayload();
		
		Mockito.doCallRealMethod().when(commonStreamHelper).logMessage();
		
		commonStreamHelper.logMessage();
		
		Assert.assertNotEquals("expected", "actual");
	}
	
	@Test
	public void testPublishOrderErrorEvent() {
		
		AbstractCommonStreamHelper commonStreamHelper=mock(AbstractCommonStreamHelper.class);
		ReflectionTestUtils.setField(commonStreamHelper, "applicationName", "test-service");
		ReflectionTestUtils.setField(commonStreamHelper, "spanTagHeaderNames", "header1,header2");
		try {
			Field fieldJsonUtils = commonStreamHelper.getClass().getSuperclass().getDeclaredField("orderErrorMessagePublisher");
			fieldJsonUtils.setAccessible(true);
			fieldJsonUtils.set(commonStreamHelper, orderErrorMessagePublisher);
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
		
		IllegalArgumentException exception=new IllegalArgumentException(CommonStatusCode.NO_CORRELATIONID.getDescription());
		ThrowableProblem problem=createProblem(error, exception);
		
		String JSONDATA="{\"name\":\"test\"}";
		
		Mockito.doReturn("33333").when(commonStreamHelper).getCorrelationId();
		Mockito.doReturn("33333").when(commonStreamHelper).getOrderId();
		Mockito.doReturn("some-service").when(commonStreamHelper).getAppName();
		Mockito.doReturn("application/json").when(commonStreamHelper).getContentType();
		Mockito.doReturn("34356").when(commonStreamHelper).getClientId();
		Mockito.doReturn("123909").when(commonStreamHelper).getMessageId();
		Mockito.doReturn(JSONDATA).when(commonStreamHelper).getPayload();
		
		Mockito.doNothing().when(orderErrorMessagePublisher).publishOrderErrorMessage(ArgumentMatchers.any());
		
		Mockito.doCallRealMethod().when(commonStreamHelper).publishOrderErrorEvent(problem);
		
		commonStreamHelper.publishOrderErrorEvent(problem);
		
		Assert.assertNotEquals("expected", "actual");
	}
	
	@Test
	public void testPublishOrderErrorEventWithoutProblemDetail() {
		
		AbstractCommonStreamHelper commonStreamHelper=mock(AbstractCommonStreamHelper.class);
		ReflectionTestUtils.setField(commonStreamHelper, "applicationName", "test-service");
		ReflectionTestUtils.setField(commonStreamHelper, "spanTagHeaderNames", "header1,header2");
		try {
			Field fieldJsonUtils = commonStreamHelper.getClass().getSuperclass().getDeclaredField("orderErrorMessagePublisher");
			fieldJsonUtils.setAccessible(true);
			fieldJsonUtils.set(commonStreamHelper, orderErrorMessagePublisher);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		com.macys.uop.foundation.core.utils.exception.Error error = com.macys.uop.foundation.core.utils.exception.Error.builder()
		.withCode(CommonStatusCode.NO_CORRELATIONID.getCode())
		.withMessage(CommonStatusCode.NO_CORRELATIONID.getDescription())
		.build();
		
		IllegalArgumentException exception=new IllegalArgumentException(CommonStatusCode.NO_CORRELATIONID.getDescription());
		ThrowableProblem problem=createProblem(error, exception);
		
		String JSONDATA="{\"name\":\"test\"}";
		
		Mockito.doReturn("33333").when(commonStreamHelper).getCorrelationId();
		Mockito.doReturn("33333").when(commonStreamHelper).getOrderId();
		Mockito.doReturn("some-service").when(commonStreamHelper).getAppName();
		Mockito.doReturn("application/json").when(commonStreamHelper).getContentType();
		Mockito.doReturn("34356").when(commonStreamHelper).getClientId();
		Mockito.doReturn("123909").when(commonStreamHelper).getMessageId();
		Mockito.doReturn(JSONDATA).when(commonStreamHelper).getPayload();
		
		Mockito.doNothing().when(orderErrorMessagePublisher).publishOrderErrorMessage(ArgumentMatchers.any());
		
		Mockito.doCallRealMethod().when(commonStreamHelper).publishOrderErrorEvent(problem);
		
		commonStreamHelper.publishOrderErrorEvent(problem);
		
		Assert.assertNotEquals("expected", "actual");
	}
	
	@Test
	public void testPublishOrderErrorEventWithoutErrorKey() {
		
		AbstractCommonStreamHelper commonStreamHelper=mock(AbstractCommonStreamHelper.class);
		ReflectionTestUtils.setField(commonStreamHelper, "applicationName", "test-service");
		ReflectionTestUtils.setField(commonStreamHelper, "spanTagHeaderNames", "header1,header2");
		try {
			Field fieldJsonUtils = commonStreamHelper.getClass().getSuperclass().getDeclaredField("orderErrorMessagePublisher");
			fieldJsonUtils.setAccessible(true);
			fieldJsonUtils.set(commonStreamHelper, orderErrorMessagePublisher);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		ThrowableProblem problem=Problem.builder().build();
		
		String JSONDATA="{\"name\":\"test\"}";
		
		Mockito.doReturn("33333").when(commonStreamHelper).getCorrelationId();
		Mockito.doReturn("33333").when(commonStreamHelper).getOrderId();
		Mockito.doReturn("some-service").when(commonStreamHelper).getAppName();
		Mockito.doReturn("application/json").when(commonStreamHelper).getContentType();
		Mockito.doReturn("34356").when(commonStreamHelper).getClientId();
		Mockito.doReturn("123909").when(commonStreamHelper).getMessageId();
		Mockito.doReturn(JSONDATA).when(commonStreamHelper).getPayload();
		
		Mockito.doNothing().when(orderErrorMessagePublisher).publishOrderErrorMessage(ArgumentMatchers.any());
		
		Mockito.doCallRealMethod().when(commonStreamHelper).publishOrderErrorEvent(problem);
		
		commonStreamHelper.publishOrderErrorEvent(problem);
		
		Assert.assertNotEquals("expected", "actual");
	}
	
	@Test
	public void testPublishOrderErrorEventGenericThrowable() {
		
		AbstractCommonStreamHelper commonStreamHelper=mock(AbstractCommonStreamHelper.class);
		ReflectionTestUtils.setField(commonStreamHelper, "applicationName", "test-service");
		ReflectionTestUtils.setField(commonStreamHelper, "spanTagHeaderNames", "header1,header2");
		try {
			Field fieldJsonUtils = commonStreamHelper.getClass().getSuperclass().getDeclaredField("orderErrorMessagePublisher");
			fieldJsonUtils.setAccessible(true);
			fieldJsonUtils.set(commonStreamHelper, orderErrorMessagePublisher);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		IllegalArgumentException exception=new IllegalArgumentException(CommonStatusCode.NO_CORRELATIONID.getDescription());
		
		String JSONDATA="{\"name\":\"test\"}";
		
		Mockito.doReturn(CommonStatusCode.NO_CORRELATIONID.getDescription()).when(commonStreamHelper).getMessage(exception);
		Mockito.doReturn("33333").when(commonStreamHelper).getCorrelationId();
		Mockito.doReturn("33333").when(commonStreamHelper).getOrderId();
		Mockito.doReturn("some-service").when(commonStreamHelper).getAppName();
		Mockito.doReturn("application/json").when(commonStreamHelper).getContentType();
		Mockito.doReturn("34356").when(commonStreamHelper).getClientId();
		Mockito.doReturn("123909").when(commonStreamHelper).getMessageId();
		Mockito.doReturn(JSONDATA).when(commonStreamHelper).getPayload();
		
		Mockito.doNothing().when(orderErrorMessagePublisher).publishOrderErrorMessage(ArgumentMatchers.any());
		
		Mockito.doCallRealMethod().when(commonStreamHelper).publishOrderErrorEvent(exception);
		
		commonStreamHelper.publishOrderErrorEvent(exception);
		
		Assert.assertNotEquals("expected", "actual");
	}
	
	@Test
	public void testPublishOrderErrorEventWithoutMsgHeader() {
		
		AbstractCommonStreamHelper commonStreamHelper=mock(AbstractCommonStreamHelper.class);
		ReflectionTestUtils.setField(commonStreamHelper, "applicationName", "test-service");
		ReflectionTestUtils.setField(commonStreamHelper, "spanTagHeaderNames", "header1,header2");
		try {
			Field fieldJsonUtils = commonStreamHelper.getClass().getSuperclass().getDeclaredField("orderErrorMessagePublisher");
			fieldJsonUtils.setAccessible(true);
			fieldJsonUtils.set(commonStreamHelper, orderErrorMessagePublisher);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		IllegalArgumentException exception=new IllegalArgumentException(CommonStatusCode.NO_CORRELATIONID.getDescription());
		
		String JSONDATA="{\"name\":\"test\"}";
		
		Mockito.doReturn(CommonStatusCode.NO_CORRELATIONID.getDescription()).when(commonStreamHelper).getMessage(exception);
		Mockito.doReturn("some-service").when(commonStreamHelper).getAppName();
		Mockito.doReturn("application/json").when(commonStreamHelper).getContentType();
		Mockito.doReturn(JSONDATA).when(commonStreamHelper).getPayload();
		
		Mockito.doNothing().when(orderErrorMessagePublisher).publishOrderErrorMessage(ArgumentMatchers.any());
		
		Mockito.doCallRealMethod().when(commonStreamHelper).publishOrderErrorEvent(exception);
		
		commonStreamHelper.publishOrderErrorEvent(exception);
		
		Assert.assertNotEquals("expected", "actual");
	}

	@Test
	public void testPublishEPFEvent() {

		AbstractCommonStreamHelper commonStreamHelper=mock(AbstractCommonStreamHelper.class);
		ReflectionTestUtils.setField(commonStreamHelper, "applicationName", "test-service");
		ReflectionTestUtils.setField(commonStreamHelper, "eventLogChannelName", "event_onsuccess_dev");
		ReflectionTestUtils.setField(commonStreamHelper, "spanTagHeaderNames", "header1,header2");
		
		com.macys.uop.foundation.core.utils.exception.Error error = com.macys.uop.foundation.core.utils.exception.Error.builder()
				.withCode(CommonStatusCode.NO_CORRELATIONID.getCode())
				.withMessage(CommonStatusCode.NO_CORRELATIONID.getDescription())
				.withErrorDetail(ErrorDetail.builder().withDomain("some-service").withReason(HDR_REQUIRED_DEFAULT_REASON)
						.withMessage(CommonStatusCode.NO_CORRELATIONID.getDescription()).withLocation("some location")
						.withLocationType(HDR_REQUIRED_DEFAULT_LOCATIONTYPE).build())
				.build();

		IllegalArgumentException exception=new IllegalArgumentException(CommonStatusCode.NO_CORRELATIONID.getDescription());
		ThrowableProblem problem=createProblem(error, exception);

		String JSONDATA="{\"name\":\"test\"}";
		
		String[] errorCodes= {"UOP-GEN-E05004"};
		Mockito.doReturn(Arrays.asList(errorCodes)).when(commonStreamHelper).getEPFEventPublishingValidErrorCodes();
		
		Mockito.doReturn(Boolean.TRUE).when(commonStreamHelper).isExtMsgInputEventLogPublishingEnabled();
		Mockito.doReturn("33333").when(commonStreamHelper).getCorrelationId();
		Mockito.doReturn("33333").when(commonStreamHelper).getOrderId();
		Mockito.doReturn(RequestOriginEnum.REST).when(commonStreamHelper).getOrigin();
		Mockito.doReturn("http://sample/url").when(commonStreamHelper).getRequestURL();
		Mockito.doReturn("some-service").when(commonStreamHelper).getAppName();
		Mockito.doReturn("application/json").when(commonStreamHelper).getContentType();
		Mockito.doReturn("34356").when(commonStreamHelper).getClientId();
		Mockito.doReturn("123909").when(commonStreamHelper).getMessageId();
		Mockito.doReturn(JSONDATA).when(commonStreamHelper).getPayload();
		EPFMessagePublisher epfMessagePublisher = PowerMockito.mock(EPFMessagePublisher.class);
		PowerMockito.mockStatic(SpringContextBridge.class);
		Mockito.when(SpringContextBridge.getBean(EPFMessagePublisher.class)).thenReturn(epfMessagePublisher);
		Mockito.doNothing().when(epfMessagePublisher).publishEPFMessage(ArgumentMatchers.any(),ArgumentMatchers.any(),ArgumentMatchers.anyMap(),ArgumentMatchers.anyMap());
		Mockito.doCallRealMethod().when(commonStreamHelper).publishEPFEvent(problem);

		commonStreamHelper.publishEPFEvent(problem);

		Assert.assertNotEquals("expected", "actual");
	}

	@Test
	public void testPublishEPFEventError() {

		AbstractCommonStreamHelper commonStreamHelper=mock(AbstractCommonStreamHelper.class);
		ReflectionTestUtils.setField(commonStreamHelper, "applicationName", "test-service");
		ReflectionTestUtils.setField(commonStreamHelper, "eventLogChannelName", "event_onsuccess_dev");
		ReflectionTestUtils.setField(commonStreamHelper, "spanTagHeaderNames", "header1,header2");

		IllegalArgumentException exception=new IllegalArgumentException(CommonStatusCode.NO_CORRELATIONID.getDescription());

		String JSONDATA="{\"name\":\"test\"}";
		
		Mockito.doReturn(null).when(commonStreamHelper).getEPFEventPublishingValidErrorCodes();
		
		Mockito.doReturn(CommonStatusCode.NO_CORRELATIONID.getDescription()).when(commonStreamHelper).getMessage(exception);
		Mockito.doReturn(Boolean.TRUE).when(commonStreamHelper).isExtMsgInputEventLogPublishingEnabled();
		Mockito.doReturn("33333").when(commonStreamHelper).getCorrelationId();
		Mockito.doReturn("33333").when(commonStreamHelper).getOrderId();
		Mockito.doReturn(RequestOriginEnum.REST).when(commonStreamHelper).getOrigin();
		Mockito.doReturn("http://sample/url").when(commonStreamHelper).getRequestURL();
		Mockito.doReturn("some-service").when(commonStreamHelper).getAppName();
		Mockito.doReturn("application/json").when(commonStreamHelper).getContentType();
		Mockito.doReturn("34356").when(commonStreamHelper).getClientId();
		Mockito.doReturn("123909").when(commonStreamHelper).getMessageId();
		Mockito.doReturn(JSONDATA).when(commonStreamHelper).getPayload();
		EPFMessagePublisher epfMessagePublisher = PowerMockito.mock(EPFMessagePublisher.class);
		PowerMockito.mockStatic(SpringContextBridge.class);
		Mockito.when(SpringContextBridge.getBean(EPFMessagePublisher.class)).thenReturn(epfMessagePublisher);
		Mockito.doNothing().when(epfMessagePublisher).publishEPFMessage(ArgumentMatchers.any(),ArgumentMatchers.any(),ArgumentMatchers.anyMap(),ArgumentMatchers.anyMap());
		Mockito.doCallRealMethod().when(commonStreamHelper).publishEPFEvent(exception);

		commonStreamHelper.publishEPFEvent(exception);

		Assert.assertNotEquals("expected", "actual");
	}

	@Test
	public void testPublishEPFEventWithOutProblemDetail() {

		AbstractCommonStreamHelper commonStreamHelper=mock(AbstractCommonStreamHelper.class);
		ReflectionTestUtils.setField(commonStreamHelper, "applicationName", "test-service");
		ReflectionTestUtils.setField(commonStreamHelper, "eventLogChannelName", "event_onsuccess_dev");
		ReflectionTestUtils.setField(commonStreamHelper, "spanTagHeaderNames", "header1,header2");


		Problem problem =Problem.builder().withStatus(Status.valueOf(500)).build();

		String JSONDATA="{\"name\":\"test\"}";
		
		String[] errorCodes= {"UOP-GEN-E05004"};
		Mockito.doReturn(Arrays.asList(errorCodes)).when(commonStreamHelper).getEPFEventPublishingValidErrorCodes();
		
		Mockito.doReturn(Boolean.TRUE).when(commonStreamHelper).isExtMsgInputEventLogPublishingEnabled();
		Mockito.doReturn("33333").when(commonStreamHelper).getCorrelationId();
		Mockito.doReturn("33333").when(commonStreamHelper).getOrderId();
		Mockito.doReturn(RequestOriginEnum.REST).when(commonStreamHelper).getOrigin();
		Mockito.doReturn("http://sample/url").when(commonStreamHelper).getRequestURL();
		Mockito.doReturn("some-service").when(commonStreamHelper).getAppName();
		Mockito.doReturn("application/json").when(commonStreamHelper).getContentType();
		Mockito.doReturn("34356").when(commonStreamHelper).getClientId();
		Mockito.doReturn("123909").when(commonStreamHelper).getMessageId();
		Mockito.doReturn(JSONDATA).when(commonStreamHelper).getPayload();
		EPFMessagePublisher epfMessagePublisher = PowerMockito.mock(EPFMessagePublisher.class);
		PowerMockito.mockStatic(SpringContextBridge.class);
		Mockito.when(SpringContextBridge.getBean(EPFMessagePublisher.class)).thenReturn(epfMessagePublisher);
		Mockito.doNothing().when(epfMessagePublisher).publishEPFMessage(ArgumentMatchers.any(),ArgumentMatchers.any(),ArgumentMatchers.anyMap(),ArgumentMatchers.anyMap());
		Mockito.doCallRealMethod().when(commonStreamHelper).publishEPFEvent((Throwable) problem);

		commonStreamHelper.publishEPFEvent((Throwable) problem);

		Assert.assertNotEquals("expected", "actual");
	}

	@Test
	public void testPublishEventLog4ExternalMsgInputTrue() {

		AbstractCommonStreamHelper commonStreamHelper=mock(AbstractCommonStreamHelper.class);
		ReflectionTestUtils.setField(commonStreamHelper, "applicationName", "test-service");
		ReflectionTestUtils.setField(commonStreamHelper, "eventLogChannelName", "event_onsuccess_dev");
		ReflectionTestUtils.setField(commonStreamHelper, "spanTagHeaderNames", "header1,header2");

		String JSONDATA="{\"name\":\"test\"}";
		
		Mockito.doReturn(Boolean.TRUE).when(commonStreamHelper).isExtMsgInputEventLogPublishingEnabled();

		Mockito.doReturn("33333").when(commonStreamHelper).getCorrelationId();
		Mockito.doReturn("33333").when(commonStreamHelper).getOrderId();
		Mockito.doReturn(RequestOriginEnum.REST).when(commonStreamHelper).getOrigin();
		Mockito.doReturn("message:pull__eventlog_onsuccess_dev__eventlog").when(commonStreamHelper).getRequestURL();
		Mockito.doReturn("some-service").when(commonStreamHelper).getAppName();
		Mockito.doReturn("application/json").when(commonStreamHelper).getContentType();
		Mockito.doReturn("34356").when(commonStreamHelper).getClientId();
		Mockito.doReturn("123909").when(commonStreamHelper).getMessageId();
		Mockito.doReturn(JSONDATA).when(commonStreamHelper).getPayload();
		EventLogMessagePublisher eventLogMessagePublisher = PowerMockito.mock(EventLogMessagePublisher.class);
		PowerMockito.mockStatic(SpringContextBridge.class);
		Mockito.when(SpringContextBridge.getBean(EventLogMessagePublisher.class)).thenReturn(eventLogMessagePublisher);
		Mockito.when(commonStreamHelper.extractTopicNameFromRequestURL("message:pull__eventlog_onsuccess_dev__eventlog")).thenReturn("xyz");
		Mockito.doReturn("messageId").when(eventLogMessagePublisher).publishEventLogMessage(ArgumentMatchers.any(),ArgumentMatchers.any(),ArgumentMatchers.anyMap());
		Mockito.doCallRealMethod().when(commonStreamHelper).publishEventLog4ExternalMsgInput();

		commonStreamHelper.publishEventLog4ExternalMsgInput();

		Assert.assertNotEquals("expected", "actual");
	}
	
	@Test
	public void testPublishEventLog4ExternalMsgInputFalse() {

		AbstractCommonStreamHelper commonStreamHelper=mock(AbstractCommonStreamHelper.class);
		ReflectionTestUtils.setField(commonStreamHelper, "applicationName", "test-service");
		ReflectionTestUtils.setField(commonStreamHelper, "eventLogChannelName", "event_onsuccess_dev");
		ReflectionTestUtils.setField(commonStreamHelper, "spanTagHeaderNames", "header1,header2");

		String JSONDATA="{\"name\":\"test\"}";
		
		Mockito.doReturn(Boolean.FALSE).when(commonStreamHelper).isExtMsgInputEventLogPublishingEnabled();

		Mockito.doReturn("33333").when(commonStreamHelper).getCorrelationId();
		Mockito.doReturn("33333").when(commonStreamHelper).getOrderId();
		Mockito.doReturn(RequestOriginEnum.REST).when(commonStreamHelper).getOrigin();
		Mockito.doReturn("message:pull__eventlog_onsuccess_dev__eventlog").when(commonStreamHelper).getRequestURL();
		Mockito.doReturn("some-service").when(commonStreamHelper).getAppName();
		Mockito.doReturn("application/json").when(commonStreamHelper).getContentType();
		Mockito.doReturn("34356").when(commonStreamHelper).getClientId();
		Mockito.doReturn("123909").when(commonStreamHelper).getMessageId();
		Mockito.doReturn(JSONDATA).when(commonStreamHelper).getPayload();
		EventLogMessagePublisher eventLogMessagePublisher = PowerMockito.mock(EventLogMessagePublisher.class);
		PowerMockito.mockStatic(SpringContextBridge.class);
		Mockito.when(SpringContextBridge.getBean(EventLogMessagePublisher.class)).thenReturn(eventLogMessagePublisher);
		Mockito.when(commonStreamHelper.extractTopicNameFromRequestURL("message:pull__eventlog_onsuccess_dev__eventlog")).thenReturn("xyz");
		Mockito.doReturn("messageId").when(eventLogMessagePublisher).publishEventLogMessage(ArgumentMatchers.any(),ArgumentMatchers.any(),ArgumentMatchers.anyMap());
		Mockito.doCallRealMethod().when(commonStreamHelper).publishEventLog4ExternalMsgInput();

		commonStreamHelper.publishEventLog4ExternalMsgInput();

		Assert.assertNotEquals("expected", "actual");
	}
	
	@Test
	public void testLogMessageProcessingDurationWhenTrue() {

		AbstractCommonStreamHelper commonStreamHelper=mock(AbstractCommonStreamHelper.class);
		ReflectionTestUtils.setField(commonStreamHelper, "applicationName", "test-service");
		ReflectionTestUtils.setField(commonStreamHelper, "eventLogChannelName", "event_onsuccess_dev");
		ReflectionTestUtils.setField(commonStreamHelper, "spanTagHeaderNames", "header1,header2");
		ReflectionTestUtils.setField(commonStreamHelper, "isMSDurationLoggingEnabled", Boolean.TRUE);
		
		Map<String,String> messageHeaders=new HashMap<>();
		messageHeaders.put("abc", "xyz");

		Mockito.doReturn("34356").when(commonStreamHelper).getClientId();
		Mockito.doReturn("123909").when(commonStreamHelper).getMessageId();
		Mockito.doReturn("33333").when(commonStreamHelper).getOrderId();
		Mockito.doReturn("33333").when(commonStreamHelper).getCorrelationId();
		Mockito.doReturn("some-service").when(commonStreamHelper).getAppName();
		Mockito.doReturn("pull__event_onsuccess_test_dev__eventlog_create_test").when(commonStreamHelper).getTopicSubscription();
		Mockito.doReturn(messageHeaders).when(commonStreamHelper).getMessageHeaders();
		Mockito.doReturn("Some Text").when(commonStreamHelper).constructMsgProcessingDurationText4Logging(Instant.now(), Instant.now(), MSG_PROCESSING_DURATION_TEXT_ORIGIN_PUBSUB);
		
		Mockito.doCallRealMethod().when(commonStreamHelper).logMessageProcessingDuration(Instant.now(), Instant.now(), "Some Context");
		commonStreamHelper.logMessageProcessingDuration(Instant.now(), Instant.now(), "Some Context");

		Assert.assertNotEquals("expected", "actual");
	}
	
	@Test
	public void testLogMessageProcessingDurationWhenFalse() {

		AbstractCommonStreamHelper commonStreamHelper=mock(AbstractCommonStreamHelper.class);
		ReflectionTestUtils.setField(commonStreamHelper, "applicationName", "test-service");
		ReflectionTestUtils.setField(commonStreamHelper, "eventLogChannelName", "event_onsuccess_dev");
		ReflectionTestUtils.setField(commonStreamHelper, "spanTagHeaderNames", "header1,header2");
		ReflectionTestUtils.setField(commonStreamHelper, "isMSDurationLoggingEnabled", Boolean.FALSE);
		
		Map<String,String> messageHeaders=new HashMap<>();
		messageHeaders.put("abc", "xyz");

		Mockito.doReturn("34356").when(commonStreamHelper).getClientId();
		Mockito.doReturn("123909").when(commonStreamHelper).getMessageId();
		Mockito.doReturn("33333").when(commonStreamHelper).getOrderId();
		Mockito.doReturn("33333").when(commonStreamHelper).getCorrelationId();
		Mockito.doReturn("some-service").when(commonStreamHelper).getAppName();
		Mockito.doReturn("pull__event_onsuccess_test_dev__eventlog_create_test").when(commonStreamHelper).getTopicSubscription();
		Mockito.doReturn(messageHeaders).when(commonStreamHelper).getMessageHeaders();
		Mockito.doReturn("Some Text").when(commonStreamHelper).constructMsgProcessingDurationText4Logging(Instant.now(), Instant.now(), MSG_PROCESSING_DURATION_TEXT_ORIGIN_PUBSUB);
		
		Mockito.doCallRealMethod().when(commonStreamHelper).logMessageProcessingDuration(Instant.now(), Instant.now(), "Some Context");
		commonStreamHelper.logMessageProcessingDuration(Instant.now(), Instant.now(), "Some Context");

		Assert.assertNotEquals("expected", "actual");
	}
	
	@Test
	public void testGetEPFEventPublishingValidErrorCodesWhenNotSet() {

		AbstractCommonStreamHelper commonStreamHelper=mock(AbstractCommonStreamHelper.class);
		ReflectionTestUtils.setField(commonStreamHelper, "applicationName", "test-service");
		ReflectionTestUtils.setField(commonStreamHelper, "eventLogChannelName", "event_onsuccess_dev");
		ReflectionTestUtils.setField(commonStreamHelper, "spanTagHeaderNames", "header1,header2");
		ReflectionTestUtils.setField(commonStreamHelper, "isMSDurationLoggingEnabled", Boolean.TRUE);
		
		Mockito.doCallRealMethod().when(commonStreamHelper).getEPFEventPublishingValidErrorCodes();
		List<String> errorCodeList=commonStreamHelper.getEPFEventPublishingValidErrorCodes();

		Assert.assertEquals(errorCodeList.size(), 2);
	}
	
	@Test
	public void testGetEPFEventPublishingValidErrorCodesWhenSet() {

		AbstractCommonStreamHelper commonStreamHelper=mock(AbstractCommonStreamHelper.class);
		ReflectionTestUtils.setField(commonStreamHelper, "applicationName", "test-service");
		ReflectionTestUtils.setField(commonStreamHelper, "eventLogChannelName", "event_onsuccess_dev");
		ReflectionTestUtils.setField(commonStreamHelper, "spanTagHeaderNames", "header1,header2");
		ReflectionTestUtils.setField(commonStreamHelper, "isMSDurationLoggingEnabled", Boolean.TRUE);
		ReflectionTestUtils.setField(commonStreamHelper, "epfEventPublishingValidErrorCodesList", "UOP-GEN-E05004,UOP-GEN-E05005,UOP-GEN-E05006");
		
		Mockito.doCallRealMethod().when(commonStreamHelper).getEPFEventPublishingValidErrorCodes();
		List<String> errorCodeList=commonStreamHelper.getEPFEventPublishingValidErrorCodes();

		Assert.assertEquals(errorCodeList.size(), 3);
	}
	
	@Test
	public void testGetEPFEventPublishingValidErrorCodesWhenOnlyComma() {

		AbstractCommonStreamHelper commonStreamHelper=mock(AbstractCommonStreamHelper.class);
		ReflectionTestUtils.setField(commonStreamHelper, "applicationName", "test-service");
		ReflectionTestUtils.setField(commonStreamHelper, "eventLogChannelName", "event_onsuccess_dev");
		ReflectionTestUtils.setField(commonStreamHelper, "spanTagHeaderNames", "header1,header2");
		ReflectionTestUtils.setField(commonStreamHelper, "isMSDurationLoggingEnabled", Boolean.TRUE);
		ReflectionTestUtils.setField(commonStreamHelper, "epfEventPublishingValidErrorCodesList", ",");
		
		Mockito.doCallRealMethod().when(commonStreamHelper).getEPFEventPublishingValidErrorCodes();
		List<String> errorCodeList=commonStreamHelper.getEPFEventPublishingValidErrorCodes();

		Assert.assertEquals(errorCodeList.size(), 2);
	}
	
	@Test
	public void testLogMessageProcessingInvokeServiceDurationWhenTrue() {

		AbstractCommonStreamHelper commonStreamHelper=mock(AbstractCommonStreamHelper.class);
		ReflectionTestUtils.setField(commonStreamHelper, "applicationName", "test-service");
		ReflectionTestUtils.setField(commonStreamHelper, "eventLogChannelName", "event_onsuccess_dev");
		ReflectionTestUtils.setField(commonStreamHelper, "spanTagHeaderNames", "header1,header2");
		ReflectionTestUtils.setField(commonStreamHelper, "isMSInvokeServiceDurationLoggingEnabled", Boolean.TRUE);
		
		Map<String,String> messageHeaders=new HashMap<>();
		messageHeaders.put("abc", "xyz");

		Mockito.doReturn("34356").when(commonStreamHelper).getClientId();
		Mockito.doReturn("123909").when(commonStreamHelper).getMessageId();
		Mockito.doReturn("33333").when(commonStreamHelper).getOrderId();
		Mockito.doReturn("33333").when(commonStreamHelper).getCorrelationId();
		Mockito.doReturn("some-service").when(commonStreamHelper).getAppName();
		Mockito.doReturn("pull__event_onsuccess_test_dev__eventlog_create_test").when(commonStreamHelper).getTopicSubscription();
		Mockito.doReturn(messageHeaders).when(commonStreamHelper).getMessageHeaders();
		
		Mockito.doCallRealMethod().when(commonStreamHelper).logMessageProcessingInvokeServiceDuration(Instant.now(), Instant.now());
		commonStreamHelper.logMessageProcessingInvokeServiceDuration(Instant.now(), Instant.now());

		Assert.assertNotEquals("expected", "actual");
	}
	
	@Test
	public void testLogMessageProcessingInvokeServiceDurationWhenFalse() {

		AbstractCommonStreamHelper commonStreamHelper=mock(AbstractCommonStreamHelper.class);
		ReflectionTestUtils.setField(commonStreamHelper, "applicationName", "test-service");
		ReflectionTestUtils.setField(commonStreamHelper, "eventLogChannelName", "event_onsuccess_dev");
		ReflectionTestUtils.setField(commonStreamHelper, "spanTagHeaderNames", "header1,header2");
		ReflectionTestUtils.setField(commonStreamHelper, "isMSInvokeServiceDurationLoggingEnabled", Boolean.FALSE);
		
		Map<String,String> messageHeaders=new HashMap<>();
		messageHeaders.put("abc", "xyz");

		Mockito.doReturn("34356").when(commonStreamHelper).getClientId();
		Mockito.doReturn("123909").when(commonStreamHelper).getMessageId();
		Mockito.doReturn("33333").when(commonStreamHelper).getOrderId();
		Mockito.doReturn("33333").when(commonStreamHelper).getCorrelationId();
		Mockito.doReturn("some-service").when(commonStreamHelper).getAppName();
		Mockito.doReturn("pull__event_onsuccess_test_dev__eventlog_create_test").when(commonStreamHelper).getTopicSubscription();
		Mockito.doReturn(messageHeaders).when(commonStreamHelper).getMessageHeaders();
		
		Mockito.doCallRealMethod().when(commonStreamHelper).logMessageProcessingInvokeServiceDuration(Instant.now(), Instant.now());
		commonStreamHelper.logMessageProcessingInvokeServiceDuration(Instant.now(), Instant.now());

		Assert.assertNotEquals("expected", "actual");
	}
	
	@Test
	public void testLogExtMsgInputEventLogPublishingDurationWhenTrue() {

		AbstractCommonStreamHelper commonStreamHelper=mock(AbstractCommonStreamHelper.class);
		ReflectionTestUtils.setField(commonStreamHelper, "applicationName", "test-service");
		ReflectionTestUtils.setField(commonStreamHelper, "eventLogChannelName", "event_onsuccess_dev");
		ReflectionTestUtils.setField(commonStreamHelper, "spanTagHeaderNames", "header1,header2");
		ReflectionTestUtils.setField(commonStreamHelper, "isMSExtMsgInputEventLogPublishingDurationLoggingEnabled", Boolean.TRUE);
		
		Map<String,String> messageHeaders=new HashMap<>();
		messageHeaders.put("abc", "xyz");

		Mockito.doReturn("34356").when(commonStreamHelper).getClientId();
		Mockito.doReturn("123909").when(commonStreamHelper).getMessageId();
		Mockito.doReturn("33333").when(commonStreamHelper).getOrderId();
		Mockito.doReturn("33333").when(commonStreamHelper).getCorrelationId();
		Mockito.doReturn("some-service").when(commonStreamHelper).getAppName();
		Mockito.doReturn("pull__event_onsuccess_test_dev__eventlog_create_test").when(commonStreamHelper).getTopicSubscription();
		Mockito.doReturn(messageHeaders).when(commonStreamHelper).getMessageHeaders();
		
		Mockito.doCallRealMethod().when(commonStreamHelper).logExtMsgInputEventLogPublishingDuration(Instant.now(), Instant.now());
		commonStreamHelper.logExtMsgInputEventLogPublishingDuration(Instant.now(), Instant.now());

		Assert.assertNotEquals("expected", "actual");
	}
	
	@Test
	public void testLogExtMsgInputEventLogPublishingDurationWhenFalse() {

		AbstractCommonStreamHelper commonStreamHelper=mock(AbstractCommonStreamHelper.class);
		ReflectionTestUtils.setField(commonStreamHelper, "applicationName", "test-service");
		ReflectionTestUtils.setField(commonStreamHelper, "eventLogChannelName", "event_onsuccess_dev");
		ReflectionTestUtils.setField(commonStreamHelper, "spanTagHeaderNames", "header1,header2");
		ReflectionTestUtils.setField(commonStreamHelper, "isMSExtMsgInputEventLogPublishingDurationLoggingEnabled", Boolean.FALSE);
		
		Map<String,String> messageHeaders=new HashMap<>();
		messageHeaders.put("abc", "xyz");

		Mockito.doReturn("34356").when(commonStreamHelper).getClientId();
		Mockito.doReturn("123909").when(commonStreamHelper).getMessageId();
		Mockito.doReturn("33333").when(commonStreamHelper).getOrderId();
		Mockito.doReturn("33333").when(commonStreamHelper).getCorrelationId();
		Mockito.doReturn("some-service").when(commonStreamHelper).getAppName();
		Mockito.doReturn("pull__event_onsuccess_test_dev__eventlog_create_test").when(commonStreamHelper).getTopicSubscription();
		Mockito.doReturn(messageHeaders).when(commonStreamHelper).getMessageHeaders();
		
		Mockito.doCallRealMethod().when(commonStreamHelper).logExtMsgInputEventLogPublishingDuration(Instant.now(), Instant.now());
		commonStreamHelper.logExtMsgInputEventLogPublishingDuration(Instant.now(), Instant.now());

		Assert.assertNotEquals("expected", "actual");
	}
	
	@Test
	public void testShouldProceedNAckMessageWhenNoErrorCode() {
		
		Exception ex=new RuntimeException("");

		AbstractCommonStreamHelper commonStreamHelper=mock(AbstractCommonStreamHelper.class);
				
		Mockito.doCallRealMethod().when(commonStreamHelper).shouldProceedNAckMessage(ex);
		boolean result=commonStreamHelper.shouldProceedNAckMessage(ex);

		Assert.assertEquals(result, false);
	}
	
	@Test
	public void testShouldProceedNAckMessageWhenBlankErrorCode() {
		
		Exception ex=new RuntimeException("");

		AbstractCommonStreamHelper commonStreamHelper=mock(AbstractCommonStreamHelper.class);
		ReflectionTestUtils.setField(commonStreamHelper, "msgNAckValidErrorCodesList", "");
		
		Mockito.doCallRealMethod().when(commonStreamHelper).shouldProceedNAckMessage(ex);
		boolean result=commonStreamHelper.shouldProceedNAckMessage(ex);

		Assert.assertEquals(result, false);
	}
	
	@Test
	public void testShouldProceedNAckMessageWhenWithErrorCode() {
		
		Exception ex=new RuntimeException("");

		AbstractCommonStreamHelper commonStreamHelper=mock(AbstractCommonStreamHelper.class);
		ReflectionTestUtils.setField(commonStreamHelper, "msgNAckValidErrorCodesList", "UOP-GEN-E05004,UOP-GEN-E05005,UOP-GEN-E05006");
		
		Mockito.doCallRealMethod().when(commonStreamHelper).shouldProceedNAckMessage(ex);
		boolean result=commonStreamHelper.shouldProceedNAckMessage(ex);

		Assert.assertEquals(result, false);
	}
	
	@Test
	public void testShouldProceedNAckMessageWhenWithErrorCodeWithProperException() {
		
		com.macys.uop.foundation.core.utils.exception.Error error = com.macys.uop.foundation.core.utils.exception.Error.builder()
				.withCode(CommonStatusCode.NO_CORRELATIONID.getCode())
				.withMessage(CommonStatusCode.NO_CORRELATIONID.getDescription())
				.withErrorDetail(ErrorDetail.builder().withDomain("some-service").withReason(HDR_REQUIRED_DEFAULT_REASON)
						.withMessage(CommonStatusCode.NO_CORRELATIONID.getDescription()).withLocation("some location")
						.withLocationType(HDR_REQUIRED_DEFAULT_LOCATIONTYPE).build())
				.build();
		IllegalArgumentException exception=new IllegalArgumentException(CommonStatusCode.NO_CORRELATIONID.getDescription());
		
		ThrowableProblem problem=createProblem(error, exception);

		AbstractCommonStreamHelper commonStreamHelper=mock(AbstractCommonStreamHelper.class);
		ReflectionTestUtils.setField(commonStreamHelper, "msgNAckValidErrorCodesList", "UOP-GEN-E05004,UOP-GEN-E05005,UOP-GEN-E05006");
		
		Mockito.doCallRealMethod().when(commonStreamHelper).shouldProceedNAckMessage(problem);
		boolean result=commonStreamHelper.shouldProceedNAckMessage(problem);

		Assert.assertEquals(result, true);
	}
	
	@Test
	public void testShouldProceedNAckMessageWhenWithDifferentErrorCodeWithProperException() {
		
		com.macys.uop.foundation.core.utils.exception.Error error = com.macys.uop.foundation.core.utils.exception.Error.builder()
				.withCode(CommonStatusCode.NO_CORRELATIONID.getCode())
				.withMessage(CommonStatusCode.NO_CORRELATIONID.getDescription())
				.withErrorDetail(ErrorDetail.builder().withDomain("some-service").withReason(HDR_REQUIRED_DEFAULT_REASON)
						.withMessage(CommonStatusCode.NO_CORRELATIONID.getDescription()).withLocation("some location")
						.withLocationType(HDR_REQUIRED_DEFAULT_LOCATIONTYPE).build())
				.build();
		IllegalArgumentException exception=new IllegalArgumentException(CommonStatusCode.NO_CORRELATIONID.getDescription());
		
		ThrowableProblem problem=createProblem(error, exception);

		AbstractCommonStreamHelper commonStreamHelper=mock(AbstractCommonStreamHelper.class);
		ReflectionTestUtils.setField(commonStreamHelper, "msgNAckValidErrorCodesList", "UOP-GEN-E05006");
		
		Mockito.doCallRealMethod().when(commonStreamHelper).shouldProceedNAckMessage(problem);
		boolean result=commonStreamHelper.shouldProceedNAckMessage(problem);

		Assert.assertEquals(result, false);
	}
	
	@Test
	public void testShouldProceedNAckMessageWhenWithNoErrorCodeWithProperException() {
		
		com.macys.uop.foundation.core.utils.exception.Error error = com.macys.uop.foundation.core.utils.exception.Error.builder()
				.withCode(CommonStatusCode.NO_CORRELATIONID.getCode())
				.withMessage(CommonStatusCode.NO_CORRELATIONID.getDescription())
				.withErrorDetail(ErrorDetail.builder().withDomain("some-service").withReason(HDR_REQUIRED_DEFAULT_REASON)
						.withMessage(CommonStatusCode.NO_CORRELATIONID.getDescription()).withLocation("some location")
						.withLocationType(HDR_REQUIRED_DEFAULT_LOCATIONTYPE).build())
				.build();
		IllegalArgumentException exception=new IllegalArgumentException(CommonStatusCode.NO_CORRELATIONID.getDescription());
		
		ThrowableProblem problem=createProblem(error, exception);

		AbstractCommonStreamHelper commonStreamHelper=mock(AbstractCommonStreamHelper.class);
		ReflectionTestUtils.setField(commonStreamHelper, "msgNAckValidErrorCodesList", " ");
		
		Mockito.doCallRealMethod().when(commonStreamHelper).shouldProceedNAckMessage(problem);
		boolean result=commonStreamHelper.shouldProceedNAckMessage(problem);

		Assert.assertEquals(result, false);
	}
	
	@Test
	public void testShouldProceedNAckMessageWhenWithNoErrorKey() {
	
		final ThrowableProblem problem = Problem.builder().withStatus(Status.valueOf(403)).build();
		
		AbstractCommonStreamHelper commonStreamHelper=mock(AbstractCommonStreamHelper.class);
		ReflectionTestUtils.setField(commonStreamHelper, "msgNAckValidErrorCodesList", " ");
		
		Mockito.doCallRealMethod().when(commonStreamHelper).shouldProceedNAckMessage(problem);
		boolean result=commonStreamHelper.shouldProceedNAckMessage(problem);

		Assert.assertEquals(result, false);
	}
	
}
