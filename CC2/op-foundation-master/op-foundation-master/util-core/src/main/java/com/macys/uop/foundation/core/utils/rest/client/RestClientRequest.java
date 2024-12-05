package com.macys.uop.foundation.core.utils.rest.client;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import lombok.Data;

/**
 * The purpose of this class is to :
 * <br>
 * <ul>
 * <li>Capture parameters that are required to make REST call</li>
 * <li>Provide builder pattern to populate REST call related information</li>
 * <li>Support providing custom {@link RestTemplate} instance for making REST call</li>
 * </ul> 
 * <br>
 * Variable names are self explanatory for their purpose.
 * 
 * @param <T> Request Payload Type
 */
@Data
public class RestClientRequest<T> {
	private String url;
	private String method;
	private HttpHeaders headers = new HttpHeaders();
	private Map<String, String> pathParams = new LinkedHashMap<>();
	private MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
	private T body;
	private RestTemplate restTemplate;
	private boolean requestLoggingEnabled=true;
	private boolean responseLoggingEnabled=true;
	private boolean headerAndPayloadLogEnabled=true;
	
	/**
	 *  Static method for creating getting builder instance.
	 *  
	 * @param <T> Type of payload
	 * 
	 * @return RestClientRequestBuilder instance
	 */
	public static <T> RestClientRequestBuilder<T> builder() {
		return new RestClientRequestBuilder<>();
	}
	
}
