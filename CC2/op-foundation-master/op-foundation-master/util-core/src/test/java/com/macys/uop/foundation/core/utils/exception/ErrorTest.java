package com.macys.uop.foundation.core.utils.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringRunner.class)
@Slf4j
public class ErrorTest {

    @Test
    public void testBuildFullErrorMessage() throws JsonProcessingException {

        ErrorDetail errorDetail = new ErrorDetail();
        errorDetail.setDomain("Domain");
        errorDetail.setLocation("error location");
        errorDetail.setLocationType("location type");
        errorDetail.setMessage("Error message");
        errorDetail.setReason("reason");

        Error error = Error.builder().withCode("404").withMessage("Sample error message").withError("404","Sample error message")
                .withErrorDetail(errorDetail).build();

        String errorMessage=error.toString();

        log.info(errorMessage);

        ObjectMapper mapper=new ObjectMapper();
        Error reconstructedErrorMessage=mapper.readValue(errorMessage, Error.class);

        Assert.assertEquals(reconstructedErrorMessage.getMessage(), error.getMessage());
    }

    @Test
    public void testBuildErrorMessage() throws JsonProcessingException {

        ErrorDetail errorDetail = new ErrorDetail();
        errorDetail.setDomain("Domain");
        errorDetail.setLocation("error location");
        errorDetail.setLocationType("location type");
        errorDetail.setMessage("Error message");
        errorDetail.setReason("reason");
        List<ErrorDetail> errorDetailsList = new ArrayList<>();
        errorDetailsList.add(errorDetail);

        Error error = Error.builder().withCode("404").withMessage("Sample error message").withError("404","Sample error message")
                .withErrorDetail(errorDetail).withErrorDetails(errorDetailsList)
                .withErrorDetail("Domain","reason","sample message")
                .withErrorDetail("Domain","reason","sample message","Location type","Location of error").build();

        String errorMessage=error.toString();

        log.info(errorMessage);

        ObjectMapper mapper=new ObjectMapper();
        Error reconstructedErrorMessage=mapper.readValue(errorMessage, Error.class);

        Assert.assertEquals(reconstructedErrorMessage.getMessage(), error.getMessage());
    }
}
