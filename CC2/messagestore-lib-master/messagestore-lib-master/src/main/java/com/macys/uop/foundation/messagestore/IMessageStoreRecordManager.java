package com.macys.uop.foundation.messagestore;

public interface IMessageStoreRecordManager {
	
	long deleteMessageStoreRecord(String messageId, String orderId, String clientId);
}
