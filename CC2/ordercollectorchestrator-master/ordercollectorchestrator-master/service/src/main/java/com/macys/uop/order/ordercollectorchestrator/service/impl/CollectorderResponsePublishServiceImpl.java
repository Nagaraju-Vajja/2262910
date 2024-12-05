package com.macys.uop.order.ordercollectorchestrator.service.impl;

import com.macys.uop.foundation.core.utils.Constant;
import com.macys.uop.foundation.core.utils.json.JsonUtils;
import com.macys.uop.order.model.Order;
import com.macys.uop.order.model.OrderLine;
import com.macys.uop.order.ordercollectorchestrator.model.Acknowledgement;
import com.macys.uop.order.ordercollectorchestrator.model.AcknowledgementOrder;
import com.macys.uop.order.ordercollectorchestrator.model.ErrorProcessorRequest;
import com.macys.uop.order.ordercollectorchestrator.model.OrderError;
import com.macys.uop.order.ordercollectorchestrator.publisher.ICollectorderResponsePublisher;
import com.macys.uop.order.ordercollectorchestrator.service.ICollectorderResponsePublishService;
import com.macys.uop.order.ordercollectorchestrator.service.IErrorprocessorService;
import com.macys.uop.order.ordercollectorchestrator.utils.OrderErrorCodes;
import com.macys.uop.order.ordercollectorchestrator.utils.OrdercollectorchestratorUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static com.macys.uop.foundation.core.utils.Constant.ORDERID_HDR;
import static com.macys.uop.order.ordercollectorchestrator.utils.OrdercollectorchestratorConstants.*;


@Component
@RequiredArgsConstructor
public class CollectorderResponsePublishServiceImpl implements ICollectorderResponsePublishService, OrdercollectorchestratorUtil {

	private final ICollectorderResponsePublisher publisher;
	public final IErrorprocessorService publisherHelper;
	private final JsonUtils jsonUtils;
	private final Map<String, String> propertyMap;

	/**
	 * Method to publish Acknowledgment
	 * @param order
	 * @param orderError
	 * @return Acknowledgement
	 * @throws InterruptedException, ExecutionException
	 */
	@Override
	public Acknowledgement publish(Order order, OrderError orderError) throws InterruptedException, ExecutionException  {

		Map<String, String> messageHeaders = new HashMap<>();
		messageHeaders.put(SOURCE_CHANNEL, order.getSourceChannel());
		messageHeaders.put(APP_NAME, SERVICENAME);
		messageHeaders.put(EVENT_TYPE, ACKNOWLEDGEMENT_HDR);
		if (!StringUtils.isBlank(order.getOrderId())) {
			messageHeaders.put(ORDERID_HDR, order.getOrderId());
		}
		if (!StringUtils.isBlank(order.getSellerOrderId())) {
			messageHeaders.put(PARTNER_ORDER_ID, order.getSellerOrderId());
			messageHeaders.put(SELLER_ORDER_ID, order.getSellerOrderId());
		} else if (!StringUtils.isBlank(order.getPartnerOrderId())) {
			messageHeaders.put(PARTNER_ORDER_ID, order.getPartnerOrderId());
			messageHeaders.put(SELLER_ORDER_ID, order.getPartnerOrderId());
		}
		messageHeaders.put(Constant.CORRELATIONID_HDR, getCorrelationId());
		messageHeaders.put(EVENT_TIME_STAMP, Instant.now().toString());
		if (!StringUtils.isBlank(order.getSourceSystem())) {
			messageHeaders.put(SOURCE_SYSTEM, order.getSourceSystem());
		}
		Acknowledgement acknowledgement = new Acknowledgement();
		AcknowledgementOrder ackOrder = new AcknowledgementOrder();
		if (!StringUtils.isBlank(order.getSellerOrderId())) {
			ackOrder.setSellerOrderId(order.getSellerOrderId());
		}
		if (!StringUtils.isBlank(order.getPartnerOrderId())) {
			ackOrder.setPartnerOrderId(order.getPartnerOrderId());
		}
		if (!StringUtils.isBlank(order.getOrderChannelDivision())) {
			ackOrder.setOrderChannelDivision(order.getOrderChannelDivision());
		}
		if (!ObjectUtils.isEmpty(order.getOrderLines())) {
			List<OrderLine> orderLineList = new ArrayList<>();
			for (OrderLine orderLine : order.getOrderLines()) {
				OrderLine ol = new OrderLine();
				ol.setLineId(orderLine.getLineId());
				ol.setReservationId(orderLine.getReservationId());
				orderLineList.add(ol);
				ackOrder.setOrderLines(orderLineList);
			}
		}

		if (ObjectUtils.isNotEmpty(orderError)) {
			if (ObjectUtils.isNotEmpty(orderError) && ObjectUtils.isNotEmpty(order)) {
				if (!StringUtils.isBlank(order.getPartnerOrderId())) {
					orderError.setPartnerOrderId(order.getPartnerOrderId());
				}
				if (!StringUtils.isBlank(order.getSellerOrderId())) {
					orderError.setSellerOrderId(order.getSellerOrderId());
				}
				if (ObjectUtils.isNotEmpty(order) && !StringUtils.isBlank(order.getSourceSystem()) && NEW_SOURCE_SYSTEMS.contains(order.getSourceSystem())) {
					orderError.setResponseCode(ONE);
					orderError.setResponseMessage(FAILED);
					orderError.setOrder(ackOrder);
				}
			}
			publisher.publishMessage(jsonUtils.convertToJson(orderError), messageHeaders);
			return null;
		} else {
			acknowledgement.setOrder(ackOrder);
			acknowledgement.setResponseCode(String.valueOf(ZERO));
			acknowledgement.setResponseMessage(SUCCESS);
			acknowledgement.setServiceName(SERVICENAME);
			acknowledgement.setSource(ORDER_PLATFORM);
				try {
				publisher.publishMessage(jsonUtils.convertToJson(acknowledgement), messageHeaders);
			} catch (ExecutionException | InterruptedException exception) {
				
				ErrorProcessorRequest request = ErrorProcessorRequest.builder()
						.throwable(exception)
						.errorCodes(OrderErrorCodes.PUBLISH_ERROR)
						.payload(jsonUtils.convertToJson(acknowledgement))
						.referenceId(order.getOrderId())
						.referenceType(ORDERID_HDR)
						.errorType(ST)
						.channelNameToPublsih(getProperty(propertyMap, COLLECTORDERRESPONSE_CHANNEL_NAME))
						.headers(messageHeaders)
						.build();
				publisherHelper.retryLater(request);
			}
			return acknowledgement;
		} 
	}
}
