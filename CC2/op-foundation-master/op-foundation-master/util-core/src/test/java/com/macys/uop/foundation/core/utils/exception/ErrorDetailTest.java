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

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringRunner.class)
@Slf4j
public class ErrorDetailTest {

    @Test
    public void testBuildErrorMessage() throws JsonProcessingException {

        ErrorDetail errorDetail = ErrorDetail.builder().withDomain("Domain").withLocation("error location")
                .withLocationType("location type").withMessage("Sample error message").withReason("reason for error").build();
        Assert.assertEquals("Domain", errorDetail.getDomain());

        String errorMessage=errorDetail.toString();

        log.info(errorMessage);

        ObjectMapper mapper=new ObjectMapper();
        ErrorDetail reconstructedErrorMessage=mapper.readValue(errorMessage, ErrorDetail.class);

        Assert.assertEquals(reconstructedErrorMessage.getLocation(), errorDetail.getLocation());
    }
}
