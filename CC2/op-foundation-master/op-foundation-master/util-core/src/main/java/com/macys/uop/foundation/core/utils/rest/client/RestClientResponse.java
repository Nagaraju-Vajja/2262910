package com.macys.uop.foundation.core.utils.rest.client;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import lombok.Data;

/**
 * The purpose of this class is to :
 * <br> 
 * <ul>
 * <li>Capture REST call response information</li>
 * <li>Provide builder pattern to populate REST call response information</li>
 * </ul> 
 * <br>
 * Variable names are self explanatory for their purpose.
 * @param <T>
 */
@Data
public class RestClientResponse<T> {
	private HttpHeaders headers;
	private HttpStatus status;
	private T body;

	/**
	 * Static method for creating getting builder instance.
	 * 
	 * @param <T> Response payload type
	 * 
	 * @return Builder instance
	 */
	public static <T> RestClientResponseBuilder<T> builder() {
		return new RestClientResponseBuilder<>();
	}
}
