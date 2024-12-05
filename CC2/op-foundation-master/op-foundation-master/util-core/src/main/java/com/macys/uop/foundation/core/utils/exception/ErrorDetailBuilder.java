package com.macys.uop.foundation.core.utils.exception;

/**
 * Error Detail message builder class having with* methods to populate Error Detail information.
 */
public class ErrorDetailBuilder {
	
	private String domain;
	private String reason;
	private String message;
	private String locationType;
	private String location;
	
	/**
	 *  This method constructs {@link ErrorDetail} instance
	 *  
	 * @return {@link ErrorDetail}
	 */
	public ErrorDetail build() {
		ErrorDetail errorDetail=new ErrorDetail();
		errorDetail.setDomain(domain);
		errorDetail.setLocation(location);
		errorDetail.setLocationType(locationType);
		errorDetail.setMessage(message);
		errorDetail.setReason(reason);
		return errorDetail;
	}
	
	public ErrorDetailBuilder withDomain(String domain) {
		this.domain = domain;
		return this;
	}
	
	public ErrorDetailBuilder withReason(String reason) {
		this.reason = reason;
		return this;
	}
	
	public ErrorDetailBuilder withMessage(String message) {
		this.message = message;
		return this;
	}
	
	public ErrorDetailBuilder withLocationType(String locationType) {
		this.locationType = locationType;
		return this;
	}
	
	public ErrorDetailBuilder withLocation(String location) {
		this.location = location;
		return this;
	}
	
	
	
}
