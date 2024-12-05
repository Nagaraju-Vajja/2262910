package com.macys.uop.order.ordercollectorchestrator.utils;

import static com.macys.uop.order.ordercollectorchestrator.utils.OrderErrorCodes.VALIDATE_ORDER_ERROR;
import static com.macys.uop.order.ordercollectorchestrator.utils.OrdercollectorchestratorConstants.*;

import com.macys.uop.foundation.core.utils.CommonStatusCode;
import com.macys.uop.foundation.core.utils.exception.Error;
import com.macys.uop.foundation.core.utils.exception.ErrorDetail;
import com.macys.uop.order.model.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Slf4j
@Component
@RequiredArgsConstructor
public class ValidatorUtil implements OrdercollectorchestratorUtil {

    private final ProfileEvaluatorUtil profileEvaluatorUtil;
    private final BusinessConfigManagerUtil businessConfigManagerUtil;
    private final Map<String, String> propertyMap;

    @Value("${kill-switch-r3}")
    private Boolean killSwitchR3;
    @Value("${enableR3businessconfig}")
    private Boolean enableR3businessconfig;


    /**
     * Method which validates Mandatory Fields in Payload
     * @param order
     */
    public void validateMandatoryParams(Order order) {
        AtomicBoolean isEmpty = new AtomicBoolean(
            Stream.of(order.getSourceChannel(), order.getOrderChannelDivision())
                .anyMatch(ObjectUtils::isEmpty));
        setAssociateDetails(order);
        List<String> mandatoryFields = new ArrayList<>();

        if(enableR3businessconfig){
    if (!isEmpty.get()) {
        Map<Integer,Map<Object,Object>> bizConfigResponseR3 = new HashMap<>();
        bizConfigResponseR3=businessConfigManagerUtil.getConfigByName(MANDATORY_FIELDS,order);

        if (!CollectionUtils.isEmpty(bizConfigResponseR3)) {
            Map <Object,Object>mandatory=bizConfigResponseR3.values().stream().findFirst().get();
            mandatoryFields= List.of(mandatory.get(FIELDS).toString().split(","));

        }
        mandatoryFields.forEach(mandateField -> {
            if (isEmpty.get()) {
                return;
            }
            switch (mandateField) {
                case SELLER_ORDER_ID:
                    isEmpty.set(ObjectUtils.isEmpty(order.getSellerOrderId()));
                    break;
                case ASSOCIATE_DETAILS:
                    isEmpty.set((ObjectUtils.isEmpty(order.getAssociateDetails())) || (
                            !ObjectUtils.isEmpty(order.getAssociateDetails())
                                    && order.getAssociateDetails().stream()
                                    .map(AssociateDetail::getAssociateId)
                                    .anyMatch(ObjectUtils::isEmpty)));
                    break;
                case PARTNER_ORDER_ID:
                    isEmpty.set(ObjectUtils.isEmpty(order.getPartnerOrderId()));
                    break;
                default:
                    break;
            }
        });

    }

            }
    else {
    if (!isEmpty.get()) {
        Map<String, Object> businessConfigResponse =
                businessConfigManagerUtil.getConfigByNameTypeSellingDivAndSellingChnl(
                        MANDATORY_FIELDS, getConfigType(order), order.getOrderChannelDivision());
        if (!CollectionUtils.isEmpty(businessConfigResponse)) {
            mandatoryFields =
                    Stream.of(businessConfigResponse.get(FIELDS).toString().split(",", -1))
                            .collect(Collectors.toList());
        }
        mandatoryFields.forEach(mandateField -> {
            if (isEmpty.get()) {
                return;
            }
            switch (mandateField) {
                case SELLER_ORDER_ID:
                    isEmpty.set(ObjectUtils.isEmpty(order.getSellerOrderId()));
                    break;
                case ASSOCIATE_DETAILS:
                    isEmpty.set((ObjectUtils.isEmpty(order.getAssociateDetails())) || (
                            !ObjectUtils.isEmpty(order.getAssociateDetails())
                                    && order.getAssociateDetails().stream()
                                    .map(AssociateDetail::getAssociateId)
                                    .anyMatch(ObjectUtils::isEmpty)));
                    break;
                case PARTNER_ORDER_ID:
                    isEmpty.set(ObjectUtils.isEmpty(order.getPartnerOrderId()));
                    break;
                default:
                    break;
            }
        });

    }
    }



        if (isEmpty.get()) {
            RetryEnum retryEnum = RetryEnum.getRetryInfoByStep(ORDER_VALIDATE);
            String additionalInfo = "Mandatory Fields Validation Failure";
            getErrorLogMessageBuilder(CommonStatusCode.BAD_REQUEST_BODY.getCode(),
                CommonStatusCode.BAD_REQUEST_BODY.getDescription(), additionalInfo, log).build().logAsError();
            Error error = Error.builder().withCode(VALIDATE_ORDER_ERROR.getCode())
                .withMessage(VALIDATE_ORDER_ERROR.getDescription()).withErrorDetail(
                    ErrorDetail.builder().withDomain(getAppName())
                        .withReason(CommonStatusCode.BAD_REQUEST_BODY.getDescription())
                        .withMessage(additionalInfo).withLocation(retryEnum.getLocation()).build())
                .build();
            throw createProblem(org.zalando.problem.Status.BAD_REQUEST.getStatusCode(), error);
        }
    }

