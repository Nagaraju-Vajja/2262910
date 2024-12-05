package com.macys.uop.foundation.core.utils.eventlog;

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
public class EventlogMessageTest {

    @Test
    public void testBuildFullEventlogMessage() throws JsonProcessingException {
        EventLogMessage message=EventLogMessage.builder()
                .withCreatedBy("order").withTransactionId("RC1289").withTransactionDesc("OrderDetails published").withTransactionTime("2021-07-12T09:47:17.277Z")
                .withChannelName("orderdetails_onsuccess_env").withHeader("orderid", "1236").withRequestType("Request").withRequestPayload("Sample request payload").withResponsePayload("Data Saved Successfully")
                .withChannelType("Internal").withResponseTs("2021-07-12T09:47:27.277Z").withEventDetail("aaa", "bbb")
                .withStatusCode("200").withStatusDesc("SUCCESS").withSubClientId("MCOM").withStatus("OK")
                .build();

        Assert.assertEquals("RC1289", message.getTransactionId());

        String jsonEventlogMessage=message.toString();

        log.info(jsonEventlogMessage);

        ObjectMapper mapper=new ObjectMapper();
        EventLogMessage reconstructedEventlogMessage=mapper.readValue(jsonEventlogMessage, EventLogMessage.class);

        Assert.assertEquals(reconstructedEventlogMessage.getTransactionDesc(), message.getTransactionDesc());
    }

    @Test
    public void testBuildOnlyMandatoryAuditMessage() throws JsonProcessingException {
        EventLogMessage message=EventLogMessage.builder()
                .withChannelName("orderdetails_onsuccess_env").withChannelType("Internal").withCreatedBy("order").withTransactionTime("2021-07-12T09:47:17.277Z")
                .withHeader("orderid", "1236").withRequestPayload("Sample request payload")
                .build();

        String jsonEventlogMessage=message.toString();

        log.info(jsonEventlogMessage);

        ObjectMapper mapper=new ObjectMapper();
        EventLogMessage reconstructedEventlogMessage=mapper.readValue(jsonEventlogMessage, EventLogMessage.class);

        Assert.assertEquals(reconstructedEventlogMessage.getCreatedBy(), message.getCreatedBy());
    }

    @Test
    public void testEventlogMessageMandatoryParamException() {
        try {
            EventLogMessage.builder()
                    .withChannelType("Internal").withCreatedBy("order").withTransactionTime("2021-07-12T09:47:17.277Z")
                    .withHeader("orderid", "1236").withRequestPayload("Sample request payload")
                    .build();
        }
        catch(IllegalArgumentException ex) {
            Assert.assertEquals("'channelName' must not be empty", ex.getMessage());
        }

        try {
            EventLogMessage.builder()
                    .withChannelName("orderdetails_onsuccess_env").withChannelType("Internal").withCreatedBy("order")
                    .withHeader("orderid", "1236").withRequestPayload("Sample request payload")
                    .build();
        }
        catch(IllegalArgumentException ex) {
            Assert.assertEquals("'transactionTime' must not be empty", ex.getMessage());
        }

        try {
            EventLogMessage.builder()
                    .withChannelName("orderdetails_onsuccess_env").withCreatedBy("order").withTransactionTime("2021-07-12T09:47:17.277Z")
                    .withHeader("orderid", "1236").withRequestPayload("Sample request payload")
                    .build();
        }
        catch(IllegalArgumentException ex) {
            Assert.assertEquals("'channelType' must not be empty", ex.getMessage());
        }

        try {
            EventLogMessage.builder()
                    .withChannelName("orderdetails_onsuccess_env").withChannelType("Internal").withTransactionTime("2021-07-12T09:47:17.277Z")
                    .withHeader("orderid", "1236").withRequestPayload("Sample request payload")
                    .build();
        }
        catch(IllegalArgumentException ex) {
            Assert.assertEquals("'createdBy' must not be empty", ex.getMessage());
        }

        try {
            EventLogMessage.builder()
                    .withChannelName("orderdetails_onsuccess_env").withChannelType("Internal").withCreatedBy("order").withTransactionTime("2021-07-12T09:47:17.277Z")
                    .withRequestPayload("Sample request payload")
                    .build();
        }
        catch(IllegalArgumentException ex) {
            Assert.assertEquals("'headers' must not be empty", ex.getMessage());
        }

        try {
            EventLogMessage.builder()
                    .withChannelName("orderdetails_onsuccess_env").withChannelType("Internal").withCreatedBy("order")
                    .withTransactionTime("2021-07-12T09:47:17.277Z").withHeader("orderid", "1236")
                    .build();
        }
        catch(IllegalArgumentException ex) {
            Assert.assertEquals("'requestPayload' must not be empty", ex.getMessage());
        }

    }
}

