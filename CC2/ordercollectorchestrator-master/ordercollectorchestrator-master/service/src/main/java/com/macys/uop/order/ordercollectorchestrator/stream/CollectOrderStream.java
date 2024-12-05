package com.macys.uop.order.ordercollectorchestrator.stream;

import static com.macys.uop.foundation.core.utils.Constant.*;

import brave.Tracer;
import com.google.pubsub.v1.PubsubMessage;
import com.macys.uop.foundation.core.utils.json.JsonUtils;
import com.macys.uop.foundation.core.utils.msg.subscriber.AbstractStreamSubscriber;
import com.macys.uop.foundation.core.utils.ordererror.OrderErrorMessagePublisher;
import com.macys.uop.foundation.core.utils.validation.MessageValidator;
import com.macys.uop.foundation.messagestore.IMessageStoreRecordManager;
import com.macys.uop.order.model.Order;
import com.macys.uop.order.ordercollectorchestrator.service.IOrdercollectorchestratorService;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gcp.pubsub.core.PubSubTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

@Component
public class CollectOrderStream extends AbstractStreamSubscriber {

    private final JsonUtils jsonUtils;
    private final IOrdercollectorchestratorService ordercollectorchestratorService;
    private final MessageValidator messageValidator;
    private final IMessageStoreRecordManager messageStoreRecordManager;

    @Value("${topic.subscription.collectorder.name}")
    private String subscriptionName;
    @Value("${custom.pubsub.listener.shutdown.waittime}")
    private int shutdownWaitTime;


    /**
     * CollectOrderStream
     * @param pubSubTemplate
     * @param tracer
     * @param orderErrorMessagePublisher
     * @param jsonUtils
     * @param messageValidator
     * @param ordercollectorchestratorService
     * @param messageStoreRecordManager
     */
    public CollectOrderStream(PubSubTemplate pubSubTemplate, Tracer tracer,
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
                ordercollectorchestratorService.collectOrder(orderSource);
            } catch (Exception e) {
                if (isCircuitBreakerError(e)) {
                    messageStoreRecordManager.deleteMessageStoreRecord(message.getMessageId(), getOrderId(), getClientId());
                }
                throw e;
            }
        }
    }

    /**
     * processHeaders
     * @param message
     * @return Map
     */
    @Override
    protected Map<String, String> processHeaders(PubsubMessage message) {
        Map<String, String> headers = new HashMap<>();
        message.getAttributesMap().entrySet().stream().forEach(entry -> {
            String key = entry.getKey();
            String value = entry.getValue();
            headers.put(key, value);
        });
        if (!headers.containsKey(ORDERID_HDR)) {
            headers.put(ORDERID_HDR, ORDERID_HDR_DEFAULT_VALUE);
        }
        if (!headers.containsKey(CORRELATIONID_HDR)) {
            headers.put(CORRELATIONID_HDR, UUID.randomUUID().toString());
        }
        headers.put(MESSAGEID_HDR, message.getMessageId());
        headers.put(CLIENTID_HDR, applicationName);
        return headers;
    }

    /**
     * get Subscription
     * @return String
     */
    @Override
    protected String getTopicSubscription() {
        return subscriptionName;
    }

	/**
	 * Returns Message Store Eventlog Flag
	 *
	 * @return boolean
	 */
	@Override
	protected Boolean isExtMsgInputEventLogPublishingEnabled() {
		return Boolean.TRUE;
	}

    @PreDestroy
    public void shutdown() throws InterruptedException {
        stopSubscriberAsync();
        Thread.sleep(shutdownWaitTime);
    }
}
