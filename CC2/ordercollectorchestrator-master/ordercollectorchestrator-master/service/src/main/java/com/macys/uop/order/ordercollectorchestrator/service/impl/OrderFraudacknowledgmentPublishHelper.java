package com.macys.uop.order.ordercollectorchestrator.service.impl;

import static com.macys.uop.foundation.core.utils.Constant.ORDERID_HDR;
import static com.macys.uop.order.ordercollectorchestrator.utils.OrdercollectorchestratorConstants.*;

import com.macys.uop.foundation.core.utils.json.JsonUtils;
import com.macys.uop.order.model.Order;
import com.macys.uop.order.ordercollectorchestrator.model.ErrorProcessorRequest;
import com.macys.uop.order.ordercollectorchestrator.publisher.ICollectorderFraudacknowledgementPublisher;
import com.macys.uop.order.ordercollectorchestrator.service.IErrorprocessorService;
import com.macys.uop.order.ordercollectorchestrator.service.IOrderFraudacknowledgmentPublishHelper;
import com.macys.uop.order.ordercollectorchestrator.utils.OrdercollectorchestratorUtil;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderFraudacknowledgmentPublishHelper
		implements IOrderFraudacknowledgmentPublishHelper, OrdercollectorchestratorUtil {

	private final ICollectorderFraudacknowledgementPublisher publisher;
	private final JsonUtils jsonUtils;
	private final IErrorprocessorService errorprocessorService;

	/**
	 * publish method for ordercreationonsuccess
	 * 
	 * @param order
	 * @param headers
	 * @return String
	 * @throws InterruptedException, ExecutionException
	 */
	@Override
	public String publish(Order order, Map<String, String> headers) throws InterruptedException, ExecutionException {
		try {
			return publisher.publishMessage(jsonUtils.convertToJson(order), headers);
		} catch (Exception exception) {
			ErrorProcessorRequest request = ErrorProcessorRequest.builder()
				.error(getError(exception, ORDER_PUBLISHMCHUBACK))
				.statusCode(getStatusCode(exception))
				.failedState(ORDER_PUBLISHMCHUBACK)
				.payload(jsonUtils.convertToJson(order))
				.referenceType(ORDERID_HDR)
				.referenceId(order.getOrderId())
				.errorType(ST)
				.headers(headers).build();
			errorprocessorService.retryLater(request);
		}
		return null;
	}
}
