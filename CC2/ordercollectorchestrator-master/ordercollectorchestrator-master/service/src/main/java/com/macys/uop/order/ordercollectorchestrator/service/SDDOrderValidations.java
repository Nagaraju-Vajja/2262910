package com.macys.uop.order.ordercollectorchestrator.service;

import com.macys.uop.order.model.Order;
import com.macys.uop.order.model.OrderLine;
import com.macys.uop.order.model.OrderLineStatus;
import com.macys.uop.order.ordercollectorchestrator.model.OrderError;
import com.macys.uop.order.ordercollectorchestrator.utils.OrderValidationErrorCodes;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.concurrent.ExecutionException;

import static com.macys.uop.order.ordercollectorchestrator.utils.OrdercollectorchestratorConstants.*;


@Component

public class SDDOrderValidations extends OrderValidations {

    public SDDOrderValidations(ICollectorderResponsePublishService collectorderResponsePublishService, IEventLogService eventLogService) {
        super(collectorderResponsePublishService,eventLogService);
    }

    public void validate(Order order) throws ExecutionException, InterruptedException {
        if (null != order && !CollectionUtils.isEmpty(order.getOrderLines())) {
            for (OrderLine ol : order.getOrderLines()) {
                if (StringUtils.isNotEmpty(ol.getFulfillmentType()) && SDD.equalsIgnoreCase(ol.getFulfillmentType())) {
                    if (StringUtils.isEmpty(ol.getDeliveryMethod())) {
                        publishAndThrowError(OrderValidationErrorCodes.ORDER_LINES_FIELDS_MISSING_SDD, order);
                    }
                    if (ObjectUtils.isEmpty(ol.getShippingInfo())) {
                        publishAndThrowError(OrderValidationErrorCodes.SHIPPINGINFO_DETAILS_MISSING_SDD, order);
                    }
                    validateShippingInfo(ol.getShippingInfo(),order);
                    if (!CollectionUtils.isEmpty(ol.getOrderLineStatuses())) {
                        for (OrderLineStatus ols : ol.getOrderLineStatuses()) {
                            if (StringUtils.isEmpty(ols.getEstimatedShipTs())) {
                                publishAndThrowError(OrderValidationErrorCodes.ESTIMATEDSHIPTS_INFO_MISSING, order);
                            }
                            if (StringUtils.isEmpty(ols.getEstimatedDeliveryTs())) {
                                ols.setEstimatedDeliveryTs(DEFAULT_DATE);
                            }
                        }
                    }
                }
            }
        }
    }

}
