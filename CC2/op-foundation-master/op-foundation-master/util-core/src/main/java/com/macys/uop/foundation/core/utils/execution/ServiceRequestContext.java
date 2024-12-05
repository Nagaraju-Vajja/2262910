package com.macys.uop.foundation.core.utils.execution;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * The primary purpose of this class is to store incoming http and messaging related information in REQUEST scope. 
 * This class helps accessing context information easily without passing them in between different layers.
 * <br>
 * In order to distinguish the origin of call, <tt>RequestOriginEnum origin</tt> should be referred.
 * <br>
 * This class supports builder pattern through Lombok.
 * <br>
 * <tt>headers</tt> is populated to store http header information and <tt>messageHeaders</tt> gets populated for incoming messages
 * <br>
 * Field names are self-explanatory.
 * <br>
 * Notes :
 * <ul>
 * <li>Information stored by this class should only be accessed through {@link ServiceContextUtil}</li>
 * <li>Domain services should never access this object directly using Spring API.</li>
 * <li>Domain Services should never modify this class information</li>
 * <li>This information can only be modified by framework classes as necessary.
 * For domain services this information is always readonly.</li>
 * </ul>
 * 
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceRequestContext implements Serializable {
	private static final long serialVersionUID = 1L;
	private String url;
	private String uri;
	private String method;
	@Builder.Default
	private HttpHeaders headers = new HttpHeaders();
	@Builder.Default
	private Map<String, String> pathParams = new LinkedHashMap<>();
	@Builder.Default
	private MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
	private transient Object body;
	private String applicationName;
	@Builder.Default
	private RequestOriginEnum origin=RequestOriginEnum.REST;
	@Builder.Default
	private Map<String,String> messageHeaders=new HashMap<>();
	private String receivedTime;
	private String callerId;
	@Builder.Default
	private String contentType=MediaType.APPLICATION_JSON_VALUE;
	
	
	/**
	 * Returns index 0 query parameter value.
	 * @param key query parameter key
	 * @return null if value is not found against the key
	 */
	public String getSingleValueQueryParam(String key) {
		return (this.queryParams.get(key) != null && !this.queryParams.get(key).isEmpty())
				? this.queryParams.get(key).get(0)
				: null;
	}

	/**
	 * Returns index 0 header parameter value.
	 * 
	 * @param key header parameter key
	 * @return null if value is not found against the key
	 */
	public String getSingleValueHeaderParam(String key) {
		return (this.headers.get(key) != null && !this.headers.get(key).isEmpty()) ? this.headers.get(key).get(0)
				: null;
	}
	
	/**
	 * Add header key value to existing http headers
	 * 
	 * @param headerName
	 * @param headerValue
	 */
	public void addHeader(String headerName, String headerValue)
	{
		headers.add(headerName, headerValue);
	}
	
	/**
	 * Add header key value to existing message headers
	 *  
	 * @param headerName
	 * @param headerValue
	 */
	public void addMessageHeader(String headerName, String headerValue)
	{
		messageHeaders.put(headerName, headerValue);
	}

	/**
	 * Whether the specific query parameter exists in query parameter multi value map
	 * @param key
	 * @return true if exists otherwise false
	 */
	public boolean containsQueryParam(String key) {
		return (this.queryParams.get(key) != null && !this.queryParams.get(key).isEmpty()) ? Boolean.TRUE
				: Boolean.FALSE;
	}

	/**
	 * Removes specific query parameter
	 * @param key
	 * @return List of existing values
	 */
	public List<String> removeQueryParam(String key) {
		return this.queryParams.remove(key);
	}

	/**
	 * Returns path parameter value for the given path parameter key
	 * @param key
	 * @return path parameter value
	 */
	public String getPathParam(String key) {
		return this.pathParams.get(key);
	}

	/**
	 * Add path parameter key value to existing path parameters
	 * 
	 * @param key
	 * @param value
	 * @return the previous value associated with <tt>key</tt>, or <tt>null</tt> if there was no mapping for <tt>key</tt>.
	 */
	public String addPathParam(String key, String value) {
		return this.pathParams.put(key, value);
	}
	
	/**
	 * Add path parameters in one shot
	 * 
	 * @param pathParam
	 */
	public void addPathParams(Map<String, String> pathParam) {
		if (!pathParam.isEmpty()) {
			this.pathParams.putAll(pathParam);
		}
	}

	/**
	 * Removes path parameter for the given key 
	 * 
	 * @param key
	 * @return the previous value associated with <tt>key</tt>, or <tt>null</tt> if there was no mapping for <tt>key</tt>.
	 */
	public Object removePathParam(String key) {
		return this.pathParams.remove(key);
	}
}
