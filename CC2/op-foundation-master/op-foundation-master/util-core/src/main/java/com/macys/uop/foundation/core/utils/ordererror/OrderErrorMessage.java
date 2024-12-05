package com.macys.uop.foundation.core.utils.ordererror;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.macys.uop.foundation.core.utils.common.StringUtil;

import lombok.Data;

/**
 * The purpose of this class is to :
 * <br> 
 * <ul>
 * <li>Capture order error information</li>
 * <li>Provide builder pattern to populate order error information</li>
 * <li>Produce JSON output structure by calling {@link #toString()}</li>
 * </ul> 
 * <p>
 */
@Data
public class OrderErrorMessage implements StringUtil {
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
	
	/**
	 * Convenient static builder method for returning {@link OrderErrorMessageBuilder} instance
	 *  
	 * @return {@link OrderErrorMessageBuilder}
	 */
	public static OrderErrorMessageBuilder builder() {
		return new OrderErrorMessageBuilder();
	}
	
	/**
	 * Instead of JsonUtils, StringBuilder is used to construct the Json.
	 * 
	 * @return Json representation of {@link OrderErrorMessage}
	 */
	@Override
	public String toString() {
		StringBuilder builder=new  StringBuilder();
		builder.append("{");
		
		if(!StringUtils.isAllBlank(orderId)) {
			builder.append("\"").append("orderId").append("\"").append(" : ").append("\"").append(orderId).append("\"").append(" , ");
		}
		
		if(!StringUtils.isAllBlank(correlationId)) {
			builder.append("\"").append("correlationId").append("\"").append(" : ").append("\"").append(correlationId).append("\"").append(" , ");
		}
		
		if(!StringUtils.isAllBlank(createdBy)) {
			builder.append("\"").append("createdBy").append("\"").append(" : ").append("\"").append(createdBy).append("\"").append(" , ");
		}
		
		if(!StringUtils.isAllBlank(createdTs)) {
			builder.append("\"").append("createdTs").append("\"").append(" : ").append("\"").append(createdTs).append("\"").append(" , ");
		}
		
		if(!StringUtils.isAllBlank(entityRefId)) {
			builder.append("\"").append("entityRefId").append("\"").append(" : ").append("\"").append(entityRefId).append("\"").append(" , ");
		}
		
		if(!StringUtils.isAllBlank(entityRefType)) {
			builder.append("\"").append("entityRefType").append("\"").append(" : ").append("\"").append(entityRefType).append("\"").append(" , ");
		}
		
		if(!StringUtils.isAllBlank(errorCode)) {
			builder.append("\"").append("errorCode").append("\"").append(" : ").append("\"").append(errorCode).append("\"").append(" , ");
		}
		
		if(!StringUtils.isAllBlank(errorDesc)) {
			builder.append("\"").append("errorDesc").append("\"").append(" : ").append("\"").append(quoteAsString(errorDesc)).append("\"").append(" , ");
		}
		
		if(!StringUtils.isAllBlank(message)) {
			builder.append("\"").append("message").append("\"").append(" : ").append("\"").append(quoteAsString(message)).append("\"").append(" , ");
		}
		
		if(!StringUtils.isAllBlank(serviceName)) {
			builder.append("\"").append("serviceName").append("\"").append(" : ").append("\"").append(serviceName).append("\"").append(" , ");
		}
		
		if(!StringUtils.isAllBlank(statusCode)) {
			builder.append("\"").append("statusCode").append("\"").append(" : ").append("\"").append(statusCode).append("\"").append(" , ");
		}
		
		if(!StringUtils.isAllBlank(statusDesc)) {
			builder.append("\"").append("statusDesc").append("\"").append(" : ").append("\"").append(quoteAsString(statusDesc)).append("\"").append(" , ");
		}
		
		if(!StringUtils.isAllBlank(lastUpdatedBy)) {
			builder.append("\"").append("lastUpdatedBy").append("\"").append(" : ").append("\"").append(lastUpdatedBy).append("\"").append(" , ");
		}
		
		if(!StringUtils.isAllBlank(stackTrace)) {
			builder.append("\"").append("stackTrace").append("\"").append(" : ").append("\"").append(quoteAsString(stackTrace)).append("\"").append(" , ");
		}
		
		if(!StringUtils.isAllBlank(topicName)) {
			builder.append("\"").append("topicName").append("\"").append(" : ").append("\"").append(topicName).append("\"").append(" , ");
		}
		
		if(!StringUtils.isAllBlank(url)) {
			builder.append("\"").append("url").append("\"").append(" : ").append("\"").append(url).append("\"").append(" , ");
		}
		
		builder.append("\"").append("lastUpdatedTs").append("\"").append(" : ").append("\"").append(lastUpdatedTs).append("\"").append(" ");
		
		builder.append("}");
		return builder.toString();
	}
}
