package com.macys.uop.foundation.messagestore;

import static com.macys.uop.foundation.core.utils.Constant.CLIENTID_HDR;
import static com.macys.uop.foundation.core.utils.Constant.CORRELATIONID_HDR;
import static com.macys.uop.foundation.core.utils.Constant.MESSAGEID_HDR;
import static com.macys.uop.foundation.core.utils.Constant.ORDERID_HDR;

import java.util.Map;

import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import com.macys.uop.foundation.core.utils.json.JsonUtils;
import com.macys.uop.foundation.core.utils.rest.client.RestClient;
import com.macys.uop.foundation.core.utils.rest.client.RestClientRequest;
import com.macys.uop.foundation.core.utils.rest.client.RestClientResponse;
import com.macys.uop.foundation.core.utils.validation.IRestBasedDuplicationCheck;

import lombok.RequiredArgsConstructor;

/**
 * Default implementation provided by foundation for REST based message duplication check
 * <br>
 * Implementation calls MessageStore service using {@link RestClient} for message duplication check.
 * 
 */
@Component
@RequiredArgsConstructor
public class RestBasedDuplicationCheckImpl implements IRestBasedDuplicationCheck {
	
	private final RestClient<String,Object> restClient;
	private final JsonUtils jsonUtils;

	/**
	 * {@link IRestBasedDuplicationCheck#isDuplicate(String, String, Map)}
	 */
	@Override
	public boolean isDuplicate(String serviceURL, String payload, Map<String, String> headers) {
		// Populate request
		RestClientRequest<String> requestContext=RestClientRequest
				.<String>builder().withUrl(serviceURL)
				.withBody(payload)
				.withMethod(HttpMethod.POST.name())
				.withHeader(CLIENTID_HDR, headers.get(CLIENTID_HDR))
				.withHeader(CORRELATIONID_HDR, headers.get(CORRELATIONID_HDR))
				.withHeader(MESSAGEID_HDR, headers.get(MESSAGEID_HDR))
				.withHeader(ORDERID_HDR, headers.get(ORDERID_HDR))
				.build();
		
		// Make the REST Call
		RestClientResponse<Object> serviceResponseContext = restClient.execute(requestContext, Object.class);
		
		// Output of message store service is converted to a Map in order to avoid dependency on UOPCommonContract
		Map<String,Object> outputMap=jsonUtils.convertValue(serviceResponseContext.getBody());

		return Boolean.valueOf(outputMap.get("isDuplicate").toString());
	}

}
