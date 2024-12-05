package com.macys.uop.foundation.businessconfigmanager.dao.config;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.google.cloud.firestore.Firestore;
import com.macys.uop.foundation.businessconfigmanager.mapper.BusinessConfigMapper;
import com.macys.uop.foundation.businessconfigmanager.utils.BusinessconfigmanagerUtil;

@Configuration
@ComponentScan(basePackages = "com.macys.uop.foundation.businessconfigmanager.dao")
public class TestConfiguration {

	@MockBean
	private CacheManager cacheManager;
	@MockBean
	private BusinessconfigmanagerUtil businessconfigmanagerUtil;
	@MockBean
	private BusinessConfigMapper businessConfigManagerMapper;
	@MockBean
	private Firestore firestore;

}
