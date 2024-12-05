package com.macys.uop.foundation.messagestore;

import static com.macys.uop.foundation.core.utils.Constant.CLIENTID_HDR;
import static com.macys.uop.foundation.core.utils.Constant.MESSAGEID_HDR;
import static com.macys.uop.foundation.core.utils.Constant.ORDERID_HDR;

import java.util.Map;
import java.util.UUID;

import org.springframework.cloud.gcp.data.spanner.core.SpannerTemplate;
import org.springframework.stereotype.Component;

import com.google.cloud.spanner.SpannerException;
import com.macys.uop.foundation.core.utils.validation.ISpannerDBBasedDuplicationCheck;

import lombok.RequiredArgsConstructor;

/**
 * Default implementation provided by foundation for direct SpannerDB based message duplication check
 *
 */
@Component
@RequiredArgsConstructor
public class SpannerDBBasedDuplicationCheckImpl implements ISpannerDBBasedDuplicationCheck {
	
	private final SpannerTemplate spannerTemplate;

	/** 
	 * {@link ISpannerDBBasedDuplicationCheck#isDuplicate(String, Map)}
	 * <br>
	 * This method returns true if record exists else return false.
	 * <br>
	 * In case of any insertion issue apart from {@link MessagestoreConstants#ALREADY_EXISTS} , method throws {@link SpannerException} .
	 */
	@Override
	public boolean isDuplicate(String payload, Map<String, String> headers) {
		boolean isDuplicateResult=false;
		
		MessageStore entity = new MessageStore();
		com.google.cloud.Timestamp time = com.google.cloud.Timestamp.now();
	    entity.setMessageStorePk(UUID.randomUUID().toString());
	    entity.setClientId(headers.get(CLIENTID_HDR));
	    entity.setMessage(payload);
	    entity.setMessageId(headers.get(MESSAGEID_HDR));
	    entity.setOrderId(headers.get(ORDERID_HDR)); 
	    entity.setCreatedBy(headers.get(CLIENTID_HDR));
	    entity.setLastUpdatedBy(headers.get(CLIENTID_HDR));
	    entity.setCreatedTs(time);
	    entity.setLastUpdatedTs(time);
	    
	    try {
	      spannerTemplate.insert(entity);
	    } catch (SpannerException spannerExp) {
	      if (spannerExp.getErrorCode().name().equals(MessagestoreConstants.ALREADY_EXISTS)) {
	    	  isDuplicateResult=true;
	      }else{
	        throw spannerExp;
	      }
	    }
		
		return isDuplicateResult;
	}

}
