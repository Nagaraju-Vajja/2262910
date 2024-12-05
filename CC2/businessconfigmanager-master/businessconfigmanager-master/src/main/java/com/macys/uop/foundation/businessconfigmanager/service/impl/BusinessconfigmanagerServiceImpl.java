package com.macys.uop.foundation.businessconfigmanager.service.impl;

import com.macys.uop.common.businessconfig.model.BusinessConfigResponse;
import com.macys.uop.common.businessconfig.model.BusinessConfigResponseList;
import com.macys.uop.common.businessconfig.model.BusinessConfigResponseV2;
import com.macys.uop.foundation.businessconfigmanager.dao.BusinessconfigmanagerDao;
import com.macys.uop.foundation.businessconfigmanager.mapper.BusinessConfigMapper;
import com.macys.uop.foundation.businessconfigmanager.service.BusinessconfigmanagerService;
import com.macys.uop.foundation.businessconfigmanager.utils.BusinessconfigmanagerConstants;
import com.macys.uop.foundation.businessconfigmanager.utils.BusinessconfigmanagerUtil;
import com.macys.uop.foundation.core.utils.exception.ProblemUtil;
import com.macys.uop.foundation.core.utils.json.JsonUtils;
import com.macys.uop.foundation.core.utils.logging.LoggingUtil;
import com.macys.uop.order.model.Order;
import com.macys.uop.order.model.api.orderdetails.OrderLine;
import com.macys.uop.order.model.api.orderdetails.Orderdetails;
import com.macys.uop.order.model.api.orderdetails.ReturnDetails;
import com.macys.uop.order.model.api.orderdetails.ReturnOrderLine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static com.macys.uop.foundation.businessconfigmanager.utils.BusinessconfigmanagerConstants.*;

//<b>THIS CLASS IS EXCLUDED FROM SONAR SCANNING REASON : CODE BECOMES UNMANAGEABLE AFTER FIXING SPLIT THE CLASS EXCEPTION , TEST CASES ARE WRITTEN TO COVER ALL THE SCENARIOS</b>
@Service
@RequiredArgsConstructor
@Slf4j
public class BusinessconfigmanagerServiceImpl implements BusinessconfigmanagerService, LoggingUtil, ProblemUtil {

    private final BusinessconfigmanagerDao businessconfigCoreDao;
    private final CacheManager cacheManager;
    private final BusinessconfigmanagerUtil businessconfigmanagerUtil;
    private final JsonUtils jsonUtils;
    private final BusinessConfigMapper businessConfigManagerMapper;


    @Value("${businessconfigmanager.businessconfig.collection.name}")
    private String collectionName;

    @Value("${businessconfigmanager.businessconfig.collection.name.v2:businessconfig_v2}")
    private String collectionNamev2;


    /**
     * This Method will give all Business configuration based on below parameters
     *
     * @param configName
     * @param sellingDivision
     * @param sellingChannel
     * @return BusinessConfigResponseList
     */
    @Override
    public BusinessConfigResponseList getConfigByNameSellingDivAndsellingChnl(String configName, String sellingDivision, String sellingChannel) {
        BusinessConfigResponseList businessConfigResponseList = new BusinessConfigResponseList();
        if (null != cacheManager.getCache(BusinessconfigmanagerConstants.CACHE_NAME) && null != cacheManager.getCache(BusinessconfigmanagerConstants.CACHE_NAME)
                .get(BusinessconfigmanagerConstants.CONFIGLIST)) { /** This block will read the data from cache */
            getLogMessageBuilder(log).withContext(BusinessconfigmanagerConstants.CONTEXT_BYNAMESELLINGDIVANDSELLINGCHNL)
                    .withAdditionalInfo(BusinessconfigmanagerConstants.CACHE_BLOCK).build().logAsInfo();
            List<BusinessConfigResponse> responseList = getBusinessConfigFromCache(configName, sellingDivision, sellingChannel);
            if (!responseList.isEmpty()) {
                businessConfigResponseList.setBusinessConfigurations(responseList);
            }
        } else { /** This block will read the data from Firestore */
            getLogMessageBuilder(log).withContext(BusinessconfigmanagerConstants.CONTEXT_BYNAMESELLINGDIVANDSELLINGCHNL)
                    .withAdditionalInfo(BusinessconfigmanagerConstants.FIRESTORE_BLOCK).build().logAsInfo();
            List<BusinessConfigResponse> responseList = businessconfigCoreDao
                    .getConfigByNameSellingDivAndSellingChannel(configName, sellingDivision, sellingChannel);
            if (!responseList.isEmpty()) {
                businessConfigResponseList.setBusinessConfigurations(responseList);
            }
        }
        return businessConfigResponseList;
    }

