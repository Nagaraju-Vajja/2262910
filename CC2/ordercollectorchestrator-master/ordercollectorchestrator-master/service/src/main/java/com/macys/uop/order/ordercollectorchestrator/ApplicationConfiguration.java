package com.macys.uop.order.ordercollectorchestrator;

import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.retry.annotation.EnableRetry;

@Configuration
@EnableRetry
@ImportResource("classpath:app-config.xml")
public class ApplicationConfiguration {
    @Bean
    @ConfigurationProperties
    public Map<String, String> propertyMap() {
        return new HashMap<>();
    }
}