    public void setAssociateDetails(Order order) {
        if (CollectionUtils.isEmpty(order.getAssociateDetails()) ||
                (!CollectionUtils.isEmpty(order.getAssociateDetails())
                        && StringUtils.isEmpty(order.getAssociateDetails().get(0).getAssociateId()))) {
            List<AssociateDetail> associateDetailList = new ArrayList<>();
            AssociateDetail associateDetail = new AssociateDetail();
            if (MACYS_71.equalsIgnoreCase(order.getOrderChannelDivision()) || MCOM.equalsIgnoreCase(order.getSellingChannelType())) {
                associateDetail.setAssociateId(MACYS_71_ASSOC_ID);
            } else if (BLOOMYS_72.equalsIgnoreCase(order.getOrderChannelDivision()) || BLOOMYS_SELLING_CHANNEL_TYPE.contains(order.getSellingChannelType())) {
                associateDetail.setAssociateId(BLOOMYS_72_ASSOC_ID);
            }
            if (StringUtils.isNotEmpty(associateDetail.getAssociateId())) {
                associateDetailList.add(associateDetail);
            }
            if (!CollectionUtils.isEmpty(associateDetailList)) {
                order.setAssociateDetails(associateDetailList);
            }
        }
    }

    public void setCustomerPreferences(Order order) {
        List<CustomerPreferences> customerPreferencesList = new ArrayList<>();
        String value = PICKUP_VALUE;
        if(!CollectionUtils.isEmpty(order.getOrderLines())){
            for (OrderLine line : order.getOrderLines()) {
                if (PICKUP.equalsIgnoreCase(line.getDeliveryType()) && !SDD.equalsIgnoreCase(line.getFulfillmentType())) {
                    CustomerPreferences customerPreferences = new CustomerPreferences();
                    customerPreferences.setName(PICKUP_PERSON);
                    customerPreferences.setType(PICKUP_VALUE);
                    for (Address address : line.getAddressDetails()) {
                        if (StringUtils.isNotEmpty(address.getType()) && ALTPICKUP.equalsIgnoreCase(address.getType())) {
                            value = ALTPICKUP_VALUE;
                            break;
                        }
                    }
                    customerPreferences.setValue(value);
                    customerPreferencesList.add(customerPreferences);
                    break;
                }
            }
            order.setCustomerPreferences(customerPreferencesList);
        }
    }

