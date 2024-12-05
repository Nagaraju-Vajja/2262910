package com.macys.uop.foundation.core.utils.masking;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.util.CollectionUtils;
import org.zalando.problem.Status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.macys.uop.foundation.core.utils.CommonStatusCode;
import com.macys.uop.foundation.core.utils.exception.ErrorDetail;
import com.macys.uop.foundation.core.utils.exception.ProblemUtil;
import com.macys.uop.foundation.core.utils.logging.LogMessageBuilder;
import com.macys.uop.foundation.core.utils.logging.LogTypeEnum;
import com.macys.uop.foundation.core.utils.logging.LoggingUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * Data masking implementation which masks JSON string.
 * <br>
 * During data masking {@link JsonMasker#maskConfigMap} is referred for masking configuration.
 * <br>
 * Refer to resource folder masking-config.json file for masking configuration details.
 */
@Slf4j
public class JsonMasker implements IDataMasker, LoggingUtil, ProblemUtil
{
	private Map<String,IDataMasker> maskConfigMap=new HashMap<>();
	private final ObjectMapper objMapper = new ObjectMapper(); 
	
	public JsonMasker(Map<String,IDataMasker> maskConfigMap) {
		this.maskConfigMap=maskConfigMap;
	}
	
	/**
	 * Method which masks valid JSON string.
	 *  
	 * {@link IDataMasker#maskData(String)} 
	 */
	@Override
	public String maskData(String jsonData) {
		if (StringUtils.isAllBlank(jsonData)) {
			return "";
		}
		if(CollectionUtils.isEmpty(maskConfigMap)) {
			return jsonData;
		}
		String result=null;
		try {
			JsonNode rootNode = objMapper.readTree(jsonData);
			maskNode(rootNode);
			result= objMapper.writeValueAsString(rootNode);
		} catch (JsonProcessingException e) {
			com.macys.uop.foundation.core.utils.exception.Error errorInfo = com.macys.uop.foundation.core.utils.exception.Error
    				.builder()
    				.withCode(CommonStatusCode.JSON_PROCESSING_ERROR.getCode())
    				.withMessage(CommonStatusCode.JSON_PROCESSING_ERROR.getDescription())
    				.withErrorDetail(ErrorDetail.builder()
    						.withDomain("Global")
    						.withReason("Json Processing Error")
    						.withMessage(e.getMessage())
    						.build())
    				.build();
        	
        	new LogMessageBuilder()
        	.withContext("Json Masking")
        	.withLogType(LogTypeEnum.ERROR)
			.withErrorCode(CommonStatusCode.JSON_PROCESSING_ERROR.getCode())
			.withErrorMessage(CommonStatusCode.JSON_PROCESSING_ERROR.getDescription())
			.withStackTrace(ExceptionUtils.getStackTrace(e))
			.withLogger(log)
    		.buildDisableChecking()
    		.logAsError();
    		
    		throw createProblem(Status.INTERNAL_SERVER_ERROR.getStatusCode(), errorInfo);
		}
		return result;
	}
	
	/**
	 * Recursive method to mask JSON node value.
	 * <br>
	 * This method recursively traverses all the leaf nodes and masks their data based on the {@link JsonMasker#maskConfigMap} entry.
	 * 
	 * @param root JsonNode
	 */
	private void maskNode(JsonNode root) {
	    if(root.isObject()) {
	        Iterator<String> fieldNames = root.fieldNames();
	        while(fieldNames.hasNext()) {
	            String fieldName = fieldNames.next();
	            JsonNode fieldValue = root.get(fieldName);
	            if(maskConfigMap.containsKey(fieldName)) {
	            	((ObjectNode) root).put(fieldName, maskConfigMap.get(fieldName).maskData(fieldValue.asText()));  
		        }
	            maskNode(fieldValue);
	        }
	    } 
	    else if(root.isArray()) {
	        ArrayNode arrayNode = (ArrayNode) root;
	        for(int i = 0; i < arrayNode.size(); i++) {
	            JsonNode arrayElement = arrayNode.get(i);
	            maskNode(arrayElement);
	        }
	    } 
	}
	
}
