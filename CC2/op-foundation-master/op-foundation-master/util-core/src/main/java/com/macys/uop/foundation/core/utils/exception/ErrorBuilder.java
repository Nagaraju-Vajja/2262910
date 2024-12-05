package com.macys.uop.foundation.core.utils.exception;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import com.macys.uop.foundation.core.utils.CommonStatusCode;
/**
 * Provides Builder Pattern to construct {@link Error} and {@link ErrorDetail} 
 * <br>
 * with* methods are part of building {@link Error} information 
 */
public class ErrorBuilder implements ProblemUtil {
	private String code;
	private String message;
	private String referenceId;
	private String referenceType;
	private List<ErrorDetail> errorDetails = new ArrayList<>();
	
	/**
	 * Builds the {@link Error} object with mandatory field checking.
	 * Throws exception if mandatory value not set.
	 * 
	 * @return Error
	 */
	public Error build() {
		Assert.hasText(code, "'code' must not be empty");
		if (StringUtils.isEmpty(message)) {
			message = CommonStatusCode.INTERNAL_SERVICE_ERROR.getDescription();
		}
		
		errorDetails.stream().forEach(errorDetail -> {
			Assert.hasText(errorDetail.getDomain(), "'domain' must not be empty");
			Assert.hasText(errorDetail.getReason(), "'reason' must not be empty");
			if (StringUtils.isEmpty(errorDetail.getMessage())) {
				errorDetail.setMessage(CommonStatusCode.INTERNAL_SERVICE_ERROR.getDescription());
			}
		});
		

		Error error = new Error();
		error.setCode(code);
		error.setMessage(message);
		error.setReferenceId(referenceId);
		error.setReferenceType(referenceType);
		error.setErrorDetails(errorDetails);
		return error;
	}

	public ErrorBuilder withCode(String code) {
		this.code = code;
		return this;
	}

	public ErrorBuilder withMessage(String message) {
		this.message = message;
		return this;
	}

	public ErrorBuilder withReferenceId(String referenceId) {
		this.referenceId = referenceId;
		return this;
	}

	public ErrorBuilder withReferenceType(String referenceType) {
		this.referenceType = referenceType;
		return this;
	}
	
	public ErrorBuilder withError(String code, String message) {
		this.code = code;
		this.message = message;
		return this;
	}

	public ErrorBuilder withErrorDetail(ErrorDetail errorDetail) {
		errorDetails.add(errorDetail);
		return this;
	}
	
	public ErrorBuilder withErrorDetails(List<ErrorDetail> errorDetailsList) {
		errorDetails.addAll(errorDetailsList);
		return this;
	}

	public ErrorBuilder withErrorDetail(String domain, String reason, String message) {
		errorDetails.add(ErrorDetail.builder().withDomain(domain).withReason(reason).withMessage(message).build());
		return this;
	}

	public ErrorBuilder withErrorDetail(String domain, String reason, String message, String locationType,
			String location) {
		errorDetails.add(ErrorDetail.builder().withDomain(domain).withReason(reason).withMessage(message)
				.withLocationType(locationType).withLocation(location).build());
		return this;
	}

}
