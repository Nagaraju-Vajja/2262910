package com.macys.uop.order.ordercollectorchestrator.service.impl;

import static com.macys.uop.foundation.core.utils.Constant.*;
import static com.macys.uop.order.ordercollectorchestrator.utils.OrdercollectorchestratorConstants.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import com.macys.uop.foundation.core.utils.exception.Error;
import com.macys.uop.foundation.core.utils.exception.ErrorDetail;
import com.macys.uop.foundation.core.utils.json.JsonUtils;
import com.macys.uop.foundation.core.utils.task.StepExecution;
import com.macys.uop.foundation.core.utils.task.TaskExecution;
import com.macys.uop.foundation.core.utils.test.TestContextUtil;
import com.macys.uop.order.ordercollectorchestrator.model.ErrorProcessorRequest;
import com.macys.uop.order.ordercollectorchestrator.publisher.IErrorprocessorErrorrequestPublisher;
import com.macys.uop.order.ordercollectorchestrator.service.IErrorprocessorService;
import com.macys.uop.order.ordercollectorchestrator.utils.OrderErrorCodes;
import com.macys.uop.order.ordercollectorchestrator.utils.OrdercollectorchestratorUtil;
import com.macys.uop.order.ordercollectorchestrator.utils.ValidatorUtil;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.HttpServerErrorException;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=ErrorprocessorServiceImpl.class)
public class ErrorprocessorServiceImplTest implements TestContextUtil,
	OrdercollectorchestratorUtil {

	@Autowired
	private IErrorprocessorService errorprocessorService;

	@MockBean
	private JsonUtils jsonUtils;

	@MockBean
	private ValidatorUtil validatorUtil;

	@MockBean
	private IErrorprocessorErrorrequestPublisher errorprocessorPublisher;

	@Before
	public void beforeTest() {
		initContext();

		Map<String, String> propertyMap = new HashMap<>();
		propertyMap.put("event", "event-topic");
		propertyMap.put("ordercollectorchestrator", "create-topic");
		propertyMap.put("selfretrymaxcount", "1");
		propertyMap.put("selfretryduration", "1");

		HttpHeaders headers = new HttpHeaders();
		headers.add(ORDERID_HDR, "123123");
		headers.add(CLIENTID_HDR, "ordercollectorchestrator");
		headers.add(CORRELATIONID_HDR, "123123");
		headers.add(MESSAGEID_HDR, "234234");
		getServiceRequestContext().setHeaders(headers);
		getServiceRequestContext().setApplicationName("ordercollectorchestrator");
	}

	@After
	public void afterTest() {
		clearContext();
	}

	private Map<String, String> defaultMessageHeader() {
		Map<String, String> messageHeaders = new HashMap<>();
		messageHeaders.put(ORDERID_HDR, getOrderId());
		messageHeaders.put(CLIENTID_HDR, getAppName());
		messageHeaders.put(CORRELATIONID_HDR, getCorrelationId());
		messageHeaders.put(MESSAGEID_HDR, getMessageId());

		return messageHeaders;
	}

	@Test
	public void testRetryLater() throws ExecutionException, InterruptedException {
		TaskExecution taskExecution = new TaskExecution();
		taskExecution.setId("taskid");
		taskExecution.setError(new HttpServerErrorException(HttpStatus.SERVICE_UNAVAILABLE));

		Map<String, String> headers = defaultMessageHeader();
		headers.put(RETRY, TRUE);

		Mockito.when(jsonUtils.convertToJson(Mockito.any())).thenReturn("String");
		Mockito.when(errorprocessorPublisher.publishMessage(Mockito.any(), Mockito.any())).thenReturn("OK");
		Mockito.when(validatorUtil.getProperty(Mockito.anyString())).thenReturn("1");
		ErrorProcessorRequest request = ErrorProcessorRequest.builder()
			.error(getErrorObject(OrderErrorCodes.CONNECTION_ERROR, ORDER_CREATE, null))
			.statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
			.failedState(ORDER_CREATE)
			.payload("payload")
			.referenceType(ORDERID_HDR)
			.referenceId("1")
			.errorType(ET)
			.headers(headers).build();
		assertEquals("OK", errorprocessorService.retryLater(request));
	}

	@Test
	public void testRetryLater_Pubsub() throws ExecutionException, InterruptedException {

		TaskExecution taskExecution = new TaskExecution();
		taskExecution.setId("taskid");
		StepExecution stepExecution = new StepExecution();
		stepExecution.setExecutionId("id");
		stepExecution.setStatus("FAILURE");
		stepExecution.setName("event");
		taskExecution.setStepExecutions(List.of(stepExecution));
		taskExecution.setError(new InterruptedException("interrupted while publishing"));

		Mockito.when(jsonUtils.convertToJson(Mockito.any())).thenReturn("String");
		Mockito.when(errorprocessorPublisher.publishMessage(Mockito.any(), Mockito.any())).thenReturn("OK");
		Mockito.when(validatorUtil.getProperty(Mockito.anyString())).thenReturn("1");
		ErrorProcessorRequest request = ErrorProcessorRequest.builder()
			.error(getErrorObject(OrderErrorCodes.PUBLISH_ORDERCREATION_ERROR, ORDER_LOGEVENT, null))
			.statusCode(HttpStatus.FORBIDDEN.value())
			.failedState(ORDER_LOGEVENT)
			.payload("payload")
			.referenceType(ORDERID_HDR)
			.referenceId("1")
			.errorType(ET)
			.headers(defaultMessageHeader()).build();
		assertEquals("OK", errorprocessorService.retryLater(request));
	}

	@Test
	public void testRetryLaterException() throws ExecutionException, InterruptedException {

		TaskExecution taskExecution = new TaskExecution();
		taskExecution.setId("taskid");
		taskExecution.setError(new InterruptedException("interrupted while publishing"));

		Error error = getErrorObject(OrderErrorCodes.PUBLISH_ORDERCREATION_ERROR, ORDER_PUBLISHCREATESUCCESS, "locationType");
		error.setErrorDetails(null);

		Map<String, String> headers = defaultMessageHeader();
		headers.put(RETRY, "false");

		Mockito.when(jsonUtils.convertToJson(Mockito.any())).thenReturn("MessageString");
		Mockito.when(errorprocessorPublisher.publishMessage(Mockito.any(), Mockito.any())).thenThrow(new ExecutionException(new Throwable("TEST")));
		Mockito.when(validatorUtil.getProperty(Mockito.anyString())).thenReturn("1");
		ErrorProcessorRequest request = ErrorProcessorRequest.builder()
			.error(getErrorObject(OrderErrorCodes.PUBLISH_ORDERCREATION_ERROR, ORDER_PUBLISHCREATESUCCESS, null))
			.statusCode(HttpStatus.BAD_REQUEST.value())
			.failedState(ORDER_PUBLISHCREATESUCCESS)
			.payload("payload")
			.referenceType(ORDERID_HDR)
			.referenceId("1")
			.errorType(ST)
			.headers(defaultMessageHeader()).build();
		assertThrows(Exception.class, () -> errorprocessorService.retryLater(request));
	}

	@Test
	public void testRetryLaterExceptionWithErrorDetailNull() throws ExecutionException, InterruptedException {

		TaskExecution taskExecution = new TaskExecution();
		taskExecution.setId("taskid");
		taskExecution.setError(new InterruptedException("interrupted while publishing"));
		List<ErrorDetail> list = Collections.<ErrorDetail>emptyList();

		Error error = getErrorObject(OrderErrorCodes.PUBLISH_ORDERCREATION_ERROR, ORDER_PUBLISHCREATESUCCESS, "locationType");
		error.setErrorDetails(list);

		Map<String, String> headers = defaultMessageHeader();
		headers.put(RETRY, "true");

		Mockito.when(jsonUtils.convertToJson(Mockito.any())).thenReturn("MessageString");
		Mockito.when(errorprocessorPublisher.publishMessage(Mockito.any(), Mockito.any())).thenThrow(new InterruptedException("TEST"));
		Mockito.when(validatorUtil.getProperty(Mockito.anyString())).thenReturn("1");
		ErrorProcessorRequest request = ErrorProcessorRequest.builder()
			.error(getErrorObject(OrderErrorCodes.PUBLISH_ORDERCREATION_ERROR, ORDER_PUBLISHCREATESUCCESS, null))
			.statusCode(HttpStatus.BAD_REQUEST.value())
			.failedState(ORDER_PUBLISHCREATESUCCESS)
			.payload("payload")
			.referenceType(ORDERID_HDR)
			.referenceId("1")
			.errorType(ST)
			.headers(defaultMessageHeader()).build();
		assertThrows(InterruptedException.class, () -> errorprocessorService.retryLater(request));
	}

	@Test
	public void testRetryLaterException_nullretry() throws ExecutionException, InterruptedException {

		TaskExecution taskExecution = new TaskExecution();
		taskExecution.setId("taskid");
		StepExecution stepExecution = new StepExecution();
		stepExecution.setExecutionId("id");
		stepExecution.setStatus("FAILURE");
		stepExecution.setName("ordercollectorchestrator");
		taskExecution.setStepExecutions(List.of(stepExecution));
		taskExecution.setError(new InterruptedException("interrupted while publishing"));

		Mockito.when(jsonUtils.convertToJson(Mockito.any())).thenReturn("MessageString");
		Mockito.when(errorprocessorPublisher.publishMessage(Mockito.any(), Mockito.any())).thenReturn("OK");
		Mockito.when(validatorUtil.getProperty(Mockito.anyString())).thenReturn("1");
		ErrorProcessorRequest request = ErrorProcessorRequest.builder()
			.error(getErrorObject(OrderErrorCodes.PUBLISH_ORDERCREATION_ERROR, ORDER_PUBLISHCREATESUCCESS, null))
			.statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
			.failedState(ORDER_PUBLISHCREATESUCCESS)
			.payload("payload")
			.referenceType(ORDERID_HDR)
			.referenceId("1")
			.errorType(ST)
			.headers(defaultMessageHeader()).build();
		assertEquals("OK",errorprocessorService.retryLater(request));
	}

	@Test
	public void testRetryLater1() throws ExecutionException, InterruptedException {
		TaskExecution taskExecution = new TaskExecution();
		taskExecution.setId("taskid");
		taskExecution.setError(new HttpServerErrorException(HttpStatus.SERVICE_UNAVAILABLE));

		Map<String, String> headers = defaultMessageHeader();
		headers.put(RETRY, TRUE);

		Mockito.when(jsonUtils.convertToJson(Mockito.any())).thenReturn("MessageString");
		Mockito.when(errorprocessorPublisher.publishMessage(Mockito.any(), Mockito.any())).thenReturn("OK");
		Mockito.when(validatorUtil.getProperty(Mockito.anyString())).thenReturn("1");
		ErrorProcessorRequest request = ErrorProcessorRequest.builder()
			.error(getErrorObject(OrderErrorCodes.CONNECTION_ERROR, ORDER_ENRICH, "note"))
			.statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
			.failedState(ORDER_ENRICH)
			.payload("payload")
			.referenceType(ORDERID_HDR)
			.referenceId("1")
			.errorType(ET)
			.headers(headers).build();
		assertEquals("OK",errorprocessorService.retryLater(request));
	}

	private Error getErrorObject(OrderErrorCodes errorCodes, String location, String locationType) {
		return Error.builder()
					.withCode(errorCodes.getCode())
					.withMessage(errorCodes.getDescription())
					.withErrorDetail(ErrorDetail.builder()
						.withDomain("domain")
						.withReason(errorCodes.getDescription())
						.withMessage("errorMessage")
						.withLocation(location)
						.withLocationType(locationType)
						.build())
					.build();
	}
}