    /**
     * This method will return the specific businessconfig based on configname,configType,sellingDivision,sellingChannel
     *
     * @param configName name of businessconfig
     * @return BusinessConfigResponse businessconfig response object
     */
    @Override
    public BusinessConfigResponseV2 getConfigByNameV2(String configName) {
        BusinessConfigResponseV2 businessConfigResponse = new BusinessConfigResponseV2();
        Map<String, Object> configValue = new HashMap<>();
        if (null != cacheManager.getCache(BusinessconfigmanagerConstants.CACHE_NAME_V2) && null != cacheManager.getCache(BusinessconfigmanagerConstants.CACHE_NAME_V2)
                .get(BusinessconfigmanagerConstants.CONFIGLIST)) { /** This block will read the data from cache */
            getLogMessageBuilder(log)
                    .withContext(BusinessconfigmanagerConstants.CONTEXT_BYNAME_V2)
                    .withAdditionalInfo(BusinessconfigmanagerConstants.CACHE_BLOCK).build().logAsInfo();
            configValue = new HashMap<>();
            Map<String, Object> cacheValue = (Map<String, Object>) cacheManager.getCache(BusinessconfigmanagerConstants.CACHE_NAME_V2)
                    .get(BusinessconfigmanagerConstants.CONFIGLIST).get();
            if (null != cacheValue) {

                Iterator<Map.Entry<String, Object>> it = cacheValue.entrySet().iterator();

                while (it.hasNext()) {

                    Map<String, Object> map = new HashMap();
                    map.putAll((Map<String, Object>) it.next().getValue());
                    if (businessconfigmanagerUtil.checkMatchConfiguration(map, configName)) {
                        configValue.putAll(map);
                        break;

                    }
                }

                if (!configValue.isEmpty()) {
                    businessConfigResponse = businessConfigManagerMapper.convertMapToObjectV2(configValue);
                } else {
                    businessConfigResponse = businessconfigCoreDao.getConfigByNameV2(configName);
                }
            }
        } else { /** This block will read the data from Firestore */
            getLogMessageBuilder(log)
                    .withContext(BusinessconfigmanagerConstants.CONTEXT_BYNAME_V2)
                    .withAdditionalInfo(BusinessconfigmanagerConstants.FIRESTORE_BLOCK).build().logAsInfo();
            businessConfigResponse = businessconfigCoreDao.getConfigByNameV2(configName);
        }
        return businessConfigResponse;
    }

    @Override
    public Map<String, Object> getProfile(String profileId) {
        BusinessConfigResponseV2 businessConfigResponse = getConfigByNameV2("profile");
        Map<String, Object> profileRules = new HashMap<>();
        List<Object> list = (ArrayList<Object>) businessConfigResponse.getConfigTypes();
        list.stream().forEach(field -> {
            List<String> configTypeKeyList = new ArrayList<>();
            ((HashMap) field).keySet().forEach(keySet -> configTypeKeyList.add(keySet.toString()));
            HashMap<?, ?> configDefination = (HashMap<?, ?>) ((HashMap<?, ?>) field).get(CONFIG_DEFINATION);
            String configValue = (String) ((HashMap<?, ?>) field).get(CONFIG_VALUE);
            String profileName = (String) ((HashMap<?, ?>) field).get("configId");
            profileRules.put(profileName, configValue);
        });
        return jsonUtils.convertFromJson(String.valueOf(profileRules.get(profileId)), Map.class);
    }


