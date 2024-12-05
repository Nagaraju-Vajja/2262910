package com.macys.uop.foundation.messagestore;

import com.macys.uop.foundation.core.utils.json.JsonUtils;
import com.macys.uop.foundation.core.utils.rest.client.RestClient;
import com.macys.uop.foundation.core.utils.rest.client.RestClientResponse;
import com.macys.uop.foundation.core.utils.test.TestContextUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

import static com.macys.uop.foundation.core.utils.Constant.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = RestBasedDuplicationCheckImpl.class)
public class RestBasedDuplicationCheckTest implements TestContextUtil {

    @Autowired
    RestBasedDuplicationCheckImpl restBasedDuplicationCheck;

    @MockBean
    private RestClient<String, Object> restClient;

    @MockBean
    private JsonUtils jsonUtils;


    HttpHeaders headers = new HttpHeaders();

    @Before
    public void setUp() {
        initContext();
        restBasedDuplicationCheck = new RestBasedDuplicationCheckImpl(restClient,jsonUtils);
    }

    @After
    public void afterTest() {
        clearContext();
    }


    @Test
    public void testIsDuplicate() {
        String payload = "Sample payload";
        RestClientResponse<Object> responseContext = new RestClientResponse<>();
        responseContext.setBody(false);
        Mockito.when(restClient.execute(Mockito.any(), Mockito.any())).thenReturn(responseContext);
        Mockito.when(jsonUtils.convertValue(Mockito.any())).thenReturn(getOutputMap());
        boolean response = restBasedDuplicationCheck.isDuplicate("/sample:url/",payload, getHeaders());
        Assert.assertFalse(response);
    }

    private Map<String,String> getHeaders(){
        Map<String,String> headersMap = new HashMap<>();
        headersMap.put(ORDERID_HDR, "11");
        headersMap.put(MESSAGEID_HDR, "22");
        headersMap.put(CLIENTID_HDR, "33");
        return headersMap;
    }

    private Map<String,Object> getOutputMap(){
        Map<String,Object> outputMap = new HashMap<>();
        outputMap.put("isDuplicate",false);
        return outputMap;
    }
}
