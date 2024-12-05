package com.macys.uop.order.ordercollectorchestrator.utils;

import  static com.macys.uop.order.ordercollectorchestrator.utils.OrdercollectorchestratorConstants.*;

public enum RetryEnum {

    EVENT_LOG("ORDER_LOGEVENT", "EventLog", "event_onsuccess.channel_name", PUBSUB, "event_onsuccess.channel_name", null),
    AUDIT_LOG("ORDER_LOGAUDIT", "Audit", "audit_onsuccess.channel_name", PUBSUB, "audit_onsuccess.channel_name", null),
    VALIDATE_ORDER("ORDER_VALIDATE", SERVICENAME, "ordercollectorchestrator.input.channel.name", PUBSUB, "ordercollectorchestrator.input.channel.name", null),
    PARTNER_FULFILLMENT_ID_LOCK("PARTNER_FULFILLMENT_ID_LOCK", LOCK_MANAGER, "lockmanager-service.createLockTransaction",HTTP,"ordercollectorchestrator.input.channel.name", POST),
    STAMP_PROFILE("ORDER_STAMPPROFILE", PROFILE_EVALUATOR, "profileevaluator.profilemanager.base_uri",HTTP, "ordercollectorchestrator.input.channel.name", GET),
    CREATE_ORDER("ORDER_CREATE", ORDER_COLLECT, "ordercollect-service",HTTP, "ordercollectorchestrator.input.channel.name", POST),
    LOCK_ORDER("ORDER_LOCK", LOCK_MANAGER, "lockmanager-service.createLockTransaction",HTTP,"ordercollectorchestrator.input.channel.name", POST),
    ENRICH_ORDER("ORDER_ENRICH", ORDER_ENRICHMENT, "orderenrichment-service",HTTP,"ordercollectorchestrator.input.channel.name", POST),
    PUBLISH_ORDER_CREATION("ORDER_PUBLISHCREATESUCCESS", ORDER_PUBLISHCREATESUCCESS, "ordercreation_onsuccess.channel_name",PUBSUB,"ordercreation_onsuccess.channel_name",null),
    PUBLISH_TO_MCHUB("ORDER_PUBLISHMCHUBACK", SERVICENAME, "collectorder_fraudacknowledgment.channel_name",PUBSUB,"collectorder_fraudacknowledgment.channel_name",null),
    ERROR_PROCESSOR("errorProcessor", SERVICENAME, "errorprocessor_errorrequest.channel_name",PUBSUB,"errorprocessor_errorrequest.channel_name",null),
    ENHANCEBOSSPLUS_ORDER("ORDER_ENHANCEBOSSPLUS", SERVICENAME, "ordercollectorchestrator.input.channel.name", PUBSUB, "ordercollectorchestrator.input.channel.name", null);

    private final String stepName;
    private final String location;
    private final String errorSourceSearchKey;
    private final String errorSource;
    private final String callBackSource;
    private final String httpMethod;

    RetryEnum(String stepName, String location, String errorSourceSearchKey, String errorSource, String callBackSource, String httpMethod) {
        this.stepName = stepName;
        this.location = location;
        this.errorSourceSearchKey = errorSourceSearchKey;
        this.errorSource = errorSource;
        this.callBackSource = callBackSource;
        this.httpMethod = httpMethod;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public String getCallBackSource() {
        return callBackSource;
    }

    public String getErrorSourceSearchKey() {
        return errorSourceSearchKey;
    }

    public String getErrorSource() {
        return errorSource;
    }

    public String getLocation() {
        return location;
    }

    public String getStepName() {
        return stepName;
    }

    public static RetryEnum getRetryInfoByStep(String stepName) {
        for (RetryEnum e : RetryEnum.values()) {
            if (e.stepName.equalsIgnoreCase(stepName)) {
                return e;
            }
        }
        return null;// not found
    }

    public static RetryEnum getRetryInfoByLocation(String location) {
        for (RetryEnum e : RetryEnum.values()) {
            if (e.location.equalsIgnoreCase(location)) {
                return e;
            }
        }
        return null;// not found
    }
}
