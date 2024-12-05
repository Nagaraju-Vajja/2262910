package com.macys.uop.order.ordercollectorchestrator.utils;

public enum OrderValidationErrorCodes {

    BILLING_INFO_MISSING("UOP-ORVAL-E10001", "BillingInfo details are missing"),
    LOYALTYTYPE_DETAILS_MISSING("UOP-ORVAL-E10002", "Either loyaltyType or loyaltyId is missing from LoyaltyDetails"),
    PARTNERORDERID_FIELD_MISSING("UOP-ORVAL-E10003", "partnerOrderId field is missing"),
    ORDER_LINES_MISSING("UOP-ORVAL-E10004", "orderLines details are missing"),
    ORDER_LINE_STATUS_MISSING("UOP-ORVAL-E10006", "orderLineStatus details are missing"),
    OFFERID_FIELD_MISSING ("UOP-ORVAL-E10007","offerId field is missing"),
    ORDER_LINES_FIELDS_MISSING_STH("UOP-ORVAL-E10008","deliveryMethod details is missing from orderLines[] for STH type"),
    SHIPPINGINFO_DETAILS_MISSING_STH("UOP-ORVAL-E10009","shippingInfo details are missing from orderLines[] for STH type"),
    ADDRESS_DETAILS_ERROR_S2AP("UOP-ORVAL-E10010","addressDetails are missing for S2AP type"),
    ADDRESSDETAILS_MISSING_FIELDS_ERROR_S2AP("UOP-ORVAL-E10011","type field is missing from orderLine.addressDetails for S2AP type"),
    ATTN_FIELD_MISSING_S2AP("UOP-ORVAL-E10012","attn field is missing in orderLine.addressDetails for S2AP type"),
    ADDRESS_DETAILS_ERROR_BOSS("UOP-ORVAL-E10013","addressDetails are missing for BOSS type"),
    ADDRESSDETAILS_MISSING_FIELDS_ERROR_BOSS("UOP-ORVAL-E10014","type field is missing from orderLine.addressDetails for BOSS type"),
    ADDRESS_DETAILS_ERROR_BOPS("UOP-ORVAL-E10015","addressDetails are missing for BOPS type"),
    ADDRESSDETAILS_MISSING_FIELDS_ERROR_BOPS("UOP-ORVAL-E10016","type field is missing from orderLine.addressDetails for BOPS type"),
    ORDER_LINES_FIELDS_MISSING_BOSS("UOP-ORVAL-E10017","deliveryMethod details is missing from orderLines[] for BOSS type"),
    ORDER_LINES_FIELDS_MISSING_SDD("UOP-ORVAL-E10018","deliveryMethod details is missing from orderLines[] for SDD type"),
    SHIPPINGINFO_DETAILS_MISSING_SDD("UOP-ORVAL-E10019","shippingInfo details are missing from orderLines[] for SDD type"),
    ORDER_LINES_FIELDS_MISSING_S2AP("UOP-ORVAL-E10020","deliveryMethod details is missing from orderLines[] for S2AP type"),
    PICKUP_CHANNEL_MISSING_S2AP("UOP-ORVAL-E10021","pickupChannel field detail is missing from orderLine[].orderLinestatus for S2AP type"),
    ORDER_LINE_STATUS_PICKUPLOCATION_MISSING_BOSS("UOP-ORVAL-E10023","pickupLocation field details is missing from orderLine[].orderLinestatus for BOSS type"),
    ORDER_LINE_STATUS_PICKUPSTORE_MISSING_BOSS("UOP-ORVAL-E10024","pickupStore field details is missing from orderLine[].orderLinestatus for BOSS type"),
    ORDER_LINE_STATUS_PICKUPDIVISION_MISSING_BOSS("UOP-ORVAL-E10025","pickupDivision field details is missing from orderLine[].orderLinestatus for BOSS type"),
    PICKUP_CHANNEL_MISSING_BOSS("UOP-ORVAL-E10026","pickupChannel field detail is missing from orderLine[].orderLinestatus for BOSS type"),
    ORDER_LINE_STATUS_FIELDS_MISSING_BOPS("UOP-ORVAL-E10027","One of mandatory fields(pickupLocation,pickupStore,pickupDivision) are missing from orderLine[].orderLinestatus for BOPS type"),
    ORDER_LINE_STATUS_PICKUPLOCATION_MISSING_BOPS("UOP-ORVAL-E10028","pickupLocation field details is missing from orderLine[].orderLinestatus for BOPS type"),
    ORDER_LINE_STATUS_PICKUPSTORE_MISSING_BOPS("UOP-ORVAL-E10029","pickupStore field details is missing from orderLine[].orderLinestatus for BOPS type"),
    ORDER_LINE_STATUS_PICKUPDIVISION_MISSING_BOPS("UOP-ORVAL-E10030","pickupDivision field details is missing from orderLine[].orderLinestatus for BOPS type"),
    PICKUP_CHANNEL_MISSING_BOPS("UOP-ORVAL-E10031","pickupChannel field detail is missing from orderLine[].orderLinestatus for BOPS type"),
    ORDERCHANNELDIVISION_FIELD_MISSING("UOP-ORVAL-E10032", "orderChannelDivision field detail is missing"),
    ORDERLOCATION_FIELD_MISSING("UOP-ORVAL-E10033", "orderLocation field detail is missing"),
    SELLINGCHANNELTYPE_FIELD_MISSING("UOP-ORVAL-E10034", "sellingChannelType field detail is missing"),
    SOURCECHANNEL_FIELD_MISSING("UOP-ORVAL-E10035", "sourceChannel field detail is missing"),
    BILLING_INFOS_ADDRESSLINE1_MISSING("UOP-ORVAL-E10036", "addressLine1 field detail is missing from BillingInfo"),
    BILLING_INFOS_CITY_MISSING("UOP-ORVAL-E10037", "city field detail is missing from BillingInfo"),
    BILLING_INFOS_COUNTRY_MISSING("UOP-ORVAL-E10038", "country field detail is missing from BillingInfo"),
    BILLING_INFOS_EMAILID_MISSING("UOP-ORVAL-E10039", "emailId field detail is missing from BillingInfo"),
    BILLING_INFOS_FIRSTNAME_MISSING("UOP-ORVAL-E10040", "firstName field detail is missing from BillingInfo"),
    BILLING_INFOS_LASTNAME_MISSING("UOP-ORVAL-E10041", "lastName field detail is missing from BillingInfo"),
    BILLING_INFOS_STATE_MISSING("UOP-ORVAL-E10042", "state field detail is missing from BillingInfo"),
    BILLING_INFOS_ZIPCODE_MISSING("UOP-ORVAL-E10043", "zipcode field detail is missing from BillingInfo"),
    ORDER_LINES_FULFILLMENTTYPE_MISSING("UOP-ORVAL-E10044", "fulfillmentType field detail is missing from orderLines[]"),
    ORDER_LINES_LINEID_MISSING("UOP-ORVAL-E10045", "lineId field detail is missing from orderLines[]"),
    ORDER_LINES_ORDERED_QTY_MISSING("UOP-ORVAL-E10046", "orderedQty field detail is missing from orderLines[]"),
    ORDER_LINES_UNITPRICE_MISSING("UOP-ORVAL-E10047", "unitPrice field detail is missing from orderLines[]"),
    ORDER_LINES_UPCNUMBER_MISSING("UOP-ORVAL-E10048", "upcNumber field detail is missing from orderLines[]"),
    ORDER_LINE_TAXES_CHARGETYPE_MISSING("UOP-ORVAL-E10049", "chargeType field detail is missing from orderLineTaxes[]"),
    ORDER_LINE_TAXES_CHARGECATEGORY_MISSING("UOP-ORVAL-E10050", "chargeCategory field detail is missing from orderLineTaxes[]"),
    ORDER_LINE_TAXES_TAX_FIELDS_MISSING("UOP-ORVAL-E10051", "any one of (tax or taxPerUnit) details are missing from orderLineTaxes[]"),
    ORDER_LINE_CHARGES_CHARGETYPE_MISSING("UOP-ORVAL-E10052", "chargeType field detail is missing from orderLineCharges[]"),
    ORDER_LINE_CHARGES_CHARGECATEGORY_MISSING("UOP-ORVAL-E10053", "chargeCategory field detail is missing from orderLineCharges[]"),
    ORDER_LINE_CHARGES_AMOUNT_PERUNIT_MISSING("UOP-ORVAL-E10054", "any one of (chargeAmount or chargePerUnit) details are missing from orderLineCharges[]"),
    ORDER_LINES_STATUS_FULFILLMENTCHANNEL_MISSING("UOP-ORVAL-E10055", "fulfillmentChannel field detail is missing from orderLines[]"),
    ORDER_LINES_STATUS_FULFILLMENTDIVISION_MISSING("UOP-ORVAL-E10056", "fulfillmentDivision field detail is missing from orderLine[].orderLinestatus"),
    ORDER_LINES_STATUS_FULFILLMENTLOCATION_MISSING("UOP-ORVAL-E10057", "fulfillmentLocation field detail is missing from orderLine[].orderLinestatus"),
    ORDER_LINES_STATUS_FULFILLMENTSTORE_MISSING("UOP-ORVAL-E10058", "fulfillmentStore field detail is missing from orderLine[].orderLinestatus"),
    ORDER_LINES_STATUS_SHIPMENTID_MISSING("UOP-ORVAL-E10059", "refShipmentId field detail is missing from orderLine[].orderLinestatus"),
    ORDER_LINE_STATUS_STATUSQTY_MISSING("UOP-ORVAL-E10060", "statusQuantity field detail is missing from orderLine[].orderLinestatus"),
    PRODUCT_GROUP_MISSING("UOP-ORVAL-E10061", "productGroup field detail is missing from orderLine[].productAdditionalAttributes for BOSS type"),
    PRODUCT_ADDL_ATTRIBUTE_DETAILS_MISSING_BOSS("UOP-ORVAL-E10062", "productAdditionalAttributes field details is missing from orderLine[] for BOSS type"),
    SELLERID_MISSING("UOP-ORVAL-E10063", "sellerId field details is missing from orderLine[].marketPlaceDetails"),
    MARKETPLACEDETAILS_MISSING("UOP-ORVAL-E10064", "marketPlaceDetails field details is missing from order"),
    SELLINGCHANNELTYPE_INFO_MISSING("UOP-ORVAL-F10001", "SellingChannelType must be one of MCOM,BCOM,ESEND"),
    ORDERCHANNELDIVISION_INFO_MISSING("UOP-ORVAL-F10002", "OrderChannelDivision must be one of 71,72"),
    ORDERPURPOSE_INFO_MISSING("UOP-ORVAL-F10003", "orderPurpose must be one of SALE,RETURN,REFUND"),
    FULFILLMENTTYPE_INFO_MISSING("UOP-ORVAL-F10004", "FulfillmentType must be one of STH,S2AP,BOPS,BOSS,SDD"),
    DELIVERYTYPE_INFO_MISSING("UOP-ORVAL-F10005", "deliveryType must be one of PICKUP,SHIPPING,ELECTRONIC"),
    DELIVERYMETHOD_INFO_MISSING("UOP-ORVAL-F10006", "deliveryMethod must be one of GROUND,TWODAYAIR,OVERNIGHT,EMAIL,SAMEDAY,GROUND SHIPPING,EXPEDITE,PREMIUM"),
    LINETYPE_INFO_MISSING("UOP-ORVAL-F10007", "lineType must be one of PRODUCT,EGC,VGC,CHARIT"),
    CHARGECATEGORY_INFO_MISSING("UOP-ORVAL-F10008", "ChargeCategory must be one of CHARGE,DISCOUNT"),
    FULFILLMENTCHANNEL_INFO_MISSING("UOP-ORVAL-F10009", "FulfillmentChannel must be one of STR1,EXT,POOL,DROPSHIP,FACS,EMAIL,ORDD,SPECIAL,SPEC,ORDR"),
    BILLING_INFOS_COUNTRY_CODE_MISSING("UOP-ORVAL-E10065", "countryCode field detail is missing from BillingInfo"),
    ESTIMATEDSHIPTS_INFO_MISSING("UOP-ORVAL-E10066", "estimatedShipTs field detail is missing from orderLine[].orderLinestatus"),
    ESTIMATEDDELIVERYTS_INFO_MISSING("UOP-ORVAL-E10067", "estimatedDeliveryTs field detail is missing from orderLine[].orderLinestatus"),
    PARSE_ERROR("1000", "Error occured while parsing order payload"),
    ORDER_CHARGES_CHARGETYPE_MISSING("UOP-ORVAL-E10068", "chargeType field detail is missing from orderCharges"),
    ORDER_CHARGES_CHARGECATEGORY_MISSING("UOP-ORVAL-E10069", "chargeCategory field detail is missing from orderCharges"),
    ORDER_CHARGES_AMOUNT_PERUNIT_MISSING("UOP-ORVAL-E10070", "any one of (chargeAmount or chargePerUnit) details are missing from orderCharges"),
    ORDER_TAXES_CHARGETYPE_MISSING("UOP-ORVAL-E10071", "chargeType field detail is missing from orderTaxes"),
    ORDER_TAXES_CHARGECATEGORY_MISSING("UOP-ORVAL-E10072", "chargeCategory field detail is missing from orderTaxes"),
    ORDER_TAXES_TAX_FIELDS_MISSING("UOP-ORVAL-E10073", "any one of (tax or taxPerUnit) details are missing from orderTaxes"),
    PAYMENT_BLOCK_ERROR("UOP-ORVAL-E10074", "Payment block is empty or null"),
    FULFILLMENTCHANNEL_ERROR_MESSAGE("UOP-ORVAL-E10075", "Fulfillment channel should not be EXT for 1P order's"),
    FULFILLMENTCHANNEL_3P_ERROR_MESSAGE("UOP-ORVAL-E10076", "Fulfillment channel should be EXT for 3P order's"),
    SHIIPINGINFOS_ADDRESSLINE1_MISSING("UOP-ORVAL-E10077", "addressLine1 field detail is missing from ShippingInfo"),
    SHIIPINGINFOS_CITY_MISSING("UOP-ORVAL-E10078", "city field detail is missing from ShippingInfo"),
    SHIIPINGINFOS_COUNTRY_MISSING("UOP-ORVAL-E10079", "country field detail is missing from ShippingInfo"),
    SHIIPINGINFOS_FIRSTNAME_MISSING("UOP-ORVAL-E10080", "firstName field detail is missing from ShippingInfo"),
    SHIIPINGINFOS_LASTNAME_MISSING("UOP-ORVAL-E10081", "lastName field detail is missing from ShippingInfo"),
    SHIIPINGINFOS_STATE_MISSING("UOP-ORVAL-E10082", "state field detail is missing from ShippingInfo"),
    SHIIPINGINFOS_ZIPCODE_MISSING("UOP-ORVAL-E10083", "zipcode field detail is missing from ShippingInfo");

    private String code;
    private String description;

    private OrderValidationErrorCodes(String code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * get code
     * @return String
     */
    public String getCode() {
        return String.valueOf(this.code);
    }

    /**
     * get description
     * @return String
     */
    public String getDescription() {
        return this.description;
    }

    public static OrderValidationErrorCodes getNameByValue(String value) {
        for (OrderValidationErrorCodes e : OrderValidationErrorCodes.values()) {
            if (e.code.equalsIgnoreCase(value)) {
                return e;
            }
        }
        return null;// not found
    }
}
