package com.macys.uop.foundation.core.utils.execution;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.junit4.SpringRunner;

import com.macys.uop.foundation.core.utils.Constant;
import com.macys.uop.foundation.core.utils.test.TestContextUtil;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ServiceContextUtil.class)
public class ServiceContextUtilDefaultHeadersTest implements TestContextUtil, ServiceContextUtil {
   

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
    public void testGetDefaultHeaders(){
        Map<String, String> headers = new HashMap<>();
        headers.put(Constant.MESSAGEID_HDR, "1");
        headers.put(Constant.ORDERID_HDR, "2");
        headers.put(Constant.CLIENTID_HDR, "Ordercollectorchestrator");
        headers.put(Constant.CORRELATIONID_HDR, "4");
        Assert.assertEquals(headers, getDefaultHeaders());
    }

    @Test
    public void testGetDefaultHttpHeaders(){
        HttpHeaders headers = new HttpHeaders();
        headers.add(Constant.MESSAGEID_HDR, "1");
        headers.add(Constant.ORDERID_HDR, "2");
        headers.add(Constant.CLIENTID_HDR, "Ordercollectorchestrator");
        headers.add(Constant.CORRELATIONID_HDR, "4");
        Assert.assertEquals(headers, getDefaultHttpHeaders());
    }
    
}

