package com.macys.uop.foundation.businessconfigmanager.mapper;

import com.macys.uop.common.businessconfig.model.BusinessConfigResponse;
import com.macys.uop.common.businessconfig.model.BusinessConfigResponseV2;
import com.macys.uop.foundation.businessconfigmanager.utils.BusinessconfigmanagerConstants;
import com.macys.uop.order.model.Order;
import com.macys.uop.order.model.api.orderdetails.Orderdetails;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Map;

@Mapper(imports = {BusinessconfigmanagerConstants.class },componentModel = "spring")
public interface BusinessConfigMapper {
	
	@Mapping(target = "configName", source = "map", qualifiedByName = "mapConfigName")
	@Mapping(target = "configType", source = "map", qualifiedByName = "mapConfigType")
	@Mapping(target = "configValue", source = "map", qualifiedByName = "mapConfigValue")
	@Mapping(target = "configDesc", source = "map", qualifiedByName = "mapConfigDesc")
	@Mapping(target = "createdBy", source = "map", qualifiedByName = "mapCreatedBy")
	@Mapping(target = "createdTs", source = "map", qualifiedByName = "mapCreatedTs")
	@Mapping(target = "lastUpdatedBy", source = "map", qualifiedByName = "mapLastUpdatedBy")
	@Mapping(target = "lastUpdatedTs", source = "map", qualifiedByName = "mapLastUpdatedTs")
	@Mapping(target = "status", source = "map", qualifiedByName = "mapStatus")
	@Mapping(target = "sellingChannel", source = "map", qualifiedByName = "mapSellingChannel")
	@Mapping(target = "sellingDivision", source = "map", qualifiedByName = "mapSellingDivision")
	BusinessConfigResponse convertMapToObject(Map map);
		
	@Named("mapConfigName")
	default String mapConfigName(Map map) {
		return  (String) map.get(BusinessconfigmanagerConstants.CONFIG_NAME);
	}
	@Named("mapConfigType")
	default String mapConfigType(Map map) {
		return  (String) map.get(BusinessconfigmanagerConstants.CONFIG_TYPE);
	}
	@Named("mapConfigTypes")
	default Object mapConfigTypes(Map map) {
		return  (Object) map.get(BusinessconfigmanagerConstants.CONFIG_TYPES);
	}
	@Named("mapConfigValue")
	default String mapConfigValue(Map map) {
		return  (String) map.get(BusinessconfigmanagerConstants.CONFIG_VALUE);
	}
	@Named("mapConfigDesc")
	default String mapConfigDesc(Map map) {
		return  (String) map.get(BusinessconfigmanagerConstants.CONFIG_DESC);
	}
	@Named("mapCreatedBy")
	default String mapCreatedBy(Map map) {
		return  (String) map.get(BusinessconfigmanagerConstants.CREATED_BY);
	}
	@Named("mapCreatedTs")
	default String mapCreatedTs(Map map) {
		return  (String) map.get(BusinessconfigmanagerConstants.CREATED_TS);
	}
	@Named("mapLastUpdatedBy")
	default String mapLastUpdatedBy(Map map) {
		return  (String) map.get(BusinessconfigmanagerConstants.LAST_UPDATED_BY);
	}
	@Named("mapLastUpdatedTs")
	default String mapLastUpdatedTs(Map map) {
		return  (String) map.get(BusinessconfigmanagerConstants.LAST_UPDATED_TS);
	}
	@Named("mapStatus")
	default String mapStatus(Map map) {
		return  (String) map.get(BusinessconfigmanagerConstants.STATUS);
	}
	@Named("mapSellingChannel")
	default String mapsellingChannel(Map map) {
		return  (String) map.get(BusinessconfigmanagerConstants.SELLING_CHANNEL);
	}
	@Named("mapDomain")
	default String mapDomain(Map map) {
		return  (String) map.get("domain");
	}
	@Named("mapSellingDivision")
	default String mapSellingDivision(Map map) {
		return  (String) map.get(BusinessconfigmanagerConstants.SELLING_DIVISION);
	}

	@Mapping(target = "configName", source = "map", qualifiedByName = "mapConfigName")
	@Mapping(target = "configTypes", source = "map", qualifiedByName = "mapConfigTypes")
	@Mapping(target = "createdBy", source = "map", qualifiedByName = "mapCreatedBy")
	@Mapping(target = "createdTs", source = "map", qualifiedByName = "mapCreatedTs")
	@Mapping(target = "lastUpdatedBy", source = "map", qualifiedByName = "mapLastUpdatedBy")
	@Mapping(target = "lastUpdatedTs", source = "map", qualifiedByName = "mapLastUpdatedTs")
	@Mapping(target = "status", source = "map", qualifiedByName = "mapStatus")
	@Mapping(target = "domain", source = "map", qualifiedByName = "mapDomain")
	BusinessConfigResponseV2 convertMapToObjectV2(Map map);

	Orderdetails maporderToOrderDetails(Order order);
}
