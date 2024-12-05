package com.macys.uop.foundation.core.utils.json;

import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;

/**
 * The main objective of this interface is to provide common helper methods to manipulate JSON. 
 * <br>
 * This interface mostly hides underlying JSON library usage.  
 * 
 */
public interface JsonUtils {
	
	/**
	 * Converts the Object to Json String
	 * 
	 * @param obj input Object
	 * 
	 * @return Json String. Returns null in case of exception.
	 */
	String convertToJson(Object obj);
	
	/**
	 * Converts the Object to Json String Pretty
	 * 
	 * @param obj input Object
	 * 
	 * @return Json Pretty String. Returns null in case of exception.
	 */
	String convertToJsonPretty(Object obj);
	
	/**
	 * Takes Json String as input and convert to Object of required type.
	 * 
	 * @param <T> Type of the Object to be converted.
	 * @param json Input Json String
	 * 
	 * @param requiredType Type of the Object to be converted.
	 * 
	 * @return Converted object of type T
	 */
	<T> T convertFromJson(String json, Class<T> requiredType);
	
	/**
	 * Takes an Object and Convert it to Map
	 * 
	 * @param fromValue Object
	 * 
	 * @return Map<String,Object>
	 */
	Map<String,Object> convertValue(Object fromValue);
	
	
	/**
	 * Takes Json String and Convert it to Map<String,String>
	 * 
	 * @param json String
	 * 
	 * @return Map<String,String>
	 */
	Map<String,String> convertJsonValue(String json);
	
	/**
	 * This generic method helps in converting a json string to a Object of type T with help of TypeReference
	 * 
	 * @param <T> return type
	 * @param json input string
	 * @param valueTypeRef of type T
	 * 
	 * @return T
	 */
	public <T> T convertJsonValue(String json, TypeReference<T> valueTypeRef);
}
