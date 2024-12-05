package com.macys.uop.foundation.core.utils.exception;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.junit4.SpringRunner;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

import com.macys.uop.foundation.core.utils.Constant;
import com.macys.uop.foundation.core.utils.execution.ServiceContextUtil;
import com.macys.uop.foundation.core.utils.test.TestContextUtil;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ServiceContextUtil.class)
public class ExceptionHandlerUtilTest implements TestContextUtil, ExceptionHandlerUtil {
   

    @Before
    public void beforeTest() {
        initContext();
        HttpHeaders headers = new HttpHeaders();
        headers.add(Constant.MESSAGEID_HDR, "1");
        headers.add(Constant.ORDERID_HDR, "2");
        headers.add(Constant.CLIENTID_HDR, "3");
        headers.add(Constant.CORRELATIONID_HDR, "4");
        getServiceRequestContext().setHeaders(headers);
        getServiceRequestContext().setApplicationName("Ordercollectorchestrator");
    }

    @After
    public void afterTest(){
        clearContext();
    }

    @Test
    public void handleServiceFailureInstanceThrowableProblem(){
    	Throwable problem = Problem.builder().withStatus(Status.valueOf(500)).with("key", "value").build();
    	Assert.assertThrows(Exception.class, ()-> handleServiceFailure(problem));
    }
    
    @Test
    public void handleServiceFailureInstanceThrowable(){
    	Throwable throwable = new RuntimeException("Some Exception");
    	Assert.assertThrows(Exception.class, ()-> handleServiceFailure(throwable));
    }
    
}
