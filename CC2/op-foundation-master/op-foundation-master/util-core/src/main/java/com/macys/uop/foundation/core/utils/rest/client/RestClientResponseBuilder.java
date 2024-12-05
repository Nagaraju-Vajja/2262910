package com.macys.uop.foundation.core.utils.rest.client;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

/**
 * Provides builder pattern to populate  {@link RestClientResponse} information
 *
 * @param <T> Respose type
 */
public class RestClientResponseBuilder<T> {
	private HttpHeaders headers;
	private HttpStatus status;
	private T body;

	/**
	 * Populates {@link RestClientResponse} related information 
	 * 
	 * @return {@link RestClientResponse} instance
	 */
	public RestClientResponse<T> build() {
		RestClientResponse<T> responseContext = new RestClientResponse<>();
		responseContext.setHeaders(headers);
		responseContext.setStatus(status);
		responseContext.setBody(body);
		return responseContext;
	}

	public RestClientResponseBuilder<T> withHeaders(HttpHeaders headers) {
		this.headers = headers;
		return this;
	}

	public RestClientResponseBuilder<T> withHeader(String headerName, String headerValue) {
		List<String> listOfValues = new ArrayList<>();
		listOfValues.add(headerValue);
		this.headers.put(headerName, listOfValues);
		return this;
	}

	public RestClientResponseBuilder<T> withStatus(HttpStatus status) {
		this.status = status;
		return this;
	}

	public RestClientResponseBuilder<T> withBody(T body) {
		this.body = body;
		return this;
	}

}
