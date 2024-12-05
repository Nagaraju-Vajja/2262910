package com.macys.uop.foundation.core.utils.msg.publisher;

import java.util.concurrent.Executor;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.sleuth.instrument.async.LazyTraceExecutor;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.macys.uop.foundation.core.utils.test.TestContextUtil;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringRunner.class)
@SpringBootTest(classes = { MessagePublisherClientConfiguration.class })
public class MessagePublisherClientConfigurationTest implements TestContextUtil {

    @Autowired
    private MessagePublisherClientConfiguration messagePublisherClientConfiguration;

    @Before
    public void beforeTest() {
        messagePublisherClientConfiguration = new MessagePublisherClientConfiguration();
        ReflectionTestUtils.setField(messagePublisherClientConfiguration,"corePoolSize","5");
        ReflectionTestUtils.setField(messagePublisherClientConfiguration,"maxPoolSize","120");
        ReflectionTestUtils.setField(messagePublisherClientConfiguration,"keepAliveSeconds","2");
        ReflectionTestUtils.setField(messagePublisherClientConfiguration,"queueCapacity","20");
        initContext();
    }

    @After
    public void afterTest() {
        clearContext();
    }

    @Test
    public void testExecutor(){
        BeanFactory beanFactory = PowerMockito.mock(BeanFactory.class);
        Executor executor = messagePublisherClientConfiguration.executor(beanFactory);
        Assert.assertTrue(executor instanceof LazyTraceExecutor);
    }
}
