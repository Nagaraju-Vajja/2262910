package com.macys.uop.order.ordercollectorchestrator.publisher;

import java.util.Map;
import java.util.concurrent.ExecutionException;

public interface ICollectorderFraudacknowledgementPublisher {

	String publishMessage(String payload, Map<String, String> headers) throws InterruptedException, ExecutionException;
}
