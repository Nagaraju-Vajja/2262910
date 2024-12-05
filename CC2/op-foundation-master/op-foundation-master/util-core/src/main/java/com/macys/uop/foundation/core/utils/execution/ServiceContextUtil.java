package com.macys.uop.foundation.core.utils.execution;

import static com.macys.uop.foundation.core.utils.Constant.CLIENTID_HDR;
import static com.macys.uop.foundation.core.utils.Constant.CORRELATIONID_HDR;
import static com.macys.uop.foundation.core.utils.Constant.EXECUTIONID_HDR;
import static com.macys.uop.foundation.core.utils.Constant.MESSAGEID_HDR;
import static com.macys.uop.foundation.core.utils.Constant.ORDERID_HDR;
import static com.macys.uop.foundation.core.utils.Constant.SERVICE_REQUEST_CONTEXT;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;


/**
 * {@link ServiceRequestContext} instance is restricted to be accessed directly by domain services.
 * <br>
 * This interface helps providing utility methods for retrieving <tt>ServiceRequestContext</tt> related information.
 *
 */
public interface ServiceContextUtil {
	
	/**
	 * Retrieves <tt>ServiceRequestContext</tt> from <tt>RequestContextHolder</tt>
	 * 
	 * @return ServiceRequestContext
	 */
	default ServiceRequestContext getServiceRequestContext() {
		ServiceRequestContext requestContext=null;
		Object objContext=RequestContextHolder.getRequestAttributes()!=null ? 
				RequestContextHolder.getRequestAttributes().getAttribute(SERVICE_REQUEST_CONTEXT, RequestAttributes.SCOPE_REQUEST) : null;
		if(objContext!=null) {
			requestContext=(ServiceRequestContext) objContext;
		}
		return requestContext;
	}

	/**
	 * Stores <tt>ServiceRequestContext</tt> in <tt>RequestContextHolder</tt> with REQUEST scope
	 * 
	 * @param context to be stored 
	 */
	default void setServiceRequestContext(ServiceRequestContext context) {
		RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
		attributes.setAttribute(SERVICE_REQUEST_CONTEXT, context, RequestAttributes.SCOPE_REQUEST);
		RequestContextHolder.setRequestAttributes(attributes, true);
	}

	/**
	 * Convenient method to retrieve  <tt>clientId</tt> header from {@link ServiceRequestContext}
	 * 
	 * @return <tt>clientId</tt> header value
	 */
	default String getClientId() {
		String result=null;
		Object objContext=RequestContextHolder.getRequestAttributes()!=null ? 
				RequestContextHolder.getRequestAttributes().getAttribute(SERVICE_REQUEST_CONTEXT, RequestAttributes.SCOPE_REQUEST) : null;
		if(objContext!=null) {
			ServiceRequestContext requestContext=(ServiceRequestContext) objContext;
			result=requestContext.getSingleValueHeaderParam(CLIENTID_HDR);
		}
		return result;
	}

	/**
	 * Convenient method to retrieve  <tt>messageId</tt> header from {@link ServiceRequestContext}
	 * 
	 * @return <tt>messageId</tt> header value
	 */
	default String getMessageId() {
		String result=null;
		Object objContext=RequestContextHolder.getRequestAttributes()!=null ? 
				RequestContextHolder.getRequestAttributes().getAttribute(SERVICE_REQUEST_CONTEXT, RequestAttributes.SCOPE_REQUEST) : null;
		if(objContext!=null) {
			ServiceRequestContext requestContext=(ServiceRequestContext) objContext;
			result=requestContext.getSingleValueHeaderParam(MESSAGEID_HDR);
		}
		return result;
	}

	/**
	 * Convenient method to retrieve  <tt>orderId</tt> header from {@link ServiceRequestContext}
	 * 
	 * @return <tt>orderId</tt> header value
	 */
	default String getOrderId() {
		String result=null;
		Object objContext=RequestContextHolder.getRequestAttributes()!=null ? 
				RequestContextHolder.getRequestAttributes().getAttribute(SERVICE_REQUEST_CONTEXT, RequestAttributes.SCOPE_REQUEST) : null;
		if(objContext!=null) {
			ServiceRequestContext requestContext=(ServiceRequestContext) objContext;
			result=requestContext.getSingleValueHeaderParam(ORDERID_HDR);
		}
		return result;
	}

