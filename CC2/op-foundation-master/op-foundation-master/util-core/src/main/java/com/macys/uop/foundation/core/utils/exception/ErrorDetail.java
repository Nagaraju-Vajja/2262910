package com.macys.uop.foundation.core.utils.exception;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.macys.uop.foundation.core.utils.common.StringUtil;

import lombok.Data;
import lombok.NoArgsConstructor;
/**
*  Class that encapsulated error detail information structure defined in <a href="https://cloud.google.com/storage/docs/json_api/v1/status-codes">Google Status Codes</a>
*  <br>
*  Example :
*  {
*	"error": {
*	 "errors": [
*	  {
*	   "domain": "global",
*	   "reason": "required",
*	   "message": "Login Required",
*	   "locationType": "header",
*	   "location": "Authorization"
*	  }
*	 ],
*	 "code": 401,
*	 "message": "Login Required"
*	}
* } 
*/
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class ErrorDetail implements Serializable, StringUtil
{	
	private static final long serialVersionUID = 1L;
	
	private String domain;
	private String reason;
	private String message;
	private String locationType;
	private String location;
	
	/**
	 * Static method as part of builder pattern provides the builder 
	 * 
	 * @return ErrorDetailBuilder
	 */
	public static ErrorDetailBuilder builder() {
		return new ErrorDetailBuilder();
	}
	
	/**
	 * Instead of JsonUtils, StringBuilder is used to construct the Json.
	 */
	@Override
	public String toString()
	{
		StringBuilder builder=new  StringBuilder();
		builder.append("{");
			builder.append("\"").append("domain").append("\"").append(" : ").append("\"").append(domain).append("\"").append(" , ");
			builder.append("\"").append("reason").append("\"").append(" : ").append("\"").append(reason).append("\"").append(" , ");
			if(!StringUtils.isAllBlank(locationType))
				builder.append("\"").append("locationType").append("\"").append(" : ").append("\"").append(locationType).append("\"").append(" , ");
			if(!StringUtils.isAllBlank(location))
				builder.append("\"").append("location").append("\"").append(" : ").append("\"").append(location).append("\"").append(" , ");
			builder.append("\"").append("message").append("\"").append(" : ").append("\"").append(quoteAsString(message)).append("\"").append("  ");
		builder.append("}");
		return builder.toString();
	}
	
}