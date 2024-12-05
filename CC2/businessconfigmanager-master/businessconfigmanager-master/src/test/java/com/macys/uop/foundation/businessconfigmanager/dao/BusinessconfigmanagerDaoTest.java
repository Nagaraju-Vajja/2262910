package com.macys.uop.foundation.businessconfigmanager.dao;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.macys.uop.common.businessconfig.model.BusinessConfigResponseV2;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.macys.uop.foundation.core.utils.execution.ServiceContextUtil;
import com.macys.uop.foundation.core.utils.test.TestContextUtil;
import com.macys.uop.common.businessconfig.model.BusinessConfigResponse;
import com.macys.uop.common.businessconfig.model.BusinessConfigResponseList;
import com.macys.uop.foundation.businessconfigmanager.dao.config.TestConfiguration;
import com.macys.uop.foundation.businessconfigmanager.dao.impl.BusinessconfigmanagerDaoImpl;
import com.macys.uop.foundation.businessconfigmanager.mapper.BusinessConfigMapper;
import com.macys.uop.foundation.businessconfigmanager.utils.BusinessconfigmanagerUtil;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestConfiguration.class)
public class BusinessconfigmanagerDaoTest implements TestContextUtil,ServiceContextUtil {

	@Autowired
	private BusinessconfigmanagerDao businessconfigmanagerDao;

	@MockBean
	@Qualifier("createFirestoreBean")
	private Firestore firestore;

	@MockBean
	@Qualifier("businessconfigmanagerUtil")
	private BusinessconfigmanagerUtil businessconfigUtil;

	@MockBean
	@Qualifier("businessConfigMapper")
	private BusinessConfigMapper businessConfigMapper;

	@Spy
	private ApiFuture<QuerySnapshot> querySnapshotApiFuture;

	@Mock
	private QuerySnapshot querySnapshot;

	@MockBean
	private QueryDocumentSnapshot document;

	@Mock
	private CollectionReference reference;
	@Mock
	private DocumentReference documentReference;
	@Mock
	private ApiFuture<WriteResult> writer;

	@Mock
	private Query query;

	@Mock
	private BusinessConfigResponse businessConfigResponse;

	@Mock
	private BusinessConfigResponseList businessConfigResponseList;

	@Before
	public void beforeTest() {
		businessconfigmanagerDao = Mockito
				.spy(new BusinessconfigmanagerDaoImpl(businessconfigUtil, businessConfigMapper));

		initContext();

		HttpHeaders headers = new HttpHeaders();
		headers.add("messageid", "1");
		headers.add("orderid", "2");
		headers.add("clientid", "3");
		headers.add("correlationid", "4");
		getServiceRequestContext().setApplicationName("businessconfigmanager");
		getServiceRequestContext().setHeaders(headers);
		
		ReflectionTestUtils.setField(businessconfigmanagerDao, "collectionName", "businessconfig");
		ReflectionTestUtils.setField(businessconfigmanagerDao, "collectionNameV2", "businessconfig_v2");
		ReflectionTestUtils.setField(businessconfigmanagerDao, "firestore", firestore);
	}

	@After
	public void afterTest() {
		clearContext();
	}

