package com.macys.uop.foundation.core.utils.msg.subscriber;

import com.macys.uop.foundation.core.utils.exception.ProblemUtil;
import com.macys.uop.foundation.core.utils.test.TestContextUtil;
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
@SpringBootTest(classes = ProblemUtil.class)
public class SubscriberUtilTest implements TestContextUtil,SubscriberUtil {

    @MockBean
    @Qualifier("subscriberUtil")
    private SubscriberUtil subscriberUtil;

    @Before
    public void beforeTest() {
        initContext();
    }

    @After
    public void afterTest(){
        clearContext();
    }

    @Test
    public void testExtractTopicNameFromRequestURL(){
        String inputUrl = "message:pull__event_onsuccess_env__event";
        String topicName  = extractTopicNameFromRequestURL(inputUrl);
        Assert.assertEquals("event_onsuccess_env",topicName);
    }

    @Test
    public void testExtractTopicNameFromSubscriptionName(){
        String subscriptionName = "pull__event_onsuccess_env__event";
        String topicName  = extractTopicNameFromSubscriptionName(subscriptionName);
        Assert.assertEquals("event_onsuccess_env",topicName);
    }
    
    @Test
    public void testGetFullyQualifiedTopicName(){
        String topic = "pubsub_env";
        String projectId = "mtech-wms-oms-nonprod";
        Assert.assertEquals("projects/mtech-wms-oms-nonprod/topics/pubsub_env",getFullyQualifiedTopicName(topic,projectId));
    }

}
