package com.macys.uop.order.ordercollectorchestrator.service;

import com.macys.uop.order.model.Address;
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
public class BOSSOrderValidations extends OrderValidations {

    public BOSSOrderValidations(ICollectorderResponsePublishService collectorderResponsePublishService, IEventLogService eventLogService) {
        super(collectorderResponsePublishService,eventLogService);
    }

    public void validate(Order order) throws ExecutionException, InterruptedException {
        if (null != order && !CollectionUtils.isEmpty(order.getOrderLines())) {
            for (OrderLine ol : order.getOrderLines()) {
                if (StringUtils.isNotEmpty(ol.getFulfillmentType()) && BOSS.equalsIgnoreCase(ol.getFulfillmentType())) {
                    if (StringUtils.isEmpty(ol.getDeliveryMethod())) {
                        publishAndThrowError(OrderValidationErrorCodes.ORDER_LINES_FIELDS_MISSING_BOSS, order);
                    }
                    if (!CollectionUtils.isEmpty(ol.getOrderLineStatuses())) {
                        for (OrderLineStatus ols : ol.getOrderLineStatuses()) {
                            if (StringUtils.isEmpty(ols.getPickupDivision())) {
                                publishAndThrowError(OrderValidationErrorCodes.ORDER_LINE_STATUS_PICKUPDIVISION_MISSING_BOSS, order);
                            }
                            if (StringUtils.isEmpty(ols.getPickupLocation())) {
                                publishAndThrowError(OrderValidationErrorCodes.ORDER_LINE_STATUS_PICKUPLOCATION_MISSING_BOSS, order);
                            }
                            if (StringUtils.isEmpty(ols.getPickupStore())) {
                                publishAndThrowError(OrderValidationErrorCodes.ORDER_LINE_STATUS_PICKUPSTORE_MISSING_BOSS, order);
                            }
                            if (StringUtils.isEmpty(ols.getPickupChannel())) {
                                publishAndThrowError(OrderValidationErrorCodes.PICKUP_CHANNEL_MISSING_BOSS, order);
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
                        publishAndThrowError(OrderValidationErrorCodes.ADDRESS_DETAILS_ERROR_BOSS, order);
                    }
                    if (!CollectionUtils.isEmpty(ol.getAddressDetails())) {
                        for (Address address : ol.getAddressDetails()) {
                            if (StringUtils.isEmpty(address.getType())) {
                                publishAndThrowError(OrderValidationErrorCodes.ADDRESSDETAILS_MISSING_FIELDS_ERROR_BOSS, order);
                            }
                        }
                    }
                }
            }
        }
    }

}
