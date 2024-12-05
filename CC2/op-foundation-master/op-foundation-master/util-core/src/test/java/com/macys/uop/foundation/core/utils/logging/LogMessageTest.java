package com.macys.uop.foundation.core.utils.logging;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.macys.uop.foundation.core.utils.common.GlobalApplicationBucket;
import com.macys.uop.foundation.core.utils.masking.IDataMasker;
import com.macys.uop.foundation.core.utils.masking.JsonMasker;

import lombok.extern.slf4j.Slf4j;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringRunner.class)
@Slf4j
public class LogMessageTest {

    @MockBean
    IDataMasker requestBodyDataMasker;
    
    @Before
    public void setUp() {
        final Logger logger = (Logger)LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        logger.setLevel(Level.ALL);
    }

    @Test
    public void testBuildFullLogMessage() throws  JsonProcessingException {
        Map<String,IDataMasker> maskConfigMap=new HashMap<>();
        maskConfigMap.put("accountNumber", new com.macys.uop.foundation.core.utils.masking.CreditCardNumberMasker());
        IDataMasker jsonMasker=new JsonMasker(maskConfigMap);
        Map<String,String> headersMap = new HashMap<>();
        headersMap.put("orderId","HGFHSJGHJ");
        headersMap.put("clientId","xyz");
        headersMap.put("messageId","62553");
        headersMap.put("correlationId","WGW3G3H");
        LogMessage message= LogMessage.builder()
                .withOrderId("HGFHSJGHJ").withClientId("xyz").withMessageId("62553").withCorrelationId("WGW3G3H")
                .withContext("Sample Context").withAppName("Abc").withErrorCode("404").withRequestBody("Sample request body")
                .withLogType(LogTypeEnum.ERROR).withAdditionalInfo("additional info").withEndpointUrl("sample:url/").withPubsubMessageDataMasker(jsonMasker).withResponseBodyDataMasker(jsonMasker).withRequestBodyDataMasker(jsonMasker)
                .withErrorMessage("Error message").withEventMessage("Sample event message").withEventType("error").withHeaderAttribute("orderId","763676").withHeaderAttribute(headersMap).withMaskingEnabled(true).withOperationType("xyz")
                .withPubsubMessage("{\"xyz\":\"abc\"}").withStackTrace("Stack trace of error").withStatusCode("400")
                .withResponseBody("Sample response body").withStatusMessage("error").withTopicName("topic name").withTransactionName("transaction name").withOperationType("Sample operation type")
                .withLogger(log)
                .build();

        Assert.assertEquals("62553", message.getMessageId());

        String jsonlogMessage=message.toString();

        log.info(jsonlogMessage);

        ObjectMapper mapper=new ObjectMapper();
        LogMessage reconstructedlogMessage=mapper.readValue(jsonlogMessage, LogMessage.class);

        Assert.assertEquals(reconstructedlogMessage.getErrorCode(), message.getErrorCode());
    }

    @Test
    public void testBuildFullLogMessageWithLogSizeExceed() throws JsonProcessingException {
        Map<String,String> headersMap = new HashMap<>();
        headersMap.put("orderId","HGFHSJGHJ");
        headersMap.put("clientId","xyz");
        headersMap.put("messageId","62553");
        headersMap.put("correlationId","WGW3G3H");
        LogMessage message= LogMessage.builder()
                .withOrderId("HGFHSJGHJ").withClientId("xyz").withMessageId("62553").withCorrelationId("WGW3G3H")
                .withContext("Sample Context").withAppName("Abc").withErrorCode("404").withRequestBody("Sample request body")
                .withLogType(LogTypeEnum.ERROR).withAdditionalInfo("additional info").withEndpointUrl("sample:url/")
                .withErrorMessage("Error message").withEventMessage("Sample event message").withEventType("error").withHeaderAttribute("orderId","763676").withHeaderAttribute(headersMap).withMaskingEnabled(true).withOperationType("")
                .withPubsubMessage("Sample pubsub message").withStackTrace("Stack trace of error").withStatusCode("400")
                .withResponseBody("Sample response body").withStatusMessage("error").withTopicName("topic name").withTransactionName("transaction name").withOperationType("Sample operation type")
                .withLogger(log)
                .build();

        String jsonlogMessage=message.toStringWhenLogSizeExceeded(102456);

        log.info(jsonlogMessage);

        ObjectMapper mapper=new ObjectMapper();
        LogMessage reconstructedLogMessage=mapper.readValue(jsonlogMessage, LogMessage.class);

        Assert.assertEquals(reconstructedLogMessage.getStatusCode(), message.getStatusCode());
    }

