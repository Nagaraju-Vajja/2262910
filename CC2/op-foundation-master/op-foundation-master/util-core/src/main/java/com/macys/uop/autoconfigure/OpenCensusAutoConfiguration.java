package com.macys.uop.autoconfigure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

import io.opencensus.exporter.stats.prometheus.PrometheusStatsCollector;
import io.opencensus.exporter.stats.prometheus.PrometheusStatsConfiguration;
import io.prometheus.client.CollectorRegistry;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class OpenCensusAutoConfiguration {

	@Autowired
    CollectorRegistry collectorRegistry;

    @EventListener(ApplicationReadyEvent.class)
    public void EventListenerExecute() {
    	prometheusStatsConfiguration();
    }

    /**
     * Configure OpenCensus export stats to prometheus
     * Configure OpenCensus export stats to prometheus
     */
    private void prometheusStatsConfiguration() {
    	log.debug("initializing PrometheusStatsCollector for OpenCensus");
    	PrometheusStatsConfiguration configuration = PrometheusStatsConfiguration.builder()
    			.setRegistry(collectorRegistry)
    			.build();

        PrometheusStatsCollector.createAndRegister(configuration);
    }

}
