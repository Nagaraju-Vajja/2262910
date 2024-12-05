package com.macys.uop.foundation.core.utils.execution;

import static com.macys.uop.foundation.core.utils.Constant.SERVICE_REQUEST_CONTEXT;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import com.macys.uop.foundation.core.utils.Constant;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = StreamContextUtil.class)
public class StreamContextUtilTest implements StreamContextUtil {
	
	@Test
	public void testGetServiceRequestContextNothingSet() {
		Assert.assertNull(getServiceRequestContext());
	}

	@Test
	public void testGetServiceRequestContextWithoutSRCSet() {
		RequestAttributes attributes = new CustomRequestAttributes();
		RequestContextHolder.setRequestAttributes(attributes);
		Assert.assertNull(getServiceRequestContext());
	}

	@Test
	public void testGetServiceRequestContextWithSRCSet() {
		RequestAttributes attributes = new CustomRequestAttributes();
		RequestContextHolder.setRequestAttributes(attributes);
		ServiceRequestContext context = new ServiceRequestContext();
		RequestContextHolder.getRequestAttributes().setAttribute(Constant.SERVICE_REQUEST_CONTEXT, context,
				RequestAttributes.SCOPE_REQUEST);
		Assert.assertNotNull(getServiceRequestContext());
	}
	
	@Test
	public void testInitContext() {
		initContext();
		Object objContext=RequestContextHolder.getRequestAttributes()!=null ? 
				RequestContextHolder.getRequestAttributes().getAttribute(SERVICE_REQUEST_CONTEXT, RequestAttributes.SCOPE_REQUEST) : null;
		Assert.assertNotNull(objContext);
	}
	
	@Test
	public void testClearContext() {
		clearContext();
		Object objContext=RequestContextHolder.getRequestAttributes()!=null ? 
				RequestContextHolder.getRequestAttributes().getAttribute(SERVICE_REQUEST_CONTEXT, RequestAttributes.SCOPE_REQUEST) : null;
		Assert.assertNull(objContext);
	}
	
	@Test
	public void testGetPayload() {
		Assert.assertNull(getPayload());
	}

	@Test
	public void testGetPayloadWithoutSRCSet() {
		RequestAttributes attributes = new CustomRequestAttributes();
		RequestContextHolder.setRequestAttributes(attributes);
		Assert.assertNull(getPayload());
	}

	@Test
	public void testGetPayloadWithSRCSet() {
		RequestAttributes attributes = new CustomRequestAttributes();
		RequestContextHolder.setRequestAttributes(attributes);
		ServiceRequestContext context = new ServiceRequestContext();
		RequestContextHolder.getRequestAttributes().setAttribute(Constant.SERVICE_REQUEST_CONTEXT, context,
				RequestAttributes.SCOPE_REQUEST);
		Assert.assertNull(getPayload());
	}

	@Test
	public void testGetPayloadWithPayloadNull() {
		initContext();
		Object objContext = RequestContextHolder.getRequestAttributes() != null
				? RequestContextHolder.getRequestAttributes().getAttribute(SERVICE_REQUEST_CONTEXT,
						RequestAttributes.SCOPE_REQUEST)
				: null;
		ServiceRequestContext requestContext = (ServiceRequestContext) objContext;
		requestContext.setBody(null);
		Assert.assertNull(getPayload());
	}
	
	@Test
	public void testGetPayloadWithPayloadNotNull() {
		initContext();
		Object objContext = RequestContextHolder.getRequestAttributes() != null
				? RequestContextHolder.getRequestAttributes().getAttribute(SERVICE_REQUEST_CONTEXT,
						RequestAttributes.SCOPE_REQUEST)
				: null;
		ServiceRequestContext requestContext = (ServiceRequestContext) objContext;
		requestContext.setBody("somepayload");
		Assert.assertNotNull(getPayload());
	}
	
	@Test
	public void testGetMessageHeaders() {
		Assert.assertNull(getMessageHeaders());
	}

	@Test
	public void testGetMessageHeadersWithoutSRCSet() {
		RequestAttributes attributes = new CustomRequestAttributes();
		RequestContextHolder.setRequestAttributes(attributes);
		Assert.assertNull(getMessageHeaders());
	}

