package com.macys.uop.order.ordercollectorchestrator.proxy;

import com.macys.uop.order.model.Order;
import org.springframework.util.MultiValueMap;

public interface IOrdercollectProxy {

	Order collectOrder(final Order order, final MultiValueMap<String, String> headers);
}
