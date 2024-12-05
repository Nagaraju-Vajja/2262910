package com.macys.uop.foundation.businessconfigmanager.utils;

import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.zalando.problem.Status;
import org.zalando.problem.ThrowableProblem;


import com.macys.uop.foundation.core.utils.CommonStatusCode;
import com.macys.uop.foundation.core.utils.exception.ErrorDetail;
import com.macys.uop.foundation.core.utils.exception.ProblemUtil;
import com.macys.uop.foundation.core.utils.logging.LoggingUtil;

@Component
@Slf4j
public class BusinessconfigmanagerUtil implements LoggingUtil, ProblemUtil {

	/**
	 * Common method to build and throw error
	 * @param exception
	 * @param context
	 */
	public ThrowableProblem buildAndThrowError(Exception exception, String context) {
		 getErrorLogMessageBuilder(BusinessconfigmanagerStatusCode.DB_GET_ERROR.getCode(), exception.getMessage(), BusinessconfigmanagerStatusCode.DB_GET_ERROR.getDescription(), log).withContext(context).build().logAsError();
		    com.macys.uop.foundation.core.utils.exception.Error error= com.macys.uop.foundation.core.utils.exception.Error.builder().withCode(BusinessconfigmanagerStatusCode.DB_GET_ERROR.getCode())
		            .withMessage(BusinessconfigmanagerStatusCode.DB_GET_ERROR.getDescription())
		            .withErrorDetail(ErrorDetail.builder().withDomain(getServiceRequestContext().getApplicationName())
		                    .withReason(BusinessconfigmanagerStatusCode.DB_GET_ERROR.getDescription()).withMessage(exception.getMessage()).withLocation(context).build())
		            .build();
		    throw createProblem(Status.INTERNAL_SERVER_ERROR.getStatusCode(), error);
	}

	public void buildError(Exception exception, String context) {
		getErrorLogMessageBuilder(CommonStatusCode.INTERNAL_SERVICE_ERROR.getCode(), exception.getMessage(), CommonStatusCode.INTERNAL_SERVICE_ERROR.getDescription(), log).withContext(context).build().logAsError();
	}

	public void buildWarn(Exception exception, String context) {
		getErrorLogMessageBuilder(CommonStatusCode.INTERNAL_SERVICE_ERROR.getCode(), exception.getMessage(), CommonStatusCode.INTERNAL_SERVICE_ERROR.getDescription(), log).withContext(context).build().logAsWarn();
	}

	/**
	 * Method for throwing an error based on the input received
	 * 
	 * @param message
	 * @param context
	 * @param businessError
	 * @return
	 */
	public void buildAndThrowError(String message, String context, BusinessconfigmanagerStatusCode businessError) {
		getErrorLogMessageBuilder(businessError.getCode(), message,
				businessError.getDescription(), log).withContext(context).build().logAsError();
		com.macys.uop.foundation.core.utils.exception.Error error = com.macys.uop.foundation.core.utils.exception.Error
				.builder().withCode(businessError.getCode())
				.withMessage(businessError.getDescription())
				.withErrorDetail(ErrorDetail.builder().withDomain(getServiceRequestContext().getApplicationName())
						.withReason(businessError.getDescription()).withMessage(message).withLocation(context).build())
				.build();
		throw createProblem(Status.INTERNAL_SERVER_ERROR.getStatusCode(), error);
	}
	
	
	/**
	 * Method for Null check
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNullOrEmpty(String str) {
		if (str != null && !str.isEmpty())
			return false;
		return true;
	}
	
	public boolean checkMatchConfigurationWithSellingChnl(Map<String, Object> map, String configName, String configType,
			String sellingDivision, String sellingChannel) {
		return  ((String)map.get(BusinessconfigmanagerConstants.CONFIG_NAME)).equalsIgnoreCase(configName)
				&& ((String)map.get(BusinessconfigmanagerConstants.CONFIG_TYPE)).equalsIgnoreCase(configType)
				&& ((String)map.get(BusinessconfigmanagerConstants.SELLING_DIVISION)).equalsIgnoreCase(sellingDivision)
				&& null != map.get(BusinessconfigmanagerConstants.SELLING_CHANNEL)
				&& ((String)map.get(BusinessconfigmanagerConstants.SELLING_CHANNEL)).equalsIgnoreCase(sellingChannel);
	}

	public boolean checkMatchConfigurationWithoutSellingChnl(Map<String, Object> map, String configName,
			String configType, String sellingDivision) {
		return ((String)map.get(BusinessconfigmanagerConstants.CONFIG_NAME)).equalsIgnoreCase(configName)
				&& ((String)map.get(BusinessconfigmanagerConstants.CONFIG_TYPE)).equalsIgnoreCase(configType)
				&& ((String)map.get(BusinessconfigmanagerConstants.SELLING_DIVISION)).equalsIgnoreCase(sellingDivision)
				&& null == map.get(BusinessconfigmanagerConstants.SELLING_CHANNEL);
	}
	

	public boolean checkCacheMap(Map<String, Object> map, String configName, String sellingDivision,
			String sellingChannel) {
		boolean check = false;
		if (null != sellingChannel && null != map.get(BusinessconfigmanagerConstants.SELLING_CHANNEL)) {
			check = ((String)map.get(BusinessconfigmanagerConstants.CONFIG_NAME)).equalsIgnoreCase(configName)
					&& ((String)map.get(BusinessconfigmanagerConstants.SELLING_DIVISION)).equalsIgnoreCase(sellingDivision)
					&& ((String)map.get(BusinessconfigmanagerConstants.SELLING_CHANNEL)).equalsIgnoreCase(sellingChannel);
		} 
		else if(null == sellingChannel) {
			
			check = ((String)map.get(BusinessconfigmanagerConstants.CONFIG_NAME)).equalsIgnoreCase(configName)
					&& ((String)map.get(BusinessconfigmanagerConstants.SELLING_DIVISION)).equalsIgnoreCase(sellingDivision)
					&& null == map.get(BusinessconfigmanagerConstants.SELLING_CHANNEL);
		}
		return check;
	}

	/**
	 * This method checks in the cache whether the businessconfig exists or not.
	 * @param map map containing businessconfig information
	 * @param configName name of the businessconfig
	 * @return boolean return true if present in cache else false
	 */
	public boolean checkCacheMapV2(Map<String, Object> map, String configName) {
		boolean check = false;
		if (null != configName && null != map.get(BusinessconfigmanagerConstants.CONFIG_NAME)) {
			check = ((String) map.get(BusinessconfigmanagerConstants.CONFIG_NAME)).equalsIgnoreCase(configName);
		}
		return check;
	}

	public boolean checkMatchConfiguration(Map<String, Object> map, String configName) {
		return  ((String)map.get(BusinessconfigmanagerConstants.CONFIG_NAME)).equalsIgnoreCase(configName);
	}
	
	
}
