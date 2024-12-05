package com.macys.uop.order.ordercollectorchestrator.service;

import com.macys.uop.order.model.Order;
import com.macys.uop.order.model.OrderLine;
import com.macys.uop.order.model.OrderLineStatus;
import com.macys.uop.order.ordercollectorchestrator.enums.FulfillmentChannel;
import com.macys.uop.order.ordercollectorchestrator.utils.OrderValidationErrorCodes;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.concurrent.ExecutionException;

import static com.macys.uop.order.ordercollectorchestrator.enums.DeliveryType.SHIPPING;
import static com.macys.uop.order.ordercollectorchestrator.utils.OrdercollectorchestratorConstants.*;

@Component
public class STHOrderValidations extends OrderValidations {

    public STHOrderValidations(ICollectorderResponsePublishService collectorderResponsePublishService, IEventLogService eventLogService) {
        super(collectorderResponsePublishService,eventLogService);
    }

    public void validate(Order order) throws ExecutionException, InterruptedException {
        if (null != order && !CollectionUtils.isEmpty(order.getOrderLines())) {
            for (OrderLine ol : order.getOrderLines()) {
                if (StringUtils.isNotEmpty(ol.getFulfillmentType()) && STH.equalsIgnoreCase(ol.getFulfillmentType())) {
                    if (StringUtils.isEmpty(ol.getDeliveryMethod())) {
                        publishAndThrowError(OrderValidationErrorCodes.ORDER_LINES_FIELDS_MISSING_STH, order);
                    }
                    if (ObjectUtils.isEmpty(ol.getShippingInfo())) {
                        publishAndThrowError(OrderValidationErrorCodes.SHIPPINGINFO_DETAILS_MISSING_STH, order);
                    }
                    if(StringUtils.isNotEmpty(ol.getDeliveryType()) && SHIPPING.getValue().equalsIgnoreCase(ol.getDeliveryType())) {
                        validateShippingInfo(ol.getShippingInfo(), order);
                    }
                    boolean is3POrder= StringUtils.isNotBlank(ol.getMarketingPartnerId())
                            && MARKETING_PARTNER_ID_MIRAKL.equalsIgnoreCase(ol.getMarketingPartnerId());
                    if (!CollectionUtils.isEmpty(ol.getOrderLineStatuses())) {
                        for (OrderLineStatus ols : ol.getOrderLineStatuses()) {
                            if (StringUtils.isEmpty(ols.getEstimatedShipTs())) {
                                publishAndThrowError(OrderValidationErrorCodes.ESTIMATEDSHIPTS_INFO_MISSING, order);
                            }
                            if (StringUtils.isEmpty(ols.getEstimatedDeliveryTs()) && !(ORDD.equalsIgnoreCase(ols.getFulfillmentChannel())
                                    || SPEC.equalsIgnoreCase(ols.getFulfillmentChannel()))) {
                                publishAndThrowError(OrderValidationErrorCodes.ESTIMATEDDELIVERYTS_INFO_MISSING, order);
                            }else if (StringUtils.isEmpty(ols.getEstimatedDeliveryTs()) && (ORDD.equalsIgnoreCase(ols.getFulfillmentChannel())
                                    || SPEC.equalsIgnoreCase(ols.getFulfillmentChannel()))) {
                                ols.setEstimatedDeliveryTs(DEFAULT_DATE);
                            }
                            if(is3POrder && !FulfillmentChannel.EXT.getValue().equalsIgnoreCase(ols.getFulfillmentChannel())){
                                publishAndThrowError(OrderValidationErrorCodes.FULFILLMENTCHANNEL_3P_ERROR_MESSAGE, order);
                            }
                            if(!is3POrder && FulfillmentChannel.EXT.getValue().equalsIgnoreCase(ols.getFulfillmentChannel())){
                                publishAndThrowError(OrderValidationErrorCodes.FULFILLMENTCHANNEL_ERROR_MESSAGE, order);
                            }
                        }
                    }
                }
            }
        }
    }
}