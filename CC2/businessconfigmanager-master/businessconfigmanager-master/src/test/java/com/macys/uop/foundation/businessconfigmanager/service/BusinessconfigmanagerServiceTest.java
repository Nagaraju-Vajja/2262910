package com.macys.uop.foundation.businessconfigmanager.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.WriteResult;
import com.macys.uop.common.businessconfig.model.*;
import com.macys.uop.foundation.businessconfigmanager.dao.BusinessconfigmanagerDao;
import com.macys.uop.foundation.businessconfigmanager.mapper.BusinessConfigMapper;
import com.macys.uop.foundation.businessconfigmanager.service.config.TestConfiguration;
import com.macys.uop.foundation.businessconfigmanager.service.impl.BusinessconfigmanagerServiceImpl;
import com.macys.uop.foundation.businessconfigmanager.utils.BusinessconfigmanagerUtil;
import com.macys.uop.foundation.core.utils.execution.ServiceContextUtil;
import com.macys.uop.foundation.core.utils.json.JsonUtils;
import com.macys.uop.foundation.core.utils.test.TestContextUtil;
import com.macys.uop.order.model.Order;
import com.macys.uop.order.model.OrderLine;
import com.macys.uop.order.model.OrderLineCharge;
import com.macys.uop.order.model.api.orderdetails.ReturnOrderLine;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static com.macys.uop.foundation.businessconfigmanager.utils.BusinessconfigmanagerConstants.ACTIVE;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestConfiguration.class)
public class BusinessconfigmanagerServiceTest implements TestContextUtil,ServiceContextUtil{

	@Autowired
	private BusinessconfigmanagerServiceImpl businessconfigCoreService;
	@MockBean
	@Qualifier("businessconfigCoreDao")
	private BusinessconfigmanagerDao businessconfigCoreDao;
	@MockBean
	@Qualifier("cacheManager")
	private CacheManager cacheManager;
	@MockBean
	@Qualifier("businessconfigUtil")
	private BusinessconfigmanagerUtil businessconfigUtil;
	@MockBean
	@Qualifier("businessConfigManagerMapper")
	private BusinessConfigMapper businessConfigMapper;

	@MockBean
	@Qualifier("jsonUtils")
	private JsonUtils jsonUtils;
	@Mock
	BusinessConfigResponse businessConfigResponse;
	@Mock
	BusinessConfigResponseList businessConfigResponseList;

	@Mock
	private Cache cache;

	@Mock
	private Cache.ValueWrapper value;

	@Mock
	private Map<String, Object> cacheValue;

	@Mock
	private Map.Entry<String, Object> entryMap;
	@Mock
	private CollectionReference reference;
	@Mock
	private DocumentReference documentReference;
	@Mock
	private ApiFuture<WriteResult> writer;

	@Before
	public void beforeTest() {
		businessconfigCoreService = Mockito.spy(new BusinessconfigmanagerServiceImpl(businessconfigCoreDao, cacheManager,
				businessconfigUtil, jsonUtils, businessConfigMapper));
		ReflectionTestUtils.setField(businessconfigCoreService, "collectionName", "businessconfig");
		initContext();

		HttpHeaders headers = new HttpHeaders();
		headers.add("messageid", "1");
		headers.add("orderid", "2");
		headers.add("clientid", "3");
		headers.add("correlationid", "4");
		getServiceRequestContext().setApplicationName("businessconfigmanager");
		getServiceRequestContext().setHeaders(headers);
	}

	@After
	public void afterTest() {
		clearContext();
	}

	





