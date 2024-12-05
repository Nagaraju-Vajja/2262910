package com.macys.uop.order.ordercollectorchestrator.utils;

import java.util.List;

public final class OrdercollectorchestratorConstants {
    
    public static final String EVENT_TIME_STAMP = "eventTimeStamp";
    public static final String EVENT_SOURCE = "eventSource";
    public static final String EVENT_TYPE = "eventType";
    public static final String TRUE = "true";
    public static final boolean TRUE_VALUE = true;
    public static final String UNKNOWN = "unknown";

    public static final String EXECUTION_ID = "executionId";
    public static final String REFERENCE_ID = "referenceId";
    public static final String APP_NAME = "appName";
    public static final String SELLER_ORDER_ID = "sellerOrderId";
    public static final String PARTNER_ORDER_ID = "partnerOrderId";
    public static final String ASSOCIATE_DETAILS="associateDetails";
    public static final String BIZ_ERROR_CATALOG_ENRICH="BusinessConfig for catalog enrichment not available";
    public static final String CATALOG_ENRICH="catalogEnrich";

    //Steps
    public static final String COLLECT_ORDER = "COLLECT_ORDER";
    public static final String ORDER_LOGEVENT= "ORDER_LOGEVENT";
    public static final String ORDER_VALIDATE = "ORDER_VALIDATE";
    public static final String ORDER_STAMPPROFILE = "ORDER_STAMPPROFILE";
    public static final String ORDER_CREATE = "ORDER_CREATE";
    public static final String ORDER_LOCK = "ORDER_LOCK";
    public static final String ORDER_ENRICH = "ORDER_ENRICH";
    public static final String ORDER = "ORDER";
    public static final String ORDER_PUBLISHCREATESUCCESS = "ORDER_PUBLISHCREATESUCCESS";
    public static final String ORDER_RECOVERYSTATE= "ORDER_RECOVERYSTATE";
    public static final String COMMA= ",";

    public static final String EVENT_TYPE_ORDER_ON_SUCCESS = "ORDER_CREATE";
    public static final String SERVICENAME = "ordercollectorchestrator";
    public static final String LOCK_CREATE ="LOCK_CREATE";
    public static final String LOCK_MANAGER ="lockmanager";
    public static final String MCHUB_ACK = "ORDER_FRAUDRESPONSE";
    public static final String OUTBOUND = "OUTBOUND";
    public static final String INBOUND = "INBOUND";
    public static final String ORDER_INPUT = "ORDER_INPUT";
    public static final String ORDER_NA_REF_ID = "ORDER - N/A";

    public static final String ORDER_PUBLISHMCHUBACK= "ORDER_PUBLISHMCHUBACK";
    
    public static final String PROFILE_ID  = "profileId";
    public static final String PROFILE_VERSION  = "version";

    public static final String ACK_FRAUD_RESPONSE="ACK_FRAUDRESPONSE";
    public static final String FRAUD_VALIDATION ="fraudValidation";
    public static final String FRAUD="FRAUD";
    public static final String UOP="UOP";
    public static final String FRAUD_LOCK_CODE="L0007";
    public static final String FRAUD_LOCK_REASON="Fraud lock created as Fraud response pending";
    public static final String FRAUD_LOCK_TYPE="FRAUD_LOCK";

    public static final String SUCCESS_STATUS_MESSAGE="Order Created, Enriched Successfully!";
    public static final String FRAUD_STATUS_MESSAGE="Fraud Response Processed Successfully!";
    public static final String ERROR_STATUS_MESSAGE="Error occurred while processing the request";
    public static final String ORDER_ENRICHMENT="orderenrichment";
    public static final String ORDER_COLLECT="ordercollect";
    public static final String PROFILE_EVALUATOR="profileevaluator";

    public static final String RESERVATION_ID = "reservationId";
    public static final String PAYMENT_LOCK="paymentLock";
    public static final String OK ="OK";
    public static final List<String> ERROR_CODE=List.of("UOP-ORD", "UOP-GEN");