	@Test
	public void testGetMessageHeadersWithSRCSet() {
		RequestAttributes attributes = new CustomRequestAttributes();
		RequestContextHolder.setRequestAttributes(attributes);
		ServiceRequestContext context = new ServiceRequestContext();
		RequestContextHolder.getRequestAttributes().setAttribute(Constant.SERVICE_REQUEST_CONTEXT, context,
				RequestAttributes.SCOPE_REQUEST);
		Assert.assertEquals(0, getMessageHeaders().size());
	}
	
	@Test
	public void testGetMessageHeadersWithMessageHeaders() {
		initContext();
		Object objContext=RequestContextHolder.getRequestAttributes()!=null ? 
				RequestContextHolder.getRequestAttributes().getAttribute(SERVICE_REQUEST_CONTEXT, RequestAttributes.SCOPE_REQUEST) : null;
		ServiceRequestContext requestContext=(ServiceRequestContext) objContext;
		Map<String, String> messageHeaders = new HashMap<>();
		messageHeaders.put("key1", "value1");
		messageHeaders.put("key2", "value2");
		requestContext.setMessageHeaders(messageHeaders);
		Assert.assertEquals(2, getMessageHeaders().size());
	}
	
	@Test
	public void testGetMessageHeaderValue() {
		Assert.assertNull(getMessageHeaderValue(null));
	}
	
	@Test
	public void testGetMessageHeaderValuePassedNonNull() {
		Assert.assertNull(getMessageHeaderValue("somevalue"));
	}
	
	@Test
	public void testGetMessageHeaderValueWithoutSRCSet() {
		RequestAttributes attributes = new CustomRequestAttributes();
		RequestContextHolder.setRequestAttributes(attributes);
		Assert.assertNull(getMessageHeaderValue(null));
	}
	
	@Test
	public void testGetMessageHeaderValueWithSRCSet() {
		RequestAttributes attributes = new CustomRequestAttributes();
		RequestContextHolder.setRequestAttributes(attributes);
		ServiceRequestContext context = new ServiceRequestContext();
		RequestContextHolder.getRequestAttributes().setAttribute(Constant.SERVICE_REQUEST_CONTEXT, context,
				RequestAttributes.SCOPE_REQUEST);
		Assert.assertNull(getMessageHeaderValue("somevalue"));
	}
	
	@Test
	public void testGetMessageHeaderValueWithBlank() {
		RequestAttributes attributes = new CustomRequestAttributes();
		RequestContextHolder.setRequestAttributes(attributes);
		ServiceRequestContext context = new ServiceRequestContext();
		RequestContextHolder.getRequestAttributes().setAttribute(Constant.SERVICE_REQUEST_CONTEXT, context,
				RequestAttributes.SCOPE_REQUEST);
		Map<String, String> messageHeaders = new HashMap<>();
		context.setMessageHeaders(messageHeaders);
		Assert.assertNull(getMessageHeaderValue("key"));
	}
	
	@Test
	public void testGetMessageHeaderValueWithValues() {
		RequestAttributes attributes = new CustomRequestAttributes();
		RequestContextHolder.setRequestAttributes(attributes);
		ServiceRequestContext context = new ServiceRequestContext();
		RequestContextHolder.getRequestAttributes().setAttribute(Constant.SERVICE_REQUEST_CONTEXT, context,
				RequestAttributes.SCOPE_REQUEST);
		Map<String, String> messageHeaders = new HashMap<>();
		messageHeaders.put("key1", "value1");
		messageHeaders.put("key2", "value2");
		context.setMessageHeaders(messageHeaders);
		Assert.assertEquals("value1", getMessageHeaderValue("key1"));
	}
	
	@Test
	public void testCopyMessageHeadersToRequestContextPassNullWithoutContextSet() {
		copyMessageHeadersToRequestContext(null);
		Object objContext=RequestContextHolder.getRequestAttributes()!=null ? 
				RequestContextHolder.getRequestAttributes().getAttribute(SERVICE_REQUEST_CONTEXT, RequestAttributes.SCOPE_REQUEST) : null;
		Assert.assertNull(objContext);		
	}
	