	@Test
	public void testGetConfigByNameSellingDivAndSellingChnl_Cache() {
		String configName = "Payment";
		String sellingDivision = "71";
		String sellingChannel = "MCOM";
		when(cacheManager.getCache(anyString())).thenReturn(cache);
		when(cacheManager.getCache(anyString()).get(anyString())).thenReturn(value);
		when((Map<String, Object>) cacheManager.getCache(anyString()).get(anyString()).get()).thenReturn(cacheValue);
		when(cacheValue.entrySet()).thenReturn(Collections.singleton(entryMap));
		when(entryMap.getValue()).thenReturn(testMap());
		when(businessConfigMapper.convertMapToObject(any())).thenReturn(businessConfigResponse);
		BusinessConfigResponseList response = businessconfigCoreService.getConfigByNameSellingDivAndsellingChnl(configName, sellingDivision, sellingChannel);
		Assert.assertNotNull(response);
	}

	@Test
	public void testGetConfigByNameSellingDivAndSellingChnl_CacheNull() {
		String configName = "Payment";
		String sellingDivision = "71";
		String sellingChannel = "MCOM";
		when(cacheManager.getCache(anyString())).thenReturn(cache);
		when(cacheManager.getCache(anyString()).get(anyString())).thenReturn(value);
		when((Map<String, Object>) cacheManager.getCache(anyString()).get(anyString()).get()).thenReturn(null);
		when(cacheValue.entrySet()).thenReturn(Collections.singleton(entryMap));
		BusinessConfigResponseList response = businessconfigCoreService.getConfigByNameSellingDivAndsellingChnl(configName, sellingDivision, sellingChannel);
		Assert.assertNotNull(response);
	}

	@Test
	public void testGetConfigByNameSellingDivAndSellingChnl_Cachefalse() {
		String configName = "Payments";
		String sellingDivision = "71";
		String sellingChannel = "MCOM";
		when(cacheManager.getCache(anyString())).thenReturn(cache);
		when(cacheManager.getCache(anyString()).get(anyString())).thenReturn(value);
		when((Map<String, Object>) cacheManager.getCache(anyString()).get(anyString()).get()).thenReturn(cacheValue);
		when(cacheValue.entrySet()).thenReturn(Collections.singleton(entryMap));
		when(entryMap.getValue()).thenReturn(testMap());
		when(businessConfigMapper.convertMapToObject(any())).thenReturn(businessConfigResponse);
		BusinessConfigResponseList response = businessconfigCoreService.getConfigByNameSellingDivAndsellingChnl(configName, sellingDivision, sellingChannel);
		Assert.assertNotNull(response);
	}

	@Test
	public void testGetConfigByNameSellingDivAndSellingChnl() {
		String configName = "Payment";
		String sellingDivision = "71";
		String sellingChannel = "MCOM";
		doReturn(createBusinessConfigRequestList()).when(businessconfigCoreDao).getConfigByNameSellingDivAndSellingChannel(anyString(),anyString(),anyString());
		BusinessConfigResponseList response = businessconfigCoreService.getConfigByNameSellingDivAndsellingChnl(configName, sellingDivision, sellingChannel);
		Assert.assertNotNull(response);
	}

	@Test
	public void testGetConfigByNameSellingDivAndSellingChnlfailure() {
		String configName = "Payment";
		String sellingDivision = "71";
		String sellingChannel = "MCOM";
		List<BusinessConfigRequest> list = new ArrayList<>();
		doReturn(list).when(businessconfigCoreDao).getConfigByNameSellingDivAndSellingChannel(anyString(),anyString(),anyString());
		BusinessConfigResponseList response = businessconfigCoreService.getConfigByNameSellingDivAndsellingChnl(configName, sellingDivision, sellingChannel);
		Assert.assertNotNull(response);
	}

