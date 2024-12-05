package com.macys.uop.foundation.core.utils.eventlog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.Assert;

/**
 * Actual eventlog message builder class having with* methods to populate eventlog information.
 * <br>
 * {@link #build()} method validates mandatory parameter values and throws {@link IllegalArgumentException} in case of empty value
 */
public class EventLogMessageBuilder 
{
	private String createdBy; // Mandatory
	private String transactionId;
	private String transactionDesc;
	private String transactionTime; // Mandatory
	private String channelName; // Mandatory
	private Map<String,String> headers=new HashMap<>(); // Mandatory
	private String requestType;
	private String requestPayload; // Mandatory
	private String responsePayload;
	private String channelType; // Mandatory
	private String responseTs;
	private List<EventDetail> eventDetails=new ArrayList<>();
	private String statusCode;
	private String statusDesc;
	private String subClientId;
	private String status;
	
	/**
	 * This method constructs {@link EventLogMessage} instance there by validates mandatory parameter values 
	 * and throws {@link IllegalArgumentException} in case of empty value.
	 *  
	 * @return {@Link EventLogMessage} instance
	 */
	public EventLogMessage build() {
		Assert.hasText(createdBy, "'createdBy' must not be empty");
		Assert.hasText(transactionTime, "'transactionTime' must not be empty");
		Assert.hasText(channelName, "'channelName' must not be empty");
		Assert.notEmpty(headers, "'headers' must not be empty");
		Assert.hasText(requestPayload, "'requestPayload' must not be empty");
		Assert.hasText(channelType, "'channelType' must not be empty");
		
		return constructEventLogMessage();
	}
	
	/**
	 * Creates {@link EventLogMessage} instance
	 * 
	 * @return {@link EventLogMessage}
	 */
	private EventLogMessage constructEventLogMessage() {
		EventLogMessage eventLogMessage=new EventLogMessage();
		
		eventLogMessage.setCreatedBy(createdBy);
		eventLogMessage.setTransactionId(transactionId);
		eventLogMessage.setTransactionDesc(transactionDesc);
		eventLogMessage.setTransactionTime(transactionTime);
		eventLogMessage.setResponseTs(responseTs);
		eventLogMessage.setChannelName(channelName);
		eventLogMessage.setChannelType(channelType);
		eventLogMessage.setHeaders(headers);
		eventLogMessage.setEventDetails(eventDetails);
		eventLogMessage.setRequestType(requestType);
		eventLogMessage.setRequestPayload(requestPayload);
		eventLogMessage.setResponsePayload(responsePayload);
		eventLogMessage.setStatusCode(statusCode);
		eventLogMessage.setStatusDesc(statusDesc);
		eventLogMessage.setSubClientId(subClientId);
		eventLogMessage.setStatus(status);
		
		return eventLogMessage;
	}
	
	public EventLogMessageBuilder withCreatedBy(final String createdBy) {
		this.createdBy = createdBy;
		return this;
	}
	
	public EventLogMessageBuilder withTransactionId(final String transactionId) {
		this.transactionId = transactionId;
		return this;
	}
	
	public EventLogMessageBuilder withTransactionDesc(final String transactionDesc) {
		this.transactionDesc = transactionDesc;
		return this;
	}
	
	public EventLogMessageBuilder withTransactionTime(final String transactionTime) {
		this.transactionTime = transactionTime;
		return this;
	}
	
	public EventLogMessageBuilder withResponseTs(final String responseTs) {
		this.responseTs = responseTs;
		return this;
	}
	
	public EventLogMessageBuilder withChannelName(final String channelName) {
		this.channelName = channelName;
		return this;
	}
	
	public EventLogMessageBuilder withChannelType(final String channelType) {
		this.channelType = channelType;
		return this;
	}
	
	public EventLogMessageBuilder withHeader(final String key, final String value) {
		this.headers.put(key, value);
		return this;
	}
	
	public EventLogMessageBuilder withEventDetail(final String referenceId, final String referenceType) {
		this.eventDetails.add(new EventDetail(referenceId, referenceType));
		return this;
	}
	
	public EventLogMessageBuilder withRequestType(final String requestType) {
		this.requestType = requestType;
		return this;
	}
	
	public EventLogMessageBuilder withRequestPayload(final String requestPayload) {
		this.requestPayload = requestPayload;
		return this;
	}
	
	public EventLogMessageBuilder withResponsePayload(final String responsePayload) {
		this.responsePayload = responsePayload;
		return this;
	}
	
	public EventLogMessageBuilder withStatusCode(final String statusCode) {
		this.statusCode = statusCode;
		return this;
	}
	
	public EventLogMessageBuilder withStatusDesc(final String statusDesc) {
		this.statusDesc = statusDesc;
		return this;
	}
	
	public EventLogMessageBuilder withSubClientId(final String subClientId) {
		this.subClientId = subClientId;
		return this;
	}
	
	public EventLogMessageBuilder withStatus(final String status) {
		this.status = status;
		return this;
	}
}
