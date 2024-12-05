package com.macys.uop.foundation.core.utils.validation;

import org.springframework.stereotype.Service;

import com.google.pubsub.v1.PubsubMessage;

/**
 * This is the default implementation provided by foundation to check message duplication.
 * 
 */
@Service
public class DefaultMessageDuplicationCheckServiceImpl implements IMessageDuplicationCheck {
	
	/**
	 * This method throws {@link UnsupportedOperationException} exception.
	 * <br>
	 * Dependency on "messagestore-lib" library should be provided which contains the concrete implementation of {@link IMessageDuplicationCheck}
	 */
	@Override
	public boolean isMessageDuplicate(PubsubMessage message) {
		throw new UnsupportedOperationException("Concrete implementation for IMessageDuplicationCheck#isMessageDuplicate not found!");
	}
}
