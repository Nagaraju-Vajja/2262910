package com.macys.uop.foundation.core.utils.execution;

import static com.macys.uop.foundation.core.utils.Constant.CLIENTID_HDR;
import static com.macys.uop.foundation.core.utils.Constant.CORRELATIONID_HDR;
import static com.macys.uop.foundation.core.utils.Constant.MESSAGEID_HDR;
import static com.macys.uop.foundation.core.utils.Constant.ORDERID_HDR;
import static com.macys.uop.foundation.core.utils.Constant.SERVICE_REQUEST_CONTEXT;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import com.macys.uop.foundation.core.utils.Constant;

/**
 * Helper methods related to Stream Context.
 *
 */
public interface StreamContextUtil {
	
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
	 * Initialize the Context 
	 * 
	 */
	public default void initContext() {
		RequestAttributes attributes = new CustomRequestAttributes();
		RequestContextHolder.setRequestAttributes(attributes);
		ServiceRequestContext context = new ServiceRequestContext();
		RequestContextHolder.getRequestAttributes().setAttribute(Constant.SERVICE_REQUEST_CONTEXT, context,
				RequestAttributes.SCOPE_REQUEST);
	}

	/**
	 * Clear the Attributes that are inside RequestContextHolder 
	 */
	public default void clearContext() {
		RequestContextHolder.resetRequestAttributes();
	}

	
	/**
	 * Get Message Payload
	 * 
	 * @return String 
	 */
	public default String getPayload() {
		String result=null;
		Object objContext=RequestContextHolder.getRequestAttributes()!=null ? 
				RequestContextHolder.getRequestAttributes().getAttribute(SERVICE_REQUEST_CONTEXT, RequestAttributes.SCOPE_REQUEST) : null;
		if(objContext!=null) {
			ServiceRequestContext requestContext=(ServiceRequestContext) objContext;
			result=requestContext.getBody()!=null ? requestContext.getBody().toString() : null;
		}
		return result;
	}

	
	/**
	 * Get the MessageHeaders
	 * 
	 * @return Map<String,String>
	 */
	public default Map<String,String> getMessageHeaders() {
		Map<String,String> result=null;
		Object objContext=RequestContextHolder.getRequestAttributes()!=null ? 
				RequestContextHolder.getRequestAttributes().getAttribute(SERVICE_REQUEST_CONTEXT, RequestAttributes.SCOPE_REQUEST) : null;
		if(objContext!=null) {
			ServiceRequestContext requestContext=(ServiceRequestContext) objContext;
			result=requestContext.getMessageHeaders();
		}
		return result;
	}
	
	/**
	 * Get Message Header Value
	 * 
	 * @param headerName
	 * @return
	 */
	public default String getMessageHeaderValue(String headerName) {
		String result=null;
		Map<String,String> messageHeaders=getMessageHeaders();
		if(!CollectionUtils.isEmpty(messageHeaders)) {
			result=messageHeaders.get(headerName);
		}
		return result;
	}
	
	/**
	 * Copy Message Headers to {@link ServiceRequestContext}
	 * 
	 * @param messageHeaders MessageHeaders
	 */
	public default void copyMessageHeadersToRequestContext(Map<String, String> messageHeaders) {
		if(!CollectionUtils.isEmpty(messageHeaders)) {
			HttpHeaders headers = new HttpHeaders();
			messageHeaders.entrySet().stream().forEach(es -> {
				String key = es.getKey();
				String value = es.getValue();
				List<String> listValue = new ArrayList<>();
				listValue.add(value);
				headers.put(key, listValue);
			});
			Object objContext=RequestContextHolder.getRequestAttributes()!=null ? 
					RequestContextHolder.getRequestAttributes().getAttribute(SERVICE_REQUEST_CONTEXT, RequestAttributes.SCOPE_REQUEST) : null;
			if(objContext!=null) {
				ServiceRequestContext requestContext=(ServiceRequestContext) objContext;
				requestContext.getHeaders().addAll(headers);
			}
		}	
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
	 * @return
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
	 * Convenient method to retrieve original clientId value. 
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
	 * Convenient method to retrieve Pub/Sub <tt>URL</tt> from {@link ServiceRequestContext}
	 * 
	 * @return Pub/Sub URL
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
	 * Convenient method to retrieve Pub/Sub <tt>URL</tt> from {@link ServiceRequestContext}
	 * 
	 * @return pub/Sub URL
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
}
