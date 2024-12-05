package com.macys.uop.foundation.messagestore;

import com.google.cloud.spanner.ErrorCode;
import com.google.cloud.spanner.SpannerException;
import com.google.cloud.spanner.SpannerExceptionFactory;
import com.macys.uop.foundation.core.utils.test.TestContextUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.gcp.data.spanner.core.SpannerTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

import static com.macys.uop.foundation.core.utils.Constant.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SpannerDBBasedDuplicationCheckImpl.class)
public class SpannerDBBasedDuplicationCheckTest implements TestContextUtil {

    @Autowired
    private SpannerDBBasedDuplicationCheckImpl spannerDBBasedDuplicationCheck;

    @MockBean
    SpannerTemplate spannerTemplate;

    @Before
    public void setUp() {
        spannerDBBasedDuplicationCheck = new SpannerDBBasedDuplicationCheckImpl(spannerTemplate);
        initContext();
    }

    @Test
    public void testIsDuplicate() {
        String payload = "Sample Payload";
        Boolean isDuplicate = spannerDBBasedDuplicationCheck.isDuplicate(payload,getHeaders());
        Assert.assertEquals(false,isDuplicate);
    }

    @Test
    public void testIsDuplicateTrue() {
        String payload = "Sample Payload";
        SpannerException e = SpannerExceptionFactory.newSpannerException(ErrorCode.ALREADY_EXISTS,"");
        Mockito.doThrow(e).when(spannerTemplate).insert(Mockito.any(MessageStore.class));
        try {
            spannerDBBasedDuplicationCheck.isDuplicate(payload,getHeaders());
        }catch(Exception exception){
            Assert.assertTrue(exception instanceof SpannerException);
        }
    }

    @Test
    public void testIsDuplicateException() {
        String payload = "Sample Payload";
        SpannerException e = SpannerExceptionFactory.newSpannerException(ErrorCode.NOT_FOUND,"");
        Mockito.doThrow(e).when(spannerTemplate).insert(Mockito.any(MessageStore.class));
        try {
            spannerDBBasedDuplicationCheck.isDuplicate(payload,getHeaders());
        }catch(Exception exception){
            Assert.assertTrue(exception instanceof SpannerException);
        }
    }

    private Map<String,String> getHeaders(){
        Map<String,String> headersMap = new HashMap<>();
        headersMap.put(ORDERID_HDR, "11");
        headersMap.put(MESSAGEID_HDR, "22");
        headersMap.put(CLIENTID_HDR, "33");
        return headersMap;
    }

}
