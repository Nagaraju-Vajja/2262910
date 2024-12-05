package com.macys.uop.order.ordercollectorchestrator.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.macys.uop.foundation.core.utils.execution.ServiceContextUtil;
import com.macys.uop.foundation.core.utils.json.JsonUtils;
import com.macys.uop.foundation.core.utils.logging.LoggingUtil;
import com.macys.uop.order.model.Order;
import com.macys.uop.order.model.OrderLine;
import com.macys.uop.order.ordercollectorchestrator.service.IPickUpOrderEnhanceServiceImpl;
import com.macys.uop.order.ordercollectorchestrator.utils.BusinessConfigManagerUtil;
import com.macys.uop.order.ordercollectorchestrator.utils.OrderErrorCodes;
import com.macys.uop.order.ordercollectorchestrator.utils.OrdercollectorchestratorUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static com.macys.uop.abstraction.commonlookupmapper.enums.FulfillmentTypeEnum.BOPS;
import static com.macys.uop.abstraction.commonlookupmapper.enums.FulfillmentTypeEnum.BOSS;
import static com.macys.uop.order.ordercollectorchestrator.utils.OrdercollectorchestratorConstants.*;

/***/
@Component
@RequiredArgsConstructor
@Slf4j
public class PickUpOrderEnhanceServiceImpl implements IPickUpOrderEnhanceServiceImpl, ServiceContextUtil, LoggingUtil, OrdercollectorchestratorUtil {

    private final BusinessConfigManagerUtil businessConfigManagerUtil;

    private final JsonUtils jsonUtils;

    @Value("${bossplus.enabled}")
    private boolean isBossPlusEnabled;

    /**
     * Method to enrich the pick up order for BOSS Plus flow
     *
     * @param order
     * @throws ExecutionException
     * @throws InterruptedException
     * @throws JsonProcessingException
     */
    public void enrichPickUpOrder(Order order) {
        if (isBossPlusEnabled) {
            //Fetching business config only if boss plus line is present
            Map<Integer, Map<Object, Object>> bizConfigForEligibleStore =
                    getBossPlusConfig(CONFIG_NAME_ORDER_BOSSPLUS_ENABLEFULFILLMENTSTORE, order, ORDER_ENHANCEBOSSPLUS);

            for (OrderLine orderLine : order.getOrderLines()) {
                if (StringUtils.isEmpty(orderLine.getOriginalFulfillmentType())
                        && StringUtils.isEmpty(orderLine.getPreferredFulfillmentType())) {
                    if (BOSS.getFulfillmentTypeDesc().equalsIgnoreCase(orderLine.getFulfillmentType())
                            && !CollectionUtils.isEmpty(orderLine.getOrderLineStatuses())
                            && isStoreEligibleForBossPlus(orderLine.getLineId(), bizConfigForEligibleStore)
                            && isEmailIdEligibleForBossPlus(orderLine.getLineId(), order, bizConfigForEligibleStore)
                            && orderLine.getOrderLineStatuses().get(0).getFulfillmentLocation()
                            .equalsIgnoreCase(orderLine.getOrderLineStatuses().get(0).getPickupLocation())) {
                        orderLine.setFulfillmentType(BOPS.getFulfillmentTypeDesc());
                        orderLine.getOrderLineStatuses().get(0).setReasonCode(BOSSPLUS_CREATE_REASON_CODE);
                        orderLine.getOrderLineStatuses().get(0).setReasonDesc(BOSSPLUS_CREATE_REASON_DESC);
                        orderLine.setOriginalFulfillmentType(FULFILLMENTTYPE_BOSS);
                        orderLine.setPreferredFulfillmentType(FULFILLMENTTYPE_BOSS);
                        orderLine.setDeliveryMethod(null);
                    } else if (BOPS.getFulfillmentTypeDesc().equalsIgnoreCase(orderLine.getFulfillmentType())) {
                        orderLine.setOriginalFulfillmentType(FULFILLMENTTYPE_BOPS);
                        orderLine.setPreferredFulfillmentType(FULFILLMENTTYPE_STH);
                    }
                }
            }
        }
    }

