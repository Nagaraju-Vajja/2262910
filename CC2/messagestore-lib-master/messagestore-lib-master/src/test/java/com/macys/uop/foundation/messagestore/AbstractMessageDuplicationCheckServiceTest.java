package com.macys.uop.foundation.messagestore;

import static com.macys.uop.foundation.core.utils.Constant.PROBLEM_ERROR_KEY;
import static org.mockito.Mockito.mock;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.test.context.junit4.SpringRunner;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import org.zalando.problem.ThrowableProblem;

import com.macys.uop.foundation.core.utils.CommonStatusCode;
import com.macys.uop.foundation.core.utils.exception.ErrorDetail;

@RunWith(SpringRunner.class)
@PowerMockRunnerDelegate(SpringRunner.class)
public class AbstractMessageDuplicationCheckServiceTest {
	
	@Test
	public void testDefaultMessageDuplicationCheckFallback() {
		
		AbstractMessageDuplicationCheckService messageDuplicationCheckService=mock(AbstractMessageDuplicationCheckService.class);
		
		Exception ex=new RuntimeException("Custom Exception");
		
		String fallbackType="Circuit Breaker Fallback";
		
		Mockito.doCallRealMethod().when(messageDuplicationCheckService).defaultMessageDuplicationCheckFallback(ex, fallbackType);
		Mockito.doReturn("12345").when(messageDuplicationCheckService).getClientId();
		Mockito.doReturn("2222").when(messageDuplicationCheckService).getMessageId();
		Mockito.doReturn("33333").when(messageDuplicationCheckService).getOrderId();
		Mockito.doReturn("2222").when(messageDuplicationCheckService).getCorrelationId();
		Mockito.doReturn("some-service").when(messageDuplicationCheckService).getAppName();
		
		com.macys.uop.foundation.core.utils.exception.Error errorInfo = com.macys.uop.foundation.core.utils.exception.Error
				.builder()
				.withCode(CommonStatusCode.MSSAGE_DUPLICATION_CHECK_ERROR.getCode())
				.withMessage(CommonStatusCode.MSSAGE_DUPLICATION_CHECK_ERROR.getDescription())
				.withErrorDetail(ErrorDetail.builder()
						.withDomain("test-service")
						.withReason("Message Duplication Check Error")
						.withMessage(ex.getMessage())
						.build())
				.build();
		
		ThrowableProblem problem=Problem.builder().withStatus(Status.valueOf(Status.INTERNAL_SERVER_ERROR.getStatusCode())).with(PROBLEM_ERROR_KEY, errorInfo).build();
		Mockito.doReturn(problem).when(messageDuplicationCheckService).createProblem(ArgumentMatchers.anyInt(), ArgumentMatchers.any());
		
		ThrowableProblem result=messageDuplicationCheckService.defaultMessageDuplicationCheckFallback(ex, fallbackType);
		
		Assert.assertNotEquals(result.getStatus().getStatusCode(), Status.INTERNAL_SERVER_ERROR);
	}
}
