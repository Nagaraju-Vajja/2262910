package com.macys.uop.foundation.businessconfigmanager.service.config;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.macys.uop.foundation.businessconfigmanager.dao.BusinessconfigmanagerDao;

@Configuration
@ComponentScan(basePackages = "com.macys.uop.foundation.businessconfigmanager.service")
public class TestConfiguration {
	 @MockBean
	  private BusinessconfigmanagerDao businessconfigmanagerCoreDao;
}
