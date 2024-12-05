package com.macys.uop.order.ordercollectorchestrator.stream;

import brave.Tracer;
import com.google.pubsub.v1.PubsubMessage;
import com.macys.uop.foundation.core.utils.json.JsonUtils;
import com.macys.uop.foundation.core.utils.msg.subscriber.AbstractStreamSubscriber;
import com.macys.uop.foundation.core.utils.ordererror.OrderErrorMessagePublisher;
import com.macys.uop.foundation.core.utils.validation.MessageValidator;
import com.macys.uop.foundation.messagestore.IMessageStoreRecordManager;
import com.macys.uop.order.model.Order;
import com.macys.uop.order.ordercollectorchestrator.service.IOrdercollectorchestratorService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gcp.pubsub.core.PubSubTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

@Component
public class CheckResponseStream extends AbstractStreamSubscriber {

    @Value("${topic.subscription.checkresponse.name}")
    private String subscriptionName;
    @Value("${custom.pubsub.listener.shutdown.waittime}")
    private int shutdownWaitTime;
    private final JsonUtils jsonUtils;
    private final IOrdercollectorchestratorService ordercollectorchestratorService;
    private final MessageValidator messageValidator;
    private final IMessageStoreRecordManager messageStoreRecordManager;


    /**
     * CheckResponseStream
     * @param pubSubTemplate
     * @param tracer
     * @param orderErrorMessagePublisher
     * @param jsonUtils
     * @param messageValidator
     * @param ordercollectorchestratorService
     * @param messageStoreRecordManager
     */
    public CheckResponseStream(PubSubTemplate pubSubTemplate, Tracer tracer,
        OrderErrorMessagePublisher orderErrorMessagePublisher, JsonUtils jsonUtils,
        MessageValidator messageValidator,
        IOrdercollectorchestratorService ordercollectorchestratorService,
        IMessageStoreRecordManager messageStoreRecordManager) {
        super(pubSubTemplate, tracer, orderErrorMessagePublisher);
        this.jsonUtils = jsonUtils;
        this.messageValidator = messageValidator;
        this.ordercollectorchestratorService = ordercollectorchestratorService;
        this.messageStoreRecordManager = messageStoreRecordManager;
    }

    /**
     * invokeService
     * @param message
     */
    @Override
    protected void invokeService(PubsubMessage message) {
        if (messageValidator.validateMessage(message)) {
            Order orderSource = jsonUtils.convertFromJson(getPayload(), Order.class);
            try {
                ordercollectorchestratorService.checkResponse(orderSource);
            } catch (Exception e) {
                if (isCircuitBreakerError(e)) {
                    messageStoreRecordManager.deleteMessageStoreRecord(message.getMessageId(), getOrderId(), getClientId());
                }
                throw e;
            }
        }
    }

    /**
     * Get Subscription
     * @return String
     */
    @Override
    protected String getTopicSubscription() {
        return subscriptionName;
    }

    @PreDestroy
    public void shutdown() throws InterruptedException {
        stopSubscriberAsync();
        Thread.sleep(shutdownWaitTime);
    }
}
