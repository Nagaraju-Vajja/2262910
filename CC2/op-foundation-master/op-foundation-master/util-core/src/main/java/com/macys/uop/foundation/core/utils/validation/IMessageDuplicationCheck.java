package com.macys.uop.foundation.core.utils.validation;

import com.google.pubsub.v1.PubsubMessage;

/**
 * Interface contract for {@link PubsubMessage} duplication check.
 * 
 * @see {@link com.macys.uop.foundation.core.utils.validation.DefaultMessageDuplicationCheckServiceImpl}
 */
public interface IMessageDuplicationCheck {
	
	/**
	 * Method to be implemented by {@link IMessageDuplicationCheck} implementation classes to check message duplication.
	 *  
	 * @param message {@link PubsubMessage}
	 * 
	 * @return true if message is duplicate else false
	 */
	boolean isMessageDuplicate(PubsubMessage message);
}
