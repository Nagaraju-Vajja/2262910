package com.macys.uop.order.ordercollectorchestrator.service.impl;

import static com.macys.uop.foundation.core.utils.Constant.*;
import static com.macys.uop.order.ordercollectorchestrator.utils.OrdercollectorchestratorConstants.*;

import com.macys.uop.foundation.core.utils.json.JsonUtils;
import com.macys.uop.order.model.Order;
import com.macys.uop.order.ordercollectorchestrator.model.ErrorProcessorRequest;
import com.macys.uop.order.ordercollectorchestrator.publisher.IOrdercreationOnsuccessPublisher;
import com.macys.uop.order.ordercollectorchestrator.service.IErrorprocessorService;
import com.macys.uop.order.ordercollectorchestrator.service.IOrderCreationSuccessPublishHelper;
import com.macys.uop.order.ordercollectorchestrator.utils.OrdercollectorchestratorUtil;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderCreationSuccessPublishHelper
		implements IOrderCreationSuccessPublishHelper, OrdercollectorchestratorUtil {

	private final IOrdercreationOnsuccessPublisher publisher;
	private final JsonUtils jsonUtils;
	private final IErrorprocessorService errorprocessorService;


	/**
	 * publish method for ordercreationonsuccess
	 * 
	 * @param order
	 * @param messageHeaders
	 * @return String
	 * @throws InterruptedException, ExecutionException
	 */
	@Override
	public String publish(Order order, Map<String, String> messageHeaders)
			throws InterruptedException, ExecutionException {
		messageHeaders.put(EVENT_SOURCE, getAppName());
		messageHeaders.put(EVENT_TYPE, EVENT_TYPE_ORDER_ON_SUCCESS);
		messageHeaders.put(ORDERID_HDR, order.getOrderId());
		messageHeaders.put(EVENT_TIME_STAMP, Instant.now().toString());

		try {
			return publisher.publishMessage(jsonUtils.convertToJson(order), messageHeaders);
		} catch (Exception exception) {
			ErrorProcessorRequest request = ErrorProcessorRequest.builder()
					.error(getError(exception, ORDER_PUBLISHCREATESUCCESS))
					.statusCode(getStatusCode(exception))
					.failedState(ORDER_PUBLISHCREATESUCCESS)
					.payload(jsonUtils.convertToJson(order))
					.referenceType(ORDERID_HDR)
					.referenceId(order.getOrderId())
					.errorType(ST)
					.headers(messageHeaders).build();
			errorprocessorService.retryLater(request);
		}
		return null;
	}
}

