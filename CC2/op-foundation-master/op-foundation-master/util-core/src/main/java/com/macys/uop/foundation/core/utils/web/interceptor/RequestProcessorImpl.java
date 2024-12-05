package com.macys.uop.foundation.core.utils.web.interceptor;

import static com.macys.uop.foundation.core.utils.Constant.CLIENTID_HDR;
import static com.macys.uop.foundation.core.utils.Constant.MESSAGEID_HDR;
import static com.macys.uop.foundation.core.utils.Constant.ORDERID_HDR;
import static com.macys.uop.foundation.core.utils.Constant.ORDERID_HDR_DEFAULT_VALUE;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

/**
 * Default implementation of {@link IRequestProcessor} provided by foundation.
 * <br> 
 * This default implementation can be overridden by the domain service by implementing {@link IRequestProcessor} and making it primary bean through @Primary annotation.
 *
 */
@Component
public class RequestProcessorImpl implements IRequestProcessor {
	
	@Value("${spring.application.name:default}")
	private String applicationName;

	@Override
	public HttpHeaders processHeaders(HttpServletRequest request) {
		
		HttpHeaders headers = extractHeaders(request);
		
		// If orderId header is not present add default value "orderId"
		if(!headers.containsKey(ORDERID_HDR)) {
			headers.add(ORDERID_HDR, ORDERID_HDR_DEFAULT_VALUE);
		}
		
		// If messageId header is not present add default value random UUID
		if(!headers.containsKey(MESSAGEID_HDR)) {
			headers.add(MESSAGEID_HDR, UUID.randomUUID().toString());
		}
		
		// Set clientId header value to application name always
		headers.remove(CLIENTID_HDR);
		headers.add(CLIENTID_HDR, applicationName);
		
		// Nothing to do with correlationId.
		
		return headers;
	}

}
