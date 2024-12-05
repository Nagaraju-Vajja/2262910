package com.macys.uop.order.ordercollectorchestrator.service;

import com.macys.uop.order.model.*;
import com.macys.uop.order.model.OrderCharge;
import com.macys.uop.order.ordercollectorchestrator.enums.*;
import com.macys.uop.order.ordercollectorchestrator.utils.OrderValidationErrorCodes;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.concurrent.ExecutionException;

@Slf4j
@Component
public class CommonOrderValidations extends OrderValidations {

    private final ICollectorderResponsePublishService collectorderResponsePublishService;
    private final IEventLogService eventLogService;

    public CommonOrderValidations(ICollectorderResponsePublishService collectorderResponsePublishService, IEventLogService eventLogService, IEventLogService eventLogService1) {
        super(collectorderResponsePublishService,eventLogService);
        this.collectorderResponsePublishService = collectorderResponsePublishService;
        this.eventLogService = eventLogService1;
    }

    @Override
    public void validate(Order order) throws ExecutionException, InterruptedException {
        if (null != order) {
            //step 1 --Validate BillingInfo and its fields
            if (null == order.getBillingInfo()) {
                publishAndThrowError(OrderValidationErrorCodes.BILLING_INFO_MISSING, order);
            }
            if (!ObjectUtils.isEmpty(order.getBillingInfo())) {
                if(StringUtils.isEmpty(order.getBillingInfo().getAddressLine1())) {
                    publishAndThrowError(OrderValidationErrorCodes.BILLING_INFOS_ADDRESSLINE1_MISSING, order);
                }
                if(StringUtils.isEmpty(order.getBillingInfo().getCity())){
                    publishAndThrowError(OrderValidationErrorCodes.BILLING_INFOS_CITY_MISSING, order);
                }
                if(StringUtils.isEmpty(order.getBillingInfo().getCountry())){
                    publishAndThrowError(OrderValidationErrorCodes.BILLING_INFOS_COUNTRY_MISSING, order);
                }
                if(StringUtils.isEmpty(order.getBillingInfo().getEmailId())){
                    publishAndThrowError(OrderValidationErrorCodes.BILLING_INFOS_EMAILID_MISSING, order);
                }
                if(StringUtils.isEmpty(order.getBillingInfo().getFirstName())){
                    publishAndThrowError(OrderValidationErrorCodes.BILLING_INFOS_FIRSTNAME_MISSING, order);
                }
                if(StringUtils.isEmpty(order.getBillingInfo().getLastName())){
                    publishAndThrowError(OrderValidationErrorCodes.BILLING_INFOS_LASTNAME_MISSING, order);
                }
                if(StringUtils.isEmpty(order.getBillingInfo().getZipCode())){
                    publishAndThrowError(OrderValidationErrorCodes.BILLING_INFOS_ZIPCODE_MISSING, order);
                }
                if(StringUtils.isEmpty(order.getBillingInfo().getState())){
                    publishAndThrowError(OrderValidationErrorCodes.BILLING_INFOS_STATE_MISSING, order);
                }
                if(StringUtils.isEmpty(order.getBillingInfo().getCountryCode())){
                    publishAndThrowError(OrderValidationErrorCodes.BILLING_INFOS_COUNTRY_CODE_MISSING, order);
                }
            }
            if (!ObjectUtils.isEmpty(order.getOrderCharges())) {
                for (OrderCharge orderCharge : order.getOrderCharges()) {
                    if (StringUtils.isEmpty(orderCharge.getChargeCategory())) {
                        publishAndThrowError(OrderValidationErrorCodes.ORDER_CHARGES_CHARGECATEGORY_MISSING, order);
                    }
                    if (StringUtils.isEmpty(orderCharge.getChargeType())) {
                        publishAndThrowError(OrderValidationErrorCodes.ORDER_CHARGES_CHARGETYPE_MISSING, order);
                    }
                    if (StringUtils.isEmpty(orderCharge.getChargeAmount()) && StringUtils.isEmpty(orderCharge.getChargePerUnit())) {
                        publishAndThrowError(OrderValidationErrorCodes.ORDER_CHARGES_AMOUNT_PERUNIT_MISSING, order);
                    }
                }
            }

            if (!ObjectUtils.isEmpty(order.getOrderTaxes())) {
                for (OrderTax orderTax : order.getOrderTaxes()) {
                    if (StringUtils.isEmpty(orderTax.getChargeCategory())) {
                        publishAndThrowError(OrderValidationErrorCodes.ORDER_TAXES_CHARGECATEGORY_MISSING, order);
                    }
                    if (StringUtils.isEmpty(orderTax.getChargeType())) {
                        publishAndThrowError(OrderValidationErrorCodes.ORDER_TAXES_CHARGETYPE_MISSING, order);
                    }
                    if (StringUtils.isEmpty(orderTax.getTax()) && StringUtils.isEmpty(orderTax.getTaxPerUnit())) {
                        publishAndThrowError(OrderValidationErrorCodes.ORDER_TAXES_TAX_FIELDS_MISSING, order);
                    }
                }
            }
            //step 2 --Validate LoyaltyDetails fields
            if (!CollectionUtils.isEmpty(order.getLoyaltyDetails()) && (StringUtils.isEmpty(order.getLoyaltyDetails().get(0).getLoyaltyType()) || StringUtils.isEmpty(order.getLoyaltyDetails().get(0).getLoyaltyId()))) {
                publishAndThrowError(OrderValidationErrorCodes.LOYALTYTYPE_DETAILS_MISSING, order);
            }
            //step 3 --Validate other orderheader fields
            if (StringUtils.isEmpty(order.getOrderChannelDivision())) {
                publishAndThrowError(OrderValidationErrorCodes.ORDERCHANNELDIVISION_FIELD_MISSING, order);
            }
            //if(StringUtils.isEmpty(order.getOrderLocation())){
              //  publishAndThrowError(OrderValidationErrorCodes.ORDERLOCATION_FIELD_MISSING, orderError, order);
            //}
            if(StringUtils.isEmpty(order.getSellingChannelType())){
                publishAndThrowError(OrderValidationErrorCodes.SELLINGCHANNELTYPE_FIELD_MISSING, order);
            }
            if(StringUtils.isEmpty(order.getSourceChannel())){
                publishAndThrowError(OrderValidationErrorCodes.SOURCECHANNEL_FIELD_MISSING, order);
            }
            if(StringUtils.isEmpty(order.getPartnerOrderId())){
                publishAndThrowError(OrderValidationErrorCodes.PARTNERORDERID_FIELD_MISSING, order);
            }
            if(CollectionUtils.isEmpty(order.getPayments())){
                publishAndThrowError(OrderValidationErrorCodes.PAYMENT_BLOCK_ERROR, order);
            }
            //step 4 --Validate  orderlInes details
            validateOrderLines(order);
            OrderValidations sthOrderValidations = new STHOrderValidations(collectorderResponsePublishService,eventLogService);
            OrderValidations bossOrderValidations = new BOSSOrderValidations(collectorderResponsePublishService,eventLogService);
            OrderValidations bopsOrderValidations = new BOPSOrderValidations(collectorderResponsePublishService,eventLogService);
            OrderValidations s2apOrderValidations = new S2APOrderValidations(collectorderResponsePublishService,eventLogService);
            OrderValidations sddOrderValidations = new SDDOrderValidations(collectorderResponsePublishService,eventLogService);

            sthOrderValidations.validate(order);
            bossOrderValidations.validate(order);
            bopsOrderValidations.validate(order);
            s2apOrderValidations.validate(order);
            sddOrderValidations.validate(order);

        }
    }

