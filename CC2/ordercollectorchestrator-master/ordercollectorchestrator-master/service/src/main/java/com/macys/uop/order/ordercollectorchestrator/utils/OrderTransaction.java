package com.macys.uop.order.ordercollectorchestrator.utils;

public enum OrderTransaction {

    CREATED("ORDER_CREATED", "Order Created", "ORDER_CREATED", "Created Order Published to OrderSourcing", "1000", "Created"),
    MCHUB_ACK("ORDER_FRAUDRESPONSE", "Fraud confirmation update from McHub(Accept/Declined)", "ORDER_FRAUDACKNOWLEDGE","Ack Message Sent To McHub", null, null),
    ORDER_REQ("ORDER_CREATED", "Order Created", "ORDER_INPUT","Order Request Received From EDI/McHub", null, null),
    ORDER_ACKNOWLEDGE("ORDER_ACKNOWLEDGE", "Order Acknowledged", "ORDER_OUPUT","ACK response for digital checkout orders", null, null),
    ORDER_RECEIVED("ORDER_RECEIVED", "OrderCreate Request Received ", "ORDER_INPUT","Order Request Received From Digital CHECKOUT", null, null);



    private String transactionId;
    private String transactionDescription;
    private String eventReferenceId;
    private String eventReferenceType;
    private String statusCode;
    private String statusDescription;

    /**
     * OrderTransaction
     * @param transactionId
     * @param transactionDescription
     * @param eventReferenceId
     * @param eventReferenceType
     * @param statusCode
     * @param statusDescription
     */
    OrderTransaction(String transactionId, String transactionDescription, String eventReferenceId, String eventReferenceType, String statusCode,
        String statusDescription) {
        this.transactionId = transactionId;
        this.transactionDescription = transactionDescription;
        this.eventReferenceId = eventReferenceId;
        this.eventReferenceType = eventReferenceType;
        this.statusCode = statusCode;
        this.statusDescription = statusDescription;
    }

    /**
     * Get Name By TransactionId
     * @param transactionId
     * @return OrderTransaction
     */
    public static OrderTransaction getNameByTransactionId(String transactionId) {
        for (OrderTransaction e : OrderTransaction.values()) {
            if (e.transactionId.equalsIgnoreCase(transactionId)) {
                return e;
            }
        }
        return null;// not found
    }

    /**
     * Get Name By Status code
     * @param statusCode
     * @return OrderTransaction
     */
    public static OrderTransaction getNameByStatusCode(String statusCode) {
        for (OrderTransaction e : OrderTransaction.values()) {
            if (e.statusCode.equalsIgnoreCase(statusCode)) {
                return e;
            }
        }
        return null;// not found
    }

    /**
     * Get TransactionId
     * @return String
     */
    public String getTransactionId() {
        return transactionId;
    }

    /**
     * Get TransactionDescription
     * @return String
     */
    public String getTransactionDescription() {
        return transactionDescription;
    }

    /**
     * Get EventReferenceId
     * @return String
     */
    public String getEventReferenceId() { return eventReferenceId; }

    /**
     * Get EventReferenceType
     * @return String
     */
    public String getEventReferenceType() { return eventReferenceType; }

    /**
     * Get StatusCode
     * @return String
     */
    public String getStatusCode() {
        return statusCode;
    }

    /**
     * Get StatusDescription
     * @return String
     */
    public String getStatusDescription() {
        return statusDescription;
    }
}
