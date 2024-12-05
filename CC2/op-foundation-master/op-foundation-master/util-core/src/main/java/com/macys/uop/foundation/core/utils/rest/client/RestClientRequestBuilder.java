package com.macys.uop.foundation.core.utils.rest.client;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * Provides builder pattern to populate  {@link RestClientRequest} related information.
 *
 * @param <T> Request Payload Type
 */
public class RestClientRequestBuilder<T> {

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
	 * Populates {@link RestClientRequest} related information
	 * 
	 * @return {@link RestClientRequest} instance
	 */
	public RestClientRequest<T> build() {
		RestClientRequest<T> instance = new RestClientRequest<>();
		instance.setUrl(url);
		instance.setMethod(method);
		instance.setHeaders(headers);
		instance.setPathParams(pathParams);
		instance.setQueryParams(queryParams);
		instance.setBody(body);
		instance.setRestTemplate(restTemplate);
		instance.setRequestLoggingEnabled(requestLoggingEnabled);
		instance.setResponseLoggingEnabled(responseLoggingEnabled);
		instance.setHeaderAndPayloadLogEnabled(headerAndPayloadLogEnabled);
		return instance;
	}

	public RestClientRequestBuilder<T> withUrl(String url) {
		this.url = url;
		return this;
	}

	public RestClientRequestBuilder<T> withMethod(String method) {
		this.method = method;
		return this;
	}

	public RestClientRequestBuilder<T> withHeaders(HttpHeaders headers) {
		this.headers = headers;
		return this;
	}

	public RestClientRequestBuilder<T> withHeader(String headerName, String headerValue) {
		List<String> listOfValues = new ArrayList<>();
		listOfValues.add(headerValue);
		this.headers.put(headerName, listOfValues);
		return this;
	}

	public RestClientRequestBuilder<T> withPathParams(Map<String, String> pathParams) {
		this.pathParams = pathParams;
		return this;
	}

	public RestClientRequestBuilder<T> withPathParam(String pathParamName, String pathParamValue) {
		this.pathParams.put(pathParamName, pathParamValue);
		return this;
	}

	public RestClientRequestBuilder<T> withQueryParams(MultiValueMap<String, String> queryParams) {
		this.queryParams = queryParams;
		return this;
	}

	public RestClientRequestBuilder<T> withQueryParam(String queryParamName, String queryParamValue) {
		List<String> listOfValues = new ArrayList<>();
		listOfValues.add(queryParamValue);
		this.queryParams.put(queryParamName, listOfValues);
		return this;
	}

	public RestClientRequestBuilder<T> withBody(T body) {
		this.body = body;
		return this;
	}
	
	public RestClientRequestBuilder<T> withRestTemplate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
		return this;
	}
	
	public RestClientRequestBuilder<T> withRequestLoggingEnabled(boolean requestLoggingEnabled) {
		this.requestLoggingEnabled = requestLoggingEnabled;
		return this;
	}
	
	public RestClientRequestBuilder<T> withResponseLoggingEnabled(boolean responseLoggingEnabled) {
		this.responseLoggingEnabled = responseLoggingEnabled;
		return this;
	}

	public RestClientRequestBuilder<T> withHeaderAndPayloadLogEnabled(boolean headerAndPayloadLogEnabled) {
		this.headerAndPayloadLogEnabled = headerAndPayloadLogEnabled;
		return this;
	}

}
