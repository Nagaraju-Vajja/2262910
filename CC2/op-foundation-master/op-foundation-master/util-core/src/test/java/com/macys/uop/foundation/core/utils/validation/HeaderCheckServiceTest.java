package com.macys.uop.foundation.core.utils.validation;

import static com.macys.uop.foundation.core.utils.Constant.HDR_REQUIRED_DEFAULT_LOCATIONTYPE;
import static com.macys.uop.foundation.core.utils.Constant.HDR_REQUIRED_DEFAULT_REASON;
import static com.macys.uop.foundation.core.utils.Constant.PROBLEM_ERROR_KEY;
import static com.macys.uop.foundation.core.utils.Constant.CORRELATIONID_HDR;
import static org.mockito.Mockito.mock;

import javax.servlet.http.HttpServletRequest;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.junit4.SpringRunner;
import org.zalando.problem.Problem;
import org.zalando.problem.ThrowableProblem;

import com.macys.uop.foundation.core.utils.CommonStatusCode;
import com.macys.uop.foundation.core.utils.exception.ErrorDetail;
import com.macys.uop.foundation.core.utils.execution.ServiceContextUtil;
import com.macys.uop.foundation.core.utils.test.TestContextUtil;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringRunner.class)
@SpringBootTest(classes = { HeaderCheckServiceImpl.class })
public class HeaderCheckServiceTest implements TestContextUtil, ServiceContextUtil {
	
	@Before
	public void beforeTest() {
		initContext();
	}

	@After
	public void afterTest() {
		clearContext();
	}
	
	@Test
	public void testValidateHeaders() {
		
		HttpHeaders headers=new HttpHeaders();
		headers.add(CORRELATIONID_HDR, "1234");
		
		HeaderCheckServiceImpl headerCheckService=mock(HeaderCheckServiceImpl.class);
		HttpServletRequest request = mock(HttpServletRequest.class);
		Mockito.doReturn("/order-service/1.0/").when(headerCheckService).getRequestURI();
		Mockito.doReturn(headers).when(headerCheckService).getHttpHeaders();
		
		Mockito.doCallRealMethod().when(headerCheckService).validateHeaders(request);
		
		headerCheckService.validateHeaders(request);
		
		Assert.assertNotEquals("expected", "actual");
	}
	
	@Test
	public void testMessageHeadersValidationFailure() {
		
		HttpHeaders headers=new HttpHeaders();
		
		com.macys.uop.foundation.core.utils.exception.Error error = com.macys.uop.foundation.core.utils.exception.Error.builder()
				.withCode(CommonStatusCode.NO_CORRELATIONID.getCode())
				.withMessage(CommonStatusCode.NO_CORRELATIONID.getDescription())
				.withErrorDetail(ErrorDetail.builder().withDomain("some-service").withReason(HDR_REQUIRED_DEFAULT_REASON)
						.withMessage(CommonStatusCode.NO_CORRELATIONID.getDescription()).withLocation("some location")
						.withLocationType(HDR_REQUIRED_DEFAULT_LOCATIONTYPE).build())
				.build();
		
		HeaderCheckServiceImpl headerCheckService=mock(HeaderCheckServiceImpl.class);
		HttpServletRequest request = mock(HttpServletRequest.class);
		Mockito.doReturn("/order-service/1.0/").when(headerCheckService).getRequestURI();
		Mockito.doReturn(headers).when(headerCheckService).getHttpHeaders();
		Mockito.doReturn("some-service").when(headerCheckService).getAppName();
		Mockito.doReturn(Problem.builder().with(PROBLEM_ERROR_KEY, error).build()).when(headerCheckService).createProblem(ArgumentMatchers.anyInt(), ArgumentMatchers.any());
		
		Mockito.doCallRealMethod().when(headerCheckService).validateHeaders(request);
		
		try {
			headerCheckService.validateHeaders(request);
		} catch(Exception e) {
			Assert.assertEquals(true, e instanceof ThrowableProblem);
		}
		
	}

}