	/**
	 * Convenient method to retrieve  <tt>correlationId</tt> header from {@link ServiceRequestContext}
	 * 
	 * @return <tt>correlationId</tt> header value
	 */
	default String getCorrelationId() {
		String result=null;
		Object objContext=RequestContextHolder.getRequestAttributes()!=null ? 
				RequestContextHolder.getRequestAttributes().getAttribute(SERVICE_REQUEST_CONTEXT, RequestAttributes.SCOPE_REQUEST) : null;
		if(objContext!=null) {
			ServiceRequestContext requestContext=(ServiceRequestContext) objContext;
			result=requestContext.getSingleValueHeaderParam(CORRELATIONID_HDR);
		}
		return result;
	}
	
	/**
	 * Convenient method to retrieve original <tt>clientId</tt> value. 
	 * 
	 * @return  original clientId value. 
	 */
	default String getCallerId() {
		String result=null;
		Object objContext=RequestContextHolder.getRequestAttributes()!=null ? 
				RequestContextHolder.getRequestAttributes().getAttribute(SERVICE_REQUEST_CONTEXT, RequestAttributes.SCOPE_REQUEST) : null;
		if(objContext!=null) {
			ServiceRequestContext requestContext=(ServiceRequestContext) objContext;
			result=requestContext.getCallerId();
		}
		return result;
	}
	
	/**
	 * Convenient method to retrieve  <tt>executionId</tt> header from {@link ServiceRequestContext}
	 * 
	 * @return <tt>executionId</tt> header value
	 */
	default String getExecutionId() {
		String result=null;
		Object objContext=RequestContextHolder.getRequestAttributes()!=null ? 
				RequestContextHolder.getRequestAttributes().getAttribute(SERVICE_REQUEST_CONTEXT, RequestAttributes.SCOPE_REQUEST) : null;
		if(objContext!=null) {
			ServiceRequestContext requestContext=(ServiceRequestContext) objContext;
			result=requestContext.getSingleValueHeaderParam(EXECUTIONID_HDR);
		}
		return result;
	}

	/**
	 * Convenient method to retrieve <tt>spring.application.name</tt>  
	 * 
	 * @return spring application name set in environment
	 */
	default String getAppName() {
		String result=null;
		Object objContext=RequestContextHolder.getRequestAttributes()!=null ? 
				RequestContextHolder.getRequestAttributes().getAttribute(SERVICE_REQUEST_CONTEXT, RequestAttributes.SCOPE_REQUEST) : null;
		if(objContext!=null) {
			ServiceRequestContext requestContext=(ServiceRequestContext) objContext;
			result=requestContext.getApplicationName();
		}
		return result;
	}

	/**
	 * Convenient method to retrieve  <tt>origin</tt> from {@link ServiceRequestContext}
	 * <p>
	 * Represents origin of call. Http or Messaging
	 * 
	 * @return origin
	 */
	default RequestOriginEnum getOrigin() {
		RequestOriginEnum result=null;
		Object objContext=RequestContextHolder.getRequestAttributes()!=null ? 
				RequestContextHolder.getRequestAttributes().getAttribute(SERVICE_REQUEST_CONTEXT, RequestAttributes.SCOPE_REQUEST) : null;
		if(objContext!=null) {
			ServiceRequestContext requestContext=(ServiceRequestContext) objContext;
			result=requestContext.getOrigin();
		}
		return result;
	}