    //Payment Lock
    public static final String PAYMENT_LOCK_TYPE="PAYMENT_LOCK";
    public static final String PAYMENT="PAYMENT";
    public static final String PAYMENT_LOCK_CODE="L0042";
    public static final String PAYMENT_LOCK_REASON="Payment Lock : Exchange/EEC Order Creation";

    //Error Processor
    public static final String FAILED_STATE = "failedState";
    public static final String FAILED_SUB_STATE = "failedSubState";
    public static final String RETRY = "retry";
    public static final String RETRY_COUNT = "retryCount";
    public static final String PUBSUB = "PUBSUB";
    public static final String HTTP = "HTTP";
    public static final String RECOVERY_INITIAL_STEPS = "recovery";
    public static final String POST="POST";
    public static final String GET="GET";
    public static final String ERROR_PROCESSOR="errorProcessor";
    public static final String ST="selfretry";
    public static final String ET="errorretry";
    public static final String EL="errorlog";   
    public static final String INITIAL_STEPS = "command.collectorder.steps";

    //MCHUB ACk
    public static final String RES_NBR = "resNbr";
    public static final String REFERRING_ID="referringId";
    public static final String SUB_CLIENT_ID="subClientId";
    public static final String ORDER_METHOD="orderMethod";
    public static final String SITE="Site";
    public static final String MCOM="MCOM";
    public static final String DURATION="duration";
    public static final String MAXCOUNT="maxcount";

    //BIZ Config
    public static final String MANDATORY_FIELDS="mandatoryfields";
    public static final String FIELDS="Fields";
    public static final String CONFIG_TYPE="system";
    public static final String SELLING_DIVISION="0";
    public static final String ORDER_RESPONSE="orderResponse";
    public static final String BIZ_ERROR_PAYMENT_LOCK="BusinessConfig for payment lock not available";
    public static final String BIZ_ERROR_ORDER_RESPONSE="BusinessConfig for order response not available";

    public static final int FOUR_HUNDRED = 400;
    public static final int ZERO = 0;
    
    public static final String MARKETING_PARTNER_ID="marketingPartnerId";
    public static final String MARKETING_PARTNER_ID_MIRAKL="MIRAKL";
    
    public static final String PARTNER_FULFILLMENT_ID_LOCK = "PARTNER_FULFILLMENT_ID_LOCK";
    public static final String PARTNER_FULFILLMENT_ID_LOCKED_ENTITY = "PARTNER_FULFILLMENT_ID";
    public static final String PARTNER_FULFILLMENT_ID_LOCK_CODE="L0026";
    public static final String PARTNER_FULFILLMENT_ID_LOCK_REASON="Partner Fulfillment id response pending";
    public static final String PARTNER_FULFILLMENT_ID_LOCK_TYPE="PARTNER_FULFILLMENT_ID";
    public static final String ORDER_LOCK_MIRAKL_CONFIG="order_lock_mirakl";
    public static final String PARTNER_FULFILLMENT_ID_LOCK_ATTRIBUTE ="partnerFulfillmentIdLock";
    public static final String BIZ_ERROR_PARTNER_FULFILLMENT_ID_LOCK="BusinessConfig for Partner Fulfillment Id Lock not available";
    public static final String SALE = "SALE";
    public static final String COLLECTORDER_PROXY = "ordercollect-proxy";
    public static final String ENRICHORDER_PROXY = "orderenrichment-proxy";
    public static final String ORDERID = "orderId";
    public static final String MESSAGEID = "messageId";
    public static final String CORRELATIONID = "correlationId";
    public static final String CLIENTID = "clientId";

    public static final String REGISTRYFLAG = "hasRegistry";

