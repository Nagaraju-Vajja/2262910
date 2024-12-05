package com.macys.uop.order.ordercollectorchestrator.utils;

import org.apache.commons.lang3.StringUtils;

public enum OrderErrorCodes {

    VALIDATE_ORDER_ERROR("ORDER_VALIDATE", "UOP-ORD-E10001", "Mandatory Order Parameters missing"),

    SERVICE_UNAVAILABLE_ERROR(null, "UOP-ORD-E10002","Unable to Call Service"),

    PUBLISH_ORDERCREATION_ERROR("ORDER_PUBLISHCREATESUCCESS","UOP-ORD-E10003","Exception Occurred While Publishing Message To OrderSourcing"),

    CONNECTION_ERROR(null,"UOP-ORD-E10007", "Connection Refused While Calling the service"),

    ERROR_PROCESSOR_ERROR("errorProcessor", "UOP-ORD-E10004","Exception Occurred While Publishing To Error Processor Service"),

    PUBLISHTOMCHUB_ERROR("ORDER_PUBLISHMCHUBACK", "UOP-ORD-E10005","Exception Occurred While Publishing Acknowledgement Message"),

    EVENT_LOG_ERROR("ORDER_LOGEVENT","UOP-ORD-E10006","Exception Occurred While Logging Event in EventLog Service"),

    AUDIT_LOG_ERROR("ORDER_LOGAUDIT","UOP-ORD-E10009","Exception Occurred While Logging Audit in Audit Service"),

    LOCK_ORDER_ERROR("ORDER_LOCK", "UOP-ORD-E10008", "Exception Occurred While Calling the LockManager service"),

    BIZCONFIG_ERROR(null, "UOP-ORD-E10010", "Business Configuration is Not Available "),
    PUBLISH_ERROR(null,"UOP-ORD-E10027", "Exception Occurred While Publishing Acknowledgement Message"),

    MANDATORY_FIELDS_VALIDATION(null,"1", "MandatoryFields are Missing"),
    MANDATORY_FIELDS(null,"MANDATORY FIELDS", "MANDATORY FIELDS"),

    RUNTIME_EXCEPTION(null, "UOP-ORD-E10083", "Exception Occurred while executing OrderCollectOrchestrator ");

    private String stepName;
    private String code;
    private String description;

    /**
     * OrderErrorCodes
     * @param stepName
     * @param code
     * @param description
     */
    private OrderErrorCodes(String stepName, String code, String description) {
        this.stepName = stepName;
        this.code = code;
        this.description = description;
    }

    /**
     * GetCode By StepName
     * @param stepName
     * @return OrderErrorCodes
     */
    public static OrderErrorCodes getCodeByStepName(String stepName) {
        for (OrderErrorCodes e : OrderErrorCodes.values()) {
            if (StringUtils.isNotEmpty(e.stepName) && e.stepName.equalsIgnoreCase(stepName)) {
                return e;
            }
        }
        return null;// not found
    }

    /**
     * getStepName
     * @return String
     */
    public String getStepName() {
        return stepName;
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

    public static OrderErrorCodes getNameByValue(String value) {
        for (OrderErrorCodes e : OrderErrorCodes.values()) {
            if (e.code.equalsIgnoreCase(value)) {
                return e;
            }
        }
        return null;// not found
    }
}
