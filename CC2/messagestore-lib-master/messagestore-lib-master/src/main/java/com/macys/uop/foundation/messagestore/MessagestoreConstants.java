package com.macys.uop.foundation.messagestore;

/**
 * Class that captures messagestore-lib related constants
 *
 */
public class MessagestoreConstants {

    public static final String ALREADY_EXISTS = "ALREADY_EXISTS";
    
    public static final String DELETE_MESSAGESTOREECORD_QUERY = "DELETE from messagestore where messageId = @messageId and orderId = @orderId and clientId = @clientId";

    private MessagestoreConstants(){

    }
}
