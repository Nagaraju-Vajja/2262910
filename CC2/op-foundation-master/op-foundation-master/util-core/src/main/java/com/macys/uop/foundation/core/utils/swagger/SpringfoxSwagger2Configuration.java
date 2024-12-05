package com.macys.uop.foundation.core.utils.swagger;

import static com.macys.uop.foundation.core.utils.Constant.CLIENTID_HDR;
import static com.macys.uop.foundation.core.utils.Constant.CORRELATIONID_HDR;
import static com.macys.uop.foundation.core.utils.Constant.MESSAGEID_HDR;
import static com.macys.uop.foundation.core.utils.Constant.MESSAGEID_HDR_DEFAULT_VALUE;
import static com.macys.uop.foundation.core.utils.Constant.ORDERID_HDR;
import static com.macys.uop.foundation.core.utils.Constant.ORDERID_HDR_DEFAULT_VALUE;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestController;

import com.macys.uop.foundation.core.utils.Constant;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.RequestParameterBuilder;
import springfox.documentation.schema.ScalarType;
import springfox.documentation.service.Contact;
import springfox.documentation.service.ParameterType;
import springfox.documentation.service.RequestParameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * The purpose of this class is to initialize {@link Docket} which is a  builder intended to be the primary interface into the Springfox framework.
 * <br>
 * Domain services can define their own {@link Docket} by disabling {@link SpringfoxSwagger2Configuration} through property <b>default.swagger.configuration.enabled</b>
 * <br>
 * This default implementation creates default place holders for following header parameters in swagger ui.
 * <ul>
 * <li>{@link Constant#CLIENTID_HDR}</li>
 * <li>{@link Constant#CORRELATIONID_HDR}</li>
 * <li>{@link Constant#MESSAGEID_HDR}</li>
 * <li>{@link Constant#ORDERID_HDR}</li>
 * <li></li>
 * </ul> 
 */
@Configuration
@ConditionalOnProperty(name = "default.swagger.configuration.enabled", havingValue = "true", matchIfMissing = true)
public class SpringfoxSwagger2Configuration {
	@Value("${api.title:}")
	private String title;

	@Value("${api.description:}")
	private String description;

	@Value("${api.version:}")
	private String version;

	@Value("${api.termsOfService:}")
	private String termsOfService;

	@Value("${api.license.name:}")
	private String licenseName;

	@Value("${api.license.url:}")
	private String licenseUrl;

	@Value("${api.contact.name:}")
	private String contactName;

	@Value("${api.contact.email:}")
	private String contactEmail;

	@Value("${api.contact.url:}")
	private String contactUrl;

	@Value("${api.restPackage:}")
	private String restPackage;
	
	@Value("${spring.application.name:default}")
	private String applicationName;

	/**
	 * Initializes default {@link Docket} instance through various properties set in application.properties file. 
	 * 
	 * @return {@link Docket}
	 */
	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2).select()
				.apis(StringUtils.hasText(restPackage) ? RequestHandlerSelectors.basePackage(restPackage)
						: RequestHandlerSelectors.withClassAnnotation(RestController.class))
				.paths(PathSelectors.any()).build()
				.apiInfo(new ApiInfoBuilder()
						.title(title)
						.description(description)
						.version(version)
						.termsOfServiceUrl(termsOfService)
						.contact(new Contact(contactName, contactUrl, contactEmail))
						.license(licenseName)
						.licenseUrl(licenseUrl)
						.build()).globalRequestParameters(operationParameters());
	}
	
	/**
	 * Populates list of {@link RequestParameter} with default values to be displayed in swagger ui 
	 * 
	 * @return {@link List<RequestParameter>}
	 */
	private List<RequestParameter> operationParameters() {
	    List<RequestParameter> headers = new ArrayList<>();
	    headers.add(new RequestParameterBuilder()
	            .name(CLIENTID_HDR)  
	            .in(ParameterType.HEADER)
	            .query(q -> q.defaultValue(applicationName)
	                         .model(modelSpecificationBuilder -> modelSpecificationBuilder.scalarModel(ScalarType.STRING)))
	            .required(false)
	            .build());

	    headers.add(new RequestParameterBuilder()
	            .name(CORRELATIONID_HDR)  
	            .in(ParameterType.HEADER)
	            .query(q -> q.defaultValue("12345")
	                         .model(modelSpecificationBuilder -> modelSpecificationBuilder.scalarModel(ScalarType.STRING)))
	            .required(false)
	            .build());
	    
	    headers.add(new RequestParameterBuilder()
	            .name(MESSAGEID_HDR)  
	            .in(ParameterType.HEADER)
	            .query(q -> q.defaultValue(MESSAGEID_HDR_DEFAULT_VALUE)
	                         .model(modelSpecificationBuilder -> modelSpecificationBuilder.scalarModel(ScalarType.STRING)))
	            .required(false)
	            .build());
	    
	    headers.add(new RequestParameterBuilder()
	            .name(ORDERID_HDR)  
	            .in(ParameterType.HEADER)
	            .query(q -> q.defaultValue(ORDERID_HDR_DEFAULT_VALUE)
	                         .model(modelSpecificationBuilder -> modelSpecificationBuilder.scalarModel(ScalarType.STRING)))
	            .required(false)
	            .build());
	    return headers;
	}

}
