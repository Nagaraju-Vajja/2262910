package com.macys.uop.foundation.core.utils.validation;

import com.google.pubsub.v1.PubsubMessage;

/**
 * Interface contract for validating {@link PubsubMessage} headers.
 * <p>
 * 
 * @see {@link com.macys.uop.foundation.core.utils.validation.MessageHeaderCheckServiceImpl}
 */
public interface IMessageHeaderCheck {
	
	/**
	 * Method to be implemented by {@link IMessageHeaderCheck} implementation classes to validate {@link PubsubMessage} headers.
	 * <br>
	 * In case of validation failure, this method should :
	 * <ul>
	 * <li>Populate {@link com.macys.uop.foundation.core.utils.exception.Error} information</li>
	 * <li>Log error</li>
	 * <li>Create and throw {@link org.zalando.problem.ThrowableProblem}</li>
	 * </ul> 
	 * @param message {@link PubsubMessage}
	 */
	void validateMessageHeaders(PubsubMessage message);
}
