package com.macys.uop.foundation.core.utils.common;

import com.macys.uop.foundation.core.utils.test.TestContextUtil;

import lombok.extern.slf4j.Slf4j;

import java.time.Instant;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = StringUtil.class)
@Slf4j
public class StringUtilTest implements StringUtil, TestContextUtil {

    @MockBean
    @Qualifier("stringUtil")
    private StringUtil stringUtil;

    @Before
    public void beforeTest() {
        initContext();
    }

    @After
    public void afterTest(){
        clearContext();
    }

    @Test
    public void testQuoteAsString(){
        String input = "input";
        char[] firstOutput = quoteAsString(input);
        char[] secondOutput = quoteAsString(input);
        String string = new String(firstOutput);
        String string2 = new String(secondOutput);
        Assert.assertEquals(string,string2);
    }

    @Test
    public void testQuoteAsStringBlank(){
        String input = "";
        char[] firstOutput = quoteAsString(input);
        char[] secondOutput = quoteAsString(input);
        String string = new String(firstOutput);
        String string2 = new String(secondOutput);
        Assert.assertEquals(string,string2);
    }
    
    @Test
    public void testMsgProcDurTxt4LoggingStartTimeNull() {
    	Instant startTime=null;
    	Instant endTime=Instant.now();
    	String origin="PubSub";
    	
    	try {
    		constructMsgProcessingDurationText4Logging(startTime, endTime, origin);
    	} catch(IllegalArgumentException iae) {
    		Assert.assertEquals(iae.getMessage(),"'startTime' must not be null");
    	}
    }
    
    @Test
    public void testMsgProcDurTxt4LoggingEndTimeNull() {
    	Instant startTime=Instant.now();
    	Instant endTime=null;
    	String origin="PubSub";
    	
    	try {
    		constructMsgProcessingDurationText4Logging(startTime, endTime, origin);
    	} catch(IllegalArgumentException iae) {
    		Assert.assertEquals(iae.getMessage(),"'endTime' must not be null");
    	}
    }
    
    @Test
    public void testMsgProcDurTxt4LoggingOriginNull() {
    	Instant startTime=Instant.now();
    	Instant endTime=Instant.now();
    	String origin=null;
    	
    	try {
    		constructMsgProcessingDurationText4Logging(startTime, endTime, origin);
    	} catch(IllegalArgumentException iae) {
    		Assert.assertEquals(iae.getMessage(),"'origin' must not be empty");
    	}
    }
    
    @Test
    public void testMsgProcDurTxt4Logging() {
    	Instant startTime=Instant.now();
    	Instant endTime=Instant.now();
    	String origin="PubSub";
    	
    	String text=constructMsgProcessingDurationText4Logging(startTime, endTime, origin);
    	
    	log.info(text);
    	
    	Assert.assertNotNull(text);
    }
}
