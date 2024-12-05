package com.macys.uop.order.ordercollectorchestrator.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import com.google.cloud.firestore.DocumentChange;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.ListenerRegistration;
import com.macys.uop.foundation.businessconfigmanager.service.BusinessconfigmanagerService;
import com.macys.uop.foundation.businessconfigmanager.utils.BusinessconfigmanagerConstants;
import com.macys.uop.foundation.core.utils.json.JsonUtils;
import com.macys.uop.foundation.core.utils.logging.LoggingUtil;
import com.macys.uop.order.model.Order;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class BusinessConfigManagerUtil implements LoggingUtil {

    @Value("${businessconfigmanager.businessconfig.collection.name}")
    private String collectionName;

    @Value("${businessconfigmanager.businessconfig.collection.name.v2}")
    private String collectionNameV2;

    @Value("${firebase.credential.path}")
    private String jsonPath;

    private final CacheManager cacheManager;

    private final JsonUtils jsonUtils;

    private final BusinessconfigmanagerService businessconfigmanagerService;

    private final Firestore firestore;

    private List<ListenerRegistration> listenerRegistrations;

    private List<ListenerRegistration> listenerRegistrationsV2;

    @PostConstruct
    public void init() {
        log.info("adding BusinessConfigManagerUtil firestore listeners");
        listenerRegistrations = addListenerToCollection();
        log.info("adding BusinessConfigManagerUtil V2 firestore listeners");
        listenerRegistrationsV2 = addListenerToCollectionV2();
    }

    @PreDestroy
    public void destroy() {
        log.info("removing {} BusinessConfigManagerUtil firestore listeners", listenerRegistrations.size());
        listenerRegistrations.forEach(ListenerRegistration::remove);
        log.info("removing {} BusinessConfigManagerUtil V2 firestore listeners", listenerRegistrationsV2.size());
        listenerRegistrationsV2.forEach(ListenerRegistration::remove);
    }

    private List<ListenerRegistration> addListenerToCollection() {
        final List<ListenerRegistration> listenerRegistrations = new ArrayList<>();

        firestore.listCollections().forEach(collectionReference -> {
            if (collectionReference.getId().equalsIgnoreCase(collectionName)) {
                ListenerRegistration listenerRegistration = collectionReference.addSnapshotListener((snapshots, error) -> {
                    Map<String, Object> configList = new HashMap<>();
                    for (DocumentChange dc : snapshots.getDocumentChanges()) {
                        if (dc.getType() == DocumentChange.Type.ADDED) {
                            if (cacheManager.getCache(BusinessconfigmanagerConstants.CACHE_NAME) != null
                                    && cacheManager.getCache(BusinessconfigmanagerConstants.CACHE_NAME).get(BusinessconfigmanagerConstants.CONFIGLIST) != null) {
                                configList = (Map<String, Object>) cacheManager.getCache(BusinessconfigmanagerConstants.CACHE_NAME)
                                        .get(BusinessconfigmanagerConstants.CONFIGLIST).get();
                            }
                            configList.put(dc.getDocument().getData().get(BusinessconfigmanagerConstants.CONFIG_NAME) + "_"
                                            + dc.getDocument().getData().get(BusinessconfigmanagerConstants.CONFIG_TYPE) + "_"
                                            + dc.getDocument().getData().get(BusinessconfigmanagerConstants.SELLING_DIVISION) + "_"
                                            + dc.getDocument().getData().get(BusinessconfigmanagerConstants.SELLING_CHANNEL),
                                    dc.getDocument().getData());
                            cacheManager.getCache(BusinessconfigmanagerConstants.CACHE_NAME).put(BusinessconfigmanagerConstants.CONFIGLIST, configList);
                        }
                        if (dc.getType() == DocumentChange.Type.MODIFIED) {
                            configList = (Map<String, Object>) cacheManager.getCache(BusinessconfigmanagerConstants.CACHE_NAME)
                                    .get(BusinessconfigmanagerConstants.CONFIGLIST).get();
                            configList.put(dc.getDocument().getData().get(BusinessconfigmanagerConstants.CONFIG_NAME) + "_"
                                    + dc.getDocument().getData().get(BusinessconfigmanagerConstants.CONFIG_TYPE) + "_"
                                    + dc.getDocument().getData().get(BusinessconfigmanagerConstants.SELLING_DIVISION) + "_"
                                    + dc.getDocument().getData().get(BusinessconfigmanagerConstants.SELLING_CHANNEL), dc.getDocument().getData());
                            cacheManager.getCache(BusinessconfigmanagerConstants.CACHE_NAME).put(BusinessconfigmanagerConstants.CONFIGLIST, configList);
                        }
                        if (dc.getType() == DocumentChange.Type.REMOVED) {
                            configList = (Map<String, Object>) cacheManager.getCache(BusinessconfigmanagerConstants.CACHE_NAME)
                                    .get(BusinessconfigmanagerConstants.CONFIGLIST).get();
                            configList.remove(dc.getDocument().getData().get(BusinessconfigmanagerConstants.CONFIG_NAME) + "_"
                                    + dc.getDocument().getData().get(BusinessconfigmanagerConstants.CONFIG_TYPE) + "_"
                                    + dc.getDocument().getData().get(BusinessconfigmanagerConstants.SELLING_DIVISION) + "_"
                                    + dc.getDocument().getData().get(BusinessconfigmanagerConstants.SELLING_CHANNEL));
                            cacheManager.getCache(BusinessconfigmanagerConstants.CACHE_NAME).put(BusinessconfigmanagerConstants.CONFIGLIST, configList);
                        }
                    }
                });
                listenerRegistrations.add(listenerRegistration);
            }
        });
        return listenerRegistrations;
    }

    /**
     * This method is a listener to the businessconfig collection present in Firestore DB and is used for adding modifying and removing data from the businessconfig cahce.
     */
    private List<ListenerRegistration> addListenerToCollectionV2() {
        final List<ListenerRegistration> listenerRegistrations = new ArrayList<>();

        firestore.listCollections().forEach(collectionReference -> {
            if (collectionReference.getId().equalsIgnoreCase(collectionNameV2)) {
                ListenerRegistration listenerRegistration = collectionReference.addSnapshotListener((snapshots, error) -> {
                    Map<String, Object> configList = new HashMap<>();
                    for (DocumentChange dc : snapshots.getDocumentChanges()) {
                        if (dc.getType() == DocumentChange.Type.ADDED) {
                            if (cacheManager.getCache(BusinessconfigmanagerConstants.CACHE_NAME_V2) != null && cacheManager.getCache(BusinessconfigmanagerConstants.CACHE_NAME_V2)
                                    .get(BusinessconfigmanagerConstants.CONFIGLIST) != null) {
                                configList = (Map<String, Object>) cacheManager.getCache(BusinessconfigmanagerConstants.CACHE_NAME_V2)
                                        .get(BusinessconfigmanagerConstants.CONFIGLIST).get();
                            }
                            configList.put(dc.getDocument().getData().get(BusinessconfigmanagerConstants.CONFIG_NAME).toString(),
                                    dc.getDocument().getData());
                            cacheManager.getCache(BusinessconfigmanagerConstants.CACHE_NAME_V2).put(BusinessconfigmanagerConstants.CONFIGLIST, configList);
                        }
                        if (dc.getType() == DocumentChange.Type.MODIFIED) {
                            configList = (Map<String, Object>) cacheManager.getCache(BusinessconfigmanagerConstants.CACHE_NAME_V2)
                                    .get(BusinessconfigmanagerConstants.CONFIGLIST).get();
                            configList.put(dc.getDocument().getData().get(BusinessconfigmanagerConstants.CONFIG_NAME).toString(),
                                    dc.getDocument().getData());
                            cacheManager.getCache(BusinessconfigmanagerConstants.CACHE_NAME_V2).put(BusinessconfigmanagerConstants.CONFIGLIST, configList);
                        }
                        if (dc.getType() == DocumentChange.Type.REMOVED) {
                            configList = (Map<String, Object>) cacheManager.getCache(BusinessconfigmanagerConstants.CACHE_NAME_V2)
                                    .get(BusinessconfigmanagerConstants.CONFIGLIST).get();
                            configList.remove(dc.getDocument().getData().get(BusinessconfigmanagerConstants.CONFIG_NAME));
                            cacheManager.getCache(BusinessconfigmanagerConstants.CACHE_NAME_V2).put(BusinessconfigmanagerConstants.CONFIGLIST, configList);
                        }
                    }
                });
                listenerRegistrations.add(listenerRegistration);
            }
        });
        return listenerRegistrations;
    }

    /**
     * Method to get BusinessConfig from firestore
     * @param configName
     * @param configType
     * @param sellingDivision
     * @return BusinessConfigResponse
     */
    public Map<String, Object> getConfigByNameTypeSellingDivAndSellingChnl(String configName,
                                                                           String configType, String sellingDivision) {
        String businessConfigResponse = businessconfigmanagerService
                .getConfigByNameTypeSellingDivAndSellingChnl(configName, configType, sellingDivision,
                        null).getConfigValue();
        getLogMessageBuilder(log).withContext("businessConfigByTypeSellingChlAndDiv")
                .withAdditionalInfo("businessConfigResponse: " + businessConfigResponse).build()
                .logAsInfo();
        return StringUtils.isEmpty(businessConfigResponse) ? null : jsonUtils.convertFromJson(businessConfigResponse, Map.class);
    }

    public Map<Integer, Map<Object, Object>> getConfigByName(String configName, Order order) {
        Map<Integer, String> businessConfigResponse = businessconfigmanagerService.evaluateBusinessConfig(configName, order);
        Map<Integer,Map<Object, Object>> configMap = new HashMap<>();
        businessConfigResponse.keySet().forEach(key -> {
            Map<Object, Object> map = new HashMap<>();
            map = jsonUtils.convertFromJson(businessConfigResponse.get(key), Map.class);
            configMap.put(key, map);
        });
        getLogMessageBuilder(log).withContext("getConfigByNameV2").withAdditionalInfo("businessConfigResponse: " +
                businessConfigResponse).build().logAsInfo();
        return ObjectUtils.isEmpty(businessConfigResponse) ? null : configMap;
    }
}