	@Test
	public void testCopyMessageHeadersToRequestContextPassNullWithContextSet() {
		RequestAttributes attributes = new CustomRequestAttributes();
		RequestContextHolder.setRequestAttributes(attributes);
		ServiceRequestContext context = new ServiceRequestContext();
		RequestContextHolder.getRequestAttributes().setAttribute(Constant.SERVICE_REQUEST_CONTEXT, context,
				RequestAttributes.SCOPE_REQUEST);
		copyMessageHeadersToRequestContext(null);
		Object objContext=RequestContextHolder.getRequestAttributes()!=null ? 
				RequestContextHolder.getRequestAttributes().getAttribute(SERVICE_REQUEST_CONTEXT, RequestAttributes.SCOPE_REQUEST) : null;
		ServiceRequestContext requestContext=(ServiceRequestContext) objContext;		
		Assert.assertEquals(0, requestContext.getHeaders().size());		
	}
	
	@Test
	public void testCopyMessageHeadersToRequestContextPassEmptyWithContextSet() {
		RequestAttributes attributes = new CustomRequestAttributes();
		RequestContextHolder.setRequestAttributes(attributes);
		ServiceRequestContext context = new ServiceRequestContext();
		RequestContextHolder.getRequestAttributes().setAttribute(Constant.SERVICE_REQUEST_CONTEXT, context,
				RequestAttributes.SCOPE_REQUEST);
		copyMessageHeadersToRequestContext(new HashMap<>());
		Object objContext=RequestContextHolder.getRequestAttributes()!=null ? 
				RequestContextHolder.getRequestAttributes().getAttribute(SERVICE_REQUEST_CONTEXT, RequestAttributes.SCOPE_REQUEST) : null;
		ServiceRequestContext requestContext=(ServiceRequestContext) objContext;		
		Assert.assertEquals(0, requestContext.getHeaders().size());		
	}
	
	@Test
	public void testCopyMessageHeadersToRequestContextPassAllValid() {
		RequestAttributes attributes = new CustomRequestAttributes();
		RequestContextHolder.setRequestAttributes(attributes);
		ServiceRequestContext context = new ServiceRequestContext();
		RequestContextHolder.getRequestAttributes().setAttribute(Constant.SERVICE_REQUEST_CONTEXT, context,
				RequestAttributes.SCOPE_REQUEST);
		Map<String, String> messageHeaders = new HashMap<>();
		messageHeaders.put("key1", "value1");
		messageHeaders.put("key2", "value2");
		copyMessageHeadersToRequestContext(messageHeaders);
		Object objContext=RequestContextHolder.getRequestAttributes()!=null ? 
				RequestContextHolder.getRequestAttributes().getAttribute(SERVICE_REQUEST_CONTEXT, RequestAttributes.SCOPE_REQUEST) : null;
		ServiceRequestContext requestContext=(ServiceRequestContext) objContext;		
		Assert.assertEquals(2, requestContext.getHeaders().size());		
	}
	
	@Test
	public void testCopyMessageHeadersToRequestContextPassEmpty() {
		copyMessageHeadersToRequestContext(new HashMap<>());
		Object objContext=RequestContextHolder.getRequestAttributes()!=null ? 
				RequestContextHolder.getRequestAttributes().getAttribute(SERVICE_REQUEST_CONTEXT, RequestAttributes.SCOPE_REQUEST) : null;
		Assert.assertNull(objContext);		
	}
	
	
	@Test
	public void testGetClientId() {
		Assert.assertNull(getClientId());
	}

	@Test
	public void testGetClientIdWithoutSRCSet() {
		RequestAttributes attributes = new CustomRequestAttributes();
		RequestContextHolder.setRequestAttributes(attributes);
		Assert.assertNull(getClientId());
	}

	@Test
	public void testGetClientIdWithSRCSet() {
		RequestAttributes attributes = new CustomRequestAttributes();
		RequestContextHolder.setRequestAttributes(attributes);
		ServiceRequestContext context = new ServiceRequestContext();
		RequestContextHolder.getRequestAttributes().setAttribute(Constant.SERVICE_REQUEST_CONTEXT, context,
				RequestAttributes.SCOPE_REQUEST);
		Assert.assertNull(getClientId());
	}

