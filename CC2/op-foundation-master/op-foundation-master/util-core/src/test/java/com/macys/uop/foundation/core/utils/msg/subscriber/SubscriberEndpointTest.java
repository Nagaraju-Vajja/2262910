package com.macys.uop.foundation.core.utils.msg.subscriber;

import static org.mockito.Mockito.mock;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

import com.google.api.core.ApiService.State;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringRunner.class)
public class SubscriberEndpointTest {
	
	@MockBean
	private ApplicationContext ctx;
	
	@Test
	public void testGetSubscribers() {
		SubscriberEndpoint subscriberEndpoint=mock(SubscriberEndpoint.class);
		try {
			Field fieldJsonUtils = subscriberEndpoint.getClass().getSuperclass().getDeclaredField("ctx");
			fieldJsonUtils.setAccessible(true);
			fieldJsonUtils.set(subscriberEndpoint, ctx);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		SubscriberServiceApi subServiceAPI1=mock(SubscriberServiceApi.class);
		SubscriberServiceApi subServiceAPI2=mock(SubscriberServiceApi.class);
		
		Mockito.doReturn("subscriptionId1").when(subServiceAPI1).getSubscriptionId();
		Mockito.doReturn("subscriptionName1").when(subServiceAPI1).getSubscriptionName();
		Mockito.doReturn(State.RUNNING).when(subServiceAPI1).getSubscriberState();
		
		Mockito.doReturn("subscriptionId2").when(subServiceAPI2).getSubscriptionId();
		Mockito.doReturn("subscriptionName2").when(subServiceAPI2).getSubscriptionName();
		Mockito.doReturn(State.RUNNING).when(subServiceAPI2).getSubscriberState();
		
		Map<String,SubscriberServiceApi> subscriberMap=new HashMap<>();
		subscriberMap.put("Subscriber1", subServiceAPI1);
		subscriberMap.put("Subscriber2", subServiceAPI2);
		
		Mockito.doReturn(subscriberMap).when(ctx).getBeansOfType(SubscriberServiceApi.class);
		
		Mockito.doCallRealMethod().when(subscriberEndpoint).getSubscribers();
		
		List<SubscriberInfo> result= subscriberEndpoint.getSubscribers();
		
		Assert.assertEquals(2, result.size());
	}
	
	@Test
	public void testPerformActionStart() {
		SubscriberEndpoint subscriberEndpoint=mock(SubscriberEndpoint.class);
		try {
			Field fieldJsonUtils = subscriberEndpoint.getClass().getSuperclass().getDeclaredField("ctx");
			fieldJsonUtils.setAccessible(true);
			fieldJsonUtils.set(subscriberEndpoint, ctx);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		SubscriberServiceApi subServiceAPI1=mock(SubscriberServiceApi.class);
		SubscriberServiceApi subServiceAPI2=mock(SubscriberServiceApi.class);
		
		Mockito.doReturn("subscriptionId1").when(subServiceAPI1).getSubscriptionId();
		Mockito.doReturn("subscriptionName1").when(subServiceAPI1).getSubscriptionName();
		Mockito.doReturn(State.RUNNING).when(subServiceAPI1).getSubscriberState();
		Mockito.doNothing().when(subServiceAPI1).startSubscriberAsync();
		Mockito.doNothing().when(subServiceAPI1).stopSubscriberAsync();
		
		Mockito.doReturn("subscriptionId2").when(subServiceAPI2).getSubscriptionId();
		Mockito.doReturn("subscriptionName2").when(subServiceAPI2).getSubscriptionName();
		Mockito.doReturn(State.RUNNING).when(subServiceAPI2).getSubscriberState();
		Mockito.doNothing().when(subServiceAPI2).startSubscriberAsync();
		Mockito.doNothing().when(subServiceAPI2).stopSubscriberAsync();
		
		Map<String,SubscriberServiceApi> subscriberMap=new HashMap<>();
		subscriberMap.put("Subscriber1", subServiceAPI1);
		subscriberMap.put("Subscriber2", subServiceAPI2);
		
		Mockito.doReturn(subscriberMap).when(ctx).getBeansOfType(SubscriberServiceApi.class);
		
		Mockito.doCallRealMethod().when(subscriberEndpoint).performAction("subscriptionId1", "start");
		String startResult= subscriberEndpoint.performAction("subscriptionId1", "start");
		
		Assert.assertNotEquals(startResult, "Request Submitted");
	}
	
	@Test
	public void testPerformActionStop() {
		SubscriberEndpoint subscriberEndpoint=mock(SubscriberEndpoint.class);
		try {
			Field fieldJsonUtils = subscriberEndpoint.getClass().getSuperclass().getDeclaredField("ctx");
			fieldJsonUtils.setAccessible(true);
			fieldJsonUtils.set(subscriberEndpoint, ctx);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		SubscriberServiceApi subServiceAPI1=mock(SubscriberServiceApi.class);
		SubscriberServiceApi subServiceAPI2=mock(SubscriberServiceApi.class);
		
		Mockito.doReturn("subscriptionId1").when(subServiceAPI1).getSubscriptionId();
		Mockito.doReturn("subscriptionName1").when(subServiceAPI1).getSubscriptionName();
		Mockito.doReturn(State.RUNNING).when(subServiceAPI1).getSubscriberState();
		Mockito.doNothing().when(subServiceAPI1).startSubscriberAsync();
		Mockito.doNothing().when(subServiceAPI1).stopSubscriberAsync();
		
		Mockito.doReturn("subscriptionId2").when(subServiceAPI2).getSubscriptionId();
		Mockito.doReturn("subscriptionName2").when(subServiceAPI2).getSubscriptionName();
		Mockito.doReturn(State.RUNNING).when(subServiceAPI2).getSubscriberState();
		Mockito.doNothing().when(subServiceAPI2).startSubscriberAsync();
		Mockito.doNothing().when(subServiceAPI2).stopSubscriberAsync();
		
		Map<String,SubscriberServiceApi> subscriberMap=new HashMap<>();
		subscriberMap.put("Subscriber1", subServiceAPI1);
		subscriberMap.put("Subscriber2", subServiceAPI2);
		
		Mockito.doReturn(subscriberMap).when(ctx).getBeansOfType(SubscriberServiceApi.class);
		
		Mockito.doCallRealMethod().when(subscriberEndpoint).performAction("subscriptionId2", "stop");
		String stopResult= subscriberEndpoint.performAction("subscriptionId2", "stop");
		
		Assert.assertNotEquals(stopResult, "Request Submitted");
	}
	
	@Test
	public void testPerformActionInvalidAction() {
		SubscriberEndpoint subscriberEndpoint=mock(SubscriberEndpoint.class);
		try {
			Field fieldJsonUtils = subscriberEndpoint.getClass().getSuperclass().getDeclaredField("ctx");
			fieldJsonUtils.setAccessible(true);
			fieldJsonUtils.set(subscriberEndpoint, ctx);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		SubscriberServiceApi subServiceAPI1=mock(SubscriberServiceApi.class);
		SubscriberServiceApi subServiceAPI2=mock(SubscriberServiceApi.class);
		
		Mockito.doReturn("subscriptionId1").when(subServiceAPI1).getSubscriptionId();
		Mockito.doReturn("subscriptionName1").when(subServiceAPI1).getSubscriptionName();
		Mockito.doReturn(State.RUNNING).when(subServiceAPI1).getSubscriberState();
		Mockito.doNothing().when(subServiceAPI1).startSubscriberAsync();
		Mockito.doNothing().when(subServiceAPI1).stopSubscriberAsync();
		
		Mockito.doReturn("subscriptionId2").when(subServiceAPI2).getSubscriptionId();
		Mockito.doReturn("subscriptionName2").when(subServiceAPI2).getSubscriptionName();
		Mockito.doReturn(State.RUNNING).when(subServiceAPI2).getSubscriberState();
		Mockito.doNothing().when(subServiceAPI2).startSubscriberAsync();
		Mockito.doNothing().when(subServiceAPI2).stopSubscriberAsync();
		
		Map<String,SubscriberServiceApi> subscriberMap=new HashMap<>();
		subscriberMap.put("Subscriber1", subServiceAPI1);
		subscriberMap.put("Subscriber2", subServiceAPI2);
		
		Mockito.doReturn(subscriberMap).when(ctx).getBeansOfType(SubscriberServiceApi.class);
		
		Mockito.doCallRealMethod().when(subscriberEndpoint).performAction("subscriptionId2", "run");
		String invalidResult= subscriberEndpoint.performAction("subscriptionId2", "run");
		
		Assert.assertEquals(invalidResult, getStatusJson("Invalid Action! Operation Aborted!"));
	}
	
	@Test
	public void testPerformActionInvalidSubscription() {
		SubscriberEndpoint subscriberEndpoint=mock(SubscriberEndpoint.class);
		try {
			Field fieldJsonUtils = subscriberEndpoint.getClass().getSuperclass().getDeclaredField("ctx");
			fieldJsonUtils.setAccessible(true);
			fieldJsonUtils.set(subscriberEndpoint, ctx);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		SubscriberServiceApi subServiceAPI1=mock(SubscriberServiceApi.class);
		SubscriberServiceApi subServiceAPI2=mock(SubscriberServiceApi.class);
		
		Mockito.doReturn("subscriptionId1").when(subServiceAPI1).getSubscriptionId();
		Mockito.doReturn("subscriptionName1").when(subServiceAPI1).getSubscriptionName();
		Mockito.doReturn(State.RUNNING).when(subServiceAPI1).getSubscriberState();
		Mockito.doNothing().when(subServiceAPI1).startSubscriberAsync();
		Mockito.doNothing().when(subServiceAPI1).stopSubscriberAsync();
		
		Mockito.doReturn("subscriptionId2").when(subServiceAPI2).getSubscriptionId();
		Mockito.doReturn("subscriptionName2").when(subServiceAPI2).getSubscriptionName();
		Mockito.doReturn(State.RUNNING).when(subServiceAPI2).getSubscriberState();
		Mockito.doNothing().when(subServiceAPI2).startSubscriberAsync();
		Mockito.doNothing().when(subServiceAPI2).stopSubscriberAsync();
		
		Map<String,SubscriberServiceApi> subscriberMap=new HashMap<>();
		subscriberMap.put("Subscriber1", subServiceAPI1);
		subscriberMap.put("Subscriber2", subServiceAPI2);
		
		Mockito.doReturn(subscriberMap).when(ctx).getBeansOfType(SubscriberServiceApi.class);
		
		Mockito.doCallRealMethod().when(subscriberEndpoint).performAction("xxx", "run");
		String invalidResult= subscriberEndpoint.performAction("xxx", "run");
		
		Assert.assertEquals(invalidResult, getStatusJson("Invalid SubscriptionId"));
	}
	
	private String getStatusJson(String statusDescription) {
		return "{\"status\":\""+statusDescription+"\"}";
	}
	
	
}
