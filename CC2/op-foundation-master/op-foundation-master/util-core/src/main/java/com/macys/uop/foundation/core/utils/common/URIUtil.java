package com.macys.uop.foundation.core.utils.common;

public interface URIUtil {
	
	/**
	 * This method checks if input URI path matches a list of predefined paths.
	 * <br>
	 * This method is used to filter out URI paths for which application will not log request contents 
	 *  
	 * @param requestURI
	 * 
	 * @return true if matches else false
	 */
	default boolean isURIInExclusionList(String requestURI) {
		return requestURI.contains("/actuator") 
				|| requestURI.contains("/api-docs")
				|| (requestURI.contains("/error") && !requestURI.endsWith("/ordererror/v1/orders/error"))
				|| requestURI.contains("/swagger-resources") 
				|| requestURI.contains("/swagger-ui");
	}
}
