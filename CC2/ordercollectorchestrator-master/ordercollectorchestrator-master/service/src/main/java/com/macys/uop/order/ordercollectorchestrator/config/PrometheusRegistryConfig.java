package com.macys.uop.order.ordercollectorchestrator.config;

import static com.macys.uop.order.ordercollectorchestrator.utils.OrdercollectorchestratorConstants.UNKNOWN;

import com.macys.uop.order.ordercollectorchestrator.publisher.impl.CollectorderFraudacknowledgementPublisherImpl;
import com.macys.uop.order.ordercollectorchestrator.publisher.impl.ErrorprocessorErrorrequestPublisherImpl;
import com.macys.uop.order.ordercollectorchestrator.publisher.impl.EventOnsuccessPublisherImpl;
import com.macys.uop.order.ordercollectorchestrator.publisher.impl.OrdercreationOnsuccessPublisherImpl;
import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.config.MeterFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Micrometer registry configuration
 */

@Configuration
public class PrometheusRegistryConfig {

    @Value("${topic.subscription.collectorder.name}")
    private String collectOrderSubscriptionName;
    
    @Value("${topic.subscription.checkresponse.name}")
    private String fraudSubscriptionName;
    
    @Value("${event_onsuccess.channel_name}")
	private String eventLogSuccessTopic;

    @Value("${ordercreation_onsuccess.channel_name}")
    private String orderCreationOnSuccessTopic;

    @Value("${collectorder_fraudacknowledgment.channel_name}")
    private String fraudackTopic;
    
    @Value("${errorprocessor_errorrequest.channel_name}")
    private String errorProcessorTopic;

    private String publishToOrderCreationOnSuccess = OrdercreationOnsuccessPublisherImpl.class.getCanonicalName();
    private String publishToFraudacknowledgment = CollectorderFraudacknowledgementPublisherImpl.class.getCanonicalName();
    private String publishToEventlog = EventOnsuccessPublisherImpl.class.getCanonicalName();
    private String publishToErrorProcessor = ErrorprocessorErrorrequestPublisherImpl.class.getCanonicalName();

    private String collectOrder = "collectOrder";
    private String checkResponse = "checkResponse";

    @Value("${spring.application.name}")
    private String appName;

    @Bean
    public MeterRegistryCustomizer<MeterRegistry> metricsCommonTags() {
        return MeterRegistry::config;
    }

    @Bean
    public TimedAspect timedAspect(MeterRegistry registry) {
        return new TimedAspect(registry);
    }

    @Bean
    public MeterFilter meterFilter() {
        return meterFilter;
    }


    private MeterFilter meterFilter = new MeterFilter() {
        @Override
        public Meter.Id map(Meter.Id id) {
            String success = "success";
            String failure = "failure";
            String processed = "Message processed";
            String notProcessed = "Message not processed";

            if (id.getName().equals("messaging_requests_total")) {

                if (id.getTag("exception").equals("none")) {
                    //Success
                    return id.getTag("method").equals(collectOrder) ?
                            defineMeterId(id, collectOrderSubscriptionName, processed, success)
                            : id.getTag("method").equals(checkResponse) ?
                            defineMeterId(id, fraudSubscriptionName, processed, success)
                            : id.getTag("class").equals(publishToOrderCreationOnSuccess) ?
                            defineMeterId(id, orderCreationOnSuccessTopic, processed, success)
                            : id.getTag("class").equals(publishToFraudacknowledgment) ?
                            defineMeterId(id, fraudackTopic, processed, success)
                            : id.getTag("class").equals(publishToEventlog) ?
                            defineMeterId(id, eventLogSuccessTopic, processed, success)
                            : id.getTag("class").equals(publishToErrorProcessor) ?
                            defineMeterId(id, errorProcessorTopic, processed, success)
                            : id;
                }

                if (!id.getTag("exception").equals("none")) {
                    //Failure
                    return id.getTag("method").equals(collectOrder) ?
                            defineMeterId(id, collectOrderSubscriptionName, notProcessed, failure)
                            : id.getTag("method").equals(checkResponse) ?
                            defineMeterId(id, fraudSubscriptionName, notProcessed, failure)
                            : id.getTag("class").equals(publishToOrderCreationOnSuccess) ?
                            defineMeterId(id, orderCreationOnSuccessTopic, notProcessed, failure)
                            : id.getTag("class").equals(publishToFraudacknowledgment) ?
                            defineMeterId(id, fraudackTopic, notProcessed, failure)
                            : id.getTag("class").equals(publishToEventlog) ?
                            defineMeterId(id, eventLogSuccessTopic, notProcessed, failure)
                            : id.getTag("class").equals(publishToErrorProcessor) ?
                            defineMeterId(id, errorProcessorTopic, processed, success)
                            : id;
                }
            }
            return id;
        }

        private Meter.Id defineMeterId(Meter.Id id, String pubSubAddress, String message, String successOrFailure) {
            String type = "type";
            String pubsub = "pubsub";
            String remoteHost = "remote_host";
            String topic = "topic";
            String status = "status";
            String outcome = "outcome";

            return id.withTags(Tags.concat(Tags.of("app.name", appName,
                    type, pubsub,
                    remoteHost, UNKNOWN,
                    topic, pubSubAddress,
                    status, successOrFailure,
                    outcome, message), id.getTagsAsIterable()));
        }
    };
}
