package com.macys.uop.order.ordercollectorchestrator.service;

import com.macys.uop.order.model.Order;
import com.macys.uop.order.ordercollectorchestrator.model.OrderError;
import com.macys.uop.order.ordercollectorchestrator.utils.OrderTransaction;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public interface IEventLogService {

	String createEventLog(Order requestBody, String channelName, String channelType, OrderTransaction transaction,
			String referenceType, String referenceId, Map<String, String> headers,String subClinetId)
			throws InterruptedException, ExecutionException;
	String createEventLog(Order requestBody,OrderError responseBody, String channelType, OrderTransaction transaction,
						  String referenceType, String referenceId, Map<String, String> headers)
			throws InterruptedException, ExecutionException;
}
