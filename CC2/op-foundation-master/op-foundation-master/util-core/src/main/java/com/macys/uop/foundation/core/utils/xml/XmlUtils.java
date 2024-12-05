package com.macys.uop.foundation.core.utils.xml;

public interface XmlUtils {
	
	/**
	 * Converts the Object to Xml String
	 * 
	 * @param obj input Object
	 * @return Xml String. Returns null in case of exception.
	 */
	String convertToXml(Object obj);
	
	/**
	 * Takes Xml String as input and convert to Object of required type T
	 * 
	 * @param <T> Type of the Object to be converted.
	 * @param xml  Input Xml String
	 * @param requiredType Type of the Object to be converted.
	 * 
	 * @return Converted object of type T
	 */
	<T> T convertFromXml(String xml, Class<T> requiredType);
}