    /**
     * This Method will specific Business configuration based on below parameters
     *
     * @param configName
     * @param configType
     * @param sellingDivision
     * @param sellingChannel
     * @return BusinessConfigResponse
     */
    @Override
    public BusinessConfigResponse getConfigByNameTypeSellingDivAndSellingChnl(String configName, String configType,
                                                                              String sellingDivision, String sellingChannel) {
        if ("PICKUP".equalsIgnoreCase(configType) || "MIXED".equalsIgnoreCase(configType)) {
            configType = "STH";
        }
        getLogMessageBuilder(log)
                .withContext(BusinessconfigmanagerConstants.CONTEXT_BYNAMETYPESELLINGDIVISIONANDSELLINGCHANNEL)
                .withAdditionalInfo(BusinessconfigmanagerConstants.FETCHING_BUSINESSCONFIG + configName + BusinessconfigmanagerConstants.UNDERSCORE + configType + BusinessconfigmanagerConstants.UNDERSCORE + sellingDivision + BusinessconfigmanagerConstants.UNDERSCORE + sellingChannel).build().logAsInfo();
        BusinessConfigResponse businessConfigResponse = new BusinessConfigResponse();
        if (null != cacheManager.getCache(BusinessconfigmanagerConstants.CACHE_NAME) && null != cacheManager.getCache(BusinessconfigmanagerConstants.CACHE_NAME)
                .get(BusinessconfigmanagerConstants.CONFIGLIST)) { /** This block will read the data from cache */
            getLogMessageBuilder(log)
                    .withContext(BusinessconfigmanagerConstants.CONTEXT_BYNAMETYPESELLINGDIVISIONANDSELLINGCHANNEL)
                    .withAdditionalInfo(BusinessconfigmanagerConstants.CACHE_BLOCK).build().logAsInfo();
            Map<String, Object> configValue = new HashMap<>();
            Map<String, Object> cacheValue = (Map<String, Object>) cacheManager.getCache(BusinessconfigmanagerConstants.CACHE_NAME)
                    .get(BusinessconfigmanagerConstants.CONFIGLIST).get();
            if (null != cacheValue) {
                Iterator<Map.Entry<String, Object>> it = cacheValue.entrySet().iterator();

                while (it.hasNext()) {

                    Map<String, Object> map = new HashMap();
                    map.putAll((Map<String, Object>) it.next().getValue());
                    if (sellingChannel == null && (businessconfigmanagerUtil
                            .checkMatchConfigurationWithoutSellingChnl(map, configName, configType, sellingDivision))) {

                        configValue.putAll(map);

                    } else {
                        if (businessconfigmanagerUtil.checkMatchConfigurationWithSellingChnl(map, configName,
                                configType, sellingDivision, sellingChannel)) {
                            configValue.putAll(map);
                            break;
                        }

                    }
                }
                if (!configValue.isEmpty()) {
                    businessConfigResponse = businessConfigManagerMapper.convertMapToObject(configValue);
                } else {
                    getLogMessageBuilder(log)
                            .withContext(BusinessconfigmanagerConstants.CONTEXT_BYNAMETYPESELLINGDIVISIONANDSELLINGCHANNEL)
                            .withAdditionalInfo(BusinessconfigmanagerConstants.CACHE_FIRESTORE_BLOCK).build().logAsInfo();
                    configValue = businessconfigCoreDao.getConfigByNameTypeSellingDivAndSellingChnl(configName,
                            configType, sellingDivision, sellingChannel);
                    if (configValue.containsKey(BusinessconfigmanagerConstants.CONFIG_NAME)) {
                        businessConfigResponse = businessConfigManagerMapper.convertMapToObject(configValue);
                        getLogMessageBuilder(log)
                                .withContext(BusinessconfigmanagerConstants.CONTEXT_BYNAMETYPESELLINGDIVISIONANDSELLINGCHANNEL)
                                .withAdditionalInfo(BusinessconfigmanagerConstants.CACHE_UPDATE).build().logAsInfo();
                        cacheValue.put(
                                businessConfigResponse.getConfigName() + "_" + businessConfigResponse.getConfigType()
                                        + "_" + businessConfigResponse.getSellingDivision() + "_"
                                        + businessConfigResponse.getSellingChannel(),
                                configValue);
                        cacheManager.getCache(BusinessconfigmanagerConstants.CACHE_NAME)
                                .put(BusinessconfigmanagerConstants.CONFIGLIST, cacheValue);
                    }
                }
            }
        } else { /** This block will read the data from Firestore */
            getLogMessageBuilder(log)
                    .withContext(BusinessconfigmanagerConstants.CONTEXT_BYNAMETYPESELLINGDIVISIONANDSELLINGCHANNEL)
                    .withAdditionalInfo(BusinessconfigmanagerConstants.FIRESTORE_BLOCK).build().logAsInfo();
            Map<String, Object> configValue = businessconfigCoreDao.getConfigByNameTypeSellingDivAndSellingChnl(
                    configName, configType, sellingDivision, sellingChannel);
            if (configValue.containsKey(BusinessconfigmanagerConstants.CONFIG_NAME)) {
                businessConfigResponse = businessConfigManagerMapper.convertMapToObject(configValue);
            }
        }
        return businessConfigResponse;
    }


