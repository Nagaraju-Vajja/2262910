package com.macys.uop.foundation.core.utils.msg.subscriber;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

/**
 * The main purpose of this class is to : 
 * <p>
 * <ul>
 * <li>Expose custom Spring Boot Actuator endpoints to manipulate all subscribers extending {@link com.macys.uop.foundation.core.utils.msg.subscriber.AbstractStreamSubscriber}</li>
 * <li>List Subscribers, Example URL : {@linkplain http://host:port/.../actuator/subscribers} GET Method </li>
 * <li>Start Subscriber in Async mode, Example URL : {@linkplain http://host:port/.../actuator/subscribers/c566d035?action=start} POST Method</li>
 * <li>Stop Subscriber in Async mode, Example URL : {@linkplain http://host:port/.../actuator/subscribers/c566d035?action=start} POST Method</li>
 * </ul>
 */
@Component
@Endpoint(id="subscribers")
@RequiredArgsConstructor
public class SubscriberEndpoint {	
	
	private final ApplicationContext ctx;
	
	/**
	 * Returns the list of subscribers. Subscribers should extend from {@link com.macys.uop.foundation.core.utils.msg.subscriber.AbstractStreamSubscriber}
	 * 
	 * @return {@code List<SubscriberInfo>}
	 */
	@ReadOperation
	public List<SubscriberInfo> getSubscribers() {
		List<SubscriberInfo> subscribers=new ArrayList<>();
		
		Map<String,SubscriberServiceApi> subscriberMap=ctx.getBeansOfType(SubscriberServiceApi.class);
		subscriberMap.forEach((k, v) -> {
			SubscriberInfo subscriber=new SubscriberInfo();
			subscriber.setSubscriptionId(v.getSubscriptionId());
			subscriber.setSubscriptionName(v.getSubscriptionName());
			subscriber.setSubscriptionStatus(v.getSubscriberState().name());
			subscribers.add(subscriber);
		});
		return subscribers;
	}
	
	/**
	 * Perform Start and Stop action on specified Subscription Id. 
	 * 
	 * @param arg0 Subscriber Id
	 * @param action start or stop
	 * @return Status String
	 */
	@WriteOperation
    public String performAction(@Selector String arg0, String action) {
		String result=getStatusJson("Request Submitted");
		Map<String,SubscriberServiceApi> subscriberMap=ctx.getBeansOfType(SubscriberServiceApi.class);
		
		Optional<SubscriberServiceApi> optionalSubscriberServiceApi = subscriberMap.entrySet().stream()
				  .filter(map -> map.getValue().getSubscriptionId().equals(arg0))
				  .map(Map.Entry::getValue)
				  .findFirst();
		if(optionalSubscriberServiceApi.isPresent()) {
			SubscriberServiceApi subscriberServiceApi=optionalSubscriberServiceApi.get();
			if("start".equalsIgnoreCase(action)) {
				subscriberServiceApi.startSubscriberAsync();
			} else if("stop".equalsIgnoreCase(action)) {
				subscriberServiceApi.stopSubscriberAsync();
			} else {
				result=getStatusJson("Invalid Action! Operation Aborted!");
			}
			return result;
		} else {
			return getStatusJson("Invalid SubscriptionId");
		}
    }
	
	/**
	 * Returns operation status in Json
	 * 
	 * @param statusDescription String
	 * @return Json String
	 */
	private String getStatusJson(String statusDescription) {
		return "{\"status\":\""+statusDescription+"\"}";
	}
}
