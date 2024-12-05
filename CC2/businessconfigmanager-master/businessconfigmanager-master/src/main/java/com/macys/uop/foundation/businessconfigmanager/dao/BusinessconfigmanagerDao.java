package com.macys.uop.foundation.businessconfigmanager.dao;

import java.util.List;
import java.util.Map;
import com.macys.uop.common.businessconfig.model.BusinessConfigResponse;
import com.macys.uop.common.businessconfig.model.BusinessConfigResponseV2;


public interface BusinessconfigmanagerDao {

	
	List<BusinessConfigResponse> getConfigByNameSellingDivAndSellingChannel(String configName, String sellingDivision, String sellingChannel);

	Map<String, Object> getConfigByNameTypeSellingDivAndSellingChnl(String configName, String configType,
			String sellingDivision, String sellingChannel);

	BusinessConfigResponseV2 getConfigByNameV2(String configName);
	

}
