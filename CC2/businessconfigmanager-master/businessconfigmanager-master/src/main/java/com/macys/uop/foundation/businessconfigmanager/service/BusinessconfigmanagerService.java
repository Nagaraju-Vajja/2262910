package com.macys.uop.foundation.businessconfigmanager.service;

import com.macys.uop.common.businessconfig.model.BusinessConfigResponse;
import com.macys.uop.common.businessconfig.model.BusinessConfigResponseList;
import com.macys.uop.common.businessconfig.model.BusinessConfigResponseV2;
import com.macys.uop.order.model.Order;
import com.macys.uop.order.model.api.orderdetails.Orderdetails;

import java.util.Map;

public interface BusinessconfigmanagerService {


    BusinessConfigResponseList getConfigByNameSellingDivAndsellingChnl(String configName, String sellingDivision,
                                                                       String sellingChannel);

    BusinessConfigResponse getConfigByNameTypeSellingDivAndSellingChnl(String configName, String configType,
                                                                       String sellingDivision, String sellingChannel);

    BusinessConfigResponseV2 getConfigByNameV2(String configName);

    Map<Integer, String> evaluateBusinessConfig(String configName, Orderdetails order);

    Map<Integer, String> evaluateBusinessConfig(String configName, Order order);

    Map<String, String> evaluateProfile(Orderdetails orderdetails);

    Map<String, String> evaluateProfile(Order order);

    Map<String, Object> getProfile(String profileId);
}
