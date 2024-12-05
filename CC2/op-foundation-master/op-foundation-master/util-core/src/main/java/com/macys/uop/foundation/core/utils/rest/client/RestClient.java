package com.macys.uop.foundation.core.utils.rest.client;

import org.springframework.web.client.RestTemplate;

/**
 * REST client wrapper implementation which facilitates the following
 * <br>
 * <ul>
 * <li>Logs request and response related information</li>
 * <li>Logs invocation time</li>
 * <li>Provides builder pattern for creating {@link RestClientRequest<T>}  object </li>
 * <li>Supports providing custom {@link RestTemplate} instance for making REST call</li>
 * </ul>
 * <br>
 * @param <T> Request Payload Type
 * @param <R> Response Payload Type
 * 
 * @see {@link com.macys.uop.foundation.core.utils.validation.DefaultMessageDuplicationCheckServiceImpl}
 */
public interface RestClient<T,R> {
	
	/**
	 * Utility method to make REST call.
	 * <br>
	 * This method do not capture and convert any internal exception to {@link org.zalando.problem.ThrowableProblem}. 
	 * <br>
	 * It is up to the domain service implementation class to take appropriate action by dealing with the raw exception.
	 * <br>
	 * This is helpful in case of circuit breaker and retry implementation where based on some raw exception type or http status, 
	 * <br>
	 * decisions are taken whether to open/close the circuit or retry more.
	 * 
	 * @param clientRequest  {@link RestClientRequest<T>} that wrap REST client request parameters
	 * @param outputClass Type of response
	 * 
	 * @return RestClientResponse
	 */
	RestClientResponse<R> execute(RestClientRequest<T> clientRequest, Class<R> outputClass);
}
