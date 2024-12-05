package com.macys.uop.foundation.core.utils.msg.subscriber;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Class that captures Pub/Sub subscriber information.
 * <br>
 * It is used to list all application subscribers extended from {@link com.macys.uop.foundation.core.utils.msg.subscriber.AbstractStreamSubscriber}.
 * 
 * @see {@link com.macys.uop.foundation.core.utils.msg.subscriber.SubscriberEndpoint#getSubscribers()}
 */
@Data
@NoArgsConstructor
public class SubscriberInfo implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String subscriptionId;
	private String subscriptionName;
	private String subscriptionStatus;
}