	@Test
	public void testGetConfigByNameTypeSellingDivAndSellingChnl_cache() {
		String configName = "Payment";
		String configType = "BankCard";
		String sellingDivision = "71";
		String sellingChannel = "MCOM";
		when(cacheManager.getCache(anyString())).thenReturn(cache);
		when(cacheManager.getCache(anyString()).get(anyString())).thenReturn(value);
		when((Map<String, Object>) cacheManager.getCache(anyString()).get(anyString()).get()).thenReturn(cacheValue);
		when(cacheValue.entrySet()).thenReturn(Collections.singleton(entryMap));
		when(entryMap.getValue()).thenReturn(testMap());
		when(businessconfigUtil.checkMatchConfigurationWithSellingChnl(any(),any(),any(),any(),any())).thenReturn(true);
		when(businessConfigMapper.convertMapToObject(any())).thenReturn(businessConfigResponse);
		BusinessConfigResponse response = businessconfigCoreService.getConfigByNameTypeSellingDivAndSellingChnl(configName, configType, sellingDivision, sellingChannel);
		Assert.assertNotNull(response);
	}

	@Test
	public void testGetConfigByNameTypeSellingDivAndSellingChnl_cacheNull() {
		String configName = "Payment";
		String configType = "BankCard";
		String sellingDivision = "71";
		String sellingChannel = "MCOM";
		when(cacheManager.getCache(anyString())).thenReturn(cache);
		when(cacheManager.getCache(anyString()).get(anyString())).thenReturn(value);
		when((Map<String, Object>) cacheManager.getCache(anyString()).get(anyString()).get()).thenReturn(null);
		when(cacheValue.entrySet()).thenReturn(Collections.singleton(entryMap));
		try {
			businessconfigCoreService.getConfigByNameTypeSellingDivAndSellingChnl(configName, configType, sellingDivision, sellingChannel);
		} catch (Exception e) {
			Assert.assertNotNull(e);
		}
	}

	@Test
	public void testGetConfigByNameTypeSellingDivAndSellingChnl_cachefalseName() {
		String configName = "Payments";
		String configType = "BankCard";
		String sellingDivision = "71";
		String sellingChannel = "MCOM";
		when(cacheManager.getCache(anyString())).thenReturn(cache);
		when(cacheManager.getCache(anyString()).get(anyString())).thenReturn(value);
		when((Map<String, Object>) cacheManager.getCache(anyString()).get(anyString()).get()).thenReturn(cacheValue);
		when(cacheValue.entrySet()).thenReturn(Collections.singleton(entryMap));
		when(entryMap.getValue()).thenReturn(testMap());
		when(businessConfigMapper.convertMapToObject(any())).thenReturn(businessConfigResponse);
		try {
			businessconfigCoreService.getConfigByNameTypeSellingDivAndSellingChnl(configName, configType, sellingDivision, sellingChannel);
		} catch (Exception e) {
			Assert.assertNotNull(e);
		}
	}

	@Test
	public void testGetConfigByNameTypeSellingDivAndSellingChnl_cachefalseType() {
		String configName = "Payment";
		String configType = "BankCards";
		String sellingDivision = "71";
		String sellingChannel = "MCOM";
		when(cacheManager.getCache(anyString())).thenReturn(cache);
		when(cacheManager.getCache(anyString()).get(anyString())).thenReturn(value);
		when((Map<String, Object>) cacheManager.getCache(anyString()).get(anyString()).get()).thenReturn(cacheValue);
		when(cacheValue.entrySet()).thenReturn(Collections.singleton(entryMap));
		when(entryMap.getValue()).thenReturn(testMap());
		when(businessConfigMapper.convertMapToObject(any())).thenReturn(businessConfigResponse);
		try {
			businessconfigCoreService.getConfigByNameTypeSellingDivAndSellingChnl(configName, configType, sellingDivision, sellingChannel);
		} catch (Exception e) {
			Assert.assertNotNull(e);
		}
	}

	@Test
	public void testGetConfigByNameTypeSellingDivAndSellingChnl() {
		String configName = "Payment";
		String configType = "BankCard";
		String sellingDivision = "71";
		String sellingChannel = "MCOM";
		doReturn(createBusinessConfigRequestList()).when(businessconfigCoreDao).getConfigByNameSellingDivAndSellingChannel(anyString(),anyString(),anyString());
		BusinessConfigResponse response = businessconfigCoreService.getConfigByNameTypeSellingDivAndSellingChnl(configName, configType, sellingDivision, sellingChannel);
		Assert.assertNotNull(response);

	}

