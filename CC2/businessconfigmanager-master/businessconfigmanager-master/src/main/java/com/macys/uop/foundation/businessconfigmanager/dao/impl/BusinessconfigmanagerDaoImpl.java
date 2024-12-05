package com.macys.uop.foundation.businessconfigmanager.dao.impl;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import com.google.cloud.firestore.*;
import com.macys.uop.common.businessconfig.model.BusinessConfigResponseV2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.common.collect.Lists;

import com.macys.uop.foundation.businessconfigmanager.dao.BusinessconfigmanagerDao;
import com.macys.uop.foundation.businessconfigmanager.mapper.BusinessConfigMapper;
import com.macys.uop.foundation.businessconfigmanager.utils.BusinessconfigmanagerConstants;
import com.macys.uop.foundation.businessconfigmanager.utils.BusinessconfigmanagerUtil;
import com.macys.uop.foundation.core.utils.exception.ProblemUtil;
import com.macys.uop.foundation.core.utils.logging.LoggingUtil;
import com.macys.uop.common.businessconfig.model.BusinessConfigResponse;


import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BusinessconfigmanagerDaoImpl implements BusinessconfigmanagerDao, LoggingUtil, ProblemUtil {

	
	private final BusinessconfigmanagerUtil businessconfigmanagerUtil;
	private final BusinessConfigMapper businessConfigManagerMapper;

	private Firestore firestore = null;

	@Value("${businessconfigmanager.businessconfig.collection.name}")
	private String collectionName;

	@Value("${businessconfigmanager.businessconfig.collection.name.v2:businessconfig_v2}")
	private String collectionNameV2;

	@Value("${firebase.credential.path}")
	private String jsonPath;

	/**
	 * Firestore Bean creation
	 *
	 * @return Firestore
	 */
	@Bean(name = "createFirestoreBean")
	public Firestore createFirestoreBean() {
		GoogleCredentials credentials = null;
		try {
			credentials = GoogleCredentials.fromStream(new FileInputStream(jsonPath))
					.createScoped(Lists.newArrayList("https://www.googleapis.com/auth/cloud-platform"));
			firestore = FirestoreOptions.newBuilder().setCredentials(credentials).build().getService();
		} catch (IOException e) {
			businessconfigmanagerUtil.buildError(e, BusinessconfigmanagerConstants.CONTEXT_FIRESTORE);
		}
		return firestore;
	}


	/**
	 * This Method will give List of BusinessConfigResponse based on below parameters
	 * 
	 * @param configName
	 * @param sellingDivision
	 * @param sellingChannel
	 * @return List<BusinessConfigResponse>
	 */
	@Override
	public List<BusinessConfigResponse> getConfigByNameSellingDivAndSellingChannel(String configName,
			String sellingDivision, String sellingChannel) {
		
		if (!BusinessconfigmanagerUtil.isNullOrEmpty(sellingChannel)) {
			sellingChannel = sellingChannel.toLowerCase();
		}
		Query query;
		CollectionReference configurations = firestore.collection(collectionName);
		query = configurations.whereEqualTo(BusinessconfigmanagerConstants.CONFIG_NAME, configName.toLowerCase())
				.whereEqualTo(BusinessconfigmanagerConstants.SELLING_DIVISION, sellingDivision)
				.whereEqualTo(BusinessconfigmanagerConstants.SELLING_CHANNEL, sellingChannel);

		ApiFuture<QuerySnapshot> querySnapshot = query.get();
		List<BusinessConfigResponse> responseList = new ArrayList<>();
		try {
			querySnapshot.get().getDocuments().stream()
					.forEach(document -> responseList.add(businessConfigManagerMapper.convertMapToObject(document.getData())));
		} catch (InterruptedException | ExecutionException e) {
			throw businessconfigmanagerUtil.buildAndThrowError(e, BusinessconfigmanagerConstants.CONTEXT_BYNAMESELLINGDIVANDSELLINGCHNL);
		}
		return responseList;
	}

	/**
	 * This Method will give Business configuration based on below parameters
	 * 
	 * @param configName
	 * @param configType
	 * @param sellingDivision
	 * @param sellingChannel
	 * @return Map<String, Object>
	 */
	@Override
	public Map<String, Object> getConfigByNameTypeSellingDivAndSellingChnl(String configName, String configType,
			String sellingDivision, String sellingChannel) {

		if (!BusinessconfigmanagerUtil.isNullOrEmpty(sellingChannel)) {
			sellingChannel = sellingChannel.toLowerCase();
		}
		CollectionReference configurations = firestore.collection(collectionName);
		Query query = configurations.whereEqualTo(BusinessconfigmanagerConstants.CONFIG_NAME, configName.toLowerCase())
				.whereEqualTo(BusinessconfigmanagerConstants.CONFIG_TYPE, configType.toLowerCase())
				.whereEqualTo(BusinessconfigmanagerConstants.SELLING_DIVISION, sellingDivision)
				.whereEqualTo(BusinessconfigmanagerConstants.SELLING_CHANNEL, sellingChannel);
		ApiFuture<QuerySnapshot> querySnapshot = query.get();
		Map<String, Object> configValue = new HashMap<>();
		try {
			querySnapshot.get().getDocuments().stream().forEach(document -> configValue.putAll(document.getData()));
		} catch (InterruptedException | ExecutionException e) {
			throw businessconfigmanagerUtil.buildAndThrowError(e,
					BusinessconfigmanagerConstants.CONTEXT_BYNAMETYPESELLINGDIVISIONANDSELLINGCHANNEL);
		}
		return configValue;
	}

	/**
	 * This method will give all businessconfigs based on configName
	 *
	 * @param configName name of the businessconfig
	 * @return BusinessConfigResponse response object
	 */
	@Override
	public BusinessConfigResponseV2 getConfigByNameV2(String configName) {

		Query query;
		BusinessConfigResponseV2 businessConfigResponse = new BusinessConfigResponseV2();
		CollectionReference configurations = firestore.collection(collectionNameV2);
		if (!BusinessconfigmanagerUtil.isNullOrEmpty(configName)) {
			query = configurations.whereEqualTo(BusinessconfigmanagerConstants.CONFIG_NAME, configName.toLowerCase());
			ApiFuture<QuerySnapshot> querySnapshot = query.get();
			try {
				List<QueryDocumentSnapshot> value = querySnapshot.get().getDocuments();
				if(!value.isEmpty()) {
					businessConfigResponse = businessConfigManagerMapper.convertMapToObjectV2(value.get(0).getData());
				}
			} catch (InterruptedException | ExecutionException e) {
				throw businessconfigmanagerUtil.buildAndThrowError(e, BusinessconfigmanagerConstants.CONTEXT_BYNAME_V2_DAO);
			}
		}
		return businessConfigResponse;
	}
}