	@Test
	public void testGetConfigByNameSellingDivAndSellingChannel() throws ExecutionException, InterruptedException {
		String configName = "Payment";
		String sellingDivision = "71";
		String sellingChannel = "MCOM";
		when(firestore.collection(anyString())).thenReturn(reference);
		when(reference.whereEqualTo(anyString(), any())).thenReturn(query);
		when(query.whereEqualTo(anyString(), any())).thenReturn(query);
		when(query.whereEqualTo(anyString(), any())).thenReturn(query);
		when(query.get()).thenReturn(querySnapshotApiFuture);
		when(querySnapshotApiFuture.get()).thenReturn(querySnapshot);
		when(querySnapshot.getDocuments()).thenReturn(createDocumentList());
		when(document.getData()).thenReturn(testMap());
		when(businessConfigMapper.convertMapToObject(any())).thenReturn(businessConfigResponse);
		List<BusinessConfigResponse> response = businessconfigmanagerDao
				.getConfigByNameSellingDivAndSellingChannel(configName, sellingDivision, sellingChannel);
		Assert.assertNotNull(response);
	}

	
	@Test
	public void testGetConfigByNameandSellingDivision() throws ExecutionException, InterruptedException {
		String configName = "Payment";
		String sellingDivision = "71";
		when(firestore.collection(anyString())).thenReturn(reference);
		when(reference.whereEqualTo(anyString(), any())).thenReturn(query);
		when(query.whereEqualTo(anyString(), any())).thenReturn(query);
		when(query.get()).thenReturn(querySnapshotApiFuture);
		when(querySnapshotApiFuture.get()).thenReturn(querySnapshot);
		when(querySnapshot.getDocuments()).thenReturn(createDocumentList());
		when(document.getData()).thenReturn(testMap());
		when(businessConfigMapper.convertMapToObject(any())).thenReturn(businessConfigResponse);
		List<BusinessConfigResponse> response = businessconfigmanagerDao
				.getConfigByNameSellingDivAndSellingChannel(configName, sellingDivision, null);
		Assert.assertNotNull(response);
	}
	

	@Test
	public void testGetConfigByNameTypeSellingDivAndSellingChnl() throws ExecutionException, InterruptedException {
		String configName = "Payment";
		String configType = "BankCard";
		String sellingDivision = "71";
		String sellingChannel = "MCOM";
		when(firestore.collection(anyString())).thenReturn(reference);
		when(reference.whereEqualTo(anyString(), any())).thenReturn(query);
		when(query.whereEqualTo(anyString(), any())).thenReturn(query);
		when(query.whereEqualTo(anyString(), any())).thenReturn(query);
		when(query.whereEqualTo(anyString(), any())).thenReturn(query);
		when(query.get()).thenReturn(querySnapshotApiFuture);
		when(querySnapshotApiFuture.get()).thenReturn(querySnapshot);
		when(querySnapshot.getDocuments()).thenReturn(createDocumentList());
		when(document.getData()).thenReturn(testMap());
		when(businessConfigMapper.convertMapToObject(any())).thenReturn(businessConfigResponse);
		Map<String, Object> response = businessconfigmanagerDao.getConfigByNameTypeSellingDivAndSellingChnl(configName,
				configType, sellingDivision, sellingChannel);
		Assert.assertNotNull(response);

	}
	
	@Test
	public void testGetConfigByNameTypeSellingDivAndSellingChnlNull() throws ExecutionException, InterruptedException {
		String configName = "Payment";
		String configType = "BankCard";
		String sellingDivision = "71";
		when(firestore.collection(anyString())).thenReturn(reference);
		when(reference.whereEqualTo(anyString(), any())).thenReturn(query);
		when(query.whereEqualTo(anyString(), any())).thenReturn(query);
		when(query.whereEqualTo(anyString(), any())).thenReturn(query);
		when(query.whereEqualTo(anyString(), any())).thenReturn(query);
		when(query.get()).thenReturn(querySnapshotApiFuture);
		when(querySnapshotApiFuture.get()).thenReturn(querySnapshot);
		when(querySnapshot.getDocuments()).thenReturn(createDocumentList());
		when(document.getData()).thenReturn(testMap());
		when(businessConfigMapper.convertMapToObject(any())).thenReturn(businessConfigResponse);
		Map<String, Object> response = businessconfigmanagerDao.getConfigByNameTypeSellingDivAndSellingChnl(configName,
				configType, sellingDivision, null);
		Assert.assertNotNull(response);

	}
	


	
	@Test
	public void testGetConfigByNameTypeSellingDivAndSellingChnl_Exception()
			throws ExecutionException, InterruptedException {
		String configName = "Payment";
		String configType = "BankCard";
		String sellingDivision = "71";
		String sellingChannel = "MCOM";
		when(firestore.collection(anyString())).thenReturn(reference);
		when(reference.whereEqualTo(anyString(), any())).thenReturn(query);
		when(query.whereEqualTo(anyString(), any())).thenReturn(query);
		when(query.whereEqualTo(anyString(), any())).thenReturn(query);
		when(query.whereEqualTo(anyString(), any())).thenReturn(query);
		when(query.get()).thenReturn(querySnapshotApiFuture);
		when(querySnapshotApiFuture.get()).thenThrow(ExecutionException.class);
		try {
			Map<String, Object> response = businessconfigmanagerDao.getConfigByNameTypeSellingDivAndSellingChnl(
					configName, configType, sellingDivision, sellingChannel);
		} catch (Exception e) {
			Assert.assertNotNull(e);
		}

	}

