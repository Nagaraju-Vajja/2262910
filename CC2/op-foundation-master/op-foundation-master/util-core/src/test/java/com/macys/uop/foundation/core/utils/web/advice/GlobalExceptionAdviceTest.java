package com.macys.uop.foundation.core.utils.web.advice;

import static com.macys.uop.foundation.core.utils.Constant.HDR_REQUIRED_DEFAULT_LOCATIONTYPE;
import static com.macys.uop.foundation.core.utils.Constant.HDR_REQUIRED_DEFAULT_REASON;
import static com.macys.uop.foundation.core.utils.Constant.PROBLEM_ERROR_KEY;
import static org.mockito.Mockito.mock;

import java.lang.reflect.Method;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.http.MockHttpInputMessage;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.context.request.WebRequest;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import org.zalando.problem.ThrowableProblem;

import com.macys.uop.foundation.core.utils.CommonStatusCode;
import com.macys.uop.foundation.core.utils.epf.EPFMessagePublisher;
import com.macys.uop.foundation.core.utils.exception.ErrorDetail;
import com.macys.uop.foundation.core.utils.json.JsonUtils;
import com.macys.uop.foundation.core.utils.ordererror.OrderErrorMessagePublisher;
import com.macys.uop.foundation.core.utils.test.model.Employee;
import com.macys.uop.foundation.core.utils.xml.XmlUtils;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringRunner.class)
public class GlobalExceptionAdviceTest {
	
	@MockBean
	private JsonUtils jsonUtils;
	
	@MockBean
	private XmlUtils xmlUtils;
	
	@MockBean
	private OrderErrorMessagePublisher orderErrorMessagePublisher;
	
	@MockBean
	private EPFMessagePublisher epfMessagePublisher;
	
	@Test
	public void testConstructor() {
		new GlobalExceptionAdvice(jsonUtils, xmlUtils, orderErrorMessagePublisher, epfMessagePublisher);
		Assert.assertNotEquals("expected", "actual");
	}
	
	@Test
	public void testThrowableProblemHandler() {
		
		com.macys.uop.foundation.core.utils.exception.Error error = com.macys.uop.foundation.core.utils.exception.Error.builder()
				.withCode(CommonStatusCode.NO_CORRELATIONID.getCode())
				.withMessage(CommonStatusCode.NO_CORRELATIONID.getDescription())
				.withErrorDetail(ErrorDetail.builder().withDomain("some-service").withReason(HDR_REQUIRED_DEFAULT_REASON)
						.withMessage(CommonStatusCode.NO_CORRELATIONID.getDescription()).withLocation("some location")
						.withLocationType(HDR_REQUIRED_DEFAULT_LOCATIONTYPE).build())
				.build();
		
		ThrowableProblem problem=Problem.builder().withStatus(Status.BAD_REQUEST).with(PROBLEM_ERROR_KEY, error).build();
		
		GlobalExceptionAdvice globalExceptionAdvice=mock(GlobalExceptionAdvice.class);
		
		ResponseEntity<com.macys.uop.foundation.core.utils.exception.Error> responseEntity=new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
		
		Mockito.doNothing().when(globalExceptionAdvice).logAndPublishErrorEvents(error, problem, 500);
		
		Mockito.doCallRealMethod().doReturn(responseEntity).when(globalExceptionAdvice).throwableProblemHandler(problem);
		
		ResponseEntity<com.macys.uop.foundation.core.utils.exception.Error> resultEntity = globalExceptionAdvice.throwableProblemHandler(problem);
		
		Assert.assertEquals(responseEntity.getStatusCode(), resultEntity.getStatusCode());
	}
	
	@Test
	public void testThrowableProblemHandlerStatusNull() {
		
		com.macys.uop.foundation.core.utils.exception.Error error = com.macys.uop.foundation.core.utils.exception.Error.builder()
				.withCode(CommonStatusCode.NO_CORRELATIONID.getCode())
				.withMessage(CommonStatusCode.NO_CORRELATIONID.getDescription())
				.withErrorDetail(ErrorDetail.builder().withDomain("some-service").withReason(HDR_REQUIRED_DEFAULT_REASON)
						.withMessage(CommonStatusCode.NO_CORRELATIONID.getDescription()).withLocation("some location")
						.withLocationType(HDR_REQUIRED_DEFAULT_LOCATIONTYPE).build())
				.build();
		
		ThrowableProblem problem=Problem.builder().build();
		
		GlobalExceptionAdvice globalExceptionAdvice=mock(GlobalExceptionAdvice.class);
		
		ResponseEntity<com.macys.uop.foundation.core.utils.exception.Error> responseEntity=new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
		
		Mockito.doNothing().when(globalExceptionAdvice).logAndPublishErrorEvents(error, problem, 500);
		
		Mockito.doCallRealMethod().doReturn(responseEntity).when(globalExceptionAdvice).throwableProblemHandler(problem);
		
		ResponseEntity<com.macys.uop.foundation.core.utils.exception.Error> resultEntity = globalExceptionAdvice.throwableProblemHandler(problem);
		
		Assert.assertEquals(responseEntity.getStatusCode(), resultEntity.getStatusCode());
	}
	
