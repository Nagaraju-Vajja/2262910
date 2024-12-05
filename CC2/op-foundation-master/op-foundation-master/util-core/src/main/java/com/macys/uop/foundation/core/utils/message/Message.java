package com.macys.uop.foundation.core.utils.message;

/**
 * Helps creating generic org.springframework.messaging.Message<T> through builder pattern.
 *
 */
public interface Message
{
	/**
	 * Static method for returning MessageBuilder instance. 
	 * 
	 * @param <T> Type of Payload
	 * 
	 * @return MessageBuilder
	 */
	static <T> MessageBuilder<T> builder() {
        return new MessageBuilder<>();
    }
}
