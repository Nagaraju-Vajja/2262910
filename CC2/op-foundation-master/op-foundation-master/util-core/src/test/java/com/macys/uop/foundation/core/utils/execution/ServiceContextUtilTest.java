package com.macys.uop.foundation.core.utils.execution;

import static com.macys.uop.foundation.core.utils.Constant.SERVICE_REQUEST_CONTEXT;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import com.macys.uop.foundation.core.utils.Constant;
import com.macys.uop.foundation.core.utils.test.TestContextUtil;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ServiceContextUtil.class)
public class ServiceContextUtilTest implements TestContextUtil, ServiceContextUtil {

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
	public void testSetServiceRequestContextNull() {
		setServiceRequestContext(null);
		Assert.assertNull(getServiceRequestContext());
	}

	@Test
	public void testSetServiceRequestContextNotNull() {
		ServiceRequestContext context = new ServiceRequestContext();
		setServiceRequestContext(context);
		Assert.assertNotNull(getServiceRequestContext());
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
	public void testGetExecutionId() {
		Assert.assertNull(getExecutionId());
	}

	@Test
	public void testGetExecutionIdWithoutSRCSet() {
		RequestAttributes attributes = new CustomRequestAttributes();
		RequestContextHolder.setRequestAttributes(attributes);
		Assert.assertNull(getExecutionId());
	}

	@Test
	public void testGetExecutionIdWithSRCSet() {
		RequestAttributes attributes = new CustomRequestAttributes();
		RequestContextHolder.setRequestAttributes(attributes);
		ServiceRequestContext context = new ServiceRequestContext();
		RequestContextHolder.getRequestAttributes().setAttribute(Constant.SERVICE_REQUEST_CONTEXT, context,
				RequestAttributes.SCOPE_REQUEST);
		Assert.assertNull(getExecutionId());
	}

	@Test
	public void testGetExecutionIdAllSetWithExecutionHeader() {
		initContext();
		HttpHeaders headers = new HttpHeaders();
		headers.add(Constant.EXECUTIONID_HDR, "3");
		getServiceRequestContext().setHeaders(headers);
		Assert.assertNotNull(getExecutionId());
	}

	@Test
	public void testGetExecutionIdAllSetWithoutExecutionHeader() {
		initContext();
		HttpHeaders headers = new HttpHeaders();
		getServiceRequestContext().setHeaders(headers);
		Assert.assertNull(getExecutionId());
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
	public void testGetRequestURI() {
		Assert.assertNull(getRequestURI());
	}

	@Test
	public void testGetRequestURIWithoutSRCSet() {
		RequestAttributes attributes = new CustomRequestAttributes();
		RequestContextHolder.setRequestAttributes(attributes);
		Assert.assertNull(getRequestURI());
	}

	@Test
	public void testGetRequestURIWithSRCSet() {
		RequestAttributes attributes = new CustomRequestAttributes();
		RequestContextHolder.setRequestAttributes(attributes);
		ServiceRequestContext context = new ServiceRequestContext();
		RequestContextHolder.getRequestAttributes().setAttribute(Constant.SERVICE_REQUEST_CONTEXT, context,
				RequestAttributes.SCOPE_REQUEST);
		Assert.assertNull(getRequestURI());
	}

	@Test
	public void testGetRequestURIWithRequestURI() {
		initContext();
		Object objContext = RequestContextHolder.getRequestAttributes() != null
				? RequestContextHolder.getRequestAttributes().getAttribute(SERVICE_REQUEST_CONTEXT,
						RequestAttributes.SCOPE_REQUEST)
				: null;
		ServiceRequestContext requestContext = (ServiceRequestContext) objContext;
		requestContext.setUri("someuri");
		Assert.assertNotNull(getRequestURI());
	}

	@Test
	public void testGetRequestHttpMethod() {
		Assert.assertNull(getRequestHttpMethod());
	}

	@Test
	public void testGetRequestHttpMethodWithoutSRCSet() {
		RequestAttributes attributes = new CustomRequestAttributes();
		RequestContextHolder.setRequestAttributes(attributes);
		Assert.assertNull(getRequestHttpMethod());
	}

	@Test
	public void testGetRequestHttpMethodWithSRCSet() {
		RequestAttributes attributes = new CustomRequestAttributes();
		RequestContextHolder.setRequestAttributes(attributes);
		ServiceRequestContext context = new ServiceRequestContext();
		RequestContextHolder.getRequestAttributes().setAttribute(Constant.SERVICE_REQUEST_CONTEXT, context,
				RequestAttributes.SCOPE_REQUEST);
		Assert.assertNull(getRequestHttpMethod());
	}

	@Test
	public void testGetRequestHttpMethodWithRequestHttpMethod() {
		initContext();
		Object objContext = RequestContextHolder.getRequestAttributes() != null
				? RequestContextHolder.getRequestAttributes().getAttribute(SERVICE_REQUEST_CONTEXT,
						RequestAttributes.SCOPE_REQUEST)
				: null;
		ServiceRequestContext requestContext = (ServiceRequestContext) objContext;
		requestContext.setMethod("POST");
		Assert.assertNotNull(getRequestHttpMethod());
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

	@Test
	public void testGetUri() {
		Assert.assertNull(getUri());
	}

	@Test
	public void testGetUriWithoutSRCSet() {
		RequestAttributes attributes = new CustomRequestAttributes();
		RequestContextHolder.setRequestAttributes(attributes);
		Assert.assertNull(getUri());
	}

	@Test
	public void testGetUriWithSRCSet() {
		RequestAttributes attributes = new CustomRequestAttributes();
		RequestContextHolder.setRequestAttributes(attributes);
		ServiceRequestContext context = new ServiceRequestContext();
		RequestContextHolder.getRequestAttributes().setAttribute(Constant.SERVICE_REQUEST_CONTEXT, context,
				RequestAttributes.SCOPE_REQUEST);
		Assert.assertNull(getUri());
	}

	@Test
	public void testGetUriWithUri() {
		initContext();
		Object objContext = RequestContextHolder.getRequestAttributes() != null
				? RequestContextHolder.getRequestAttributes().getAttribute(SERVICE_REQUEST_CONTEXT,
						RequestAttributes.SCOPE_REQUEST)
				: null;
		ServiceRequestContext requestContext = (ServiceRequestContext) objContext;
		requestContext.setUri("someuri");
		Assert.assertNotNull(getUri());
	}

	@Test
	public void testGetHttpMethod() {
		Assert.assertNull(getHttpMethod());
	}

	@Test
	public void testGetHttpMethodWithoutSRCSet() {
		RequestAttributes attributes = new CustomRequestAttributes();
		RequestContextHolder.setRequestAttributes(attributes);
		Assert.assertNull(getHttpMethod());
	}

	@Test
	public void testGetHttpMethodWithSRCSet() {
		RequestAttributes attributes = new CustomRequestAttributes();
		RequestContextHolder.setRequestAttributes(attributes);
		ServiceRequestContext context = new ServiceRequestContext();
		RequestContextHolder.getRequestAttributes().setAttribute(Constant.SERVICE_REQUEST_CONTEXT, context,
				RequestAttributes.SCOPE_REQUEST);
		Assert.assertNull(getHttpMethod());
	}

	@Test
	public void testGetHttpMethodWithHttpMethod() {
		initContext();
		Object objContext = RequestContextHolder.getRequestAttributes() != null
				? RequestContextHolder.getRequestAttributes().getAttribute(SERVICE_REQUEST_CONTEXT,
						RequestAttributes.SCOPE_REQUEST)
				: null;
		ServiceRequestContext requestContext = (ServiceRequestContext) objContext;
		requestContext.setMethod("POST");
		Assert.assertNotNull(getHttpMethod());
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
	public void testGetBody() {
		Assert.assertNull(getBody());
	}

	@Test
	public void testGetBodyWithoutSRCSet() {
		RequestAttributes attributes = new CustomRequestAttributes();
		RequestContextHolder.setRequestAttributes(attributes);
		Assert.assertNull(getBody());
	}

	@Test
	public void testGetBodyWithSRCSet() {
		RequestAttributes attributes = new CustomRequestAttributes();
		RequestContextHolder.setRequestAttributes(attributes);
		ServiceRequestContext context = new ServiceRequestContext();
		RequestContextHolder.getRequestAttributes().setAttribute(Constant.SERVICE_REQUEST_CONTEXT, context,
				RequestAttributes.SCOPE_REQUEST);
		Assert.assertNull(getBody());
	}

	@Test
	public void testGetBodyWithBody() {
		initContext();
		Object objContext = RequestContextHolder.getRequestAttributes() != null
				? RequestContextHolder.getRequestAttributes().getAttribute(SERVICE_REQUEST_CONTEXT,
						RequestAttributes.SCOPE_REQUEST)
				: null;
		ServiceRequestContext requestContext = (ServiceRequestContext) objContext;
		requestContext.setBody("somebody");
		Assert.assertNotNull(getBody());
	}

	@Test
	public void testGetHttpHeaders() {
		Assert.assertNull(getHttpHeaders());
	}

	@Test
	public void testGetHttpHeadersWithoutSRCSet() {
		RequestAttributes attributes = new CustomRequestAttributes();
		RequestContextHolder.setRequestAttributes(attributes);
		Assert.assertNull(getHttpHeaders());
	}

	@Test
	public void testGetHttpHeadersWithSRCSet() {
		RequestAttributes attributes = new CustomRequestAttributes();
		RequestContextHolder.setRequestAttributes(attributes);
		ServiceRequestContext context = new ServiceRequestContext();
		RequestContextHolder.getRequestAttributes().setAttribute(Constant.SERVICE_REQUEST_CONTEXT, context,
				RequestAttributes.SCOPE_REQUEST);
		Assert.assertEquals(getHttpHeaders().size(), 0);
	}

	@Test
	public void testGetHttpHeadersWithHttpHeaders() {
		initContext();
		Object objContext = RequestContextHolder.getRequestAttributes() != null
				? RequestContextHolder.getRequestAttributes().getAttribute(SERVICE_REQUEST_CONTEXT,
						RequestAttributes.SCOPE_REQUEST)
				: null;
		ServiceRequestContext requestContext = (ServiceRequestContext) objContext;
		HttpHeaders headers = new HttpHeaders();
		headers.add(Constant.MESSAGEID_HDR, "1");
		headers.add(Constant.ORDERID_HDR, "2");
		headers.add(Constant.CLIENTID_HDR, "3");
		headers.add(Constant.CORRELATIONID_HDR, "4");
		requestContext.setHeaders(headers);
		Assert.assertEquals(getHttpHeaders().size(), 4);
	}

	@Test
	public void testGetSingleValueHttpHeaders() {
		Assert.assertNull(getSingleValueHttpHeaders());
	}

	@Test
	public void testGetSingleValueHttpHeadersWithoutSRCSet() {
		RequestAttributes attributes = new CustomRequestAttributes();
		RequestContextHolder.setRequestAttributes(attributes);
		Assert.assertNull(getSingleValueHttpHeaders());
	}

	@Test
	public void testGetSingleValueHttpHeadersWithSRCSet() {
		RequestAttributes attributes = new CustomRequestAttributes();
		RequestContextHolder.setRequestAttributes(attributes);
		ServiceRequestContext context = new ServiceRequestContext();
		RequestContextHolder.getRequestAttributes().setAttribute(Constant.SERVICE_REQUEST_CONTEXT, context,
				RequestAttributes.SCOPE_REQUEST);
		Assert.assertNull(getSingleValueHttpHeaders());
	}

	@Test
	public void testGetSingleValueHttpHeadersWithHttpHeadersSet() {
		initContext();
		Object objContext = RequestContextHolder.getRequestAttributes() != null
				? RequestContextHolder.getRequestAttributes().getAttribute(SERVICE_REQUEST_CONTEXT,
						RequestAttributes.SCOPE_REQUEST)
				: null;
		ServiceRequestContext requestContext = (ServiceRequestContext) objContext;
		HttpHeaders headers = new HttpHeaders();
		headers.add(Constant.MESSAGEID_HDR, "1");
		headers.add(Constant.MESSAGEID_HDR, "11");
		headers.add(Constant.ORDERID_HDR, "2");
		headers.add(Constant.CLIENTID_HDR, "3");
		headers.add(Constant.CORRELATIONID_HDR, "4");
		requestContext.setHeaders(headers);
		Assert.assertEquals(getSingleValueHttpHeaders().size(), 4);
	}

	@Test
	public void testGetSingleValueHttpHeadersWithBlankHttpHeadersSet() {
		initContext();
		Object objContext = RequestContextHolder.getRequestAttributes() != null
				? RequestContextHolder.getRequestAttributes().getAttribute(SERVICE_REQUEST_CONTEXT,
						RequestAttributes.SCOPE_REQUEST)
				: null;
		ServiceRequestContext requestContext = (ServiceRequestContext) objContext;
		HttpHeaders headers = new HttpHeaders();
		requestContext.setHeaders(headers);
		Assert.assertNull(getSingleValueHttpHeaders());
	}
	
	@Test
	public void testGetSingleValueHeaderParam() {
		Assert.assertNull(getSingleValueHeaderParam(null));
	}
	
	@Test
	public void testGetSingleValueHeaderParamPassedNonNull() {
		Assert.assertNull(getSingleValueHeaderParam("somevalue"));
	}
	
	@Test
	public void testGetSingleValueHeaderParamWithoutSRCSet() {
		RequestAttributes attributes = new CustomRequestAttributes();
		RequestContextHolder.setRequestAttributes(attributes);
		Assert.assertNull(getSingleValueHeaderParam(null));
	}
	
	@Test
	public void testGetSingleValueHeaderParamWithSRCSet() {
		RequestAttributes attributes = new CustomRequestAttributes();
		RequestContextHolder.setRequestAttributes(attributes);
		ServiceRequestContext context = new ServiceRequestContext();
		RequestContextHolder.getRequestAttributes().setAttribute(Constant.SERVICE_REQUEST_CONTEXT, context,
				RequestAttributes.SCOPE_REQUEST);
		Assert.assertNull(getSingleValueHeaderParam("somevalue"));
	}
	
	@Test
	public void testGetSingleValueHeaderParamWithHeadersBlank() {
		RequestAttributes attributes = new CustomRequestAttributes();
		RequestContextHolder.setRequestAttributes(attributes);
		ServiceRequestContext context = new ServiceRequestContext();
		RequestContextHolder.getRequestAttributes().setAttribute(Constant.SERVICE_REQUEST_CONTEXT, context,
				RequestAttributes.SCOPE_REQUEST);
		HttpHeaders headers = new HttpHeaders();
		context.setHeaders(headers);
		Assert.assertNull(getSingleValueHeaderParam("somevalue"));
	}
	
	@Test
	public void testGetSingleValueHeaderParamWithHeaders() {
		RequestAttributes attributes = new CustomRequestAttributes();
		RequestContextHolder.setRequestAttributes(attributes);
		ServiceRequestContext context = new ServiceRequestContext();
		RequestContextHolder.getRequestAttributes().setAttribute(Constant.SERVICE_REQUEST_CONTEXT, context,
				RequestAttributes.SCOPE_REQUEST);
		HttpHeaders headers = new HttpHeaders();
		headers.add(Constant.MESSAGEID_HDR, "1");
		headers.add(Constant.MESSAGEID_HDR, "11");
		headers.add(Constant.ORDERID_HDR, "2");
		headers.add(Constant.CLIENTID_HDR, "3");
		headers.add(Constant.CORRELATIONID_HDR, "4");
		context.setHeaders(headers);
		Assert.assertEquals("1", getSingleValueHeaderParam(Constant.MESSAGEID_HDR));
	}
	
	@Test
	public void testGetQueryParameters() {
		Assert.assertNull(getQueryParameters());
	}

	@Test
	public void testGetQueryParametersWithoutSRCSet() {
		RequestAttributes attributes = new CustomRequestAttributes();
		RequestContextHolder.setRequestAttributes(attributes);
		Assert.assertNull(getQueryParameters());
	}

	@Test
	public void testGetQueryParametersWithSRCSet() {
		RequestAttributes attributes = new CustomRequestAttributes();
		RequestContextHolder.setRequestAttributes(attributes);
		ServiceRequestContext context = new ServiceRequestContext();
		RequestContextHolder.getRequestAttributes().setAttribute(Constant.SERVICE_REQUEST_CONTEXT, context,
				RequestAttributes.SCOPE_REQUEST);
		Assert.assertEquals(0, getQueryParameters().size());
	}
	
	@Test
	public void testGetQueryParametersWithQueryParameters() {
		initContext();
		Object objContext=RequestContextHolder.getRequestAttributes()!=null ? 
				RequestContextHolder.getRequestAttributes().getAttribute(SERVICE_REQUEST_CONTEXT, RequestAttributes.SCOPE_REQUEST) : null;
		ServiceRequestContext requestContext=(ServiceRequestContext) objContext;
		MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
		queryParams.add("key", "value");
		queryParams.add("key", "value1");
		queryParams.add("key1", "value1");
		requestContext.setQueryParams(queryParams);
		Assert.assertEquals(2, getQueryParameters().size());
	}
	
	@Test
	public void testGetSingleValueQueryParameters() {
		Assert.assertNull(getSingleValueQueryParameters());
	}

	@Test
	public void testGetSingleValueQueryParametersWithoutSRCSet() {
		RequestAttributes attributes = new CustomRequestAttributes();
		RequestContextHolder.setRequestAttributes(attributes);
		Assert.assertNull(getSingleValueQueryParameters());
	}

	@Test
	public void testGetSingleValueQueryParametersWithSRCSet() {
		RequestAttributes attributes = new CustomRequestAttributes();
		RequestContextHolder.setRequestAttributes(attributes);
		ServiceRequestContext context = new ServiceRequestContext();
		RequestContextHolder.getRequestAttributes().setAttribute(Constant.SERVICE_REQUEST_CONTEXT, context,
				RequestAttributes.SCOPE_REQUEST);
		Assert.assertEquals(0, getQueryParameters().size());
	}
	
	@Test
	public void testGetSingleValueQueryParametersWithQueryParametersBlank() {
		initContext();
		Object objContext=RequestContextHolder.getRequestAttributes()!=null ? 
				RequestContextHolder.getRequestAttributes().getAttribute(SERVICE_REQUEST_CONTEXT, RequestAttributes.SCOPE_REQUEST) : null;
		ServiceRequestContext requestContext=(ServiceRequestContext) objContext;
		MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
		requestContext.setQueryParams(queryParams);
		Assert.assertEquals(0, getSingleValueQueryParameters().size());
	}
	
	@Test
	public void testGetSingleValueQueryParametersWithQueryParameters() {
		initContext();
		Object objContext=RequestContextHolder.getRequestAttributes()!=null ? 
				RequestContextHolder.getRequestAttributes().getAttribute(SERVICE_REQUEST_CONTEXT, RequestAttributes.SCOPE_REQUEST) : null;
		ServiceRequestContext requestContext=(ServiceRequestContext) objContext;
		MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
		queryParams.add("key", "value");
		queryParams.add("key", "value1");
		queryParams.add("key1", "value1");
		requestContext.setQueryParams(queryParams);
		Assert.assertEquals(2, getSingleValueQueryParameters().size());
	}
	
	@Test
	public void testGetSingleValueQueryParam() {
		Assert.assertNull(getSingleValueQueryParam(null));
	}
	
	@Test
	public void testGetSingleValueQueryParamPassedNonNull() {
		Assert.assertNull(getSingleValueQueryParam("somevalue"));
	}
	
	@Test
	public void testGetSingleValueQueryParamWithoutSRCSet() {
		RequestAttributes attributes = new CustomRequestAttributes();
		RequestContextHolder.setRequestAttributes(attributes);
		Assert.assertNull(getSingleValueQueryParam(null));
	}
	
	@Test
	public void testGetSingleValueQueryParamWithSRCSet() {
		RequestAttributes attributes = new CustomRequestAttributes();
		RequestContextHolder.setRequestAttributes(attributes);
		ServiceRequestContext context = new ServiceRequestContext();
		RequestContextHolder.getRequestAttributes().setAttribute(Constant.SERVICE_REQUEST_CONTEXT, context,
				RequestAttributes.SCOPE_REQUEST);
		Assert.assertNull(getSingleValueQueryParam("somevalue"));
	}
	
	@Test
	public void testGetSingleValueQueryParamWithQuerysBlank() {
		RequestAttributes attributes = new CustomRequestAttributes();
		RequestContextHolder.setRequestAttributes(attributes);
		ServiceRequestContext context = new ServiceRequestContext();
		RequestContextHolder.getRequestAttributes().setAttribute(Constant.SERVICE_REQUEST_CONTEXT, context,
				RequestAttributes.SCOPE_REQUEST);
		MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
		context.setQueryParams(queryParams);
		Assert.assertNull(getSingleValueQueryParam("key"));
	}
	
	@Test
	public void testGetSingleValueQueryParamWithQuerys() {
		RequestAttributes attributes = new CustomRequestAttributes();
		RequestContextHolder.setRequestAttributes(attributes);
		ServiceRequestContext context = new ServiceRequestContext();
		RequestContextHolder.getRequestAttributes().setAttribute(Constant.SERVICE_REQUEST_CONTEXT, context,
				RequestAttributes.SCOPE_REQUEST);
		MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
		queryParams.add("key", "value");
		queryParams.add("key", "value1");
		queryParams.add("key1", "value1");
		context.setQueryParams(queryParams);
		Assert.assertEquals("value", getSingleValueQueryParam("key"));
	}
}
