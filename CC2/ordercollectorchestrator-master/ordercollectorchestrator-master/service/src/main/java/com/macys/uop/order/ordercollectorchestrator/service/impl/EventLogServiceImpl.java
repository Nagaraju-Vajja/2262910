package com.macys.uop.order.ordercollectorchestrator.service.impl;

import static com.macys.uop.foundation.core.utils.Constant.*;
import static com.macys.uop.order.ordercollectorchestrator.utils.OrdercollectorchestratorConstants.*;

import com.macys.uop.foundation.core.utils.eventlog.EventLogMessage;
import com.macys.uop.foundation.core.utils.eventlog.EventLogMessageBuilder;
import com.macys.uop.foundation.core.utils.json.JsonUtils;
import com.macys.uop.order.model.Order;
import com.macys.uop.order.ordercollectorchestrator.model.ErrorProcessorRequest;
import com.macys.uop.order.ordercollectorchestrator.model.OrderError;
import com.macys.uop.order.ordercollectorchestrator.publisher.IEventOnsuccessPublisher;
import com.macys.uop.order.ordercollectorchestrator.service.IErrorprocessorService;
import com.macys.uop.order.ordercollectorchestrator.service.IEventLogService;
import com.macys.uop.order.ordercollectorchestrator.utils.OrderTransaction;
import com.macys.uop.order.ordercollectorchestrator.utils.OrdercollectorchestratorUtil;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventLogServiceImpl implements IEventLogService, OrdercollectorchestratorUtil {

	private final IEventOnsuccessPublisher eventOnsuccessPublisher;
	private final JsonUtils jsonUtils;
	private final IErrorprocessorService errorprocessorService;
	@Value("${collectorder_response.channel_name}")
	private String collectOrderResponseTopicName;

	/**
	 * Log Event
	 *
	 * @param requestBody
	 * @param channelName
	 * @param channelType
	 * @param transaction
	 * @param referenceType
	 * @param referenceId
	 * @param headers
	 * @return String
	 * @throws InterruptedException, ExecutionException
	 */
	@Override
	public String createEventLog(Order requestBody,
								 String channelName,
								 String channelType,
								 OrderTransaction transaction,
								 String referenceType,
								 String referenceId,
								 Map<String, String> headers, String subClinetId)
			throws InterruptedException, ExecutionException {


		Stream<String> stream = Stream.of(MCHUB_ACK);
		boolean isTransactionMatch = stream.anyMatch(s -> s.contains(transaction.getTransactionId()));

		EventLogMessageBuilder builder = EventLogMessage.builder()
				.withCreatedBy(SERVICENAME)
				.withTransactionId(transaction.getTransactionId())
				.withTransactionDesc(transaction.getTransactionDescription())
				.withTransactionTime(Instant.now().toString())
				.withChannelName(channelName)
				.withChannelType(channelType)
				.withRequestType(null)
				.withRequestPayload(jsonUtils.convertToJson(requestBody))
				.withResponsePayload(null)
				.withSubClientId(subClinetId)
				.withEventDetail(transaction.getEventReferenceId(), transaction.getEventReferenceType())
				.withStatus(requestBody.getMinStatusCode())
				.withStatusDesc(requestBody.getOrderStatus())
				.withStatusCode(OK);

		addHeaderToBuilder(builder, headers);
		EventLogMessage eventlogMessage = builder.build();


		//Set MCHub Headers
		if (isTransactionMatch) {
			eventlogMessage.setHeaders(headers);
		}
		headers.put(CLIENTID_HDR, requestBody.getSellingChannelType());
		try {
			return eventOnsuccessPublisher.publishMessage(jsonUtils.convertToJson(eventlogMessage), headers);
		} catch (Exception exception) {
			if (ORDER_INPUT.equalsIgnoreCase(transaction.getEventReferenceId())) {
				referenceId = ORDER_NA_REF_ID;
			}

			ErrorProcessorRequest request = ErrorProcessorRequest.builder()
					.error(getError(exception, ORDER_LOGEVENT))
					.statusCode(getStatusCode(exception))
					.failedState(ORDER_LOGEVENT)
					.payload(jsonUtils.convertToJson(eventlogMessage))
					.referenceType(ORDERID_HDR)
					.referenceId(referenceId)
					.errorType(ST)
					.headers(headers).build();
			errorprocessorService.retryLater(request);
		}
		return null;
	}

	@Override
	public String createEventLog(Order requestBody, OrderError responseBody, String channelType, OrderTransaction transaction, String referenceType, String referenceId, Map<String, String> headers) throws InterruptedException, ExecutionException {
		String subClientId= StringUtils.isNotEmpty(requestBody.getSourceSystem()) && NEW_SOURCE_SYSTEMS.contains(requestBody.getSourceSystem()) ? requestBody.getSourceSystem() :requestBody.getSourceChannel();
		EventLogMessageBuilder builder = EventLogMessage.builder()
				.withCreatedBy(SERVICENAME)
				.withTransactionId(transaction.getTransactionId())
				.withTransactionDesc(transaction.getTransactionDescription())
				.withTransactionTime(Instant.now().toString())
				.withChannelName(collectOrderResponseTopicName)
				.withChannelType(channelType)
				.withRequestType(null)
				.withRequestPayload(jsonUtils.convertToJson(requestBody))
				.withResponsePayload(jsonUtils.convertToJson(responseBody))
				.withSubClientId(subClientId)
				.withEventDetail(transaction.getEventReferenceId(), transaction.getEventReferenceType())
				.withStatus(ONE)
				.withStatusDesc(FAILED)
				.withStatusCode(OK);

		addHeaderToBuilder(builder, headers);
		EventLogMessage eventlogMessage = builder.build();
		headers.put(CLIENTID_HDR, requestBody.getSellingChannelType());
		headers.put(ORDERID_HDR, requestBody.getPartnerOrderId());
		try {
			return eventOnsuccessPublisher.publishMessage(jsonUtils.convertToJson(eventlogMessage), headers);
		} catch (Exception exception) {
			if (ORDER_INPUT.equalsIgnoreCase(transaction.getEventReferenceId())) {
				referenceId = ORDER_NA_REF_ID;
			}

			ErrorProcessorRequest request = ErrorProcessorRequest.builder()
					.error(getError(exception, ORDER_LOGEVENT))
					.statusCode(getStatusCode(exception))
					.failedState(ORDER_LOGEVENT)
					.payload(jsonUtils.convertToJson(eventlogMessage))
					.referenceType(ORDERID_HDR)
					.referenceId(referenceId)
					.errorType(ST)
					.headers(headers).build();
			errorprocessorService.retryLater(request);
		}
		return null;
	}
}


