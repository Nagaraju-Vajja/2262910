package com.macys.uop.foundation.messagestore;

import static com.macys.uop.foundation.core.utils.Constant.CLIENTID_HDR;
import static com.macys.uop.foundation.core.utils.Constant.MESSAGEID_HDR;
import static com.macys.uop.foundation.core.utils.Constant.ORDERID_HDR;
import static com.macys.uop.foundation.messagestore.MessagestoreConstants.DELETE_MESSAGESTOREECORD_QUERY;

import org.springframework.cloud.gcp.data.spanner.core.SpannerTemplate;
import org.springframework.stereotype.Component;

import com.google.cloud.spanner.Statement;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class MessageStoreRecordManagerImpl implements IMessageStoreRecordManager {

	private final SpannerTemplate spannerTemplate;
	
	@Override
	public long deleteMessageStoreRecord(String messageId, String orderId, String clientId) {
		
		Statement deleteMessageStore = Statement.newBuilder(DELETE_MESSAGESTOREECORD_QUERY)
				.bind(MESSAGEID_HDR).to(messageId)
				.bind(CLIENTID_HDR).to(clientId)
				.bind(ORDERID_HDR).to(orderId)
				.build();
		
		return spannerTemplate.executeDmlStatement(deleteMessageStore);
	}

}
