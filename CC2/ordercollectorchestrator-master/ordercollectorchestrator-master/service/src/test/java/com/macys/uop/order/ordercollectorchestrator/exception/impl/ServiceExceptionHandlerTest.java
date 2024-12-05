package com.macys.uop.order.ordercollectorchestrator.exception.impl;

import static org.junit.Assert.assertEquals;

import com.macys.uop.foundation.core.utils.CommonStatusCode;
import com.macys.uop.foundation.core.utils.Constant;
import com.macys.uop.foundation.core.utils.exception.Error;
import com.macys.uop.foundation.core.utils.exception.ErrorDetail;
import com.macys.uop.foundation.core.utils.exception.ProblemUtil;
import com.macys.uop.foundation.core.utils.execution.ServiceContextUtil;
import com.macys.uop.foundation.core.utils.test.TestContextUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.junit4.SpringRunner;
import org.zalando.problem.DefaultProblem;
import org.zalando.problem.Status;
import org.zalando.problem.ThrowableProblem;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { ServiceExceptionHandler.class })
public class ServiceExceptionHandlerTest implements TestContextUtil, ProblemUtil,
    ServiceContextUtil {

	@Mock
	private JoinPoint joinPoint;

	@Mock
	private Signature signature;

	@Autowired
	private ServiceExceptionHandler serviceExceptionHandler;

	@Before
	public void beforeTest() {
		initContext();
        HttpHeaders headers = new HttpHeaders();
        headers.add(Constant.MESSAGEID_HDR, "1");
        headers.add(Constant.ORDERID_HDR, "2");
        headers.add(Constant.CLIENTID_HDR, "3");
        headers.add(Constant.CORRELATIONID_HDR, "4");
        getServiceRequestContext().setApplicationName("ordercollectorchestrator");
        getServiceRequestContext().setHeaders(headers);
	}

	@After
	public void afterTest() {
		clearContext();
	}

	@Test
	public void testHandleExceptionNormal() {
		getServiceRequestContext().setUrl("/host:port/context");

		Exception exception = new RuntimeException("Communication Error");
		try {
			serviceExceptionHandler.handleException(joinPoint, exception);
		} catch (Exception e) {
			assertEquals(500, ((DefaultProblem) e).getStatus().getStatusCode());
			assertEquals("Internal Server Error", ((DefaultProblem) e).getStatus().getReasonPhrase());
		}

	}

	@Test
	public void testHandleExceptionThrowableProblem() {

		Error validationError = Error
				.builder().withCode(CommonStatusCode.BAD_REQUEST.getCode())
				.withMessage(CommonStatusCode.BAD_REQUEST.getDescription())
				.withErrorDetail(ErrorDetail.builder().withDomain(getServiceRequestContext().getApplicationName())
						.withReason("Validation Failure").withMessage("Mandatory Fields Missing")
						.withLocation("ordercollectorchestrator").build())
				.build();
		ThrowableProblem problem= createProblem(Status.BAD_REQUEST.getStatusCode(), validationError);

		try {
			serviceExceptionHandler.handleException(joinPoint,problem);
		} catch (Exception e) {
			assertEquals(400, ((DefaultProblem) e).getStatus().getStatusCode());
			assertEquals("Bad Request", ((DefaultProblem) e).getStatus().getReasonPhrase());
		}
	}
}