    public List<BusinessConfigResponse> getBusinessConfigFromCache(String configName, String sellingDivision,
                                                                   String sellingChannel) {
        List<BusinessConfigResponse> responseList = new ArrayList<>();
        Map<String, Object> cacheValue = (Map<String, Object>) cacheManager.getCache(BusinessconfigmanagerConstants.CACHE_NAME)
                .get(BusinessconfigmanagerConstants.CONFIGLIST).get();
        if (null != cacheValue) {
            cacheValue.entrySet().stream().forEach(e -> {
                Map<String, Object> map = new HashMap();
                map.putAll((Map<String, Object>) e.getValue());
                if (businessconfigmanagerUtil.checkCacheMap(map, configName, sellingDivision, sellingChannel)) {
                    responseList.add(businessConfigManagerMapper.convertMapToObject(map));
                }
            });
        }
        return responseList;
    }

    public List<BusinessConfigResponse> getBusinessConfigFromCacheV2(String configName) {
        List<BusinessConfigResponse> responseList = new ArrayList<>();
        Map<String, Object> cacheValue = (Map<String, Object>) cacheManager.getCache(BusinessconfigmanagerConstants.CACHE_NAME_V2)
                .get(BusinessconfigmanagerConstants.CONFIGLIST).get();
        if (null != cacheValue) {
            cacheValue.entrySet().stream().forEach(e -> {
                Map<String, Object> map = new HashMap();
                map.putAll((Map<String, Object>) e.getValue());
                if (businessconfigmanagerUtil.checkCacheMapV2(map, configName)) {
                    responseList.add(businessConfigManagerMapper.convertMapToObject(map));
                }
            });
        }
        return responseList;
    }

    @Override
    public Map<Integer, String> evaluateBusinessConfig(String configName, Orderdetails order) {

        return evaluateBusinessConfiguration(configName, order);
    }

    @Override
    public Map<Integer, String> evaluateBusinessConfig(String configName, Order order) {

        return evaluateBusinessConfiguration(configName, businessConfigManagerMapper.maporderToOrderDetails(order));
    }

    @Override
    public Map<String, String> evaluateProfile(Order order) {

        return evaluateProfile(businessConfigManagerMapper.maporderToOrderDetails(order));
    }

    @Override
    public Map<String, String> evaluateProfile(Orderdetails orderdetails) {
        Map<String, String> matchedProfile = new HashMap<>();
        BusinessConfigResponseV2 businessConfigResponse = getConfigByNameV2("profile");
        if (businessConfigResponse.getStatus().equalsIgnoreCase(ACTIVE)) {
            AtomicInteger count = new AtomicInteger();
            AtomicReference<String> configValue = new AtomicReference<>();
            AtomicReference<String> profileId = new AtomicReference<>();
            List<Object> list = (ArrayList<Object>) businessConfigResponse.getConfigTypes();
            Map<String, Integer> configMap = new HashMap<>();
            evaluateLineLevelConfiguration(orderdetails, list, configValue, count, configMap, null, null, null, businessConfigResponse, profileId, matchedProfile);
        }
        return matchedProfile;
    }

    public Map<Integer, String> evaluateBusinessConfiguration(String configName, Orderdetails order) {
        BusinessConfigResponseV2 businessConfigResponse = getConfigByNameV2(configName);
        if (businessConfigResponse.getStatus().equalsIgnoreCase(ACTIVE)) {
            AtomicInteger count = new AtomicInteger();
            AtomicReference<String> configValue = new AtomicReference<>();
            AtomicReference<String> profileId = new AtomicReference<>();
            List<Object> list = (ArrayList<Object>) businessConfigResponse.getConfigTypes();
            Map<Integer, String> matchedConfiguration = new HashMap<>();
            if(list.size()==1) {
                list.stream().forEach(field -> {
                    List<String> configTypeKeyList = new ArrayList<>();
                    ((HashMap) field).keySet().forEach(keySet -> configTypeKeyList.add(keySet.toString()));
                    HashMap<?, ?> configDefination = (HashMap<?, ?>) ((HashMap<?, ?>) field).get(CONFIG_DEFINATION);
                    if (configDefination.isEmpty()) {
                        matchedConfiguration.put(0, (String) ((HashMap<?, ?>) field).get(CONFIG_VALUE));
                    }
                });
                if(!matchedConfiguration.isEmpty()) {
                    return matchedConfiguration;
                }
            }
             if(businessConfigResponse.getDomain().equalsIgnoreCase(RETURNS)){
                if(ObjectUtils.isNotEmpty(order.getReturns().get(0).getReturnOrderLines())){
                    order.getReturns().get(0).getReturnOrderLines().stream().forEach(orderLine -> {
                        Map<String, Integer> configMap = new HashMap<>();
                        evaluateLineLevelConfiguration(order, list, configValue, count, configMap, matchedConfiguration, null, orderLine, businessConfigResponse, null, null);
                    });
                }
                else{
                    ReturnOrderLine orderLines = new ReturnOrderLine();
                    orderLines.setLineId("0");
                    List<ReturnOrderLine> orderLineList = new ArrayList<>();
                    orderLineList.add(orderLines);
                    ReturnDetails returnDetails = new ReturnDetails();
                    List<ReturnDetails> returnDetailsList = new ArrayList<>();
                    returnDetails.setReturnOrderLines(orderLineList);
                    returnDetailsList.add(returnDetails);
                    order.setReturns(returnDetailsList);
                    order.getOrderLines().stream().forEach(orderLine -> {
                        Map<String, Integer> configMap = new HashMap<>();
                        evaluateLineLevelConfiguration(order, list, configValue, count, configMap, matchedConfiguration, orderLine, null, businessConfigResponse, null, null);
                    });
                }
            }
            else {
                if (ObjectUtils.isNotEmpty(order.getOrderLines())) {
                    order.getOrderLines().stream().forEach(orderLine -> {
                        Map<String, Integer> configMap = new HashMap<>();
                        evaluateLineLevelConfiguration(order, list, configValue, count, configMap, matchedConfiguration, orderLine, null, businessConfigResponse, null, null);
                    });
                }
                else{
                    OrderLine orderLines = new OrderLine();
                    orderLines.setLineId(0);
                    List<OrderLine> orderLineList = new ArrayList<>();
                    orderLineList.add(orderLines);
                    order.setOrderLines(orderLineList);
                    order.getOrderLines().stream().forEach(orderLine -> {
                        Map<String, Integer> configMap = new HashMap<>();
                        evaluateLineLevelConfiguration(order, list, configValue, count, configMap, matchedConfiguration, orderLine, null, businessConfigResponse, null, null);
                    });
                }
            }
            return matchedConfiguration;
        } else {
            return null;
        }
    }

