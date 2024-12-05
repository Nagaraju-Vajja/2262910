package com.macys.uop.order.ordercollectorchestrator.utils;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.macys.uop.foundation.businessconfigmanager.service.BusinessconfigmanagerService;
import com.macys.uop.foundation.core.utils.json.JsonUtils;
import com.macys.uop.foundation.core.utils.logging.LoggingUtil;
import com.macys.uop.foundation.profileevaluator.service.ProfileEvaluatorService;
import com.macys.uop.order.model.Order;
import com.macys.uop.order.model.OrderLine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.macys.uop.order.ordercollectorchestrator.utils.OrdercollectorchestratorConstants.PROFILE_ID;
import static com.macys.uop.order.ordercollectorchestrator.utils.OrdercollectorchestratorConstants.PROFILE_VERSION;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProfileEvaluatorUtil implements LoggingUtil {

    private final ProfileEvaluatorService profileEvaluatorService;
    private final BusinessconfigmanagerService businessconfigmanagerService;
    private final JsonUtils jsonUtils;
    @Value("${order.temoporary.file}")
    private String temporaryProfileFlag;
    @Value("${order.profile.pickup}")
    private String temporaryProfilPickup;
    @Value("${order.profile.mixed}")
    private String temporaryProfilMixed;
    @Value("${kill-switch-r3}")
    private Boolean killSwitchR3;

    /**
     * Method to get profile from firestore
     * @param profileType
     * @param order
     * @param orderId
     * @return Order
     */
//    public Order evaluateProfile(String profileType,Order order, String orderId){
//        try {
//            Set<String> lineFulfilmentType = new HashSet<String>();
//        	ObjectMapper mapper = new ObjectMapper();
//        	 Map<String, String> matchedProfile = profileEvaluatorService.evaluateProfile(profileType,mapper.writeValueAsString(order),orderId);
//            getLogMessageBuilder(log).withContext("evaluateProfile")
//                .withAdditionalInfo("matchedProfile: " + matchedProfile).build().logAsInfo();
//            if(ObjectUtils.isNotEmpty(matchedProfile)) {
//                order.setProfileId(matchedProfile.get(PROFILE_ID));
//                order.setProfileVersion(matchedProfile.get(PROFILE_VERSION));
//                if(!killSwitchR3 && isEligible(order)) {
//                    if ("Y".equalsIgnoreCase(temporaryProfileFlag)) {
//                        for (OrderLine line : order.getOrderLines()) {
//                            if ("BOPS,BOSS,SDD,S2AP".contains(line.getFulfillmentType())) {
//                                lineFulfilmentType.add("PICKUP");
//                            } else {
//                                lineFulfilmentType.add("STH");
//                            }
//                        }
//                        if (lineFulfilmentType.size() > 1) {
//                            order.setProfileId("MIXED");
//                            order.setProfileVersion("3");
//                        } else if ("PICKUP".equalsIgnoreCase(lineFulfilmentType.stream().findAny().get())) {
//                            order.setProfileId("PICKUP");
//                            order.setProfileVersion("3");
//                        }
//                    }
//                }
//            }
//        }
//        catch (JsonProcessingException e){
//            getLogMessageBuilder(log).withContext("evaluateProfile")
//                .withAdditionalInfo("Error: " + e).build().logAsError();
//        }
//        return order;
//    }

    public Map<String, Object> getProfileRules(String profileType, String profileId, String profileVersion) {
            Map<String,Object> rules= profileEvaluatorService.getProfileRules(profileType,profileId,profileVersion);
            getLogMessageBuilder(log).withContext("getProfileRules")
                .withAdditionalInfo("rules: " + rules).build().logAsInfo();
        return rules;
    }

    public Map<String, Object> getProfileRulesV2(String profileId) {
        Map<String, Object> rules = businessconfigmanagerService.getProfile(profileId);
        getLogMessageBuilder(log).withContext("getProfileRules")
                .withAdditionalInfo("rules: " + rules).build().logAsInfo();
        return rules;
    }
    public boolean isEligible(Order order){
        boolean eligible = true;
        if(ObjectUtils.isNotEmpty(order) && ObjectUtils.isNotEmpty(order.getOrderLines())){
            for(OrderLine lines : order.getOrderLines()){
                if(ObjectUtils.isEmpty(lines.getFulfillmentType())){
                    eligible = false;
                    break;
                }
            }
        }
        return eligible;
    }

    public Order evaluateProfileV2(String profileType,Order order, String orderId){
        Map<String, String> matchedProfile = businessconfigmanagerService.evaluateProfile(order);
        getLogMessageBuilder(log).withContext("evaluateProfile")
                .withAdditionalInfo("matchedProfile: " + matchedProfile).build().logAsInfo();
        if(ObjectUtils.isNotEmpty(matchedProfile)) {
            order.setProfileId(matchedProfile.entrySet().stream().findFirst().get().getKey());
        }
        return order;
    }
}
