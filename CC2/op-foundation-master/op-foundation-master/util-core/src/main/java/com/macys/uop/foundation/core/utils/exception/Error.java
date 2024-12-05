package com.macys.uop.foundation.core.utils.exception;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.macys.uop.foundation.core.utils.common.StringUtil;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Class that encapsulated error information structure defined in <a href="https://cloud.google.com/storage/docs/json_api/v1/status-codes">Google Status Codes</a>
 * <br>
 * Example :
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
public class Error implements Serializable, StringUtil {
	private static final long serialVersionUID = 1L;
	
	private String code;
	private String message;
	private String referenceId;
	private String referenceType;
	private List<ErrorDetail> errorDetails = new ArrayList<>();

	/**
	 * Static method as part of builder pattern provides the builder 
	 * 
	 * @return ErrorBuilder
	 */
	public static ErrorBuilder builder() {
		return new ErrorBuilder();
	}

	/**
	 * Instead of JsonUtils, StringBuilder is used to construct the Json.
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("{");
		builder.append("\"").append("code").append("\"").append(" : ").append("\"").append(code).append("\"").append(" , ");
		builder.append("\"").append("referenceId").append("\"").append(" : ").append("\"").append(referenceId).append("\"").append(" , ");
		builder.append("\"").append("referenceType").append("\"").append(" : ").append("\"").append(referenceType).append("\"").append(" , ");

		if (errorDetails != null && !errorDetails.isEmpty()) {
			builder.append("\"").append("message").append("\"").append(" : ").append("\"").append(message).append("\"").append(" , ");

			builder.append("\"").append("errorDetails").append("\"").append(" : ");

			builder.append("[ ");
			for (int i = 0; i < errorDetails.size(); i++) {
				ErrorDetail errorDetail = errorDetails.get(i);
				if (i == (errorDetails.size() - 1)) {
					builder.append(" ").append(errorDetail.toString()).append(" ");
				} else {
					builder.append(" ").append(errorDetail.toString()).append(" , ");
				}
			}
			builder.append(" ]");
		} else {
			builder.append("\"").append("message").append("\"").append(" : ").append("\"").append(quoteAsString(message)).append("\"").append("  ");
		}


		builder.append("}");
		return builder.toString();
	}

}