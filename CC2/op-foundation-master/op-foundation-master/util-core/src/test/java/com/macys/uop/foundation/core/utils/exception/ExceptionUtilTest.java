package com.macys.uop.foundation.core.utils.exception;

import static com.macys.uop.foundation.core.utils.Constant.DEFAULT_EXCEPTION_MESSAGE_WHEN_BLANK;

import java.util.concurrent.ExecutionException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import org.zalando.problem.ThrowableProblem;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ExceptionUtil.class)
public class ExceptionUtilTest implements ExceptionUtil {
	
	private static final String EXCEPTION_MESSAGE="Custom Exception Message";
	
	@Test
	public void testGetMessageThrowableNull() {
		try {
			getMessage(null);
		} catch(IllegalArgumentException iae) {
			Assert.assertEquals("'throwable' cannot be null", iae.getMessage());
		}
	}
	
	@Test
	public void testGetMessageWhenBlank() {
		String message=getMessage(new NullPointerException());
		Assert.assertEquals(DEFAULT_EXCEPTION_MESSAGE_WHEN_BLANK, message);
	}
	
	@Test
	public void testGetMessage() {
		String message=getMessage(new RuntimeException(EXCEPTION_MESSAGE));
		Assert.assertEquals(EXCEPTION_MESSAGE, message);
	}
	
	@Test
    public void testGetStatusCodeHttpClientErrorException(){
        Throwable throwable = new HttpClientErrorException(HttpStatus.BAD_REQUEST);
        Assert.assertEquals(400,getStatusCode(throwable));
    }

    @Test
    public void testGetStatusCodeHttpServerErrorException(){
        Throwable throwable = new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR);
        Assert.assertEquals(500,getStatusCode(throwable));
    }

    @Test
    public void testGetStatusCodeInterruptedException(){
        Throwable throwable = new InterruptedException();
        Assert.assertEquals(throwable.hashCode(),getStatusCode(throwable));
    }

    @Test
    public void testGetStatusCodeExecutionException(){
        Throwable throwable = new ExecutionException("some exception",new RuntimeException("some exception"));
        Assert.assertEquals(throwable.hashCode(),getStatusCode(throwable));
    }

    @Test
    public void testGetStatusCodeThrowableProblem(){
    	ThrowableProblem problem = Problem.builder().withStatus(Status.valueOf(500)).with("key", "value").build();
    	Assert.assertEquals(problem.getStatus().getStatusCode(),getStatusCode(problem));
    }
    
    @Test
    public void testGetStatusCodeAllElse(){
    	Throwable throwable=new RuntimeException("Some Exception");
    	Assert.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(),getStatusCode(throwable));
    }

}
