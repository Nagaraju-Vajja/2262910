package com.macys.uop.order.ordercollectorchestrator.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;

@EnableCaching
@Configuration
public class EhCacheConfiguration {

    @Bean
    @Primary
    public CacheManager ehCacheManagerOrchestrator() {
        return new EhCacheCacheManager(cacheMangerOrchestratorFactory().getObject());
    }

    @Bean
    public CacheManager springCacheManager() {
        return new ConcurrentMapCacheManager("mapRules");
    }

    @Bean
    public EhCacheManagerFactoryBean cacheMangerOrchestratorFactory() {
        EhCacheManagerFactoryBean bean = new EhCacheManagerFactoryBean();
        bean.setConfigLocation(new ClassPathResource("ehcache.xml"));
        bean.setShared(true);
        return bean;
    }
}
