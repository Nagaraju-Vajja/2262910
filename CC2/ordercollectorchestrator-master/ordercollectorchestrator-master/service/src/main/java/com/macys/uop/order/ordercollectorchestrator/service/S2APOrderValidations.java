package com.macys.uop.order.ordercollectorchestrator.service;

import com.macys.uop.order.model.Address;
import com.macys.uop.order.model.Order;
import com.macys.uop.order.model.OrderLine;
import com.macys.uop.order.model.OrderLineStatus;
import com.macys.uop.order.ordercollectorchestrator.model.OrderError;
import com.macys.uop.order.ordercollectorchestrator.utils.OrderValidationErrorCodes;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.concurrent.ExecutionException;

import static com.macys.uop.order.ordercollectorchestrator.utils.OrdercollectorchestratorConstants.*;

@Component
public class S2APOrderValidations extends OrderValidations {
    public S2APOrderValidations(ICollectorderResponsePublishService collectorderResponsePublishService, IEventLogService eventLogService) {
        super(collectorderResponsePublishService,eventLogService);
    }

    public void validate(Order order) throws ExecutionException, InterruptedException {
        if (null != order && !CollectionUtils.isEmpty(order.getOrderLines())) {
            for (OrderLine ol : order.getOrderLines()) {
                if (StringUtils.isNotEmpty(ol.getFulfillmentType()) && S2AP.equalsIgnoreCase(ol.getFulfillmentType())) {
                    if (StringUtils.isEmpty(ol.getDeliveryMethod())) {
                        publishAndThrowError(OrderValidationErrorCodes.ORDER_LINES_FIELDS_MISSING_S2AP, order);
                    } else if (!CollectionUtils.isEmpty(ol.getOrderLineStatuses())) {
                        for (OrderLineStatus ols : ol.getOrderLineStatuses()) {
                            if (S2AP.contains(ol.getFulfillmentType()) && StringUtils.isEmpty(ols.getPickupChannel())) {
                                publishAndThrowError(OrderValidationErrorCodes.PICKUP_CHANNEL_MISSING_S2AP, order);
                            }
                            if (StringUtils.isEmpty(ols.getEstimatedShipTs())) {
                                publishAndThrowError(OrderValidationErrorCodes.ESTIMATEDSHIPTS_INFO_MISSING, order);
                            }
                            if (StringUtils.isEmpty(ols.getEstimatedDeliveryTs())) {
                                publishAndThrowError(OrderValidationErrorCodes.ESTIMATEDDELIVERYTS_INFO_MISSING, order);
                            }
                        }
                    }

                    if (null == ol.getAddressDetails()) {
                        publishAndThrowError(OrderValidationErrorCodes.ADDRESS_DETAILS_ERROR_S2AP, order);
                    }
                    if (!CollectionUtils.isEmpty(ol.getAddressDetails())) {
                        for (Address address : ol.getAddressDetails()) {
                            if (StringUtils.isEmpty(address.getType())) {
                                publishAndThrowError(OrderValidationErrorCodes.ADDRESSDETAILS_MISSING_FIELDS_ERROR_S2AP, order);
                            }
                            if (StringUtils.isEmpty(address.getAttn())) {
                                publishAndThrowError(OrderValidationErrorCodes.ATTN_FIELD_MISSING_S2AP, order);
                            }
                        }
                    }
                }
            }
        }
    }
}
