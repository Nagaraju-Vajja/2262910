package com.macys.uop.foundation.businessconfigmanager.utils;

public class BusinessconfigmanagerConstants {
	
	public static final String CONFIG_NAME = "configName";
	public static final String CONFIG_TYPE = "configType";
	public static final String CONFIG_TYPES = "configTypes";
	public static final String CONFIG_VALUE = "configValue";
	public static final String CONFIG_DESC = "configDesc";
	public static final String SELLING_DIVISION = "sellingDivision";
	public static final String SELLING_CHANNEL = "sellingChannel"; 
	public static final String EFFECTIVE_START_TS = "effectiveStartTs";
	public static final String EFFECTIVE_END_TS = "effectiveEndTs";
	public static final String CREATED_BY = "createdBy";
	public static final String CREATED_TS = "createdTs";
	public static final String LAST_UPDATED_BY = "lastUpdatedBy";
	public static final String LAST_UPDATED_TS = "lastUpdatedTs";
	public static final String STATUS = "status";
	public static final String ACTIVE = "active";
	public static final String INACTIVE = "inactive";
	public static final String CONFIGLIST = "configList";
	public static final String CACHE_NAME = "businessconfig";
	public static final String CACHE_NAME_V2 = "businessconfig_v2";
	public static final String CONFIG_DEFINATION = "configDefination";
	public static final String METHOD_DOESNOT_EXIST = "Method does not exist";
	public static final String GET = "get";
	public static final String RETURNS = "returns";

    public static final String CONTEXT_BYNAMESELLINGDIVANDSELLINGCHNL = "Businessconfigmanager-getConfigByNameSellingDivAndSellingChannel";
	public static final String CONTEXT_BYNAMETYPESELLINGDIVISIONANDSELLINGCHANNEL = "Businessconfigmanager-getConfigByNameTypeSellingDivAndSellingChannel";
	public static final String CONTEXT_BYNAME_V2 = "Businessconfig Service-getConfigByName-v2";
	public static final String CONTEXT_BYNAME_V2_DAO = "Businessconfig Dao-getConfigByName-v2";

	public static final String CONTEXT_FIRESTORE = "Businessconfigmanager-createFirestoreBean";
    public static final String DB_ERROR = "DB Error";
    public static final String DATA_FETCH_ERROR = "Error while fetching Business Configuration Details";
    public static final String CACHE_BLOCK = "Data fetching from cache";
    public static final String FIRESTORE_BLOCK = "Data fetching from Firestore DB";
    public static final String CACHE_FIRESTORE_BLOCK = "Data not found in Cache,checking in Firestore DB";
    public static final String CACHE_UPDATE = "Updating the existing Cache";
    public static final String FETCHING_BUSINESSCONFIG="Fetching Businesconfig details for ";
    public static final String UNDERSCORE = "_";
	private BusinessconfigmanagerConstants() {}
}
