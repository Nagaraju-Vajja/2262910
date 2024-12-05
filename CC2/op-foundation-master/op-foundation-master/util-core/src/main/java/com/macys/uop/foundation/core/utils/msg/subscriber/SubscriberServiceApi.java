package com.macys.uop.foundation.core.utils.msg.subscriber;

import com.google.api.core.ApiService.State;

/**
 * The main purpose of this interface is to provide methods which can be used to provide subscriber information and dynamically start/stop them.
 * <br> 
 * Default implementation of this interface is provided by {@link com.macys.uop.foundation.core.utils.msg.subscriber.AbstractStreamSubscriber} which all subscribers extend from.
 *  
 */
public interface SubscriberServiceApi {
	
	/**
	 * Initiates service startup and returns immediately.
	 */
	void startSubscriberAsync();
	
	/**
	 * Initiates service shutdown and returns immediately.
	 */
	void stopSubscriberAsync();
	
	/**
	 * Is current subscriber running.
	 * 
	 * @return True if running else false.
	 * @throws IllegalArgumentException if {@link com.macys.uop.foundation.core.utils.msg.subscriber.AbstractStreamSubscriber#subscriber} instance is null.
	 */
	boolean isSubscriberRunning();
	
	/**
	 * Returns the current subscriber state. 
	 * 
	 * @return State
	 * @throws IllegalArgumentException if {@link com.macys.uop.foundation.core.utils.msg.subscriber.AbstractStreamSubscriber#subscriber} instance is null.
	 */
	State getSubscriberState();
	
	/**
	 * Return the subscription name the current subscriber is subscribed to.
	 * 
	 * @return Subscription Name
	 * @throws IllegalArgumentException if {@link com.macys.uop.foundation.core.utils.msg.subscriber.AbstractStreamSubscriber#subscriber} instance is null.
	 */
	String getSubscriptionName();
	
	/**
	 * Return the subscription id for the subscriber. It is a unique string which represents a subscriber.
	 * <p> 
	 * The default implementation uses {@code Integer.toHexString(this.getClass().getName().hashCode())} to generate the unique id which can be overridden by implementation class.
	 * @return Unique Subscription Id
	 */
	String getSubscriptionId();
}