    public void validateFieldValues(Order order) throws ExecutionException, InterruptedException {
        if (null != order) {
            if(!SellingChannelType.isValid(order.getSellingChannelType())){
                publishAndThrowError(OrderValidationErrorCodes.SELLINGCHANNELTYPE_INFO_MISSING, order);
            }
            if(!OrderChannelDivision.isValid(order.getOrderChannelDivision())){
                publishAndThrowError(OrderValidationErrorCodes.ORDERCHANNELDIVISION_INFO_MISSING, order);
            }
            if (order.getOrderCharges() != null) {
                for (OrderCharge orderCharge : order.getOrderCharges()) {
                    if (!ChargeCategory.isValid(orderCharge.getChargeCategory())) {
                        publishAndThrowError(OrderValidationErrorCodes.CHARGECATEGORY_INFO_MISSING, order);
                    }
                }
            }
            if (order.getOrderTaxes() != null) {
                for (OrderTax orderTax : order.getOrderTaxes()) {
                    if (!ChargeCategory.isValid(orderTax.getChargeCategory())) {
                        publishAndThrowError(OrderValidationErrorCodes.CHARGECATEGORY_INFO_MISSING, order);
                    }
                }
            }
            validateFieldValueOrderLines(order);
        }
    }

    private void validateFieldValueOrderLines(Order order) throws ExecutionException, InterruptedException {
        for (OrderLine ol : order.getOrderLines()) {
            if(!FulfillmentType.isValid(ol.getFulfillmentType())) {
                publishAndThrowError(OrderValidationErrorCodes.FULFILLMENTTYPE_INFO_MISSING, order);
            }
            if(!LineType.isValid(ol.getLineType())) {
                publishAndThrowError(OrderValidationErrorCodes.LINETYPE_INFO_MISSING, order);
            }
            if(!DeliveryMethod.isValid(ol.getDeliveryMethod()) && !FulfillmentType.BOPS.getValue().equalsIgnoreCase(ol.getFulfillmentType())) {
                publishAndThrowError(OrderValidationErrorCodes.DELIVERYMETHOD_INFO_MISSING, order);
            }
            if(!DeliveryType.isValid(ol.getDeliveryType())) {
                publishAndThrowError(OrderValidationErrorCodes.DELIVERYTYPE_INFO_MISSING, order);
            }
            if(ol.getOrderLineCharges() != null) {
                for (OrderLineCharge orderLineCharge : ol.getOrderLineCharges()) {
                    if (!ChargeCategory.isValid(orderLineCharge.getChargeCategory())) {
                        publishAndThrowError(OrderValidationErrorCodes.CHARGECATEGORY_INFO_MISSING, order);
                    }
                }
            }else{
                ol.setOrderLineCharges(Collections.emptyList());
            }
            if(ol.getOrderLineTaxes() != null) {
                for (OrderLineTax orderLineTax : ol.getOrderLineTaxes()) {
                    if (!ChargeCategory.isValid(orderLineTax.getChargeCategory())) {
                        publishAndThrowError(OrderValidationErrorCodes.CHARGECATEGORY_INFO_MISSING, order);
                    }
                }
            }else{
                ol.setOrderLineTaxes(Collections.emptyList());
            }
            for(OrderLineStatus orderLineStatus: ol.getOrderLineStatuses()){
                if (!FulfillmentChannel.isValid(orderLineStatus.getFulfillmentChannel())) {
                    publishAndThrowError(OrderValidationErrorCodes.FULFILLMENTCHANNEL_INFO_MISSING, order);
                }
            }
        }

    }

