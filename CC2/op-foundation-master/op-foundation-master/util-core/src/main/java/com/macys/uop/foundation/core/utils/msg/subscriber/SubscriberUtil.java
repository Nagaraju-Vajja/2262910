package com.macys.uop.foundation.core.utils.msg.subscriber;

import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.gcp.pubsub.support.PubSubTopicUtils;
import org.springframework.util.Assert;

import static com.macys.uop.foundation.core.utils.Constant.TOPIC_NAME_SEPARATOR;

public interface SubscriberUtil {
	
	/**
	 * Extract topic name from request url name
	 * 
	 * @param subscriptionName
	 * 
	 * @return topicName
	 */
	default String extractTopicNameFromRequestURL(String requestURL) {
		Assert.notNull(requestURL, "'requestURL' Name must not be null");
		String subscriptionName=StringUtils.remove(requestURL, "message:");
		return extractTopicNameFromSubscriptionName(subscriptionName);
	}
	
	/**
	 * Extract topic name from subscription name
	 * 
	 * @param subscriptionName
	 * 
	 * @return topicName
	 */
	default String extractTopicNameFromSubscriptionName(String subscriptionName) {
		Assert.notNull(subscriptionName, "'subscriptionName' Name must not be null");
		return StringUtils.substringBetween(subscriptionName, TOPIC_NAME_SEPARATOR, TOPIC_NAME_SEPARATOR);
	}
	
	/**
	 * Method to return fully qualified topic name. Assertion is already taken care by the internal API {@link PubSubTopicUtils#toProjectTopicName}
	 * 
	 * @param topic
	 * @param projectId
	 * 
	 * @return String fully qualified topic name
	 */
	public default String getFullyQualifiedTopicName(String topic, String projectId) {
		return PubSubTopicUtils.toProjectTopicName(topic, projectId).toString();
	}
}