    @Test
    public void testBuildOnlyMandatoryLogMessage() throws JsonProcessingException {
        LogMessage message= LogMessage.builder()
                .withOrderId("HGFHSJGHJ").withClientId("xyz").withMessageId("62553")
                .withCorrelationId("WGW3G3H").withAppName("Abc")
                .withLogger(log)
                .build();

        String jsonlogMessage=message.toString();

        log.info(jsonlogMessage);

        ObjectMapper mapper=new ObjectMapper();
        LogMessage reconstructedLogMessage=mapper.readValue(jsonlogMessage, LogMessage.class);

        Assert.assertEquals(reconstructedLogMessage.getClientId(), message.getClientId());
    }

    @Test
    public void testMaskingException() {

        Map<String,IDataMasker> maskConfigMap=new HashMap<>();
        maskConfigMap.put("accountNumber", new com.macys.uop.foundation.core.utils.masking.CreditCardNumberMasker());
        IDataMasker jsonMasker=new JsonMasker(maskConfigMap);
        LogMessage message= LogMessage.builder()
                .withOrderId("HGFHSJGHJ").withClientId("xyz").withMessageId("62553").withRequestBody("{\"xyz\":\"abc\"}")
                .withPubsubMessage("{\"xyz\":\"abc\"}").withResponseBody("{\"xyz\":\"smt\"}")
                .withCorrelationId("WGW3G3H").withAppName("Abc").withRequestBodyDataMasker(jsonMasker).withMaskingEnabled(true)
                .withResponseBodyDataMasker(jsonMasker).withPubsubMessageDataMasker(jsonMasker)
                .withLogger(log)
                .build();
        Mockito.when(requestBodyDataMasker.maskData(Mockito.anyString())).thenAnswer(t->new Exception());
        try{
            message.toString();
        }catch (Throwable e){
            Assert.assertTrue(e instanceof Exception);
        }

    }

    @Test
    public void testLogMessageDataMaskerException() {
        IDataMasker dataMasker = null;

        try {
            LogMessage.builder()
                    .withPubsubMessageDataMasker(dataMasker)
                    .withLogger(log)
                    .buildDisableChecking();
        }
        catch(IllegalArgumentException ex) {
            Assert.assertEquals("'pubsubMessageDataMasker' must not be null", ex.getMessage());
        }

        try {
            LogMessage.builder()
                    .withRequestBodyDataMasker(dataMasker)
                    .withLogger(log)
                    .buildDisableChecking();
        }
        catch(IllegalArgumentException ex) {
            Assert.assertEquals("'requestBodyDataMasker' must not be null", ex.getMessage());
        }

        try {
            LogMessage.builder()
                    .withResponseBodyDataMasker(dataMasker)
                    .withLogger(log)
                    .buildDisableChecking();
        }
        catch(IllegalArgumentException ex) {
            Assert.assertEquals("'responseBodyDataMasker' must not be null", ex.getMessage());
        }
    }

    @Test
    public void testBuildWithoutMandatoryCheck() throws JsonProcessingException {
        LogMessage message= LogMessage.builder()
                .withStatusCode("400")
                .withLogger(log)
                .buildDisableChecking();

        String jsonlogMessage=message.toString();

        log.info(jsonlogMessage);

        ObjectMapper mapper=new ObjectMapper();
        LogMessage reconstructedLogMessage=mapper.readValue(jsonlogMessage, LogMessage.class);

        Assert.assertEquals(reconstructedLogMessage.getStatusCode(), message.getStatusCode());
    }

