package com.macys.uop.foundation.core.utils.ordererror;

/**
 * Interface contract for publishing {@link OrderErrorMessage} to Pub/Sub topic. 
 *
 */
public interface OrderErrorMessagePublisher {
	/**
	 * Method which publishes {@link OrderErrorMessage} asynchronously 
	 * 
	 * @param orderErrorMessage Of type {@link OrderErrorMessage}
	 */
	public void publishOrderErrorMessage(OrderErrorMessage orderErrorMessage);
}
