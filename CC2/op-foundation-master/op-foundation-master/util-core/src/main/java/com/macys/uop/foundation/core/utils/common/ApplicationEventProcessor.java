package com.macys.uop.foundation.core.utils.common;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.io.CharStreams;
import com.macys.uop.foundation.core.utils.json.JsonUtils;
import com.macys.uop.foundation.core.utils.masking.ApplicationMaskingConfiguration;
import com.macys.uop.foundation.core.utils.masking.IDataMasker;
import com.macys.uop.foundation.core.utils.masking.JsonMasker;
import com.macys.uop.foundation.core.utils.masking.MaskingConfig;
import com.macys.uop.foundation.core.utils.masking.XmlMasker;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Purpose of this class is to listen to Spring Boot Application events.
 * <br>
 * Currently it is listening to Spring Boot {@link ApplicationStartedEvent} through {@link EventListener} and serving the following purpose
 * <br>
 * <ul>
 *  <li>Loading masking configuration and creating/registering default masker instances implementing {@link IDataMasker}</li>
 * </ul>
 *
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ApplicationEventProcessor
{
	private final JsonUtils jsonUtils;
	private final Environment env;
	private final ResourceLoader resourceLoader;
	
	/**
	 * Method which is registered for listening to Spring Boot {@link ApplicationStartedEvent}.
	 * If default.masking.enabled is set to true {@link #populateMaskingConfiguration()} method is called 
	 */
	@EventListener(ApplicationStartedEvent.class)
    public void applicationStarted() {
		int maxLogLineSizeInKb=Integer.parseInt(env.getProperty("maximum.logline.sizein.kb", "255"));
		GlobalApplicationBucket.setMaxLogLineSizeInKb(maxLogLineSizeInKb);
		boolean isMaskingEnabled=Boolean.parseBoolean(env.getProperty("default.masking.enabled", "true"));
		ApplicationMaskingConfiguration.setMaskingEnabled(isMaskingEnabled);
		if(isMaskingEnabled) {
			populateMaskingConfiguration();
		}	
	}
	
	/**
	 * Purpose of this method is to 
	 * <ul>
	 * <li>
	 * Step 1 <p>
	 * read the custom masking configuration file if it is specified in "maskingconfig.filepath" environment variable.
	 * If no custom masking configuration file path is specified, default masking configuration file "masking-config.json" is loaded from resources folder. 
	 * </li>
	 * <li>
	 * Step 2 <p>
	 * Create {@link JsonMasker} and {@link XmlMasker} instance 
	 * </li>
	 * <li>
	 * Step 3 <p>
	 * Register {@link JsonMasker} and {@link XmlMasker} instances in {@link ApplicationMaskingConfiguration} 
	 * </li>
	 * </ul>
	 * If no 
	 */
	private void populateMaskingConfiguration() {

		String maskingFilePath=env.getProperty("maskingconfig.filepath", "classpath:masking-config.json");
		String fileContents=maskingFilePath.contains("classpath:")? getClasspathFileContents(maskingFilePath) : getFileContents(new File(maskingFilePath));
		
		log.debug("Loaded masking configuration : "+fileContents);
		
		Map<String,IDataMasker> xmlMaskConfMap=new HashMap<>();
		Map<String,IDataMasker> jsonMaskConfMap=new HashMap<>();
		List<MaskingConfig> maskingConfigList=jsonUtils.convertJsonValue(fileContents, new TypeReference<List<MaskingConfig>>() {});
		
		if(!CollectionUtils.isEmpty(maskingConfigList)) {
			
			for( MaskingConfig maskingConfig : maskingConfigList) {
				String attributeName=maskingConfig.getAttributeName();
				String attributeType=maskingConfig.getAttributeType();
				String dataMaskerClassFQN=maskingConfig.getDataMaskerClassFQN();
				
				IDataMasker dmInstance = getIDataMaskerInstance(attributeName, dataMaskerClassFQN);
				if(attributeType.equals("XML") && dmInstance!=null) {
					xmlMaskConfMap.put(attributeName, dmInstance);
				} else if(attributeType.equals("JSON") && dmInstance!=null) {
					jsonMaskConfMap.put(attributeName, dmInstance);
				}
			}
			
			if(!CollectionUtils.isEmpty(xmlMaskConfMap)) {
				ApplicationMaskingConfiguration.setXmlMaskConfMap(xmlMaskConfMap);
			}
				
			if(!CollectionUtils.isEmpty(jsonMaskConfMap)) {
				ApplicationMaskingConfiguration.setJsonMaskConfMap(jsonMaskConfMap);
			}
		}
		else {
			log.warn("Please verify masking configuration file. No masking configuration loaded.");
		}
	}
	
	/**
	 * Construct {@link IDataMasker} instance by calling {@link Class#forName(String)} on input dataMaskerClassFQN value  
	 * 
	 * @param attributeName Element name 
	 * 
	 * @param dataMaskerClassFQN Fully qualified data masker class name
	 * 
	 * @return {@link IDataMasker} instance. In case of any exception null is returned.
	 */
	private IDataMasker getIDataMaskerInstance(String attributeName, String dataMaskerClassFQN) {
		IDataMasker dmInstance = null;
		try {
			dmInstance = (IDataMasker) Class.forName(dataMaskerClassFQN).getDeclaredConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException | SecurityException
				| ClassNotFoundException e) {
			log.error("Loading masking configuration. Unable to instantiate class "+dataMaskerClassFQN+".Excluding element :"+attributeName+".Please verify configuration.");
		}
		return dmInstance;
	}
	
	/**
	 * Reads the contents of a file from classpath into a String. 
	 * <p>
	 * {@link IllegalArgumentException} is thrown if input filePath is null 
	 * 
	 * @param filePath
	 * 
	 * @return String content. null in case of {@link IOException}
	 */
	private String getClasspathFileContents(String filePath) {
		Assert.notNull(filePath, "'filePath' must not be null");
		String content=null;
		try {
			Resource resource=resourceLoader.getResource(filePath);
			InputStream resourcee = resource.getInputStream();
			try (final Reader reader = new InputStreamReader(resourcee)) {
				content = CharStreams.toString(reader);
			}
		} catch (IOException e) {
			log.error("IOException reading contents from resource", e);
		}
		return content;
	}
	
	/**
	 * Reads the contents of a file into a String. 
	 * <p>
	 * {@link IllegalArgumentException} is thrown if input file is null 
	 * @param file to read
	 * 
	 * @return String content. null in case of {@link IOException}
	 */
	private String getFileContents(File file) {
		Assert.notNull(file, "'file' must not be null");
		String content=null;
		try {
			content=FileUtils.readFileToString(file, StandardCharsets.UTF_8.name());
		} catch (IOException e) {
			log.error("IOException for filePath="+file.getAbsolutePath(), e);
		}
		return content;
	}
}
