package com.macys.uop.foundation.core.utils.msg.publisher;

import org.springframework.messaging.Message;

import com.macys.uop.foundation.core.utils.masking.ApplicationMaskingConfiguration;
import com.macys.uop.foundation.core.utils.masking.IDataMasker;

/**
 * Utility interface for publishing Pub/Sub message asynchronously. 
 * <br>
 * Implementation class {@link AsyncMessagePublisherClient} takes care of the following during message send
 * <br>
 * <ul>
 * <li>Channel name and message payload is mandatory. Assert failure exception will be thrown</li>
 * <li>Mask payload during logging</li>
 * <li>Asynchronous message publish happens through 'messagePublishExecutor' bean.Refer {@link MessagePublisherClientConfiguration} for thread pool customizations. 
 * </li>
 * <li>Message sending is wrapped by Resilience4j Retry. By default, in case of message sending failure, retry will happen 3 times with 2 second gap.
 * </li>
 * <li>After all retries exhausted, fallback method will be invoked, which will print message 'All retry attempts failed publishing message. Critical Error. Contact Administrator!'</li>
 * <li></li>
 * <br>
 */
public interface AsyncMessagePublisherClient {
	
	/**
	 * Utility methods for sending Pub/Sub message. 
	 * <br>
	 * During message sending if "messaging.publisher.logging.enabled" flag is enabled then message is logged with foundation logger.
	 * <br>
	 * Default platform provided Json data masker {@link ApplicationMaskingConfiguration#getDefaultJsonMaskerInstance()} is used.
	 * 
	 * @param channelName Name of the channel where message will be sent.
	 * @param message Message payload.
	 * @return Generated Message Id
	 * 
	 */
	void sendMessage(String channelName, Message<String> message);
	
	
	/**
	 * Utility methods for sending Pub/Sub message. 
	 * <br>
	 * This method is useful in controlling method level message logging.This works in association with  "messaging.publisher.logging.enabled" flag.
	 * <br>
	 * Default platform provided Json data masker {@link ApplicationMaskingConfiguration#getDefaultJsonMaskerInstance()} is used.
	 * 
	 * @param channelName Name of the channel where message will be sent.
	 * @param message Message payload.
	 * @param loggingEnabled Method level logging flag.
	 * 
	 */
	void sendMessage(String channelName, Message<String> message, boolean loggingEnabled) ;
	
	
	/**
	 * Utility methods for sending Pub/Sub message. 
	 * <br>
	 * During message sending if "messaging.publisher.logging.enabled" flag is enabled then message is logged with foundation logger.
	 * <br>
	 * This method facilitates sending custom {@link IDataMasker} instance
	 * 
	 * @param channelName Name of the channel where message will be sent.
	 * @param message Message payload.
	 * @param dataMasker {@link IDataMasker} instance
	 * 
	 */
	void sendMessage(String channelName, Message<String> message, IDataMasker dataMasker) ;
	
	/**
	 * Utility methods for sending Pub/Sub message. 
	 * 
	 * @param channelName Name of the channel where message will be sent.
	 * @param message Message payload.
	 * @param dataMasker {@link IDataMasker} instance
	 * @param loggingEnabled Method level logging flag.
	 * 
	 */
	void sendMessage(String channelName, Message<String> message, IDataMasker dataMasker, boolean loggingEnabled);
}
