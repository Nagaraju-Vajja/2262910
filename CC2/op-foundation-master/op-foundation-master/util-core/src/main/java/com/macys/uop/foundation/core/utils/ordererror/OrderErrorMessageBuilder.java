package com.macys.uop.foundation.core.utils.ordererror;

import java.util.HashMap;
import java.util.Map;

/**
 * Actual order error message builder class having with* methods to populate order error information.
 */
public class OrderErrorMessageBuilder 
{
	private String orderId;
	private String correlationId;
	private String createdBy;
	private String createdTs;
	private String entityRefId;
	private String entityRefType;
	private String errorCode;
	private String errorDesc;
	private String lastUpdatedTs;
	private String message;
	private String serviceName;
	private String statusCode;
	private String statusDesc;
	private String lastUpdatedBy;
	private String stackTrace;
	private String topicName;
	private String url;
	private String messageContentType;
	
	private String publishToChannel;
	private Map<String,String> messageHeaders=new HashMap<>();
	
	public OrderErrorMessage build() {
		return constructOrderErrorMessage();
	}
	
	/**
	 * Creates {@link OrderErrorMessage} instance
	 * 
	 * @return {@link OrderErrorMessage}
	 */
	private OrderErrorMessage constructOrderErrorMessage() 
	{
		OrderErrorMessage oeMessage=new OrderErrorMessage();
		oeMessage.setOrderId(orderId);
		oeMessage.setCorrelationId(correlationId);
		oeMessage.setCreatedBy(createdBy);
		oeMessage.setCreatedTs(createdTs);
		oeMessage.setEntityRefId(entityRefId);
		oeMessage.setEntityRefType(entityRefType);
		oeMessage.setErrorCode(errorCode);
		oeMessage.setErrorDesc(errorDesc);
		oeMessage.setLastUpdatedTs(lastUpdatedTs);
		oeMessage.setMessage(message);
		oeMessage.setServiceName(serviceName);
		oeMessage.setStatusCode(statusCode);
		oeMessage.setStatusDesc(statusDesc);
		oeMessage.setLastUpdatedBy(lastUpdatedBy);
		oeMessage.setStackTrace(stackTrace);
		oeMessage.setTopicName(topicName);
		oeMessage.setUrl(url);
		oeMessage.setMessageContentType(messageContentType);
		
		oeMessage.setPublishToChannel(publishToChannel);
		oeMessage.setMessageHeaders(messageHeaders);
		
		return oeMessage;
	}
	
	public OrderErrorMessageBuilder withOrderId(final String orderId) {
		this.orderId = orderId;
		return this;
	}
	
	public OrderErrorMessageBuilder withCorrelationId(final String correlationId) {
		this.correlationId = correlationId;
		return this;
	}
	
	public OrderErrorMessageBuilder withCreatedBy(final String createdBy) {
		this.createdBy = createdBy;
		return this;
	}
	
	public OrderErrorMessageBuilder withCreatedTs(final String createdTs) {
		this.createdTs = createdTs;
		return this;
	}
	
	public OrderErrorMessageBuilder withEntityRefId(final String entityRefId) {
		this.entityRefId = entityRefId;
		return this;
	}
	
	public OrderErrorMessageBuilder withEntityRefType(final String entityRefType) {
		this.entityRefType = entityRefType;
		return this;
	}
	
	public OrderErrorMessageBuilder withErrorCode(final String errorCode) {
		this.errorCode = errorCode;
		return this;
	}
	
	public OrderErrorMessageBuilder withErrorDesc(final String errorDesc) {
		this.errorDesc = errorDesc;
		return this;
	}
	
	public OrderErrorMessageBuilder withLastUpdatedTs(final String lastUpdatedTs) {
		this.lastUpdatedTs = lastUpdatedTs;
		return this;
	}
	
	public OrderErrorMessageBuilder withMessage(final String message) {
		this.message = message;
		return this;
	}
	
	public OrderErrorMessageBuilder withServiceName(final String serviceName) {
		this.serviceName = serviceName;
		return this;
	}
	
	public OrderErrorMessageBuilder withStatusCode(final String statusCode) {
		this.statusCode = statusCode;
		return this;
	}
	
	public OrderErrorMessageBuilder withStatusDesc(final String statusDesc) {
		this.statusDesc = statusDesc;
		return this;
	}
	
	public OrderErrorMessageBuilder withLastUpdatedBy(final String lastUpdatedBy) {
		this.lastUpdatedBy = lastUpdatedBy;
		return this;
	}
	
	public OrderErrorMessageBuilder withStackTrace(final String stackTrace) {
		this.stackTrace = stackTrace;
		return this;
	}
	
	public OrderErrorMessageBuilder withTopicName(final String topicName) {
		this.topicName = topicName;
		return this;
	}
	
	public OrderErrorMessageBuilder withUrl(final String url) {
		this.url = url;
		return this;
	}
	
	public OrderErrorMessageBuilder withMessageContentType(final String messageContentType) {
		this.messageContentType = messageContentType;
		return this;
	}
	
	public OrderErrorMessageBuilder withPublishToChannel(final String publishToChannel) {
		this.publishToChannel = publishToChannel;
		return this;
	}
	
	public OrderErrorMessageBuilder withMessageHeader(final String key, final String value) {
		this.messageHeaders.put(key, value);
		return this;
	}
	
}
