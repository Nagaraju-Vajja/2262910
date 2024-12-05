package com.macys.uop.foundation.core.utils.trace;

import static com.macys.uop.foundation.core.utils.Constant.BEAN_ID_LAZY_TRACE_EXECUTOR;

import java.util.concurrent.Executor;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.sleuth.instrument.async.LazyTraceExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * This class configures {@link LazyTraceExecutor} which comes from the Sleuth library.
 * <br>
 * It is a special kind of executor that will propagate traceIds to new threads and create new spanIds in the process.
 * <br>
 * This class is useful when making parallel calls using {@link java.util.concurrent.CompletableFuture}.
 * <br>
 * <b>Special importance should be given to usage of {@link ContextCopyingDecorator} which helps making the Context and MDC related information available to child threads </b> 
 * 
 * @see {@link ContextCopyingDecorator}
 * @see {@link com.macys.uop.foundation.core.utils.future.FutureUtils}
 */
@Configuration
public class ExecutorServiceConfiguration {

	@Value("${executor.pool.corePoolSize:}")
	String corePoolSize;

	@Value("${executor.pool.maxPoolSize:}")
	String maxPoolSize;

	@Value("${executor.pool.keepAliveSeconds:}")
	String keepAliveSeconds;

	@Value("${executor.pool.queueCapacity:}")
	String queueCapacity;

	@Value("${executor.pool.threadNamePrefix:}")
	String threadNamePrefix;

	/**
	 * Configures {@link LazyTraceExecutor} to be used for Async calls
	 * 
	 * @param beanFactory Spring {@link BeanFactory}
	 * @return initialized {@link LazyTraceExecutor}
	 */
	@Bean(BEAN_ID_LAZY_TRACE_EXECUTOR)
	public Executor executor(BeanFactory beanFactory) {
		
		ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
		taskExecutor.setTaskDecorator(new ContextCopyingDecorator());
		
		if (!StringUtils.isAllBlank(corePoolSize))
			taskExecutor.setCorePoolSize(Integer.parseInt(corePoolSize.trim()));
		if (!StringUtils.isAllBlank(maxPoolSize))
			taskExecutor.setMaxPoolSize(Integer.parseInt(maxPoolSize.trim()));
		if (!StringUtils.isAllBlank(keepAliveSeconds))
			taskExecutor.setKeepAliveSeconds(Integer.parseInt(keepAliveSeconds.trim()));
		if (!StringUtils.isAllBlank(queueCapacity))
			taskExecutor.setQueueCapacity(Integer.parseInt(queueCapacity.trim()));
		if (!StringUtils.isAllBlank(threadNamePrefix))
			taskExecutor.setThreadNamePrefix(threadNamePrefix);
		
		taskExecutor.initialize();
		
		return new LazyTraceExecutor(beanFactory, taskExecutor);
	}

}