    /**
     * Method to get businessConfig based on Orderpurpose
     *
     * @param configName
     * @param key
     *
     * @return boolean
     */
    public boolean getBusinessConfigForOrder(String configName,String key) {
        boolean orderValidation = false;
        if(enableR3businessconfig){
            Map<Integer,Map<Object,Object>> bizConfigR3= businessConfigManagerUtil.getConfigByName(configName,new Order());
            Map<Object,Object> response= bizConfigR3.values().stream().findFirst().get();
            Map<String,Object> finalBizConfig = new HashMap<>();
            response.entrySet().forEach(
                            (x)->finalBizConfig.put(x.getKey().toString(),x.getValue()));

            if (CollectionUtils.isEmpty(bizConfigR3)) {
                if (!ObjectUtils.notEqual(key, PAYMENT_LOCK)) {
                    throwBusinessError(ORDER_LOCK, BIZ_ERROR_PAYMENT_LOCK, getDefaultHttpHeaders(), OrderErrorCodes.BIZCONFIG_ERROR);
                } else if (!ObjectUtils.notEqual(key, CATALOG_ENRICH)) {
                    throwBusinessError(ORDER_ENRICH, BIZ_ERROR_CATALOG_ENRICH, getDefaultHttpHeaders(), OrderErrorCodes.BIZCONFIG_ERROR);
                } else if (!ObjectUtils.notEqual(key, ORDER_RESPONSE)) {
                    throwBusinessError(ORDER_PUBLISHCREATESUCCESS, BIZ_ERROR_ORDER_RESPONSE, getDefaultHttpHeaders(), OrderErrorCodes.BIZCONFIG_ERROR);
                } else if (!ObjectUtils.notEqual(key, PARTNER_FULFILLMENT_ID_LOCK_ATTRIBUTE)) {
                    throwBusinessError(PARTNER_FULFILLMENT_ID_LOCK_ATTRIBUTE, BIZ_ERROR_PARTNER_FULFILLMENT_ID_LOCK, getDefaultHttpHeaders(), OrderErrorCodes.BIZCONFIG_ERROR);
                }
            }
            orderValidation = finalBizConfig.containsKey(key) && (boolean) finalBizConfig.get(key);
        }else{
        Map<String, Object> bizConfigMap = businessConfigManagerUtil.getConfigByNameTypeSellingDivAndSellingChnl(
                configName, CONFIG_TYPE, SELLING_DIVISION);
        if (CollectionUtils.isEmpty(bizConfigMap)) {
            if (!ObjectUtils.notEqual(key, PAYMENT_LOCK)) {
                throwBusinessError(ORDER_LOCK, BIZ_ERROR_PAYMENT_LOCK, getDefaultHttpHeaders(), OrderErrorCodes.BIZCONFIG_ERROR);
            } else if (!ObjectUtils.notEqual(key, CATALOG_ENRICH)) {
                throwBusinessError(ORDER_ENRICH, BIZ_ERROR_CATALOG_ENRICH, getDefaultHttpHeaders(), OrderErrorCodes.BIZCONFIG_ERROR);
            } else if (!ObjectUtils.notEqual(key, ORDER_RESPONSE)) {
                throwBusinessError(ORDER_PUBLISHCREATESUCCESS, BIZ_ERROR_ORDER_RESPONSE, getDefaultHttpHeaders(), OrderErrorCodes.BIZCONFIG_ERROR);
            } else if (!ObjectUtils.notEqual(key, PARTNER_FULFILLMENT_ID_LOCK_ATTRIBUTE)) {
                throwBusinessError(PARTNER_FULFILLMENT_ID_LOCK_ATTRIBUTE, BIZ_ERROR_PARTNER_FULFILLMENT_ID_LOCK, getDefaultHttpHeaders(), OrderErrorCodes.BIZCONFIG_ERROR);
            }
        }
        orderValidation = bizConfigMap.containsKey(key) && (boolean) bizConfigMap.get(key);


    }


        return orderValidation;
    }

    /**
     * Method to check is Specific Feature enabled in Profile
     *
     * @param order
     * @param featureName
     *
     * @return boolean
     */
    public boolean isProfileFeatureActive(Order order, String featureName) {
        if(!killSwitchR3) {
            return profileEvaluatorUtil.getProfileRulesV2(order.getProfileId()).
                    get(featureName).equals(true);
        }
        else {
            return profileEvaluatorUtil.getProfileRules(ORDER, order.getProfileId(),
                    order.getProfileVersion()).get(featureName).equals(true);
        }
    }

    /**
     * Method to fetch property based on search String
     *
     * @param searchString
     * @return String
     */
    public String getProperty(String searchString) {

        return propertyMap.entrySet().stream()
            .filter(e -> (e.getKey().toLowerCase().contains(searchString.toLowerCase())))
            .map(Map.Entry::getValue).findFirst().orElse(null);
    }

    /**
     * Method to fetch configType for Biz Config
     *
     * @param order
     * @return String
     */
    public String getConfigType(Order order) {
        String configType = null;
        if (StringUtils.isNotEmpty(order.getSellerOrderId())) {
            configType = order.getSourceChannel();
        } else {
            configType = order.getSellingChannelType();
        }
        return configType;
    }

    public boolean restrictEpfForExchange(String orderPurpose, String failedStep, Error errorObject) {

        if (!"EXCHANGE".equalsIgnoreCase(orderPurpose))
            return false;

        if (ORDER_LOGEVENT.equalsIgnoreCase(failedStep) || ORDER_VALIDATE.equalsIgnoreCase(failedStep)
                || ORDER_STAMPPROFILE.equalsIgnoreCase(failedStep))
            return true;

        if (ORDER_CREATE.equalsIgnoreCase(failedStep)) {
            ErrorDetail errorDetail = errorObject.getErrorDetails().stream().findFirst().orElse(null);
            
            if (ObjectUtils.isEmpty(errorDetail))
                return false;

            if(StringUtils.isEmpty(errorDetail.getLocationType()))
                return true;
            
            String failedSubState = errorDetail.getLocationType();
            List orderCollectPrePersistingSteps =
                    List.of("ORDER_DUPLICATECHECK,ORDER_PRICEVALIDATION,ORDER_PRORATIONCALCULATION,ORDER_ADDADDRESS,ORDER_PERSIST".split(","));

            return orderCollectPrePersistingSteps.contains(failedSubState);
        }
        return false;
    }
}