    public static final List<String> COMMON_ERROR_CODE = List.of(
        "UOP-ORD-E10002",
        "UOP-ORD-E10007"
    );
    public static final String DEFAULT_REF_SHIPMENT_ID = "1";
    public static final String ZOLA = "ZOLA";

    public static final String FRAUD_LOCK_CODE_NOT_INTIATED="L0101";
    public static final String FRAUD_LOCK_CODE_NOT_INTIATED_DESC="CREATED: Fraud lock created for ORDER_CREATE change request";
    public static final List<String> NEW_SOURCE_SYSTEMS = List.of("MCHECKOUT", "BCHECKOUT");
    public static final String LOCK_REASON_CODE = "lockreasoncode";
    public static final String LOCK_ID = "lockid";
    public static final String X_SUBSCLEINT_ID="xSubclientId";
    public static final String TRANSACTION_ID="transactionId";

    public static final String MACYS_71="71";
    public static final String BLOOMYS_72="72";
    public static final String S2AP="S2AP";
    public static final String MACYS_71_ASSOC_ID="71115670";
    public static final String BLOOMYS_72_ASSOC_ID="72000101";

    public static final List<String> BLOOMYS_SELLING_CHANNEL_TYPE = List.of("BCOM", "BLCOM");

    public static final String STH = "STH";
    public static final String BOSS = "BOSS";
    public static final String BOPS = "BOPS";
    public static final String SDD = "SDD";
    public static final String ORDD = "ORDD";
    public static final String SPEC = "SPEC";

    public static final String SOURCE_CHANNEL = "sourceChannel";
    public static final String SOURCE_SYSTEM = "sourceSystem";
    public static final String  ORDER_PLATFORM = "orderPlatform";
    public static final String COLLECTORDERRESPONSE_CHANNEL_NAME="collectorder_response.channel_name";
    public static final String SUCCESS="SUCCESS";
    public static final String ACKNOWLEDGEMENT_HDR = "Acknowledgement";
    public static final String ONE="1";
    public static final String ORDER_VALIDATION = "ORDER_VALIDATE";

    public static final String FAILED="FAILED";

    private OrdercollectorchestratorConstants() {}

    public static final String MCHECKOUT_SOURCE_SYSTEM= "MCHECKOUT";

    public static final String BCHECKOUT_SOURCE_SYSTEM= "BCHECKOUT";

    public static final String DEFAULT_DATE = "9999-12-31T23:59:59.999999999Z";

    public static final String SELLING_CHANNEL_TYPE = "sellingChannelType";
    public static final String PICKUP = "PICKUP";
    public static final String PICKUP_VALUE = "PickUp";
    public static final String ALTPICKUP = "ALTPICKUP";
    public static final String PICKUP_PERSON = "PickUpPerson";
    public static final String ALTPICKUP_VALUE = "AltPickUp";
    public static final String BOSS_PLUS_BIZ_CONFIGNAME_ERROR = "Business Configuration for Boss Plus not available";
    public static final String BOSSPLUS_CREATE_REASON_CODE = "OP_CREATEREASON_1";
    public static final String BOSSPLUS_CREATE_REASON_DESC = "BOSS+ Order Create - Fulfillment type was changed from a BOSS to BOPS";
    public static final String ORDER_ENHANCEBOSSPLUS = "ORDER_ENHANCEBOSSPLUS";
    public static final String CONFIG_NAME_ORDER_BOSSPLUS_ENABLEFULFILLMENTSTORE = "order.bossplus.enablefulfillmentstore";
    public static final String CONFIG_KEY_ALLOW_BOSS_PLUS = "allowBossPlus";
    public static final String FULFILLMENTTYPE_BOSS = "BOSS";
    public static final String FULFILLMENTTYPE_BOPS = "BOPS";
    public static final String FULFILLMENTTYPE_STH = "STH";
    public static final String CONFIG_KEY_ELIGIBLE_BOSSPLUS_EMAIL_ID = "eligibleBossPlusEmailId";

}
