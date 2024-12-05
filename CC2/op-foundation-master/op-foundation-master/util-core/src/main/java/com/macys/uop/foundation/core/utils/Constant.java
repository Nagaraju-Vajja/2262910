package com.macys.uop.foundation.core.utils;

/**
 * Constant class for storing default and constant vales to be used across libraries and services.
 *
 */
public final class Constant {
	
	public static final String SERVICE_REQUEST_CONTEXT = "SERVICE_REQUEST_CONTEXT";
	public static final String SERVICE_EXECUTION = "SERVICE_EXECUTION";
	public static final String BEAN_ID_LOAD_BALANCED_REST_TEMPLATE = "LOAD-BALANCED-TEMPLATE";
	public static final String BEAN_ID_DEFAULT_REST_TEMPLATE = "DEFAULT-REST-TEMPLATE";
	public static final String BEAN_ID_LAZY_TRACE_EXECUTOR = "LAZY-TRACE-EXECUTOR";
	public static final String BEAN_ID_DEFAULT_ASYNC_EXECUTOR = BEAN_ID_LAZY_TRACE_EXECUTOR;
	public static final String SPANTAG_HEADER_NAMES = "spantag.header.names";
	public static final String STREAM_CONTEXT = "STREAM_CONTEXT";

	public static final String SPAN_TRACE_ID = "spanTraceId";
	public static final String SPAN_ID = "spanId";

	public static final String ORDERID_HDR = "orderId";
	public static final String MESSAGEID_HDR = "messageId";
	public static final String CLIENTID_HDR = "clientId";
	public static final String CORRELATIONID_HDR = "correlationId";
	public static final String EXECUTIONID_HDR = "executionId";
	
	public static final String HDR_REQUIRED_DEFAULT_REASON = "required";
	public static final String HDR_REQUIRED_DEFAULT_LOCATIONTYPE = "header";
	
	public static final String APPNAME_HDR = "appname";
	public static final String APPVERSION_HDR = "appversion";
	
	public static final String ORDERID_HDR_DEFAULT_VALUE = "orderId";
	public static final String MESSAGEID_HDR_DEFAULT_VALUE = "messageId";
	public static final String CALLERID_DEFAULT_VALUE = "callerId";
	

	public static final String SERVICECALL_ID = "serviceCallId";
	
	public static final String CONTEXT_DEFAULT_LOGGING = "NOT ASSIGNED";
	
	public static final String CONTEXT_SERVER_ERROR_LOGGING = "SERVER ERROR";
	
	public static final String CONTEXT_SERVER_HEADER_VALIDATION_LOGGING = "SERVER HEADER VALIDATION ERROR";
	public static final String CONTEXT_MESSAGE_HEADER_VALIDATION_LOGGING = "MESSAGE HEADER VALIDATION ERROR";

	public static final String CONTEXT_SERVER_REQUEST_LOGGING = "SERVER REQUEST";
	public static final String CONTEXT_SERVER_RESPONSE_LOGGING = "SERVER RESPONSE";

	public static final String CONTEXT_CLIENT_REQUEST_LOGGING = "CLIENT REQUEST";
	public static final String CONTEXT_CLIENT_RESPONSE_LOGGING = "CLIENT RESPONSE";