    public void evaluateLineLevelConfiguration(Orderdetails order, List<Object> list, AtomicReference<String> configValue, AtomicInteger count, Map<String, Integer> configMap, Map<Integer, String> matchedConfiguration, OrderLine orderLine, ReturnOrderLine returnOrderLine, BusinessConfigResponseV2 businessConfigResponse, AtomicReference<String> profileId, Map<String, String> matchedProfile){

            AtomicBoolean flag = new AtomicBoolean();
            Map<Map<String, String>, Integer> profileMap = new HashMap<>();
            flag.set(true);
            LinkedHashMap<String, Integer> sortedMap = new LinkedHashMap<>();
            LinkedHashMap<Map<String, String>, Integer> sortedProfileMap = new LinkedHashMap<>();
            List<String> matchedConfigList = new ArrayList<>();
            list.stream().forEach(field -> {
                List<String> configTypeKeyList = new ArrayList<>();
                ((HashMap) field).keySet().forEach(keySet -> configTypeKeyList.add(keySet.toString()));
                HashMap<?, ?> configDefination = (HashMap<?, ?>) ((HashMap<?, ?>) field).get(CONFIG_DEFINATION);
                configValue.set((String) ((HashMap<?, ?>) field).get(CONFIG_VALUE));
                if(ObjectUtils.isNotEmpty(profileId)) {
                    profileId.set((String) ((HashMap<?, ?>) field).get("configId"));
                }
                Map<String, Boolean> map = new HashMap<>();
                Map<String, Boolean> finalMap = map;
                configDefination.entrySet().forEach(keySet -> {
                    if (keySet.getKey().toString().contains("[")) {
                        String[] lineLevelConfigs = keySet.getKey().toString().split("\\.");
                        AtomicInteger index = new AtomicInteger();
                        List<Class> classList = new ArrayList<>();
                        classList.add(0, Orderdetails.class);
                        List<Object> objList = new ArrayList<>();
                        objList.add(0, order);
                        if(businessConfigResponse.getConfigName().equalsIgnoreCase("profile")){
                            finalMap.put(lineLevelConfigs.toString(), false);
                            if(!compareRequestToProfile(lineLevelConfigs, index, classList, objList, keySet, configDefination, count, order, finalMap)){
                                flag.set(false);
                            }
                            else{
                                flag.set(true);
                            }
                            finalMap.entrySet().stream().filter(key -> key.getKey().equalsIgnoreCase(lineLevelConfigs.toString())).findFirst().get().setValue(flag.get());
                        }
                        else {
                            compareRequestToConfiguration(lineLevelConfigs, index, classList, objList, keySet, configDefination, count, orderLine, returnOrderLine);
                        }
                    } else {
                        evaluateOrderLevelCongigs(order, keySet, configDefination, count);
                    }
                });
                if(businessConfigResponse.getConfigName().equalsIgnoreCase("profile")){
                    if(map.entrySet().stream().anyMatch(key -> key.getValue().equals(false))){
                        flag.set(false);
                    }
                    else{
                        flag.set(true);
                    }
                    map.entrySet().stream().forEach(element -> {
                        if(element.getValue().equals(true)){
                            count.getAndIncrement();
                        }
                    });
                    if(flag.get()){
                        configMap.put(configValue.get(), count.get());
                        Map<String, String> profileValueMap = new HashMap<>();
                        profileValueMap.put(profileId.get(), configValue.get());
                        profileMap.put(profileValueMap, count.get());
                    }
                    map = new HashMap<>();
                }
                else if(count.get()>0) {
                    configMap.put(configValue.get(), count.get());
                }
                count.set(0);
            });
            if(!configMap.isEmpty()) {
                if(businessConfigResponse.getConfigName().equalsIgnoreCase("profile")){
                    sortedProfileMap = sortByValueProfile(profileMap);
                }
                else {
                    sortedMap = sortByValue(configMap);
                }
                LinkedHashMap<Map<String, String>, Integer> finalSortedProfileMap = new LinkedHashMap<>();
                if(businessConfigResponse.getConfigName().equalsIgnoreCase("profile")){
                    int value = sortedProfileMap.entrySet().iterator().next().getValue();
                    sortedProfileMap.entrySet().stream().forEach(profile -> {
                        if(profile.getValue().equals(value)){
                            finalSortedProfileMap.put(profile.getKey(), profile.getValue());
                        }
                    });
                }
                else {
                    int value = sortedMap.entrySet().iterator().next().getValue();
                    sortedMap.entrySet().stream().filter(entry -> entry.getValue() == value).forEach(config -> matchedConfigList.add(config.getKey()));
                }
                if (!matchedConfigList.isEmpty() || !sortedProfileMap.isEmpty()) {
                    if(businessConfigResponse.getDomain().equalsIgnoreCase(RETURNS)){
                        matchedConfiguration.put(Integer.parseInt(returnOrderLine.getLineId()), matchedConfigList.get(0));
                    }
                    else if(businessConfigResponse.getConfigName().equalsIgnoreCase("profile")){
                        finalSortedProfileMap.entrySet().stream().forEach(profile -> {
                            matchedProfile.putAll(profile.getKey());
                        });
                    }
                    else {
                        matchedConfiguration.put(orderLine.getLineId(), matchedConfigList.get(0));
                    }
                }
            }
    }

