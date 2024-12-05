package com.macys.uop.foundation.core.utils.web.configuration;

import static com.macys.uop.foundation.core.utils.Constant.BEAN_ID_DEFAULT_REST_TEMPLATE;
import static com.macys.uop.foundation.core.utils.Constant.BEAN_ID_LOAD_BALANCED_REST_TEMPLATE;

import java.net.URLConnection;
import java.util.concurrent.TimeUnit;

import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * Spring configuration class to create RestTemplate Bean.
 * <br>
 * Normal RestTemplate is set as Primary bean.
 *
 */
@Configuration
public class SpringRestConfiguration {
	
	/**
	 * Set the underlying URLConnection's connect timeout (in milliseconds).
	 * A timeout value of 0 specifies an infinite timeout.
	 * <p>Default is the system's default timeout.
	 * @see URLConnection#setConnectTimeout(int)
	 */
	@Value("${default.resttemplate.connectiontimeout.ms:60000}")
	private int connectionTimeOut;
	
	/**
	 * Set the underlying URLConnection's read timeout (in milliseconds).
	 * A timeout value of 0 specifies an infinite timeout.
	 * <p>Default is the system's default timeout.
	 * @see URLConnection#setReadTimeout(int)
	 */
	@Value("${default.resttemplate.readtimeout.ms:60000}")
	private int readTimeOut;
	
	
	@Value("${default.resttemplate.connectionmanager.maxtotal:100}")
	private int connectionManagerMaxTotal;
	
	@Value("${default.resttemplate.connectionmanager.defaultmaxperroute:20}")
	private int connectionManagerDefaultMaxPerRoute;
	
	@Value("${default.resttemplate.requestconfig.sockettimeout:5000}")
	private int requestConfigSocketTimeout;
	
	@Value("${default.resttemplate.requestconfig.connectionrequesttimeout:5000}")
	private int requestConfigConnectionRequestTimeout;

	@Value("${default.resttemplate.requestconfig.connecttimeout:5000}")
	private int requestConfigConnectTimeout;

	/**
	 * Load balanced RestTemplate
	 */
	@Bean(BEAN_ID_LOAD_BALANCED_REST_TEMPLATE)
	@LoadBalanced
	public RestTemplate loadBalancedRestTemplate() {
		SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
	    factory.setConnectTimeout(connectionTimeOut);
	    factory.setReadTimeout(readTimeOut);	 
	    return new RestTemplate(factory);
	}

	/**
	 * Default RestTemplate
	 */
	@Bean(BEAN_ID_DEFAULT_REST_TEMPLATE)
	@Primary
	public RestTemplate defaultRestTemplate() {
		
	    PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(connectionManagerMaxTotal);
        connectionManager.setDefaultMaxPerRoute(connectionManagerDefaultMaxPerRoute);

	    RequestConfig requestConfig = RequestConfig
	        .custom()
	        .setConnectionRequestTimeout(requestConfigConnectionRequestTimeout) // timeout to get connection from pool
	        .setSocketTimeout(requestConfigSocketTimeout) // standard connection timeout
	        .setConnectTimeout(requestConfigConnectTimeout) // standard connection timeout
	        .setExpectContinueEnabled(true)
	        .build();
	
	    HttpClient httpClient = HttpClientBuilder.create()
	                                             .setConnectionManager(connectionManager)
	                                             .setDefaultRequestConfig(requestConfig)
	                                             .evictIdleConnections(30, TimeUnit.SECONDS)
	                                             .evictExpiredConnections()
	                                             .build();
	
	    ClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);

	    return new RestTemplate(requestFactory );

	}
}

