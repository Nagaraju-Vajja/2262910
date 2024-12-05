package com.macys.uop.order.ordercollectorchestrator.proxy;

import com.macys.uop.order.model.Order;
import org.springframework.util.MultiValueMap;

public interface IOrderenrichmentProxy {

	Order enrichOrder(final Order orderRequest, final MultiValueMap<String, String> headers);
}
