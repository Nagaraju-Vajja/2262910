package com.macys.uop.order.ordercollectorchestrator.service.impl;

import com.macys.uop.foundation.core.utils.execution.ServiceContextUtil;
import com.macys.uop.order.model.Order;
import com.macys.uop.order.ordercollectorchestrator.proxy.IOrdercollectProxy;
import com.macys.uop.order.ordercollectorchestrator.service.IOrdercollectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrdercollectServiceImpl implements IOrdercollectService, ServiceContextUtil {

	public final IOrdercollectProxy ordercollectProxy;

	/**
	 * CollectOrder Service Call
	 *
	 * @param order
	 * @param headers
	 * @return Order
	 */
	@Override
	public Order collectOrder(Order order, HttpHeaders headers) {
		return ordercollectProxy.collectOrder(order, headers);
	}
}