	@Test
	public void testGetClientIdAllSetWithClientHeader() {
		initContext();
		HttpHeaders headers = new HttpHeaders();
		headers.add(Constant.CLIENTID_HDR, "3");
		getServiceRequestContext().setHeaders(headers);
		Assert.assertNotNull(getClientId());
	}

	@Test
	public void testGetClientIdAllSetWithoutClientHeader() {
		initContext();
		HttpHeaders headers = new HttpHeaders();
		getServiceRequestContext().setHeaders(headers);
		Assert.assertNull(getClientId());
	}
	
	@Test
	public void testGetMessageId() {
		Assert.assertNull(getMessageId());
	}

	@Test
	public void testGetMessageIdWithoutSRCSet() {
		RequestAttributes attributes = new CustomRequestAttributes();
		RequestContextHolder.setRequestAttributes(attributes);
		Assert.assertNull(getMessageId());
	}

	@Test
	public void testGetMessageIdWithSRCSet() {
		RequestAttributes attributes = new CustomRequestAttributes();
		RequestContextHolder.setRequestAttributes(attributes);
		ServiceRequestContext context = new ServiceRequestContext();
		RequestContextHolder.getRequestAttributes().setAttribute(Constant.SERVICE_REQUEST_CONTEXT, context,
				RequestAttributes.SCOPE_REQUEST);
		Assert.assertNull(getMessageId());
	}

	@Test
	public void testGetMessageIdAllSetWithMessageHeader() {
		initContext();
		HttpHeaders headers = new HttpHeaders();
		headers.add(Constant.MESSAGEID_HDR, "3");
		getServiceRequestContext().setHeaders(headers);
		Assert.assertNotNull(getMessageId());
	}

	@Test
	public void testGetMessageIdAllSetWithoutMessageHeader() {
		initContext();
		HttpHeaders headers = new HttpHeaders();
		getServiceRequestContext().setHeaders(headers);
		Assert.assertNull(getMessageId());
	}

	@Test
	public void testGetOrderId() {
		Assert.assertNull(getOrderId());
	}

	@Test
	public void testGetOrderIdWithoutSRCSet() {
		RequestAttributes attributes = new CustomRequestAttributes();
		RequestContextHolder.setRequestAttributes(attributes);
		Assert.assertNull(getOrderId());
	}

	@Test
	public void testGetOrderIdWithSRCSet() {
		RequestAttributes attributes = new CustomRequestAttributes();
		RequestContextHolder.setRequestAttributes(attributes);
		ServiceRequestContext context = new ServiceRequestContext();
		RequestContextHolder.getRequestAttributes().setAttribute(Constant.SERVICE_REQUEST_CONTEXT, context,
				RequestAttributes.SCOPE_REQUEST);
		Assert.assertNull(getOrderId());
	}

	@Test
	public void testGetOrderIdAllSetWithOrderHeader() {
		initContext();
		HttpHeaders headers = new HttpHeaders();
		headers.add(Constant.ORDERID_HDR, "3");
		getServiceRequestContext().setHeaders(headers);
		Assert.assertNotNull(getOrderId());
	}

	@Test
	public void testGetOrderIdAllSetWithoutOrderHeader() {
		initContext();
		HttpHeaders headers = new HttpHeaders();
		getServiceRequestContext().setHeaders(headers);
		Assert.assertNull(getOrderId());
	}

	@Test
	public void testGetCorrelationId() {
		Assert.assertNull(getCorrelationId());
	}

	@Test
	public void testGetCorrelationIdWithoutSRCSet() {
		RequestAttributes attributes = new CustomRequestAttributes();
		RequestContextHolder.setRequestAttributes(attributes);
		Assert.assertNull(getCorrelationId());
	}

	@Test
	public void testGetCorrelationIdWithSRCSet() {
		RequestAttributes attributes = new CustomRequestAttributes();
		RequestContextHolder.setRequestAttributes(attributes);
		ServiceRequestContext context = new ServiceRequestContext();
		RequestContextHolder.getRequestAttributes().setAttribute(Constant.SERVICE_REQUEST_CONTEXT, context,
				RequestAttributes.SCOPE_REQUEST);
		Assert.assertNull(getCorrelationId());
	}

