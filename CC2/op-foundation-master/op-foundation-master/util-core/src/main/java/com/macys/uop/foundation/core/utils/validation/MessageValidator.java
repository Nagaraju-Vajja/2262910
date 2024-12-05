package com.macys.uop.foundation.core.utils.validation;

import com.google.pubsub.v1.PubsubMessage;

/**
 * Interface contract to validate an incoming Pub/Sub message.
 * 
 * @see {@link com.macys.uop.foundation.core.utils.validation.MessageValidatorImpl}
 */
public interface MessageValidator {
	
	/**
	 * Method to be implemented by {@link MessageValidator} implementation classes to validate {@link PubsubMessage}.
	 * <br>
	 * The purpose of this method is to wrap both message header validation and duplicate check at one place for easier code maintenance.
	 * <br>
	 * Message header validation check can be disabled through setting environment property <b>default.mandatory.header.checking.enabled</b>
	 * <br>
	 * Message duplication check can be disabled through setting environment property <b>message.duplicate.checking.enabled</b>
	 * 
	 * @param message {@link PubsubMessage}
	 * 
	 * @return true if message is valid. It passes both message header validation check as well as message duplication check.
	 */
	boolean validateMessage(PubsubMessage message);
}