	@Test
	public void testGetConfigByNameSellingDivAndSellingChannel_Exception()
			throws ExecutionException, InterruptedException {
		String configName = "Payment";
		String sellingDivision = "71";
		String sellingChannel = "MCOM";
		when(firestore.collection(anyString())).thenReturn(reference);
		when(reference.whereEqualTo(anyString(), any())).thenReturn(query);
		when(query.whereEqualTo(anyString(), any())).thenReturn(query);
		when(query.whereEqualTo(anyString(), any())).thenReturn(query);
		when(query.get()).thenReturn(querySnapshotApiFuture);
		when(querySnapshotApiFuture.get()).thenThrow(ExecutionException.class);
		try {
			List<BusinessConfigResponse> response = businessconfigmanagerDao
					.getConfigByNameSellingDivAndSellingChannel(configName, sellingDivision, sellingChannel);
		} catch (Exception e) {
			Assert.assertNotNull(e);
		}
	}

	@Test
	public void testGetConfigByNameV2() throws ExecutionException, InterruptedException {
		String configName = "Payment";
		when(firestore.collection(anyString())).thenReturn(reference);
		when(reference.whereEqualTo(anyString(), any())).thenReturn(query);
		when(query.whereEqualTo(anyString(), any())).thenReturn(query);
		when(query.get()).thenReturn(querySnapshotApiFuture);
		when(querySnapshotApiFuture.get()).thenReturn(querySnapshot);
		when(querySnapshot.getDocuments()).thenReturn(createDocumentList());
		when(document.getData()).thenReturn(testMap());
		when(businessConfigMapper.convertMapToObjectV2(any())).thenReturn(createBusinessConfigResponseV2());
		BusinessConfigResponseV2 response = businessconfigmanagerDao.getConfigByNameV2(configName);
		Assert.assertNotNull(response);
	}
	
	private Map<String, Object> testMap() {
		Map<String, Object> documentData = new HashMap<>();
		documentData.put("configName", "Payment");
		documentData.put("configType", "BankCard");
		documentData.put("createdBy", "Order");
		documentData.put("createdTs", Timestamp.now());
		documentData.put("status", "Active");
		return documentData;
	}

	private Map<String, Object> testMap1() {
		Map<String, Object> documentData = new HashMap<>();
		documentData.put("configName", "Payment");
		documentData.put("configType", "BankCard");
		documentData.put("createdBy", "Order");
		documentData.put("createdTs", Timestamp.now());
		documentData.put("sellingDivision", "71");
		documentData.put("sellingChannel", "MCOM");
		documentData.put("status", "Active");
		return documentData;
	}

	private List<QueryDocumentSnapshot> createDocumentList() {
		List<QueryDocumentSnapshot> queryDocumentSnapshotList = new ArrayList<>();
		QueryDocumentSnapshot queryDocumentSnapshot = mock(QueryDocumentSnapshot.class);
		queryDocumentSnapshotList.add(queryDocumentSnapshot);
		return queryDocumentSnapshotList;
	}

	private BusinessConfigResponseV2 createBusinessConfigResponseV2() {
		BusinessConfigResponseV2 businessConfigResponse = new BusinessConfigResponseV2();
		businessConfigResponse.setConfigName("Payment");
		List<Object> list = new ArrayList<>();
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> map = new HashMap<>();
		Map<String, Object> configDefinationMap = new HashMap<>();
		configDefinationMap.put("SellingChannelType", "SITE");
		configDefinationMap.put("OrderLines[0].FulfillmentType", "STH");
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

}
