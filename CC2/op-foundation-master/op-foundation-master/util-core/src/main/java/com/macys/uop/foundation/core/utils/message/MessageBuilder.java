package com.macys.uop.foundation.core.utils.message;

import static com.macys.uop.foundation.core.utils.Constant.CLIENTID_HDR;
import static com.macys.uop.foundation.core.utils.Constant.CORRELATIONID_HDR;
import static com.macys.uop.foundation.core.utils.Constant.MESSAGEID_HDR;
import static com.macys.uop.foundation.core.utils.Constant.ORDERID_HDR;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.messaging.support.GenericMessage;

/**
 * Message Builder class to build generic spring message org.springframework.messaging.Message<T>
 *
 * @param <T> Type of Payload
 * 
 * with* methods are part of builder pattern to compose Message.
 */
public class MessageBuilder<T> {
	
	private T payload;
	private Map<String, Object> headers = new LinkedHashMap<>();

	public MessageBuilder<T> withPayload(T payload) {
		this.payload = payload;
		return this;
	}

	public MessageBuilder<T> withHeader(String key, Object value) {
		this.headers.put(key, value);
		return this;
	}

	public MessageBuilder<T> withHeaders(Map<String, ? extends Object> headers) {
		this.headers.putAll(headers);
		return this;
	}

	public MessageBuilder<T> withOrderId(String orderId) {
		return withHeader(ORDERID_HDR, orderId);
	}

	public MessageBuilder<T> withMessageId(String messageId) {
		return withHeader(MESSAGEID_HDR, messageId);
	}

	public MessageBuilder<T> withClientId(String clientId) {
		return withHeader(CLIENTID_HDR, clientId);
	}

	public MessageBuilder<T> withCorrelationId(String correlationId) {
		return withHeader(CORRELATIONID_HDR, correlationId);
	}

	public org.springframework.messaging.Message<T> build() {
		return new GenericMessage<>(payload, headers);
	}
}