	public static final String CONTEXT_PUBLISHER_MESSAGE_LOGGING = "MESSAGE PUBLISHER";
	public static final String CONTEXT_ASYNC_PUBLISHER_MESSAGE_LOGGING = "ASYNC MESSAGE PUBLISHER";
	public static final String CONTEXT_EVENT_PUBLISHER_MESSAGE_LOGGING = "EVENT MESSAGE PUBLISHER";
	public static final String CONTEXT_SUBSCRIBER_MESSAGE_LOGGING = "MESSAGE SUBSCRIBER";
	public static final String CONTEXT_SUBSCRIBER_MESSAGE_RECEIVED_LOGGING = "MESSAGE SUBSCRIBER RECEIVED";
	public static final String CONTEXT_SUBSCRIBER_MESSAGE_AUTO_ACKNOWLEDGE_LOGGING = "MESSAGE SUBSCRIBER AUTO ACKNOWLEDGED";
	public static final String CONTEXT_SUBSCRIBER_MESSAGE_CLIENT_ACKNOWLEDGE_LOGGING = "MESSAGE SUBSCRIBER CLIENT ACKNOWLEDGED";
	public static final String CONTEXT_SUBSCRIBER_MESSAGE_CLIENT_NACKNOWLEDGE_LOGGING = "MESSAGE SUBSCRIBER CLIENT NACKNOWLEDGED";
	public static final String CONTEXT_SUBSCRIBER_INVOKE_SERVICE_LOGGING = "MESSAGE SUBSCRIBER INVOKE SERVICE";
	public static final String CONTEXT_SUBSCRIBER_EXTMSGINPUT_EVENTLOG_PUBLISHING_LOGGING = "MESSAGE SUBSCRIBER EXTMSGINPUT EVENTLOG PUBLISHING";
	public static final String CONTEXT_SUBSCRIBER_ERROR_MESSAGE_LOGGING = "MESSAGE SUBSCRIBER ERROR";
	public static final String CONTEXT_ORDERERROR_PUBLISHER_MESSAGE_LOGGING = "ORDERERROR MESSAGE PUBLISHER";
	public static final String CONTEXT_EVENTLOG_PUBLISHER_MESSAGE_LOGGING = "EVENTLOG MESSAGE PUBLISHER";
	public static final String CONTEXT_MESSAGE_SUBSCRIBER_RESTART = "MESSAGE SUBSCRIBER RESTART";

	public static final String CONTEXT_MESSAGE_SUBSCRIBER_RESTART_GRACEFUL_SHUTDOWN = "MESSAGE SUBSCRIBER RESTART GRACEFUL SHUTDOWN";
	
	public static final String MSG_DUP_CHK_TYPE_REST="rest";
	public static final String MSG_DUP_CHK_TYPE_SPANNERDB="spannerdb";
	
	public static final String OPERATION_NOT_SUPPORTED_YET="Not supported yet.";
	
	public static final String PROBLEM_ERROR_KEY="error";
	
	public static final String BAD_REQUEST_BODY_ERROR_DEFAULT_REASON="Invalid";
	
	public static final String LOG_LINE_SIZE_EXCEEDED_MESSAGE="Log message size exceeded configured value! Logging with minimal required information! Current log message size in bytes =";
	
	public static final String TOPIC_NAME_SEPARATOR="__";
	public static final int INTERNAL_SERVICE_ERROR_CODE=500;
	
	public static final String MESSAGE_ACKNOWLEDGE_TYPE_IMMEDIATE="immediate";
	public static final String MESSAGE_ACKNOWLEDGE_TYPE_ONCOMPLETION="oncompletion";
	
	public static final String EPF_PAYLOAD_KEY="PAYLOAD";
	public static final String EPF_CONTENT_TYPE_KEY="CONTENT-TYPE";
	public static final String EPF_ORIGIN_KEY="ORIGIN";
	public static final String EPF_REQUEST_URL_KEY="REQUEST-URL";
	public static final String EPF_HTTP_METHOD_KEY="HTTP-METHOD";
	
	public static final String EPF_ORIGIN_REST_KEY="REST";
	public static final String EPF_ORIGIN_PUBSUB_KEY="PUBSUB";
	
	public static final String EXTERNAL_MESSAGE_INPUT_TRANSACTION_ID="EXTERNAL_MESSAGE_INPUT";
	public static final String EXTERNAL_MESSAGE_INPUT_TRANSACTION_DESC="Message received from external system";
	public static final String EXTERNAL_MESSAGE_INPUT_TRANSACTION_CHANNEL_TYPE="INBOUND";
	public static final String EXTERNAL_MESSAGE_INPUT_STATUS_CODE="OK";
	
	public static final String DEFAULT_EXCEPTION_MESSAGE_WHEN_BLANK="EXCEPTION MESSAGE BLANK";
	
	public static final String MSG_PROCESSING_DURATION_TEXT_ORIGIN_PUBSUB="PubSub";
	public static final String MSG_PROCESSING_DURATION_TEXT_ORIGIN_REST="Rest";

	private Constant() {

	}

}