	/**
	 * Convenient method to retrieve  <tt>receivedTime</tt> from {@link ServiceRequestContext}
	 * <p>
	 * This time is in UTC format. Provides the time when the request is received. 
	 * 
	 * @return String
	 */
	default String getReceivedTime() {
		String result=null;
		Object objContext=RequestContextHolder.getRequestAttributes()!=null ? 
				RequestContextHolder.getRequestAttributes().getAttribute(SERVICE_REQUEST_CONTEXT, RequestAttributes.SCOPE_REQUEST) : null;
		if(objContext!=null) {
			ServiceRequestContext requestContext=(ServiceRequestContext) objContext;
			result=requestContext.getReceivedTime();
		}
		return result;
	}

	/**
	 * Convenient method to retrieve  http request <tt>URL</tt> from {@link ServiceRequestContext}
	 * 
	 * @return http url
	 */
	default String getRequestURL() {
		String result=null;
		Object objContext=RequestContextHolder.getRequestAttributes()!=null ? 
				RequestContextHolder.getRequestAttributes().getAttribute(SERVICE_REQUEST_CONTEXT, RequestAttributes.SCOPE_REQUEST) : null;
		if(objContext!=null) {
			ServiceRequestContext requestContext=(ServiceRequestContext) objContext;
			result=requestContext.getUrl();
		}
		return result;
	}

	/**
	 * Convenient method to retrieve http request <tt>uri</tt> header from {@link ServiceRequestContext}
	 * 
	 * @return http uri
	 */
	default String getRequestURI() {
		String result=null;
		Object objContext=RequestContextHolder.getRequestAttributes()!=null ? 
				RequestContextHolder.getRequestAttributes().getAttribute(SERVICE_REQUEST_CONTEXT, RequestAttributes.SCOPE_REQUEST) : null;
		if(objContext!=null) {
			ServiceRequestContext requestContext=(ServiceRequestContext) objContext;
			result=requestContext.getUri();
		}
		return result;
	}

	/**
	 * Convenient method to retrieve  http request <tt>method</tt> from {@link ServiceRequestContext}
	 * 
	 * @return http method
	 */
	default String getRequestHttpMethod() {
		String result=null;
		Object objContext=RequestContextHolder.getRequestAttributes()!=null ? 
				RequestContextHolder.getRequestAttributes().getAttribute(SERVICE_REQUEST_CONTEXT, RequestAttributes.SCOPE_REQUEST) : null;
		if(objContext!=null) {
			ServiceRequestContext requestContext=(ServiceRequestContext) objContext;
			result=requestContext.getMethod();
		}
		return result;
	}
	
	/**
	 * Convenient method to retrieve  http request <tt>URL</tt> from {@link ServiceRequestContext}
	 * 
	 * @return http url
	 */
	default String getUrl() {
		String result=null;
		Object objContext=RequestContextHolder.getRequestAttributes()!=null ? 
				RequestContextHolder.getRequestAttributes().getAttribute(SERVICE_REQUEST_CONTEXT, RequestAttributes.SCOPE_REQUEST) : null;
		if(objContext!=null) {
			ServiceRequestContext requestContext=(ServiceRequestContext) objContext;
			result=requestContext.getUrl();
		}
		return result;
	}
	
	/**
	 * Convenient method to retrieve  http request <tt>URI</tt> from {@link ServiceRequestContext}
	 * 
	 * @return http url
	 */
	default String getUri() {
		String result=null;
		Object objContext=RequestContextHolder.getRequestAttributes()!=null ? 
				RequestContextHolder.getRequestAttributes().getAttribute(SERVICE_REQUEST_CONTEXT, RequestAttributes.SCOPE_REQUEST) : null;
		if(objContext!=null) {
			ServiceRequestContext requestContext=(ServiceRequestContext) objContext;
			result=requestContext.getUri();
		}
		return result;
	}
	
	/**
	 * Convenient method to retrieve  http request <tt>method</tt> from {@link ServiceRequestContext}
	 * 
	 * @return http method
	 */
	default String getHttpMethod() {
		String result=null;
		Object objContext=RequestContextHolder.getRequestAttributes()!=null ? 
				RequestContextHolder.getRequestAttributes().getAttribute(SERVICE_REQUEST_CONTEXT, RequestAttributes.SCOPE_REQUEST) : null;
		if(objContext!=null) {
			ServiceRequestContext requestContext=(ServiceRequestContext) objContext;
			result=requestContext.getMethod();
		}
		return result;
	}
	
