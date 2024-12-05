package com.macys.uop.foundation.core.utils.msg.publisher;

import java.util.concurrent.Executor;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.sleuth.instrument.async.LazyTraceExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class MessagePublisherClientConfiguration {
	
	@Value("${messagepublish.executor.pool.corePoolSize:10}")
	String corePoolSize;

	@Value("${messagepublish.executor.pool.maxPoolSize:100}")
	String maxPoolSize;

	@Value("${messagepublish.executor.pool.keepAliveSeconds:}")
	String keepAliveSeconds;

	@Value("${messagepublish.executor.pool.queueCapacity:}")
	String queueCapacity;

	@Value("${messagepublish.executor.pool.threadNamePrefix:async-message-publish}")
	String threadNamePrefix;
	
	@Bean("messagePublishExecutor")
	public Executor executor(BeanFactory beanFactory) {
		
		ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
		
		if (!StringUtils.isAllBlank(corePoolSize)){
			taskExecutor.setCorePoolSize(Integer.parseInt(corePoolSize.trim()));
		}

		if (!StringUtils.isAllBlank(maxPoolSize)){
			taskExecutor.setMaxPoolSize(Integer.parseInt(maxPoolSize.trim()));
		}

		if (!StringUtils.isAllBlank(keepAliveSeconds)){
			taskExecutor.setKeepAliveSeconds(Integer.parseInt(keepAliveSeconds.trim()));
		}

		if (!StringUtils.isAllBlank(queueCapacity)){
			taskExecutor.setQueueCapacity(Integer.parseInt(queueCapacity.trim()));
		}

		if (!StringUtils.isAllBlank(threadNamePrefix)){
			taskExecutor.setThreadNamePrefix(threadNamePrefix);
		}
		taskExecutor.initialize();
		
		return new LazyTraceExecutor(beanFactory, taskExecutor);
	}

}