    /**
     * Method to get businessconfig by line id
     *
     * @param lineId
     * @param bizConfig
     * @return businessconfig
     */
    public Map<Object, Object> getBossPlusConfigByLineId(Integer lineId, Map<Integer, Map<Object, Object>> bizConfig) {
        Map<Object, Object> response = bizConfig.get(lineId);
        if(CollectionUtils.isEmpty(response)) {
            return null;
        }
        return response;
    }

    /**
     * Method to get businessconfig for boss plus
     *
     * @param configName
     * @param order
     * @param stepName
     * @return businessconfig
     */
    public Map<Integer, Map<Object, Object>> getBossPlusConfig(String configName, Order order, String stepName) {
        Map<Integer, Map<Object, Object>> bizConfig = null;
        Optional<OrderLine> bossPlusOrderLineList = order.getOrderLines().stream()
                .filter(orderLine -> BOSS.getFulfillmentTypeDesc().equalsIgnoreCase(orderLine.getFulfillmentType()))
                .filter(orderLine -> !CollectionUtils.isEmpty(orderLine.getOrderLineStatuses())
                        && orderLine.getOrderLineStatuses().get(0).getFulfillmentLocation()
                        .equalsIgnoreCase(orderLine.getOrderLineStatuses().get(0).getPickupLocation())).findFirst();

        if (!ObjectUtils.isEmpty(bossPlusOrderLineList) && bossPlusOrderLineList.isPresent()) {
            try {
                bizConfig = businessConfigManagerUtil.getConfigByName(configName, order);
            } catch (Exception e) {
                getLogMessageBuilder(log).withContext("getBossPlusConfig")
                        .withAdditionalInfo("Exception occurred while getting businessConfigResponse for : " + configName)
                        .build().logAsError();
                throwBusinessError(stepName, BOSS_PLUS_BIZ_CONFIGNAME_ERROR, getDefaultHttpHeaders(),
                        OrderErrorCodes.BIZCONFIG_ERROR);
            }

        }
        return bizConfig;
    }

    /**
     * Method to check whether fulfillmentStore is eligible for bossplus order
     * @param lineId
     * @param bizConfig
     * @return true/false
     */
    public boolean isStoreEligibleForBossPlus(Integer lineId, Map<Integer, Map<Object, Object>> bizConfig) {
        if (!ObjectUtils.isEmpty(bizConfig)) {
            Map<Object, Object> config = getBossPlusConfigByLineId(lineId, bizConfig);
            return (!CollectionUtils.isEmpty(config) && TRUE.equalsIgnoreCase((String) config.get(CONFIG_KEY_ALLOW_BOSS_PLUS)));
        }
        return false;
    }

    /**
     * Method to check whether emailid is eligible for bossplus order
     *
     * @param lineId
     * @param order
     * @param bizConfig
     * @return true/false
     */
    public boolean isEmailIdEligibleForBossPlus(Integer lineId, Order order, Map<Integer, Map<Object, Object>> bizConfig) {
        Map<Object, Object> config = getBossPlusConfigByLineId(lineId, bizConfig);
        if (ObjectUtils.isEmpty(config.get(CONFIG_KEY_ELIGIBLE_BOSSPLUS_EMAIL_ID))) {
            return true;
        } else {
            String configValue = String.valueOf(config.get(CONFIG_KEY_ELIGIBLE_BOSSPLUS_EMAIL_ID));
            String trimmedConfigValue = org.apache.commons.lang3.StringUtils.deleteWhitespace(configValue.replaceAll("\\[|\\]",""));
            List<String> eligibleEmailIds = List.of(trimmedConfigValue.split(","));
            boolean isEmailIdEligible = eligibleEmailIds.stream().anyMatch(emailId -> emailId.equalsIgnoreCase(order.getBillingInfo().getEmailId()));
            getLogMessageBuilder(log).withContext("isEmailIdEligibleForBossPlus")
                    .withAdditionalInfo("isEmailIdEligibleForBossPlus :" + isEmailIdEligible)
                    .build().logAsInfo();
            return isEmailIdEligible;
        }
    }
}