	/**
	 * Convenient method to retrieve  <tt>contentType</tt> from {@link ServiceRequestContext}
	 * 
	 * @return {@link MediaType#APPLICATION_JSON_VALUE} if not set
	 */
	default String getContentType() {
		String result=null;
		Object objContext=RequestContextHolder.getRequestAttributes()!=null ? 
				RequestContextHolder.getRequestAttributes().getAttribute(SERVICE_REQUEST_CONTEXT, RequestAttributes.SCOPE_REQUEST) : null;
		if(objContext!=null) {
			ServiceRequestContext requestContext=(ServiceRequestContext) objContext;
			result=requestContext.getContentType();
		}
		return result;
	}
	
	/**
	 * Checks if content type {@link MediaType#APPLICATION_JSON_VALUE}
	 * 
	 * @return true if content type {@link MediaType#APPLICATION_JSON_VALUE} else false
	 */
	default boolean isContentTypeApplicationJson() {
		boolean result=Boolean.FALSE;
		if(!StringUtils.isAllBlank(getContentType()) && MediaType.APPLICATION_JSON_VALUE.equals(getContentType())) {
			result=Boolean.TRUE;
		}
		return result;
	}
	
	/**
	 * Checks if content type {@link MediaType#APPLICATION_XML_VALUE}
	 * 
	 * @return true if content type {@link MediaType#APPLICATION_XML_VALUE} else false
	 */
	default boolean isContentTypeApplicationXml() {
		boolean result=Boolean.FALSE;
		if(!StringUtils.isAllBlank(getContentType()) && MediaType.APPLICATION_XML_VALUE.equals(getContentType())) {
			result=Boolean.TRUE;
		}
		return result;
	}
	
	/**
	 * Convenient method to retrieve  request <tt>body</tt> from {@link ServiceRequestContext}
	 * <p>
	 * In case of REST body is of type String. In case of Pub/Sub it is String. 
	 * 
	 * @return Object
	 */
	default Object getBody() {
		Object result=null;
		Object objContext=RequestContextHolder.getRequestAttributes()!=null ? 
				RequestContextHolder.getRequestAttributes().getAttribute(SERVICE_REQUEST_CONTEXT, RequestAttributes.SCOPE_REQUEST) : null;
		if(objContext!=null) {
			ServiceRequestContext requestContext=(ServiceRequestContext) objContext;
			result=requestContext.getBody();
		}
		return result;
	}
	
	/**
	 * Convenient method to retrieve  http <tt>headers</tt> from {@link ServiceRequestContext}
	 * 
	 * @return HttpHeaders
	 */
	default HttpHeaders getHttpHeaders() {
		HttpHeaders result=null;
		Object objContext=RequestContextHolder.getRequestAttributes()!=null ? 
				RequestContextHolder.getRequestAttributes().getAttribute(SERVICE_REQUEST_CONTEXT, RequestAttributes.SCOPE_REQUEST) : null;
		if(objContext!=null) {
			ServiceRequestContext requestContext=(ServiceRequestContext) objContext;
			result=requestContext.getHeaders();
		}
		return result;
	}
	
	/**
	 * Convenient method to extract single value http headers from {@link ServiceRequestContext}
	 * <P>
	 * By default {@link ServiceRequestContext#getHeaders()} gives {@link MultiValueMap} 
	 * 
	 * @return Map<String,String> Map of header name and value
	 */
	default Map<String,String> getSingleValueHttpHeaders(){
		Map<String,String> result=null;
		Object objContext=RequestContextHolder.getRequestAttributes()!=null ? 
				RequestContextHolder.getRequestAttributes().getAttribute(SERVICE_REQUEST_CONTEXT, RequestAttributes.SCOPE_REQUEST) : null;
		if(objContext!=null) {
			ServiceRequestContext requestContext=(ServiceRequestContext) objContext;
			HttpHeaders headers = requestContext.getHeaders();
			if(headers!=null && !headers.isEmpty()) {
				result=headers.toSingleValueMap();
			}	
		}
		return result;
	}
	
