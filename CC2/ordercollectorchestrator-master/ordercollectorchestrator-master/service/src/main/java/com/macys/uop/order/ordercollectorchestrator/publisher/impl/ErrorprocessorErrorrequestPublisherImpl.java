package com.macys.uop.order.ordercollectorchestrator.publisher.impl;

import com.macys.uop.foundation.core.utils.msg.publisher.MessagePublisherClient;
import com.macys.uop.order.ordercollectorchestrator.publisher.IErrorprocessorErrorrequestPublisher;
import io.micrometer.core.annotation.Timed;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ErrorprocessorErrorrequestPublisherImpl implements IErrorprocessorErrorrequestPublisher {

    private final MessagePublisherClient messagePublisher;

    @Value("${errorprocessor_errorrequest.channel_name}")
    private String errorProcessorChannel;

    /**
     * Message Publisher to publish message for retry
     * @param payload
     * @param headers
     * @return String
     * @throws InterruptedException,ExecutionException
     */
    @Override
    @Timed(value = "messaging_requests_total", histogram = true)
    public String publishMessage(String payload, Map<String, String> headers)
        throws InterruptedException, ExecutionException {
      Message<String> message = com.macys.uop.foundation.core.utils.message.Message.<String>builder()
          .withPayload(payload)
          .withHeaders(headers)
          .build();

      return messagePublisher.sendMessage(errorProcessorChannel, message);
    }
}