	@Test
	public void testMissingRequestHeaderExceptionHandler() {
		
		com.macys.uop.foundation.core.utils.exception.Error error = com.macys.uop.foundation.core.utils.exception.Error.builder()
				.withCode(CommonStatusCode.NO_CORRELATIONID.getCode())
				.withMessage(CommonStatusCode.NO_CORRELATIONID.getDescription())
				.withErrorDetail(ErrorDetail.builder().withDomain("some-service").withReason(HDR_REQUIRED_DEFAULT_REASON)
						.withMessage(CommonStatusCode.NO_CORRELATIONID.getDescription()).withLocation("some location")
						.withLocationType(HDR_REQUIRED_DEFAULT_LOCATIONTYPE).build())
				.build();
		
		ThrowableProblem problem=Problem.builder().build();
		
		GlobalExceptionAdvice globalExceptionAdvice=mock(GlobalExceptionAdvice.class);
		
		ResponseEntity<com.macys.uop.foundation.core.utils.exception.Error> responseEntity=new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
		
		Mockito.doReturn("some-service").when(globalExceptionAdvice).getAppName();
		Mockito.doNothing().when(globalExceptionAdvice).logAndPublishErrorEvents(error, problem, 500);
		Method method=Employee.class.getMethods()[0];
		MethodParameter parameter=new MethodParameter(method, -1);
		MissingRequestHeaderException ex=new MissingRequestHeaderException("myHeader", parameter);
		
		Mockito.doCallRealMethod().doReturn("Missing request header 'myHeader' for method parameter of type String").when(globalExceptionAdvice).getMessage(ex);
		Mockito.doCallRealMethod().doReturn(responseEntity).when(globalExceptionAdvice).missingRequestHeaderExceptionHandler(ex);
		
		ResponseEntity<com.macys.uop.foundation.core.utils.exception.Error> resultEntity = globalExceptionAdvice.missingRequestHeaderExceptionHandler(ex);
		
		Assert.assertEquals(responseEntity.getStatusCode(), resultEntity.getStatusCode());
	}
	
	@Test
	public void testExceptionHandler() {
		
		com.macys.uop.foundation.core.utils.exception.Error error = com.macys.uop.foundation.core.utils.exception.Error.builder()
				.withCode(CommonStatusCode.NO_CORRELATIONID.getCode())
				.withMessage(CommonStatusCode.NO_CORRELATIONID.getDescription())
				.withErrorDetail(ErrorDetail.builder().withDomain("some-service").withReason(HDR_REQUIRED_DEFAULT_REASON)
						.withMessage(CommonStatusCode.NO_CORRELATIONID.getDescription()).withLocation("some location")
						.withLocationType(HDR_REQUIRED_DEFAULT_LOCATIONTYPE).build())
				.build();
		
		ThrowableProblem problem=Problem.builder().withStatus(Status.INTERNAL_SERVER_ERROR).with(PROBLEM_ERROR_KEY, error).build();
		
		GlobalExceptionAdvice globalExceptionAdvice=mock(GlobalExceptionAdvice.class);
		
		ResponseEntity<com.macys.uop.foundation.core.utils.exception.Error> responseEntity=new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
		
		Mockito.doNothing().when(globalExceptionAdvice).logAndPublishErrorEvents(error, problem, 500);
		
		Exception ex=new RuntimeException("Some Exception");
		
		Mockito.doCallRealMethod().doReturn("Some Exception").when(globalExceptionAdvice).getMessage(ex);
		Mockito.doCallRealMethod().doReturn(responseEntity).when(globalExceptionAdvice).exceptionHandler(ex);
		
		ResponseEntity<com.macys.uop.foundation.core.utils.exception.Error> resultEntity = globalExceptionAdvice.exceptionHandler(ex);
		
		Assert.assertEquals(responseEntity.getStatusCode(), resultEntity.getStatusCode());
	}
	