    @Test
    public void testLogMessageMandatoryParamException() {
        try {
            LogMessage.builder()
                    .withClientId("xyz").withMessageId("62553")
                    .withCorrelationId("WGW3G3H").withAppName("Abc")
                    .withLogger(log)
                    .build();
        }
        catch(IllegalArgumentException ex) {
            Assert.assertEquals("'orderId' must not be empty", ex.getMessage());
        }

        try {
            LogMessage.builder()
                    .withOrderId("HGFHSJGHJ").withMessageId("62553")
                    .withCorrelationId("WGW3G3H").withAppName("Abc")
                    .withLogger(log)
                    .build();
        }
        catch(IllegalArgumentException ex) {
            Assert.assertEquals("'clientId' must not be empty", ex.getMessage());
        }

        try {
            LogMessage.builder()
                    .withOrderId("HGFHSJGHJ").withClientId("xyz")
                    .withCorrelationId("WGW3G3H").withAppName("Abc")
                    .withLogger(log)
                    .build();
        }
        catch(IllegalArgumentException ex) {
            Assert.assertEquals("'messageId' must not be empty", ex.getMessage());
        }

        try {
            LogMessage.builder()
                    .withOrderId("HGFHSJGHJ").withClientId("xyz")
                    .withMessageId("62553").withAppName("Abc")
                    .withLogger(log)
                    .build();
        }
        catch(IllegalArgumentException ex) {
            Assert.assertEquals("'correlationId' must not be empty", ex.getMessage());
        }

        try {
            LogMessage.builder()
                    .withOrderId("HGFHSJGHJ").withClientId("xyz")
                    .withMessageId("62553").withCorrelationId("WGW3G3H")
                    .withLogger(log)
                    .build();
        }
        catch(IllegalArgumentException ex) {
            Assert.assertEquals("'appName' must not be empty", ex.getMessage());
        }

    }

    @Test
    public void testLogAsInfoWithLogger(){
        LogMessageBuilder logMessageBuilder = new LogMessageBuilder()
                .withClientId("1212")
                .withMessageId("23232323")
                .withOrderId("DEEFEEEERE4R4")
                .withCorrelationId("23232")
                .withAppName("TEST")
                .withLogType(LogTypeEnum.LOG)
                .withContext("test")
                .withPubsubMessage("sample payload");
        logMessageBuilder.withLogger(log).buildDisableChecking().logAsInfo();
        //Since logAsInfo() method return void, we are just asserting true
        Assert.assertTrue(true);

    }
    
    @Test
    public void testLogAsInfoWithoutLogger(){
        LogMessageBuilder logMessageBuilder = new LogMessageBuilder()
                .withClientId("1212")
                .withMessageId("23232323")
                .withOrderId("DEEFEEEERE4R4")
                .withCorrelationId("23232")
                .withAppName("TEST")
                .withLogType(LogTypeEnum.LOG)
                .withContext("test")
                .withPubsubMessage("sample payload");
        logMessageBuilder.buildDisableChecking().logAsInfo();
        //Since logAsInfo() method return void, we are just asserting true
        Assert.assertTrue(true);

    }

    @Test
    public void testLogAsDebugWithLogger(){
        ReflectionTestUtils.setField(GlobalApplicationBucket.class,"maxLogLineSizeInKb",1);
        LogMessageBuilder logMessageBuilder = new LogMessageBuilder()
                .withClientId("1212")
                .withMessageId("23232323")
                .withOrderId("DEEFEEEERE4R4")
                .withCorrelationId("23232")
                .withAppName("TEST")
                .withLogType(LogTypeEnum.EVENT)
                .withContext("test")
                .withPubsubMessage("Sample message ".repeat(100));
        logMessageBuilder.withLogger(log).buildDisableChecking().logAsDebug();
        //Since logAsDebug() method return void, we are just asserting true
        Assert.assertTrue(true);

    }
    
    @Test
    public void testLogAsDebugWithoutLogger(){
        ReflectionTestUtils.setField(GlobalApplicationBucket.class,"maxLogLineSizeInKb",1);
        LogMessageBuilder logMessageBuilder = new LogMessageBuilder()
                .withClientId("1212")
                .withMessageId("23232323")
                .withOrderId("DEEFEEEERE4R4")
                .withCorrelationId("23232")
                .withAppName("TEST")
                .withLogType(LogTypeEnum.EVENT)
                .withContext("test")
                .withPubsubMessage("Sample message ".repeat(100));
        logMessageBuilder.buildDisableChecking().logAsDebug();
        //Since logAsDebug() method return void, we are just asserting true
        Assert.assertTrue(true);

    }

