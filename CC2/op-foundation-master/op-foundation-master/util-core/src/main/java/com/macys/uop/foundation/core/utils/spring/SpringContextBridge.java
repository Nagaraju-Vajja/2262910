package com.macys.uop.foundation.core.utils.spring;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
 
/**
 * Helps in accessing Spring Beans from Non-Spring environment.
 * <br>
 * This class should be used in rare cases.
 */
@Component
public class SpringContextBridge implements ApplicationContextAware {
     
    /**
     * Get Bean of Type T from Spring Application Context
     * 
     * @param <T>
     * @param beanClass
     * 
     * @return Spring Bean
     */
    public static <T extends Object> T getBean(Class<T> beanClass) {
        return SpringContext.getContext().getBean(beanClass);
    }
    
    /**
     * Get property from Spring Application Context 
     * 
     * @param <T>
     * @param beanClass
     * 
     * @return Spring Bean
     */
    public static String getProperty(String propertyName) {
        return SpringContext.getContext().getEnvironment().getProperty(propertyName);
    }
     
    /**
     *Set the Spring Application Context
     */
    @Override
    public void setApplicationContext(ApplicationContext context) {
        SpringContext.setContext(context);
    }
}