    public void evaluateOrderLevelCongigs(Orderdetails order, Map.Entry<?, ?> keySet, HashMap<?, ?> configDefination, AtomicInteger count){

        String methodName = new StringBuilder().append(GET).append(keySet.getKey()).toString();
        Object values = "";
        try {
            values = (order.getClass().getMethod(methodName)).invoke(order);
        }
        catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            businessconfigmanagerUtil.buildWarn(e, BusinessconfigmanagerConstants.METHOD_DOESNOT_EXIST);
        }
        matchConfigValues(values, keySet, configDefination, count);
    }

    public LinkedHashMap<String, Integer> sortByValue(Map<String, Integer> configValueMap) {
        return configValueMap.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).collect(Collectors.toMap(
                Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

    public LinkedHashMap<Map<String, String>, Integer> sortByValueProfile(Map<Map<String, String>, Integer> configValueMap) {
        return configValueMap.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).collect(Collectors.toMap(
                Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

    public void compareRequestToConfiguration(String[] lineLevelConfigs, AtomicInteger index, List<Class> classList, List<Object> objList, Map.Entry<?, ?> keySet, HashMap<?, ?> configDefination, AtomicInteger count, OrderLine orderLine, ReturnOrderLine returnOrderLine) {

        Arrays.asList(lineLevelConfigs).stream().forEach(lines -> {
            try {
                String methodParam = lineLevelConfigs[index.get()];
                index.getAndIncrement();
                String[] conf = methodParam.split("\\[");
                Method methods = classList.get(0).getMethod(new StringBuilder().append(GET).append(conf[0]).toString());
                Object obj = null;
                if (objList.get(0) instanceof ArrayList) {
                    if (ObjectUtils.isNotEmpty(orderLine) && ((ArrayList) objList.get(0)).get(0).getClass().equals(orderLine.getClass())) {
                        obj = methods.invoke(orderLine);
                        if (obj instanceof String || ObjectUtils.isEmpty(obj)) {
                            matchConfigValues(obj, keySet, configDefination, count);
                        }
                    }
                    else if (ObjectUtils.isNotEmpty(returnOrderLine) && ((ArrayList) objList.get(0)).get(0).getClass().equals(returnOrderLine.getClass())) {
                        obj = methods.invoke(returnOrderLine);
                        if (obj instanceof String || ObjectUtils.isEmpty(obj)) {
                            matchConfigValues(obj, keySet, configDefination, count);
                        }
                    }
                    else {
                        int size = ((ArrayList<?>) objList.get(0)).size();
                        int counter = 0;
                        List<Object> configValueList = new ArrayList<>();
                        while (counter < size) {
                            obj = methods.invoke(((ArrayList) objList.get(0)).get(counter));
                            if (obj instanceof String || ObjectUtils.isEmpty(obj)) {
                                configValueList.add(obj);
                            }
                            counter++;
                        }
                        if(!configValueList.isEmpty()) {
                            matchConfigValues(configValueList, keySet, configDefination, count);
                        }
                    }
                } else {
                    obj = methods.invoke(objList.get(0));
                    if (obj instanceof String || ObjectUtils.isEmpty(obj)) {
                        matchConfigValues(obj, keySet, configDefination, count);
                    }
                }
                if (ObjectUtils.isNotEmpty(obj)) {
                    objList.add(0, obj);
                }
                if (!(objList.get(0) instanceof String) && !(objList.get(0) instanceof Integer) && ObjectUtils.isNotEmpty(obj)) {
                    if((objList.get(0)).getClass().getName().equals("java.util.ArrayList")) {
                        classList.add(0, ((ArrayList) objList.get(0)).get(0).getClass());
                    }
                    else{
                        classList.add(0, (objList.get(0).getClass()));
                    }
                }
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                businessconfigmanagerUtil.buildWarn(e, BusinessconfigmanagerConstants.METHOD_DOESNOT_EXIST);
            }
        });
    }

    public Boolean compareRequestToProfile(String[] lineLevelConfigs, AtomicInteger index, List<Class> classList, List<Object> objList, Map.Entry<?, ?> keySet, HashMap<?, ?> configDefination, AtomicInteger count, Orderdetails order, Map<String, Boolean> map) {

        AtomicBoolean isPresent = new AtomicBoolean(false);
        order.getOrderLines().stream().forEach(orderLine -> {
            index.set(0);
            classList.add(0, Orderdetails.class);
            objList.add(0, order);
        Arrays.asList(lineLevelConfigs).stream().forEach(lines -> {
                try {
                    String methodParam = lineLevelConfigs[index.get()];
                    index.getAndIncrement();
                    String[] conf = methodParam.split("\\[");
                    Method methods = classList.get(0).getMethod(new StringBuilder().append(GET).append(conf[0]).toString());
                    Object obj = null;
                    if (objList.get(0) instanceof ArrayList) {
                        if (ObjectUtils.isNotEmpty(orderLine) && ((ArrayList) objList.get(0)).get(0).getClass().equals(orderLine.getClass())) {
                            obj = methods.invoke(orderLine);
                            if (obj instanceof String || ObjectUtils.isEmpty(obj)) {
                                matchProfileValues(obj, keySet, configDefination, count, map, isPresent);
                            }
                        } else {
                            int size = ((ArrayList<?>) objList.get(0)).size();
                            int counter = 0;
                            List<Object> configValueList = new ArrayList<>();
                            while (counter < size) {
                                obj = methods.invoke(((ArrayList) objList.get(0)).get(counter));
                                if (obj instanceof String || ObjectUtils.isEmpty(obj)) {
                                    configValueList.add(obj);
                                }
                                counter++;
                            }
                            if (!configValueList.isEmpty()) {
                                matchProfileValues(configValueList, keySet, configDefination, count, map, isPresent);
                            }
                        }
                    } else {
                        obj = methods.invoke(objList.get(0));
                        if (obj instanceof String || ObjectUtils.isEmpty(obj)) {
                            matchProfileValues(obj, keySet, configDefination, count, map, isPresent);
                        }
                    }
                    if (ObjectUtils.isNotEmpty(obj)) {
                        objList.add(0, obj);
                    }
                    if (!(objList.get(0) instanceof String) && !(objList.get(0) instanceof Integer) && ObjectUtils.isNotEmpty(obj)) {
                        classList.add(0, ((ArrayList) objList.get(0)).get(0).getClass());
                    }
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    businessconfigmanagerUtil.buildWarn(e, BusinessconfigmanagerConstants.METHOD_DOESNOT_EXIST);
                }
            });
        });
        return isPresent.get();
    }


    public void matchConfigValues(Object values, Map.Entry<?, ?> keySet, HashMap<?, ?> configDefination, AtomicInteger count) {

        if (!(count.get() < 0)) {
            if (!ObjectUtils.isEmpty(values)) {
                if (configDefination.get(keySet.getKey()) instanceof ArrayList) {
                    List<String> list = (ArrayList) configDefination.get(keySet.getKey());
                    AtomicBoolean isPresent = new AtomicBoolean(false);
                    list.stream().forEach(config -> {
                        if(values instanceof ArrayList){
                            ((ArrayList<?>) values).stream().forEach(value -> {
                                if(!isPresent.get()) {
                                    if (config.equalsIgnoreCase(String.valueOf(value))) {
                                        count.getAndIncrement();
                                        isPresent.set(true);
                                    }
                                }
                            });
                        }
                        else {
                            if (config.equalsIgnoreCase(values.toString())) {
                                count.getAndIncrement();
                                isPresent.set(true);
                            }
                        }
                    });
                    if(!isPresent.get()){
                        count.set(-1);
                    }
                } else {
                    if(values instanceof ArrayList){
                        AtomicBoolean isPresent = new AtomicBoolean(false);
                        if((configDefination.get(keySet.getKey())).toString().equalsIgnoreCase("") && ObjectUtils.isEmpty(((ArrayList) values).get(0))){
                            count.getAndIncrement();
                            isPresent.set(true);
                        }
                        else {
                            ((ArrayList<?>) values).stream().forEach(value -> {
                            if(!isPresent.get()) {
                                if ((configDefination.get(keySet.getKey())).toString().equalsIgnoreCase(String.valueOf(value))) {
                                    count.getAndIncrement();
                                    isPresent.set(true);
                                }
                            }
                        });
                        }
                        if(!isPresent.get()){
                            count.set(-1);
                        }
                    }
                    else if ((configDefination.get(keySet.getKey())).toString().equalsIgnoreCase(values.toString())) {
                        count.getAndIncrement();
                    } else {
                        count.set(-1);
                    }
                }
            } else {
                if (configDefination.get(keySet.getKey()) instanceof ArrayList) {
                    AtomicBoolean isPresent = new AtomicBoolean(false);
                    ((ArrayList) configDefination.get(keySet.getKey())).stream().forEach(value -> {
                        if (("").equals(value)) {
                            count.getAndIncrement();
                            isPresent.set(true);
                        }
                    });
                    if(!isPresent.get()){
                        count.set(-1);
                    }
                }
                else{
                   if(("").equals(configDefination.get(keySet.getKey()))){
                       count.getAndIncrement();
                   }
                   else{
                       count.set(-1);
                   }
                }
            }
        }
    }

    public void matchProfileValues(Object values, Map.Entry<?, ?> keySet, HashMap<?, ?> configDefination, AtomicInteger count, Map<String, Boolean> map, AtomicBoolean isPresent) {

        if (!(count.get() < 0)) {
            if (!ObjectUtils.isEmpty(values)) {
                if (configDefination.get(keySet.getKey()) instanceof ArrayList) {
                    List<String> list = (ArrayList) configDefination.get(keySet.getKey());
                    list.stream().forEach(config -> {
                        if(values instanceof ArrayList){
                            ((ArrayList<?>) values).stream().forEach(value -> {
                                if(!isPresent.get()) {
                                    if (config.equalsIgnoreCase(String.valueOf(value))) {
                                        isPresent.set(true);
                                    }
                                }
                            });
                        }
                        else {
                            if (config.equalsIgnoreCase(values.toString())) {
                                isPresent.set(true);
                            }
                        }
                    });
                } else {
                    if(values instanceof ArrayList){
                        ((ArrayList<?>) values).stream().forEach(value -> {
                            if(!isPresent.get()) {
                                if ((configDefination.get(keySet.getKey())).toString().equalsIgnoreCase(String.valueOf(value))) {
                                    isPresent.set(true);
                                }
                            }
                        });
                    }
                    else if ((configDefination.get(keySet.getKey())).toString().equalsIgnoreCase(values.toString())) {
                        isPresent.set(true);
                    }
                }
            } else {
                if (configDefination.get(keySet.getKey()) instanceof ArrayList) {
                    ((ArrayList) configDefination.get(keySet.getKey())).stream().forEach(value -> {
                        if (("").equals(value)) {
                            isPresent.set(true);
                        }
                    });
                }
                else{
                    if(("").equals(configDefination.get(keySet.getKey()))){
                        isPresent.set(true);
                    }
                }
            }
        }
    }
}