	@Test
	public void testGetCorrelationIdAllSetWithCorrelationHeader() {
		initContext();
		HttpHeaders headers = new HttpHeaders();
		headers.add(Constant.CORRELATIONID_HDR, "3");
		getServiceRequestContext().setHeaders(headers);
		Assert.assertNotNull(getCorrelationId());
	}

	@Test
	public void testGetCorrelationIdAllSetWithoutCorrelationHeader() {
		initContext();
		HttpHeaders headers = new HttpHeaders();
		getServiceRequestContext().setHeaders(headers);
		Assert.assertNull(getCorrelationId());
	}
	
	@Test
	public void testGetAppName() {
		Assert.assertNull(getAppName());
	}

	@Test
	public void testGetAppNameWithoutSRCSet() {
		RequestAttributes attributes = new CustomRequestAttributes();
		RequestContextHolder.setRequestAttributes(attributes);
		Assert.assertNull(getAppName());
	}

	@Test
	public void testGetAppNameWithSRCSet() {
		RequestAttributes attributes = new CustomRequestAttributes();
		RequestContextHolder.setRequestAttributes(attributes);
		ServiceRequestContext context = new ServiceRequestContext();
		RequestContextHolder.getRequestAttributes().setAttribute(Constant.SERVICE_REQUEST_CONTEXT, context,
				RequestAttributes.SCOPE_REQUEST);
		Assert.assertNull(getAppName());
	}

	@Test
	public void testGetAppNameWithAppName() {
		initContext();
		Object objContext = RequestContextHolder.getRequestAttributes() != null
				? RequestContextHolder.getRequestAttributes().getAttribute(SERVICE_REQUEST_CONTEXT,
						RequestAttributes.SCOPE_REQUEST)
				: null;
		ServiceRequestContext requestContext = (ServiceRequestContext) objContext;
		requestContext.setApplicationName("someapplicationname");
		Assert.assertNotNull(getAppName());
	}

	@Test
	public void testGetOrigin() {
		Assert.assertNull(getOrigin());
	}

	@Test
	public void testGetOriginWithoutSRCSet() {
		RequestAttributes attributes = new CustomRequestAttributes();
		RequestContextHolder.setRequestAttributes(attributes);
		Assert.assertNull(getOrigin());
	}

	@Test
	public void testGetOriginWithSRCSet() {
		RequestAttributes attributes = new CustomRequestAttributes();
		RequestContextHolder.setRequestAttributes(attributes);
		ServiceRequestContext context = new ServiceRequestContext();
		RequestContextHolder.getRequestAttributes().setAttribute(Constant.SERVICE_REQUEST_CONTEXT, context,
				RequestAttributes.SCOPE_REQUEST);
		Assert.assertEquals(RequestOriginEnum.REST, getOrigin());
	}

	@Test
	public void testGetOriginWithOrigin() {
		initContext();
		Object objContext = RequestContextHolder.getRequestAttributes() != null
				? RequestContextHolder.getRequestAttributes().getAttribute(SERVICE_REQUEST_CONTEXT,
						RequestAttributes.SCOPE_REQUEST)
				: null;
		ServiceRequestContext requestContext = (ServiceRequestContext) objContext;
		requestContext.setOrigin(RequestOriginEnum.REST);
		Assert.assertNotNull(getOrigin());
	}

	@Test
	public void testGetReceivedTime() {
		Assert.assertNull(getReceivedTime());
	}

	@Test
	public void testGetReceivedTimeWithoutSRCSet() {
		RequestAttributes attributes = new CustomRequestAttributes();
		RequestContextHolder.setRequestAttributes(attributes);
		Assert.assertNull(getReceivedTime());
	}

	@Test
	public void testGetReceivedTimeWithSRCSet() {
		RequestAttributes attributes = new CustomRequestAttributes();
		RequestContextHolder.setRequestAttributes(attributes);
		ServiceRequestContext context = new ServiceRequestContext();
		RequestContextHolder.getRequestAttributes().setAttribute(Constant.SERVICE_REQUEST_CONTEXT, context,
				RequestAttributes.SCOPE_REQUEST);
		Assert.assertNull(getReceivedTime());
	}