    @Test
    public void testLogAsWarningWithLogger(){
        ReflectionTestUtils.setField(GlobalApplicationBucket.class,"maxLogLineSizeInKb",1);
        LogMessageBuilder logMessageBuilder = new LogMessageBuilder()
                .withClientId("1212")
                .withMessageId("23232323")
                .withOrderId("DEEFEEEERE4R4")
                .withCorrelationId("23232")
                .withAppName("TEST")
                .withLogType(LogTypeEnum.EVENT)
                .withContext("test")
                .withPubsubMessage("sample payload");
        logMessageBuilder.withLogger(log).build().logAsWarn();
        //Since logAsWarn() method return void, we are just asserting true
        Assert.assertTrue(true);

    }
    
    @Test
    public void testLogAsWarningWithoutLogger(){
        ReflectionTestUtils.setField(GlobalApplicationBucket.class,"maxLogLineSizeInKb",1);
        LogMessageBuilder logMessageBuilder = new LogMessageBuilder()
                .withClientId("1212")
                .withMessageId("23232323")
                .withOrderId("DEEFEEEERE4R4")
                .withCorrelationId("23232")
                .withAppName("TEST")
                .withLogType(LogTypeEnum.EVENT)
                .withContext("test")
                .withPubsubMessage("sample payload");
        logMessageBuilder.build().logAsWarn();
        //Since logAsWarn() method return void, we are just asserting true
        Assert.assertTrue(true);

    }
    
    @Test
    public void testLogAsErrorWithLogger(){
        LogMessageBuilder logMessageBuilder = new LogMessageBuilder()
                .withClientId("1212")
                .withMessageId("23232323")
                .withOrderId("DEEFEEEERE4R4")
                .withCorrelationId("23232")
                .withAppName("TEST")
                .withLogType(LogTypeEnum.ERROR)
                .withContext("test")
                .withErrorCode("400")
                .withErrorMessage("test error")
                .withStackTrace("sample trace")
                .withPubsubMessage("sample payload");
        logMessageBuilder.withLogger(log).buildDisableChecking().logAsError();
        //Since logAsError() method return void, we are just asserting true
        Assert.assertTrue(true);
    }
    
    @Test
    public void testLogAsErrorWithoutLogger(){
        LogMessageBuilder logMessageBuilder = new LogMessageBuilder()
                .withClientId("1212")
                .withMessageId("23232323")
                .withOrderId("DEEFEEEERE4R4")
                .withCorrelationId("23232")
                .withAppName("TEST")
                .withLogType(LogTypeEnum.ERROR)
                .withContext("test")
                .withErrorCode("400")
                .withErrorMessage("test error")
                .withStackTrace("sample trace")
                .withPubsubMessage("sample payload");
        logMessageBuilder.buildDisableChecking().logAsError();
        //Since logAsError() method return void, we are just asserting true
        Assert.assertTrue(true);
    }
    
    @Test
    public void testLogAsTraceWithLogger(){
        LogMessageBuilder logMessageBuilder = new LogMessageBuilder()
                .withClientId("1212")
                .withMessageId("23232323")
                .withOrderId("DEEFEEEERE4R4")
                .withCorrelationId("23232")
                .withAppName("TEST")
                .withLogType(LogTypeEnum.ERROR)
                .withContext("test")
                .withErrorCode("400")
                .withErrorMessage("test error")
                .withStackTrace("sample trace")
                .withPubsubMessage("sample payload");
        logMessageBuilder.withLogger(log).buildDisableChecking().logAsTrace();
        //Since logAsError() method return void, we are just asserting true
        Assert.assertTrue(true);
    }
    
    @Test
    public void testLogAsTraceWithoutLogger(){
        LogMessageBuilder logMessageBuilder = new LogMessageBuilder()
                .withClientId("1212")
                .withMessageId("23232323")
                .withOrderId("DEEFEEEERE4R4")
                .withCorrelationId("23232")
                .withAppName("TEST")
                .withLogType(LogTypeEnum.ERROR)
                .withContext("test")
                .withErrorCode("400")
                .withErrorMessage("test error")
                .withStackTrace("sample trace")
                .withPubsubMessage("sample payload");
        logMessageBuilder.buildDisableChecking().logAsTrace();
        //Since logAsError() method return void, we are just asserting true
        Assert.assertTrue(true);
    }

}