	/**
	 * Get first index header value from key 
	 * 
	 * @param key Header name
	 * 
	 * @return null if key does not exist
	 */
	default String getSingleValueHeaderParam(String key) {
		String result=null;
		Object objContext=RequestContextHolder.getRequestAttributes()!=null ? 
				RequestContextHolder.getRequestAttributes().getAttribute(SERVICE_REQUEST_CONTEXT, RequestAttributes.SCOPE_REQUEST) : null;
		if(objContext!=null) {
			ServiceRequestContext requestContext=(ServiceRequestContext) objContext;
			result=requestContext.getSingleValueHeaderParam(key);
		}
		return result;
	}
	
	/**
	 * Convenient method to retrieve  http <tt>query parameters</tt> from {@link ServiceRequestContext}
	 * 
	 * @return MultiValueMap<String, String>
	 */
	default MultiValueMap<String, String> getQueryParameters() {
		MultiValueMap<String, String> result=null;
		Object objContext=RequestContextHolder.getRequestAttributes()!=null ? 
				RequestContextHolder.getRequestAttributes().getAttribute(SERVICE_REQUEST_CONTEXT, RequestAttributes.SCOPE_REQUEST) : null;
		if(objContext!=null) {
			ServiceRequestContext requestContext=(ServiceRequestContext) objContext;
			result=requestContext.getQueryParams();
		}
		return result;
	}
	
	/**
	 * Convenient method to extract single value query parameters from {@link ServiceRequestContext}
	 * <P>
	 * By default {@link ServiceRequestContext#getQueryParams()} gives {@link MultiValueMap} 
	 * 
	 * @return Map<String,String> Map of query parameter name and value
	 */
	default Map<String,String> getSingleValueQueryParameters() {
		Map<String,String> result=null;
		Object objContext=RequestContextHolder.getRequestAttributes()!=null ? 
				RequestContextHolder.getRequestAttributes().getAttribute(SERVICE_REQUEST_CONTEXT, RequestAttributes.SCOPE_REQUEST) : null;
		if(objContext!=null) {
			ServiceRequestContext requestContext=(ServiceRequestContext) objContext;
			result=requestContext.getQueryParams().toSingleValueMap();
		}
		return result;
	}
	
	/**
	 * Get first index query value from key 
	 * 
	 * @param key query name
	 * 
	 * @return null if key does not exist
	 */
	default String getSingleValueQueryParam(String key) {
		String result=null;
		Object objContext=RequestContextHolder.getRequestAttributes()!=null ? 
				RequestContextHolder.getRequestAttributes().getAttribute(SERVICE_REQUEST_CONTEXT, RequestAttributes.SCOPE_REQUEST) : null;
		if(objContext!=null) {
			ServiceRequestContext requestContext=(ServiceRequestContext) objContext;
			result=requestContext.getSingleValueQueryParam(key);
		}
		return result;
	}
	
	
	/**
	 * Populate and return default headers from context
	 * 
	 * @return HttpHeaders
	 */
	public default HttpHeaders getDefaultHttpHeaders() {
		HttpHeaders headers = new HttpHeaders();
		
		headers.add(ORDERID_HDR, getOrderId());
		headers.add(CLIENTID_HDR, getAppName());
		headers.add(CORRELATIONID_HDR, getCorrelationId());
		headers.add(MESSAGEID_HDR, getMessageId());
		
		return headers;
	}
	
	
	/**
	 * Populate and return default headers from context
	 * 
	 * @return Map<String,String>
	 */
	public default Map<String,String> getDefaultHeaders() {
		Map<String,String> headers = new HashMap<>();
		
		headers.put(ORDERID_HDR, getOrderId());
		headers.put(CLIENTID_HDR, getAppName());
		headers.put(CORRELATIONID_HDR, getCorrelationId());
		headers.put(MESSAGEID_HDR, getMessageId());
		
		return headers;
	}
}