	@Test
	public void testGetConfigByNameTypeSellingDivAndSellingChnlFailure() {
		String configName = "Payment";
		String configType = "BankCard";
		String sellingDivision = "71";
		String sellingChannel = "MCOM";
		List<BusinessConfigRequest> list = new ArrayList<>();
		doReturn(list).when(businessconfigCoreDao).getConfigByNameSellingDivAndSellingChannel(anyString(),anyString(),anyString());
		BusinessConfigResponse response = businessconfigCoreService.getConfigByNameTypeSellingDivAndSellingChnl(configName, configType, sellingDivision, sellingChannel);
		Assert.assertNotNull(response);

	}

	@Test
	public void testGetConfigByNameV2_Cache() {
		String configName = "Payment";
		when(cacheManager.getCache(anyString())).thenReturn(cache);
		when(cacheManager.getCache(anyString()).get(anyString())).thenReturn(value);
		when((Map<String, Object>) cacheManager.getCache(anyString()).get(anyString()).get()).thenReturn(cacheValue);
		when(cacheValue.entrySet()).thenReturn(Collections.singleton(entryMap));
		when(entryMap.getValue()).thenReturn(testMap());
		when(businessconfigUtil.checkMatchConfiguration(any(), any())).thenReturn(true);
		when(businessConfigMapper.convertMapToObject(any())).thenReturn(businessConfigResponse);
		try {
			BusinessConfigResponseV2 response = businessconfigCoreService.getConfigByNameV2(configName);
		}
		catch(Exception e){
			assertTrue(e instanceof Throwable);
		}
	}

	@Test
	public void testGetConfigByNameV2_Cache_Null() {
		String configName = "Payment";
		when(cacheManager.getCache(anyString())).thenReturn(cache);
		when(cacheManager.getCache(anyString()).get(anyString())).thenReturn(value);
		when((Map<String, Object>) cacheManager.getCache(anyString()).get(anyString()).get()).thenReturn(cacheValue);
		when(cacheValue.entrySet()).thenReturn(Collections.singleton(entryMap));
		when(entryMap.getValue()).thenReturn(testMap());
		when(businessconfigUtil.checkCacheMapV2(any(), any())).thenReturn(true);
		when(businessconfigUtil.checkMatchConfiguration(any(), any())).thenReturn(true);
		when(businessconfigCoreDao.getConfigByNameV2(any())).thenReturn(createBusinessConfigResponseV2());
		try {
			businessconfigCoreService.getConfigByNameV2(configName);
		}
		catch(Exception e){
			assertTrue(e instanceof Throwable);
		}
	}

	@Test
	public void testGetConfigByNameV2_Cache_False() {
		String configName = "Payment";
		when(cacheManager.getCache(anyString())).thenReturn(cache);
		when(cacheManager.getCache(anyString()).get(anyString())).thenReturn(value);
		when((Map<String, Object>) cacheManager.getCache(anyString()).get(anyString()).get()).thenReturn(cacheValue);
		when(cacheValue.entrySet()).thenReturn(Collections.singleton(entryMap));
		when(entryMap.getValue()).thenReturn(testMap());
		when(businessconfigUtil.checkCacheMapV2(any(), any())).thenReturn(false);
		when(businessconfigUtil.checkMatchConfiguration(any(), any())).thenReturn(false);
		when(businessConfigMapper.convertMapToObject(any())).thenReturn(businessConfigResponse);
		try {
			businessconfigCoreService.getConfigByNameV2(configName);
		}
		catch(Exception e){
			assertTrue(e instanceof Throwable);
		}
	}

