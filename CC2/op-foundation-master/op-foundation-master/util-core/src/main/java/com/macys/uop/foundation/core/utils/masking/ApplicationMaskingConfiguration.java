package com.macys.uop.foundation.core.utils.masking;

import java.util.Collections;
import java.util.Map;

import org.springframework.util.CollectionUtils;

/**
 * Objective of this singleton class is to store
 * <br>
 * <ul>
 * <li>Configuration related to XML data masking</li>
 * <li>Configuration related to JSON data masking</li>
 * <li>Default {@link JsonMasker} instance to be used unless specified</li>
 * <li>Default {@link XmlMasker} instance to be used unless specified</li>
 * </ul>
 * <br>
 * Method names are self explanatory. Documentation not needed.
 * 
 * @see {@link IDataMasker} , {@link MaskingConfig}
 */
public class ApplicationMaskingConfiguration 
{
	private static Map<String,IDataMasker> xmlMaskConfMap=null;
	private static Map<String,IDataMasker> jsonMaskConfMap=null;
	private static boolean isMaskingEnabled=true;
	
	private ApplicationMaskingConfiguration() {
	}
	
	public static Map<String, IDataMasker> getXmlMaskConfMap() {
		if(!CollectionUtils.isEmpty(xmlMaskConfMap)) {
			return null;
		}
		return Collections.unmodifiableMap(xmlMaskConfMap);
	}
	public static void setXmlMaskConfMap(Map<String, IDataMasker> xmlMaskConfMap) {
		ApplicationMaskingConfiguration.xmlMaskConfMap = xmlMaskConfMap;
	}
	
	public static Map<String, IDataMasker> getJsonMaskConfMap() {
		if(!CollectionUtils.isEmpty(jsonMaskConfMap)) {
			return null;
		}
		return Collections.unmodifiableMap(jsonMaskConfMap);
	}
	public static void setJsonMaskConfMap(Map<String, IDataMasker> jsonMaskConfMap) {
		ApplicationMaskingConfiguration.jsonMaskConfMap = jsonMaskConfMap;
	}
	
	public static boolean isMaskingEnabled() {
		return isMaskingEnabled;
	}
	public static void setMaskingEnabled(boolean maskingEnabled) {
		isMaskingEnabled=maskingEnabled;
	}
	
	public static IDataMasker getDefaultJsonMaskerInstance() {
		if(jsonMaskConfMap==null) {
			return null;
		}
		return new JsonMasker(Collections.unmodifiableMap(jsonMaskConfMap));
	}
	
	public static IDataMasker getDefaultXmlMaskerInstance() {
		if(xmlMaskConfMap==null) {
			return null;
		}
		return new XmlMasker(Collections.unmodifiableMap(xmlMaskConfMap));
	}
	
}
