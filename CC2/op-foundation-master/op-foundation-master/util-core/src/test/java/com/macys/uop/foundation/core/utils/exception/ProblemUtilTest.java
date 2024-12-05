package com.macys.uop.foundation.core.utils.exception;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.zalando.problem.ThrowableProblem;

import com.macys.uop.foundation.core.utils.test.TestContextUtil;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ProblemUtil.class)
public class ProblemUtilTest implements TestContextUtil,ProblemUtil {
    @MockBean
    @Qualifier("problemUtil")
    private ProblemUtil problemUtil;

    @Before
    public void beforeTest() {
        initContext();
    }

    @After
    public void afterTest(){
        clearContext();
    }

    @Test
    public void testCreateProblemWithError(){
        Error error = Error.builder().withCode("404").withMessage("Sample error message").build();
        ThrowableProblem problem = createProblem(error);
        Assert.assertEquals(error,problem.getParameters().get("error"));
    }

    @Test
    public void testCreateProblemWithStatusCodeAndError(){
        Error error = Error.builder().withCode("404").withMessage("Sample error message").build();
        ThrowableProblem problem = createProblem(404,error);
        Assert.assertEquals(404,problem.getStatus().getStatusCode());
    }

    @Test
    public void testCreateProblemWithAppCodeAndAppMessage(){
        Error error = Error.builder().withCode("404").withMessage("Sample error message").build();
        ThrowableProblem problem = createProblem("404","Sample error message");
        Assert.assertEquals(error,problem.getParameters().get("error"));
    }

    @Test
    public void testCreateProblemWithStatusCodeAppCodeAndAppMessage(){
        Error error = Error.builder().withCode("404").withMessage("Sample error message").build();
        ThrowableProblem problem = createProblem(404,"404","Sample error message");
        Assert.assertEquals(error,problem.getParameters().get("error"));
        Assert.assertEquals(404,problem.getStatus().getStatusCode());
    }

    @Test
    public void testCreateProblemWithAppCodeAppMessageAndThrowable(){
        Throwable throwable = new Throwable("Sample error");
        Error error = Error.builder().withCode("404").withMessage("Sample error message").build();
        ThrowableProblem problem = createProblem("404","Sample error message",throwable);
        Assert.assertEquals("Sample error",throwable.getMessage());
        Assert.assertEquals(error,problem.getParameters().get("error"));
    }

    @Test
    public void testCreateProblemWithHashCodeAppCodeAppMessageAndThrowable(){
        ErrorDetail errorDetail = new ErrorDetail();
        errorDetail.setDomain("Domain");
        errorDetail.setLocation("error location");
        errorDetail.setLocationType("location type");
        errorDetail.setMessage("Error message");
        errorDetail.setReason("reason");
        List<ErrorDetail> errorDetailsList = new ArrayList<>();
        errorDetailsList.add(errorDetail);
        Throwable throwable = new Throwable("Sample error");
        ThrowableProblem problem = createProblem(400,"400","Sample error message",throwable);
        Assert.assertEquals("Sample error",throwable.getMessage());
        Assert.assertEquals(400,problem.getStatus().getStatusCode());
    }

    @Test
    public void testCreateProblemWithHashCodeAppCodeAppMessageKeyValueAndThrowable(){
        Throwable throwable = new Throwable("Sample error");
        Error error = Error.builder().withCode("400").withMessage("Sample error message").build();
        ThrowableProblem problem = createProblem(400,"400","Sample error message","retryCount","3",throwable);
        Assert.assertEquals("Sample error",throwable.getMessage());
        Assert.assertEquals(400,problem.getStatus().getStatusCode());
        Assert.assertEquals("3",problem.getParameters().get("retryCount"));
        Assert.assertEquals(error,problem.getParameters().get("error"));
    }

    @Test
    public void testCreateProblemWithHashCodeAppCodeAppMessageKeyValueAndThrowableProblem(){
        Error error = Error.builder().withCode("400").withMessage("Sample error message").build();
        ThrowableProblem throwableProblem = createProblem(400,error);
        ThrowableProblem problem = createProblem(400,"400","Sample error message","retryCount","3",throwableProblem);
        Assert.assertEquals(400,problem.getStatus().getStatusCode());
        Assert.assertEquals("3",problem.getParameters().get("retryCount"));
        Assert.assertEquals(error,problem.getParameters().get("error"));
    }

    @Test
    public void testCreateProblemWithHashCodeAppCodeAppMessageAndThrowableProblem(){
        Error error = Error.builder().withCode("400").withMessage("Sample error message").build();
        ThrowableProblem throwableProblem = createProblem(400,error);
        ThrowableProblem problem = createProblem(400,"400","Sample error message",throwableProblem);
        Assert.assertEquals(400,problem.getStatus().getStatusCode());
        Assert.assertEquals(error,problem.getParameters().get("error"));
    }

    @Test
    public void testCreateProblemWithChannelNamePayloadHeadersAppNameAndThrowable(){
        Map<String,String> headersMap = new HashMap<>();
        headersMap.put("orderId","HSDJSDSDJSJDH");
        headersMap.put("correlationId","232233");
        Throwable throwable = new Throwable("Sample error");
        ThrowableProblem problem = createProblem("channelName","Sample payload",headersMap,"App Name",throwable);
        Error error = (Error) problem.getParameters().get("error");
        ErrorDetail errorDetail = error.getErrorDetails().get(0);
        Assert.assertEquals("Sample error",errorDetail.getMessage());
        Assert.assertEquals("App Name",errorDetail.getLocation());
    }

    @Test
    public void testCreateError(){
        Error error = createError("500","Sample error message","Sample Domain","Error reason","Detailed error message");
        Assert.assertEquals("Sample error message",error.getMessage());
        Assert.assertEquals("500",error.getCode());
    }

    @Test
    public void testExtractError(){
        Error error = Error.builder().withCode("400").withMessage("Sample error message").build();
        ThrowableProblem throwableProblem = createProblem(400,error);
        Error extractedError =extractError(throwableProblem);
        Assert.assertEquals("Sample error message",extractedError.getMessage());
        Assert.assertEquals("400",extractedError.getCode());
    }

}
