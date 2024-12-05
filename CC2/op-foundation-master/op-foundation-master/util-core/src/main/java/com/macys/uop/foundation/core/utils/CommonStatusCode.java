package com.macys.uop.foundation.core.utils;

/**
 * Enumeration for storing Code and Description.
 * Primary purpose of this enumeration is for storing Error Code and related Description
 */
public enum CommonStatusCode {
    SUCCESS("UOP-GEN-E05001", "Request was processed successfully"),
    NETWORK_ERROR("UOP-GEN-E05002", "Network Error"),
    NO_HEADERS("UOP-GEN-E05003", "No Headers Passed, pass mandatory headers"),
    NO_CORRELATIONID("UOP-GEN-E05004", "CORRELATIONID is mandatory, set it in headers"),
    NO_ORDERID("UOP-GEN-E05005", "ORDERID is mandatory, set it in headers"),
    NO_MESSAGEID("UOP-GEN-E05006", "MESSAGEID is mandatory, set it in headers"),
	NOT_FOUND("UOP-GEN-E05007", "Requested resource not found"),
    APPLICATION_ERROR("UOP-GEN-E05008", "Internal service error"),
    INTERNAL_SERVICE_ERROR("UOP-GEN-E05009", "Generic Unhandled Exception"),
    NO_CLIENTID("UOP-GEN-E05010", "CLIENTID is mandatory, set it in headers"),
    DUPLICATE_MESSAGE_ID("UOP-GEN-E05011", "Duplicate message id has been detected"),
    INVALID_CORRELATIONID("UOP-GEN-E05012", "CORRELATIONID in the request does not match with the pattern [a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}"),
    SERVICE_TIMEOUT("UOP-GEN-E05013", "The end service timed out."),
    BAD_REQUEST("UOP-GEN-E05014", "Bad Request."),
    SERVICE_NOT_REACHABLE("UOP-GEN-E05015", "End Service not reachable"),
    SERVICE_RETURNS_4XXX("UOP-GEN-E05016", "End Service returns 4XXX error"),
    RUNTIME_ERROR("UOP-GEN-E05017", "Runtime error encountered, message not stored"),
    DB_ERROR("UOP-GEN-E05018", "DB Error encountered while storing message into DB"),
    ILLEGAL_ARG_ERROR("UOP-GEN-E05019", "Illegal Arguments Error encountered"),
    BAD_REQUEST_BODY("UOP-GEN-E05020", "Error while processing or parsing request body"),
    MANDATORY_HEADERS_MISSING("UOP-GEN-E05021", "Check OrderID, CORRELATIONID, MESSAGEID, CLIENTID"),
    EXTERNAL_SERVICE_ERROR("UOP-GEN-E05022", "External service error"),
    INVALID_HTTP_REQ("UOP-GEN-E05023", "Invalid HTTP Request"),
    NO_ERROR_CODE("UOP-GEN-E05024", "CODE is mandatory, set it in Error"),
    NO_ERROR_MESSAGE("UOP-GEN-E05025", "MESSAGE is mandatory, set it in Error"),
    NO_ERRORDETAIL_DOMAIN("UOP-GEN-E05026", "DOMAIN is mandatory, set it in ErrorDetail"),
    NO_ERRORDETAIL_REASON("UOP-GEN-E05027", "REASON is mandatory, set it in ErrorDetail"),
    NO_ERRORDETAIL_MESSAGE("UOP-GEN-E05028", "MESSAGE is mandatory, set it in ErrorDetail"),
    MANDATORY_PARAMETER_MISSING("UOP-GEN-E05029", "Mandatory Parameter Missing"),
    MESSAGE_PUBSLISH_ERROR("UOP-GEN-E05030", "Error encountered while publishing message"),
    MANDATORY_HEADER_MISSING("UOP-GEN-E05031", "Mandatory Header Missing"),
	MESSAGEDIGEST_ERROR("UOP-GEN-E05032", "Message Digest Error"),
	JSON_PROCESSING_ERROR("UOP-GEN-E05033", "Json Processing Error"),
	XML_PROCESSING_ERROR("UOP-GEN-E05034", "Xml Processing Error"),
	MESSAGE_SUBSCRIBER_STARTING_ERROR("UOP-GEN-E05035","Message Subscriber Starting Error"),
	MESSAGE_SUBSCRIBER_STOPPING_ERROR("UOP-GEN-E05036","Message Subscriber Stopping Error"),
	MSSAGE_DUPLICATION_CHECK_ERROR("UOP-GEN-E05037",   "Message Duplication Check Error"),
	NO_CALLERID("UOP-GEN-E05038", "CALLERID is mandatory, set it in headers"),
	CIRCUIT_BREAKER_OPEN("UOP-GEN-E05039", "Circuit breaker is open"),
    CLIENT_TIMEOUT("UOP-GEN-E05040", "The client timed out."),
	PROXY_CALL_CIRCUIT_BREAKER_OPEN("UOP-GEN-E05041", "Proxy call circuit breaker is open"),
    MESSAGE_SUBSCRIBER_STOPPED("UOP-GEN-E05042", "Message Subscriber Stopped"),
    MESSAGE_SUBSCRIBER_STARTED("UOP-GEN-E05043", "Message Subscriber Started"),
    RESOURCE_ACCESS_EXCEPTION("UOP-GEN-E05044", "Resource Access Exception"),
    EVENT_STATE_DB_POOL_CLOSED_ERROR("UOP-GEN-E05045", "Event state DB pool has been closed error"),
    DB_POOL_CLOSED_ERROR("UOP-GEN-E05046", "DB pool has been closed error"),
    EVENT_STATE_UPSERT_FAILURE("UOP-GEN-E05047", "Event state upsert Error"),
	ABORT_TRANSACTION_ERROR("UOP-GEN-E05048", "Spanner transaction aborted due to concurrent request");


    private String code;
    private String description;

    private CommonStatusCode(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return this.code;
    }

    public String getDescription() {
        return this.description;
    }
    
}
