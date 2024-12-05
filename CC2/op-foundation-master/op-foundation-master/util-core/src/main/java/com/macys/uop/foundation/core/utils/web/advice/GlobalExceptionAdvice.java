package com.macys.uop.foundation.core.utils.web.advice;

import static com.macys.uop.foundation.core.utils.Constant.BAD_REQUEST_BODY_ERROR_DEFAULT_REASON;
import static com.macys.uop.foundation.core.utils.Constant.HDR_REQUIRED_DEFAULT_LOCATIONTYPE;
import static com.macys.uop.foundation.core.utils.Constant.HDR_REQUIRED_DEFAULT_REASON;

import javax.annotation.Nullable;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.zalando.problem.ThrowableProblem;

import com.macys.uop.foundation.core.utils.CommonStatusCode;
import com.macys.uop.foundation.core.utils.epf.EPFMessagePublisher;
import com.macys.uop.foundation.core.utils.exception.Error;
import com.macys.uop.foundation.core.utils.exception.ErrorDetail;
import com.macys.uop.foundation.core.utils.exception.ExceptionUtil;
import com.macys.uop.foundation.core.utils.json.JsonUtils;
import com.macys.uop.foundation.core.utils.ordererror.OrderErrorMessagePublisher;
import com.macys.uop.foundation.core.utils.xml.XmlUtils;

/**
 * The purpose of this class is to implement global exception handler controller advice for handling exceptions. 
 * <br>
 * This class helps in 
 * <ul>
 * <li>Creating/populating the error information</li>
 * <li>Logging exception information</li>
 * <li>Publishing Error information to Order Error and EPF topic</li>
 * </ul>
 */
@ControllerAdvice
public class GlobalExceptionAdvice extends AbstractGlobalExceptionAdvice implements ExceptionUtil {
	
	public GlobalExceptionAdvice(JsonUtils jsonUtils, XmlUtils xmlUtils,
			OrderErrorMessagePublisher orderErrorMessagePublisher, EPFMessagePublisher epfMessagePublisher) {
		super(jsonUtils, xmlUtils, orderErrorMessagePublisher, epfMessagePublisher);
	}
	
	/**
	 * Exception handler for exception type {@link ThrowableProblem}
	 * <p>
	 * {@link ThrowableProblem} is always a user defined known exception. Either thrown by AOP or by code.
	 * 
	 * @param problem {@link ThrowableProblem}
	 * 
	 * @return {@link com.macys.uop.foundation.core.utils.exception.Error}
	 */
	@ExceptionHandler(ThrowableProblem.class)
	public ResponseEntity<com.macys.uop.foundation.core.utils.exception.Error> throwableProblemHandler(
			ThrowableProblem problem) {
		com.macys.uop.foundation.core.utils.exception.Error error = null;
		HttpStatus status = null;
		if (problem.getStatus() != null) {
			status = HttpStatus.valueOf(problem.getStatus().getStatusCode());
		} else {
			status = HttpStatus.BAD_REQUEST;
		}

		if (problem.getParameters().get("error") != null) {
			error = (Error) problem.getParameters().get("error");
		} else {
			error = com.macys.uop.foundation.core.utils.exception.Error.builder()
					.withMessage(problem.getDetail() != null ? problem.getDetail()
							: CommonStatusCode.BAD_REQUEST.getDescription())
					.withCode(CommonStatusCode.BAD_REQUEST.getCode()).build();
		}
		
		logAndPublishErrorEvents(error, problem, status.value());

		return new ResponseEntity<>(error, status);
	}
	
	/**
	 * Exception handler for exception type {@link MissingRequestHeaderException}
	 * <p>
	 * This exception is thrown by Controller class when the referenced header value is missing.
	 * 
	 * @param ex {@link MissingRequestHeaderException}
	 * 
	 * @return {@link com.macys.uop.foundation.core.utils.exception.Error}
	 */
	@ExceptionHandler(MissingRequestHeaderException.class)
	public ResponseEntity<com.macys.uop.foundation.core.utils.exception.Error> missingRequestHeaderExceptionHandler(MissingRequestHeaderException ex) {
		com.macys.uop.foundation.core.utils.exception.Error error = com.macys.uop.foundation.core.utils.exception.Error
				.builder().withCode(CommonStatusCode.MANDATORY_HEADER_MISSING.getCode())
				.withMessage(CommonStatusCode.MANDATORY_HEADER_MISSING.getDescription())
				.withErrorDetail(ErrorDetail.builder()
						.withDomain(getAppName())
						.withReason(HDR_REQUIRED_DEFAULT_REASON)
						.withMessage(getMessage(ex))
						.withLocationType(HDR_REQUIRED_DEFAULT_LOCATIONTYPE)
						.build())
				.build();
		
		logAndPublishErrorEvents(error, ex, HttpStatus.BAD_REQUEST.value());
		
		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}

	/**
	 * Exception handler for exception type generic {@link Exception}
	 * Method exists to handle unexpected exception. 
	 * 
	 * @param ex {@link Exception}
	 * 
	 * @return {@link com.macys.uop.foundation.core.utils.exception.Error}
	 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<com.macys.uop.foundation.core.utils.exception.Error> exceptionHandler(Exception ex) {
		
		com.macys.uop.foundation.core.utils.exception.Error error = com.macys.uop.foundation.core.utils.exception.Error
				.builder().withMessage(getMessage(ex)).withCode(CommonStatusCode.INTERNAL_SERVICE_ERROR.getCode())
				.build();
		
		logAndPublishErrorEvents(error, ex, HttpStatus.INTERNAL_SERVER_ERROR.value());
		
		return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	/**
	 * Customize the response for HttpMessageNotReadableException.
	 * 
	 * @param ex the exception
	 * @param headers the headers to be written to the response
	 * @param status the selected response status
	 * @param request the current request
	 * 
	 * @return a {@code ResponseEntity} instance
	 */
	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(
			HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
		
		com.macys.uop.foundation.core.utils.exception.Error error = com.macys.uop.foundation.core.utils.exception.Error
				.builder().withCode(CommonStatusCode.BAD_REQUEST_BODY.getCode())
				.withMessage(CommonStatusCode.BAD_REQUEST_BODY.getDescription())
				.withErrorDetail(ErrorDetail.builder()
						.withDomain(getAppName())
						.withReason(BAD_REQUEST_BODY_ERROR_DEFAULT_REASON)
						.withMessage(getMessage(ex))
						.build())
				.build();
		
		logAndPublishErrorEvents(error, ex, HttpStatus.BAD_REQUEST.value());

		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}
	
	/**
	 * A single place to customize the response body of all exception types.
	 * 
	 * @param ex the exception
	 * @param body the body for the response
	 * @param headers the headers for the response
	 * @param status the response status
	 * @param request the current request
	 */
	@Override
	protected ResponseEntity<Object> handleExceptionInternal(
			Exception ex, @Nullable Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {

		com.macys.uop.foundation.core.utils.exception.Error error = com.macys.uop.foundation.core.utils.exception.Error
				.builder().withMessage(getMessage(ex)).withCode(CommonStatusCode.INTERNAL_SERVICE_ERROR.getCode())
				.build();
		
		logAndPublishErrorEvents(error, ex, HttpStatus.INTERNAL_SERVER_ERROR.value());
		
		return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