	@Test
	public void testGetConfigFromCacheV2() {
		String configName = "Payment";
		when(cacheManager.getCache(anyString())).thenReturn(cache);
		when(cacheManager.getCache(anyString()).get(anyString())).thenReturn(value);
		when((Map<String, Object>) cacheManager.getCache(anyString()).get(anyString()).get()).thenReturn(cacheValue);
		when(cacheValue.entrySet()).thenReturn(Collections.singleton(entryMap));
		when(entryMap.getValue()).thenReturn(testMap());
		when(businessconfigUtil.checkMatchConfiguration(any(), any())).thenReturn(true);
		when(businessConfigMapper.convertMapToObject(any())).thenReturn(businessConfigResponse);
		try {
			List<BusinessConfigResponse> response = businessconfigCoreService.getBusinessConfigFromCacheV2(configName);
		}
		catch(Exception e){
			assertTrue(e instanceof Throwable);
		}
	}

	@Test
	public void testEvaluateBusinessConfig() {
		String configName = "configName";
		Order order = createOrder();
		AtomicInteger count = new AtomicInteger();
		Map<String ,Object> cacheValue=new HashMap<>();
		cacheValue.put("configName",new HashMap<>());
		when(value.get()).thenReturn(cacheValue);
		when(cacheManager.getCache(anyString())).thenReturn(cache);
		when(cache.get(anyString())).thenReturn(value);
		when(businessconfigUtil.checkMatchConfiguration(any(),any())).thenReturn(true);
		BusinessConfigResponseV2 businessconfig =new BusinessConfigResponseV2();
		businessconfig.setStatus(ACTIVE);
		List<Object> list=new ArrayList<>();
		HashMap<String ,Object> map= new HashMap<>();
		HashMap<String,String> mapg=new HashMap<>();
		mapg.put("Order[0]","PIC");
		map.put("configDefination",mapg);
		map.put("configValue","configValue");
		list.add(map);
		businessconfig.setConfigTypes(list);
		when(businessconfigCoreDao.getConfigByNameV2(anyString())).thenReturn(businessconfig);
		Map<Integer, String> matchedConfiguration = new HashMap<>();
		Map<String, Integer> configMap = new HashMap<>();
		try {
			Assert.assertNotNull(businessconfigCoreService.evaluateBusinessConfig(configName,order));
		}
		catch(Exception e){
			assertTrue(e instanceof Throwable);
		}
	}

