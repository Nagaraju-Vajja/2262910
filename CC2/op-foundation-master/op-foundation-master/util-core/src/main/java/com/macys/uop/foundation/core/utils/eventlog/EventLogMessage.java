package com.macys.uop.foundation.core.utils.eventlog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import com.macys.uop.foundation.core.utils.common.StringUtil;

import lombok.Data;

/**
 * The purpose of this class is to capture eventlog information, support builder pattern to populate eventlog information and produce JSON output structure.
 * <br>  
 * Call to {@link #toString()} method produces Json string.
 * <br>
 * Refer to Confluence page <a href="https://confluence.federated.fds/display/OCOM/UOP+Event+Logging+-+Solution+Design">EventLogging Solution Design</a>
 * for field description. 
 */
@Data
public class EventLogMessage implements StringUtil {
	
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
	 * Convenient static builder method for returning {@link EventLogMessageBuilder} instance
	 *  
	 * @return {@link EventLogMessageBuilder}
	 */
	public static EventLogMessageBuilder builder() {
		return new EventLogMessageBuilder();
	}
	
	/**
	 * Instead of JsonUtils, StringBuilder is used to construct the Json.
	 * 
	 * @return Json representation of {@link EventLogMessage}
	 */
	@Override
	public String toString() {
		StringBuilder builder=new  StringBuilder();
		builder.append("{");
		
		if(!StringUtils.isAllBlank(transactionId)) {
			builder.append("\"").append("transactionId").append("\"").append(" : ").append("\"").append(transactionId).append("\"").append(" , ");
		}
		
		if(!StringUtils.isAllBlank(transactionDesc)) {
			builder.append("\"").append("transactionDesc").append("\"").append(" : ").append("\"").append(transactionDesc).append("\"").append(" , ");
		}
		
		builder.append("\"").append("transactionTime").append("\"").append(" : ").append("\"").append(transactionTime).append("\"").append(" , ");
		
		if(!StringUtils.isAllBlank(responseTs)) {
			builder.append("\"").append("responseTs").append("\"").append(" : ").append("\"").append(responseTs).append("\"").append(" , ");
		}
		
		builder.append("\"").append("channelName").append("\"").append(" : ").append("\"").append(channelName).append("\"").append(" , ");
		
		builder.append("\"").append("channelType").append("\"").append(" : ").append("\"").append(channelType).append("\"").append(" , ");
		
		appendHeaders(builder);
		if(!eventDetails.isEmpty())
			appendEventDetails(builder);
		
		if(!StringUtils.isAllBlank(requestType)) {
			builder.append("\"").append("requestType").append("\"").append(" : ").append("\"").append(requestType).append("\"").append(" , ");
		}
		
		builder.append("\"").append("requestPayload").append("\"").append(" : ").append("\"").append(quoteAsString(requestPayload)).append("\"").append(" , ");
		
		if(!StringUtils.isAllBlank(responsePayload)) {
			builder.append("\"").append("responsePayload").append("\"").append(" : ").append("\"").append(quoteAsString(responsePayload)).append("\"").append(" , ");
		}
		
		if(!StringUtils.isAllBlank(statusCode)) {
			builder.append("\"").append("statusCode").append("\"").append(" : ").append("\"").append(statusCode).append("\"").append(" , ");
		}
		
		if(!StringUtils.isAllBlank(statusDesc)) {
			builder.append("\"").append("statusDesc").append("\"").append(" : ").append("\"").append(statusDesc).append("\"").append(" , ");
		}
		
		if(!StringUtils.isAllBlank(subClientId)) {
			builder.append("\"").append("subClientId").append("\"").append(" : ").append("\"").append(subClientId).append("\"").append(" , ");
		}
		
		if(!StringUtils.isAllBlank(status)) {
			builder.append("\"").append("status").append("\"").append(" : ").append("\"").append(status).append("\"").append(" , ");
		}
		
		builder.append("\"").append("createdBy").append("\"").append(" : ").append("\"").append(createdBy).append("\"");
		
		builder.append("}");
		
		return builder.toString();
	}
	
	/**
	 * Append headers array information in Json format 
	 * 
	 * @param builder {@link StringBuilder}
	 */
	private void appendHeaders(StringBuilder builder) {
		builder.append("\"headers\": { ");
		int index = 0;
		int size=headers.size();
		Iterator<Entry<String, String>> it = headers.entrySet().iterator();
	    while (it.hasNext()) {
	    	Entry<String, String> pair = it.next();
	        if (index == (size - 1)) {
	        	builder.append("\"").append(pair.getKey()).append("\"").append(" : ").append("\"").append(pair.getValue()).append("\"").append(" ");
			} else {
				builder.append("\"").append(pair.getKey()).append("\"").append(" : ").append("\"").append(pair.getValue()).append("\"").append(" , ");
			}
			index++;
	    }
		builder.append("}, ");
	}
	
	/**
	 * Append eventDetails array information in Json format 
	 * 
	 * @param builder {@link StringBuilder}
	 */
	private void appendEventDetails(StringBuilder builder) {
		builder.append("\"").append("eventDetails").append("\"").append(" : ");
		builder.append("[ ");
		for (int i = 0; i < eventDetails.size(); i++) {
			EventDetail eventDetail = eventDetails.get(i);
			if (i == (eventDetails.size() - 1)) {
				builder.append(getEventDetailJsonFragment(eventDetail));
			} else {
				builder.append(getEventDetailJsonFragment(eventDetail)).append(" , ");
			}
		}
		builder.append(" ], ");
	}
	
	/**
	 * Construct @link {@link EventDetail} related Json information
	 * 
	 * @param eventDetail @link {@link EventDetail}
	 * 
	 * @return Json String
	 */
	private String getEventDetailJsonFragment(EventDetail eventDetail) {
		StringBuilder builder=new  StringBuilder();
		builder.append("{");
		builder.append("\"").append("referenceId").append("\"").append(" : ").append("\"").append(eventDetail.getReferenceId()).append("\"").append(" , ");
		builder.append("\"").append("referenceType").append("\"").append(" : ").append("\"").append(eventDetail.getReferenceType()).append("\"");
		builder.append("}");
		return builder.toString();
	}
}