	@Test
	public void testHandleHttpMessageNotReadable() {
		
		com.macys.uop.foundation.core.utils.exception.Error error = com.macys.uop.foundation.core.utils.exception.Error.builder()
				.withCode(CommonStatusCode.NO_CORRELATIONID.getCode())
				.withMessage(CommonStatusCode.NO_CORRELATIONID.getDescription())
				.withErrorDetail(ErrorDetail.builder().withDomain("some-service").withReason(HDR_REQUIRED_DEFAULT_REASON)
						.withMessage(CommonStatusCode.NO_CORRELATIONID.getDescription()).withLocation("some location")
						.withLocationType(HDR_REQUIRED_DEFAULT_LOCATIONTYPE).build())
				.build();
		
		ResponseEntity<com.macys.uop.foundation.core.utils.exception.Error> responseEntity=new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
		
		GlobalExceptionAdvice globalExceptionAdvice=mock(GlobalExceptionAdvice.class);
		
		HttpInputMessage httpInputMessage=new MockHttpInputMessage("Some Message".getBytes());
		
		HttpMessageNotReadableException ex=new HttpMessageNotReadableException("Some Message", httpInputMessage);
		HttpHeaders headers=new HttpHeaders();
		HttpStatus status=HttpStatus.valueOf(400);
		WebRequest request=mock(WebRequest.class);
		
		ThrowableProblem problem=Problem.builder().build();
		Mockito.doReturn("some-service").when(globalExceptionAdvice).getAppName();
		Mockito.doNothing().when(globalExceptionAdvice).logAndPublishErrorEvents(error, problem, 500);
		
		Mockito.doCallRealMethod().doReturn("Some Message").when(globalExceptionAdvice).getMessage(ex);
		Mockito.doCallRealMethod().when(globalExceptionAdvice).handleHttpMessageNotReadable(ex, headers, status, request);
		
		ResponseEntity<Object> resultEntity = globalExceptionAdvice.handleHttpMessageNotReadable(ex, headers, status, request);
		
		Assert.assertEquals(responseEntity.getStatusCode(), resultEntity.getStatusCode());
	}
	
	@Test
	public void testHandleExceptionInternal() {
		
		com.macys.uop.foundation.core.utils.exception.Error error = com.macys.uop.foundation.core.utils.exception.Error.builder()
				.withCode(CommonStatusCode.NO_CORRELATIONID.getCode())
				.withMessage(CommonStatusCode.NO_CORRELATIONID.getDescription())
				.withErrorDetail(ErrorDetail.builder().withDomain("some-service").withReason(HDR_REQUIRED_DEFAULT_REASON)
						.withMessage(CommonStatusCode.NO_CORRELATIONID.getDescription()).withLocation("some location")
						.withLocationType(HDR_REQUIRED_DEFAULT_LOCATIONTYPE).build())
				.build();
		
		ResponseEntity<com.macys.uop.foundation.core.utils.exception.Error> responseEntity=new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
		
		GlobalExceptionAdvice globalExceptionAdvice=mock(GlobalExceptionAdvice.class);
		
		HttpInputMessage httpInputMessage=new MockHttpInputMessage("Some Message".getBytes());
		
		HttpMessageNotReadableException ex=new HttpMessageNotReadableException("Some Message", httpInputMessage);
		HttpHeaders headers=new HttpHeaders();
		HttpStatus status=HttpStatus.valueOf(500);
		WebRequest request=mock(WebRequest.class);
		
		ThrowableProblem problem=Problem.builder().build();
		Mockito.doReturn("some-service").when(globalExceptionAdvice).getAppName();
		Mockito.doNothing().when(globalExceptionAdvice).logAndPublishErrorEvents(error, problem, 500);
		
		Mockito.doCallRealMethod().doReturn("Some Exception").when(globalExceptionAdvice).getMessage(ex);
		Mockito.doCallRealMethod().when(globalExceptionAdvice).handleExceptionInternal(ex, "", headers, status, request);
		
		ResponseEntity<Object> resultEntity = globalExceptionAdvice.handleExceptionInternal(ex, "", headers, status, request);
		
		Assert.assertEquals(responseEntity.getStatusCode(), resultEntity.getStatusCode());
	}
}
