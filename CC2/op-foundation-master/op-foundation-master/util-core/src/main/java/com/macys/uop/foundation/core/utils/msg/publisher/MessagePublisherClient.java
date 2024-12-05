package com.macys.uop.foundation.core.utils.msg.publisher;

import java.util.concurrent.ExecutionException;

import org.springframework.messaging.Message;

import com.macys.uop.foundation.core.utils.masking.ApplicationMaskingConfiguration;
import com.macys.uop.foundation.core.utils.masking.IDataMasker;

/**
 * Utility interface for publishing Pub/Sub message. 
 * <br>
 * Implementation class {@link MessagePublisherClientImpl} takes care of the following during message send
 * <br>
 * <ul>
 * <li>Create span</li>
 * <li>Inject appropriate  tracing headers for consumers to create span</li>
 * <li>Log payload </li>
 * <li>Mask payload during logging</li>
 * </ul> 
 * <br>
 * <b>Note :</b> Utility methods throws underlying exceptions directly.
 */
public interface MessagePublisherClient 
{
	
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
	 * @throws InterruptedException
	 * @throws ExecutionException 
	 */
	String sendMessage(String channelName, Message<String> message) throws InterruptedException, ExecutionException;
	
	
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
	 * @return Generated Message Id
	 * 
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	String sendMessage(String channelName, Message<String> message, boolean loggingEnabled) throws InterruptedException, ExecutionException;
	
	
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
	 * @return Generated Message Id
	 * 
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	String sendMessage(String channelName, Message<String> message, IDataMasker dataMasker) throws InterruptedException, ExecutionException;
	
	/**
	 * Utility methods for sending Pub/Sub message. 
	 * 
	 * @param channelName Name of the channel where message will be sent.
	 * @param message Message payload.
	 * @param dataMasker {@link IDataMasker} instance
	 * @param loggingEnabled Method level logging flag.
	 * @return
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	String sendMessage(String channelName, Message<String> message, IDataMasker dataMasker, boolean loggingEnabled) throws InterruptedException, ExecutionException;
	
}