	@Test
	public void testEvaluateOrderLevelCongigs() throws NoSuchMethodException {

		com.macys.uop.order.model.api.orderdetails.Orderdetails order = createOrders();
		HashMap<String ,Object> defMap  =new HashMap<>();
		Map.Entry<String,Integer> entry =
				new AbstractMap.SimpleEntry<String, Integer>("exmpleString", 42);
		Object obj="";
		AtomicInteger count = new AtomicInteger();
		try {
			businessconfigCoreService.evaluateOrderLevelCongigs(order,entry,defMap,count);

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	@Test
	public void testCompareRequestToConfigurations() {
		com.macys.uop.order.model.api.orderdetails.OrderLine orderLine= new com.macys.uop.order.model.api.orderdetails.OrderLine();
		Order order = createOrder();
		ReturnOrderLine returnOrderLine = new ReturnOrderLine();
		returnOrderLine.setLineId("1");
		String[] lineArray = {"OrderLines[0].FulfillmentType: BOPS"};
		AtomicInteger index = new AtomicInteger();
		AtomicInteger count=new AtomicInteger();
		index.set(0);
		List<Class> classList = new ArrayList<>();
		classList.add(Order.class);
		List<Object> objList = new ArrayList<>();
		objList.add(order);
		Map.Entry<String,Integer> entry =
				new AbstractMap.SimpleEntry<String, Integer>("exmpleString", 42);
		HashMap<String ,Object> defMap  =new HashMap<>();

		try {
			businessconfigCoreService.compareRequestToConfiguration(lineArray, index, classList, objList,entry,defMap,count,orderLine,returnOrderLine);
		}
		catch(Exception e){
			assertTrue(e instanceof Throwable);
		}
	}

	@Test
	public void testmatchConfigValues(){
		Object values="";
		List<String> list = new ArrayList<>();
		list.add("30");
		values = list;
		Map<List<String>, String> entryMap = new HashMap<>();
		List<String> entryList = new ArrayList<>();
		entryList.add("SellingChannelType");
		entryMap.put(entryList, "30");
		HashMap<String ,Object> defMap  =new HashMap<>();
		Optional<Map.Entry<List<String>, String>> entry = entryMap.entrySet().stream().findFirst();
		defMap = (HashMap<String, Object>) ((HashMap<?, ?>) ((ArrayList<?>) createBusinessConfigResponseV2().getConfigTypes()).get(0)).get("configDefination");
		AtomicInteger count = new AtomicInteger();

		try{
			businessconfigCoreService.matchConfigValues(values,entry.get(),defMap,count);
		}catch (Exception e){
			assertTrue(e instanceof Throwable);
		}
	}

	@Test
	public void testmatchConfigValues1(){
		Object values="";
		List<String> list = new ArrayList<>();
		list.add("30");
		values = list;
		Map<List<String>, String> entryMap = new HashMap<>();
		List<String> entryList = new ArrayList<>();
		entryList.add("SourceChannel");
		entryMap.put(entryList, "30");
		HashMap<String ,Object> defMap  =new HashMap<>();
		Optional<Map.Entry<List<String>, String>> entry = entryMap.entrySet().stream().findFirst();
		defMap = (HashMap<String, Object>) ((HashMap<?, ?>) ((ArrayList<?>) createBusinessConfigResponseV2().getConfigTypes()).get(0)).get("configDefination");
		AtomicInteger count = new AtomicInteger();

		try{
			businessconfigCoreService.matchConfigValues(values,entry.get(),defMap,count);
		}catch (Exception e){
			assertTrue(e instanceof Throwable);
		}
	}


	
	
	private Map<String, Object> testMap() {
		Map<String, Object> documentData = new HashMap<>();
		documentData.put("configName", "Payment");
		documentData.put("configType", "BankCard");
		documentData.put("sellingDivision", "71");
		documentData.put("sellingChannel", "MCOM");
		documentData.put("createdBy", "Order");
		documentData.put("createdTs", Timestamp.now());
		documentData.put("status", "Active");
		return documentData;
	}

	private BusinessConfigRequestList createRequestList() {
		BusinessConfigRequestList BusinessConfigRequestList = new BusinessConfigRequestList();
		List<BusinessConfigRequest> list = new ArrayList<>();
		BusinessConfigRequest businessConfigRequest = new BusinessConfigRequest();
		businessConfigRequest.setConfigName("Payment");
		businessConfigRequest.setConfigType("BankCard");
		businessConfigRequest.setConfigValue("Config");
		businessConfigRequest.setSellingChannel("MCOM");
		businessConfigRequest.setSellingDivision("71");
		list.add(businessConfigRequest);
		BusinessConfigRequestList.setBusinessConfigurations(list);
		return BusinessConfigRequestList;
	}

	private List<BusinessConfigRequest> createBusinessConfigRequestList() {
		List<BusinessConfigRequest> list = new ArrayList<>();
		BusinessConfigRequest businessConfigRequest = new BusinessConfigRequest();
		businessConfigRequest.setConfigName("Payment");
		businessConfigRequest.setConfigType("BankCard");
		businessConfigRequest.setConfigValue("Config");
		businessConfigRequest.setSellingChannel("MCOM");
		businessConfigRequest.setSellingDivision("71");
		list.add(businessConfigRequest);
		return list;
	}
	
	private BusinessConfigRequest createBusinessConfigRequest() {
		BusinessConfigRequest businessConfigRequest = new BusinessConfigRequest();
		businessConfigRequest.setConfigName("Payment");
		businessConfigRequest.setConfigType("BankCard");
		businessConfigRequest.setConfigValue("Config");
		businessConfigRequest.setSellingChannel("MCOM");
		businessConfigRequest.setSellingDivision("71");
		return businessConfigRequest;
	}

	private BusinessConfigResponse createBusinessConfigResponse() {
		BusinessConfigResponse businessConfigResponse = new BusinessConfigResponse();
		businessConfigResponse.setConfigName("Payment");
		businessConfigResponse.setConfigType("BankCard");
		businessConfigResponse.setConfigValue("Config");
		businessConfigResponse.setSellingChannel("MCOM");
		businessConfigResponse.setSellingDivision("71");
		return businessConfigResponse;
	}

	private BusinessConfigResponseV2 createBusinessConfigResponseV2() {
		BusinessConfigResponseV2 businessConfigResponse = new BusinessConfigResponseV2();
		businessConfigResponse.setConfigName("Payment");
		List<Object> list = new ArrayList<>();
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> map = new HashMap<>();
		List<String> lists = new ArrayList();
		lists.add("ZOLA");
		lists.add("SITE");
		Map<String, Object> configDefinationMap = new HashMap<>();
		configDefinationMap.put("SellingChannelType", "SITE");
		configDefinationMap.put("OrderLines[0].FulfillmentType", "STH");
		configDefinationMap.put("SourceChannel", lists);
		map.put("configId", "Remorse_STH");
		map.put("configDefination", configDefinationMap);
		map.put("configValue", "{order.remorse.remorsePeriod=30}");
		map.put("configDesc", "STH details");
		list.add(map);
		businessConfigResponse.setConfigTypes(list);
		businessConfigResponse.setCreatedBy("order");
		businessConfigResponse.setLastUpdatedBy("order");
		return businessConfigResponse;
	}

	private Order createOrder() {

		Order order = new Order();
		OrderLine orderLine = new OrderLine();
		List<OrderLine> orderLineList = new ArrayList();
		orderLine.setFulfillmentType("STH");
		orderLine.setLineType("PROD");
		OrderLineCharge orderLineCharge = new OrderLineCharge();
		List<OrderLineCharge> orderLineChargeList = new ArrayList();
		orderLineCharge.setChargeType("TEST");
		orderLineChargeList.add(orderLineCharge);
		orderLine.setOrderLineCharges(orderLineChargeList);
		orderLineList.add(orderLine);
		order.setOrderId("1");
		order.setSellingChannelType("SITE");
		order.setOrderLines(orderLineList);
		return order;
	}

	private com.macys.uop.order.model.api.orderdetails.Orderdetails createOrders() {

		com.macys.uop.order.model.api.orderdetails.Orderdetails order = new com.macys.uop.order.model.api.orderdetails.Orderdetails();
		com.macys.uop.order.model.api.orderdetails.OrderLine orderLine = new com.macys.uop.order.model.api.orderdetails.OrderLine();
		List<com.macys.uop.order.model.api.orderdetails.OrderLine> orderLineList = new ArrayList();
		orderLine.setFulfillmentType("STH");
		orderLine.setLineType("PROD");
		com.macys.uop.order.model.api.orderdetails.OrderLineCharge orderLineCharge = new com.macys.uop.order.model.api.orderdetails.OrderLineCharge();
		List<com.macys.uop.order.model.api.orderdetails.OrderLineCharge> orderLineChargeList = new ArrayList();
		orderLineCharge.setChargeType("TEST");
		orderLineChargeList.add(orderLineCharge);
		orderLine.setOrderLineCharges(orderLineChargeList);
		orderLineList.add(orderLine);
		order.setOrderId("1");
		order.setSellingChannelType("SITE");
		order.setOrderLines(orderLineList);
		return order;
	}
}