	@Test
	public void testGetReceivedTimeWithReceivedTime() {
		initContext();
		Object objContext = RequestContextHolder.getRequestAttributes() != null
				? RequestContextHolder.getRequestAttributes().getAttribute(SERVICE_REQUEST_CONTEXT,
						RequestAttributes.SCOPE_REQUEST)
				: null;
		ServiceRequestContext requestContext = (ServiceRequestContext) objContext;
		requestContext.setReceivedTime("somereceivedtime");
		Assert.assertNotNull(getReceivedTime());
	}
	
	@Test
	public void testGetCallerId() {
		Assert.assertNull(getCallerId());
	}

	@Test
	public void testGetCallerIdWithoutSRCSet() {
		RequestAttributes attributes = new CustomRequestAttributes();
		RequestContextHolder.setRequestAttributes(attributes);
		Assert.assertNull(getCallerId());
	}

	@Test
	public void testGetCallerIdWithSRCSet() {
		RequestAttributes attributes = new CustomRequestAttributes();
		RequestContextHolder.setRequestAttributes(attributes);
		ServiceRequestContext context = new ServiceRequestContext();
		RequestContextHolder.getRequestAttributes().setAttribute(Constant.SERVICE_REQUEST_CONTEXT, context,
				RequestAttributes.SCOPE_REQUEST);
		Assert.assertNull(getCallerId());
	}

	@Test
	public void testGetCallerIdWithCaller() {
		initContext();
		Object objContext = RequestContextHolder.getRequestAttributes() != null
				? RequestContextHolder.getRequestAttributes().getAttribute(SERVICE_REQUEST_CONTEXT,
						RequestAttributes.SCOPE_REQUEST)
				: null;
		ServiceRequestContext requestContext = (ServiceRequestContext) objContext;
		requestContext.setCallerId("somecallerid");
		Assert.assertNotNull(getCallerId());
	}
	
	@Test
	public void testGetRequestURL() {
		Assert.assertNull(getRequestURL());
	}

	@Test
	public void testGetRequestURLWithoutSRCSet() {
		RequestAttributes attributes = new CustomRequestAttributes();
		RequestContextHolder.setRequestAttributes(attributes);
		Assert.assertNull(getRequestURL());
	}

	@Test
	public void testGetRequestURLWithSRCSet() {
		RequestAttributes attributes = new CustomRequestAttributes();
		RequestContextHolder.setRequestAttributes(attributes);
		ServiceRequestContext context = new ServiceRequestContext();
		RequestContextHolder.getRequestAttributes().setAttribute(Constant.SERVICE_REQUEST_CONTEXT, context,
				RequestAttributes.SCOPE_REQUEST);
		Assert.assertNull(getRequestURL());
	}

	@Test
	public void testGetRequestURLWithRequestURL() {
		initContext();
		Object objContext = RequestContextHolder.getRequestAttributes() != null
				? RequestContextHolder.getRequestAttributes().getAttribute(SERVICE_REQUEST_CONTEXT,
						RequestAttributes.SCOPE_REQUEST)
				: null;
		ServiceRequestContext requestContext = (ServiceRequestContext) objContext;
		requestContext.setUrl("someurl");
		Assert.assertNotNull(getRequestURL());
	}
	
	@Test
	public void testGetContentType() {
		Assert.assertNull(getContentType());
	}

	@Test
	public void testGetContentTypeWithoutSRCSet() {
		RequestAttributes attributes = new CustomRequestAttributes();
		RequestContextHolder.setRequestAttributes(attributes);
		Assert.assertNull(getContentType());
	}

	@Test
	public void testGetContentTypeWithSRCSet() {
		RequestAttributes attributes = new CustomRequestAttributes();
		RequestContextHolder.setRequestAttributes(attributes);
		ServiceRequestContext context = new ServiceRequestContext();
		RequestContextHolder.getRequestAttributes().setAttribute(Constant.SERVICE_REQUEST_CONTEXT, context,
				RequestAttributes.SCOPE_REQUEST);
		Assert.assertNotNull(getContentType());
	}

