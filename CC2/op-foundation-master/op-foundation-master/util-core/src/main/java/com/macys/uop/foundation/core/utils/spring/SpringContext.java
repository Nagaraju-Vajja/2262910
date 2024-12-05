package com.macys.uop.foundation.core.utils.spring;

import org.springframework.context.ApplicationContext;

/**
 * Stores the Spring Application Context
 * 
 */
public class SpringContext 
{
	private static ApplicationContext context;
	
	private SpringContext() { }
	
	public static ApplicationContext getContext() {
		return context;
	}

	public static void setContext(ApplicationContext context) {
		SpringContext.context = context;
	}
}