    private void validateOrderLines(Order order) throws ExecutionException, InterruptedException {
        if (CollectionUtils.isEmpty(order.getOrderLines())) {
            publishAndThrowError(OrderValidationErrorCodes.ORDER_LINES_MISSING, order);
        }
        if (!CollectionUtils.isEmpty(order.getOrderLines())) {
            for (OrderLine ol : order.getOrderLines()) {
                if(StringUtils.isEmpty(ol.getFulfillmentType())) {
                    publishAndThrowError(OrderValidationErrorCodes.ORDER_LINES_FULFILLMENTTYPE_MISSING, order);
                }
                if(ObjectUtils.isEmpty(ol.getLineId()) || 0 == ol.getLineId()) {
                    publishAndThrowError(OrderValidationErrorCodes.ORDER_LINES_LINEID_MISSING, order);
                }
                if(ObjectUtils.isEmpty(ol.getOrderedQty()) || 0 == ol.getOrderedQty()) {
                    publishAndThrowError(OrderValidationErrorCodes.ORDER_LINES_ORDERED_QTY_MISSING, order);
                }
                if(StringUtils.isEmpty(ol.getUnitPrice())) {
                    publishAndThrowError(OrderValidationErrorCodes.ORDER_LINES_UNITPRICE_MISSING, order);
                }
                if(StringUtils.isEmpty(ol.getUpcNumber())) {
                    publishAndThrowError(OrderValidationErrorCodes.ORDER_LINES_UPCNUMBER_MISSING, order);
                }
                //step 5 --Validate  orderlInes.orderLineCharges
                if (!CollectionUtils.isEmpty(ol.getOrderLineCharges())) {
                    for (OrderLineCharge olc : ol.getOrderLineCharges()) {
                        if (StringUtils.isEmpty(olc.getChargeCategory())) {
                            publishAndThrowError(OrderValidationErrorCodes.ORDER_LINE_CHARGES_CHARGECATEGORY_MISSING, order);
                        }
                        if (StringUtils.isEmpty(olc.getChargeType())) {
                            publishAndThrowError(OrderValidationErrorCodes.ORDER_LINE_CHARGES_CHARGETYPE_MISSING, order);
                        }
                        if (StringUtils.isEmpty(olc.getChargePerUnit()) && StringUtils.isEmpty(olc.getChargeAmount())) {
                            publishAndThrowError(OrderValidationErrorCodes.ORDER_LINE_CHARGES_AMOUNT_PERUNIT_MISSING, order);
                        }
                    }
                }
                //step 6 --Validate  orderlInes.orderLineStatus
                if (CollectionUtils.isEmpty(ol.getOrderLineStatuses())) {
                    publishAndThrowError(OrderValidationErrorCodes.ORDER_LINE_STATUS_MISSING, order);
                }
                if (!CollectionUtils.isEmpty(ol.getOrderLineStatuses())) {
                    for (OrderLineStatus ols : ol.getOrderLineStatuses()) {
                        if (StringUtils.isEmpty(ols.getFulfillmentChannel())) {
                            publishAndThrowError(OrderValidationErrorCodes.ORDER_LINES_STATUS_FULFILLMENTCHANNEL_MISSING, order);
                        }
                        if (StringUtils.isEmpty(ols.getFulfillmentDivision())) {
                            publishAndThrowError(OrderValidationErrorCodes.ORDER_LINES_STATUS_FULFILLMENTDIVISION_MISSING, order);
                        }
                        if (StringUtils.isEmpty(ols.getFulfillmentLocation())) {
                            publishAndThrowError(OrderValidationErrorCodes.ORDER_LINES_STATUS_FULFILLMENTLOCATION_MISSING, order);
                        }
                        if (StringUtils.isEmpty(ols.getFulfillmentStore())) {
                            publishAndThrowError(OrderValidationErrorCodes.ORDER_LINES_STATUS_FULFILLMENTSTORE_MISSING, order);
                        }
                        if (StringUtils.isEmpty(ols.getRefShipmentId())) {
                            publishAndThrowError(OrderValidationErrorCodes.ORDER_LINES_STATUS_SHIPMENTID_MISSING, order);
                        }
                        if (ObjectUtils.isEmpty(ols.getStatusQuantity()) || 0 == ols.getStatusQuantity()) {
                            publishAndThrowError(OrderValidationErrorCodes.ORDER_LINE_STATUS_STATUSQTY_MISSING, order);
                        }
                    }
                }
                //step 7 --Validate  orderlInes.orderLineTaxes
                if (!CollectionUtils.isEmpty(ol.getOrderLineTaxes())) {
                    for (OrderLineTax olt : ol.getOrderLineTaxes()) {
                        if (StringUtils.isEmpty(olt.getChargeCategory())) {
                            publishAndThrowError(OrderValidationErrorCodes.ORDER_LINE_TAXES_CHARGECATEGORY_MISSING, order);
                        }
                        if (StringUtils.isEmpty(olt.getChargeType())) {
                            publishAndThrowError(OrderValidationErrorCodes.ORDER_LINE_TAXES_CHARGETYPE_MISSING, order);
                        }
                        if (StringUtils.isEmpty(olt.getTax()) && StringUtils.isEmpty(olt.getTaxPerUnit())) {
                            publishAndThrowError(OrderValidationErrorCodes.ORDER_LINE_TAXES_TAX_FIELDS_MISSING, order);
                        }
                    }
                }
                if(!StringUtils.isEmpty(ol.getMarketingPartnerId())){
                    if (ObjectUtils.isNotEmpty(ol.getMarketPlaceDetails())) {
                        if(!StringUtils.isEmpty(ol.getMarketPlaceDetails().getSellerId())) {
                            if(StringUtils.isEmpty(ol.getOfferId())) {
                                publishAndThrowError(OrderValidationErrorCodes.OFFERID_FIELD_MISSING, order);
                            }
                        }else{
                            publishAndThrowError(OrderValidationErrorCodes.SELLERID_MISSING, order);
                        }
                    }else{
                        publishAndThrowError(OrderValidationErrorCodes.MARKETPLACEDETAILS_MISSING, order);
                    }
                }

            }
        }
    }
}