	@Test
	public void testGetContentTypeWithContentType() {
		initContext();
		Object objContext = RequestContextHolder.getRequestAttributes() != null
				? RequestContextHolder.getRequestAttributes().getAttribute(SERVICE_REQUEST_CONTEXT,
						RequestAttributes.SCOPE_REQUEST)
				: null;
		ServiceRequestContext requestContext = (ServiceRequestContext) objContext;
		requestContext.setContentType("application/json");
		Assert.assertNotNull(getContentType());
	}
	
	@Test
	public void testIsContentTypeApplicationJson() {
		Assert.assertFalse(isContentTypeApplicationJson());
	}

	@Test
	public void testIsContentTypeApplicationJsonWithCorrectContentType() {
		initContext();
		Object objContext = RequestContextHolder.getRequestAttributes() != null
				? RequestContextHolder.getRequestAttributes().getAttribute(SERVICE_REQUEST_CONTEXT,
						RequestAttributes.SCOPE_REQUEST)
				: null;
		ServiceRequestContext requestContext = (ServiceRequestContext) objContext;
		requestContext.setContentType(MediaType.APPLICATION_JSON_VALUE);
		Assert.assertTrue(isContentTypeApplicationJson());
	}

	@Test
	public void testIsContentTypeApplicationJsonWithDifferentContentType() {
		initContext();
		Object objContext = RequestContextHolder.getRequestAttributes() != null
				? RequestContextHolder.getRequestAttributes().getAttribute(SERVICE_REQUEST_CONTEXT,
						RequestAttributes.SCOPE_REQUEST)
				: null;
		ServiceRequestContext requestContext = (ServiceRequestContext) objContext;
		requestContext.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
		Assert.assertFalse(isContentTypeApplicationJson());
	}

	@Test
	public void testIsContentTypeApplicationXML() {
		Assert.assertFalse(isContentTypeApplicationXml());
	}

	@Test
	public void testIsContentTypeApplicationXmlWithCorrectContentType() {
		initContext();
		Object objContext = RequestContextHolder.getRequestAttributes() != null
				? RequestContextHolder.getRequestAttributes().getAttribute(SERVICE_REQUEST_CONTEXT,
						RequestAttributes.SCOPE_REQUEST)
				: null;
		ServiceRequestContext requestContext = (ServiceRequestContext) objContext;
		requestContext.setContentType(MediaType.APPLICATION_XML_VALUE);
		Assert.assertTrue(isContentTypeApplicationXml());
	}

	@Test
	public void testIsContentTypeApplicationXmlWithDifferentContentType() {
		initContext();
		Object objContext = RequestContextHolder.getRequestAttributes() != null
				? RequestContextHolder.getRequestAttributes().getAttribute(SERVICE_REQUEST_CONTEXT,
						RequestAttributes.SCOPE_REQUEST)
				: null;
		ServiceRequestContext requestContext = (ServiceRequestContext) objContext;
		requestContext.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
		Assert.assertFalse(isContentTypeApplicationXml());
	}
	
	@Test
	public void testGetUrl() {
		Assert.assertNull(getUrl());
	}

	@Test
	public void testGetUrlWithoutSRCSet() {
		RequestAttributes attributes = new CustomRequestAttributes();
		RequestContextHolder.setRequestAttributes(attributes);
		Assert.assertNull(getUrl());
	}

	@Test
	public void testGetUrlWithSRCSet() {
		RequestAttributes attributes = new CustomRequestAttributes();
		RequestContextHolder.setRequestAttributes(attributes);
		ServiceRequestContext context = new ServiceRequestContext();
		RequestContextHolder.getRequestAttributes().setAttribute(Constant.SERVICE_REQUEST_CONTEXT, context,
				RequestAttributes.SCOPE_REQUEST);
		Assert.assertNull(getUrl());
	}

	@Test
	public void testGetUrlWithUrl() {
		initContext();
		Object objContext = RequestContextHolder.getRequestAttributes() != null
				? RequestContextHolder.getRequestAttributes().getAttribute(SERVICE_REQUEST_CONTEXT,
						RequestAttributes.SCOPE_REQUEST)
				: null;
		ServiceRequestContext requestContext = (ServiceRequestContext) objContext;
		requestContext.setUrl("someurl");
		Assert.assertNotNull(getUrl());
	}
}
