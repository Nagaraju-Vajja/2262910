package com.macys.uop.foundation.core.utils.rest.client;

import static com.macys.uop.foundation.core.utils.Constant.BEAN_ID_DEFAULT_REST_TEMPLATE;
import static com.macys.uop.foundation.core.utils.Constant.CONTEXT_CLIENT_REQUEST_LOGGING;
import static com.macys.uop.foundation.core.utils.Constant.CONTEXT_CLIENT_RESPONSE_LOGGING;

import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;

import com.macys.uop.foundation.core.utils.logging.LogMessageBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.macys.uop.foundation.core.utils.json.JsonUtils;
import com.macys.uop.foundation.core.utils.logging.LogTypeEnum;
import com.macys.uop.foundation.core.utils.logging.LoggingUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * {@link RestClient} Implementation. 
 *
 * @param <T> Request Payload Type
 * @param <R> Response Payload Type
 * 
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RestClientImpl<T, R> implements RestClient<T, R>, LoggingUtil {

	@Value("${client.request.logging.enabled:true}")
	private boolean isClientRequestLoggingEnabled;

	@Value("${client.response.logging.enabled:true}")
	private boolean isClientResponseLoggingEnabled;

	@Value("${client.request.headerAndPayload.logging.enabled:true}")
	private boolean isClientRequestHeaderAndPayloadLoggingEnabled;

	@Value("${client.response.headerAndPayload.logging.enabled:true}")
	private boolean isClientResponseHeaderAndPayloadLoggingEnabled;
	
	private final JsonUtils jsonUtils;
	
	private final ApplicationContext applicationContext;
	
	/**
	 * {@link RestClient#execute(RestClientRequest, Class)}
	 * 
	 * 
	 */
	@Override
	public RestClientResponse<R> execute(RestClientRequest<T> clientRequest, Class<R> outputClass) {
		RestClientResponse<R> clientResponse = null;

		HttpHeaders existingHeaders = clientRequest.getHeaders();
		if (!CollectionUtils.isEmpty(existingHeaders) ) {
			if (CollectionUtils.isEmpty(existingHeaders.getAccept())) {
				existingHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			}
			if (existingHeaders.getContentType() == null) {
				existingHeaders.setContentType(MediaType.APPLICATION_JSON);
			}
		} else {
			existingHeaders = new HttpHeaders();
			existingHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			existingHeaders.setContentType(MediaType.APPLICATION_JSON);
		}
		
		RestTemplate restTemplate =null;
		if(clientRequest.getRestTemplate()!=null) {
			restTemplate=clientRequest.getRestTemplate();
		} else {
			restTemplate = applicationContext.getBean(BEAN_ID_DEFAULT_REST_TEMPLATE, RestTemplate.class);
		}

		URI uri = UriComponentsBuilder.fromHttpUrl(clientRequest.getUrl())
				.queryParams(clientRequest.getQueryParams())
				.buildAndExpand(clientRequest.getPathParams()).toUri();
		HttpMethod method = HttpMethod.valueOf(clientRequest.getMethod());
		T body = clientRequest.getBody();

		RequestEntity<T> request = null;
		if (body == null) {
			request = new RequestEntity<>(clientRequest.getHeaders(), method, uri);
		} else {
			request = new RequestEntity<>(body, clientRequest.getHeaders(), method, uri);
		}
		
		boolean requestLoggingEnabled=clientRequest.isRequestLoggingEnabled();
		boolean responseLoggingEnabled=clientRequest.isResponseLoggingEnabled();
		boolean headerAndPayloadLogEnabled = clientRequest.isHeaderAndPayloadLogEnabled();
		
		Instant invocationStartTime=Instant.now();


		if (isClientRequestLoggingEnabled && requestLoggingEnabled) {
			LogMessageBuilder logMessageBuilder = getLogMessageBuilder(log)
					.withContext(CONTEXT_CLIENT_REQUEST_LOGGING)
					.withLogType(LogTypeEnum.MSG)
					.withOperationType(method.toString())
					.withEndpointUrl(clientRequest.getUrl())
					.withAdditionalInfo("Invocation Start Time :" + invocationStartTime.toString());

			if (isClientRequestHeaderAndPayloadLoggingEnabled && headerAndPayloadLogEnabled) {
				logMessageBuilder.withHeaderAttribute(clientRequest.getHeaders().toSingleValueMap())
						.withRequestBody(jsonUtils.convertToJson(clientRequest.getBody()));
			}

			logMessageBuilder.buildDisableChecking().logAsInfo();
		}

		ResponseEntity<R> responseEntity = restTemplate.exchange(request, outputClass);
		
		Instant invocationEndTime=Instant.now();
		long invocationDuration= Duration.between(invocationStartTime, invocationEndTime).toMillis();


		if (isClientResponseLoggingEnabled && responseLoggingEnabled) {
			LogMessageBuilder logMessageBuilder = getLogMessageBuilder(log)
					.withContext(CONTEXT_CLIENT_RESPONSE_LOGGING)
					.withLogType(LogTypeEnum.MSG)
					.withOperationType(method.toString())
					.withEndpointUrl(clientRequest.getUrl())
					.withStatusCode(String.valueOf(responseEntity.getStatusCode().value()))
					.withStatusMessage(responseEntity.getStatusCode().name())
					.withAdditionalInfo("Invocation End Time :" + invocationEndTime.toString() + " , Duration In Millis :" + invocationDuration);

			if (isClientResponseHeaderAndPayloadLoggingEnabled && headerAndPayloadLogEnabled) {
				logMessageBuilder.withHeaderAttribute(responseEntity.getHeaders().toSingleValueMap())
						.withResponseBody(jsonUtils.convertToJson(responseEntity.getBody()));
			}

			logMessageBuilder.buildDisableChecking().logAsInfo();
		}

		clientResponse = RestClientResponse.<R>builder()
				.withBody(responseEntity.getBody())
				.withStatus(responseEntity.getStatusCode())
				.withHeaders(responseEntity.getHeaders())
				.build();
	
		return clientResponse;
	}
	
}
