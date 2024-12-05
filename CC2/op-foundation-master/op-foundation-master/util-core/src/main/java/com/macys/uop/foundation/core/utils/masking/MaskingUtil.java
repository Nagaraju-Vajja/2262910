package com.macys.uop.foundation.core.utils.masking;

import org.springframework.http.MediaType;

/**
 * Interface to hold common masking utility methods 
 *
 */
public interface MaskingUtil {

	
	/**
	 * Returns default {@link com.macys.uop.foundation.core.utils.masking.JsonMasker} or {@link com.macys.uop.foundation.core.utils.masking.XmlMasker}
	 * <br> instance based on the Content Type i.e application/json or application/xml
	 * 
	 * @param contentType Content Type i.e application/json or application/xml
	 * 
	 * @return {@link IDataMasker} instance
	 */
	default IDataMasker getMaskerInstance(String contentType) {
		IDataMasker dataMasker = null;
		if (MediaType.APPLICATION_JSON_VALUE.equals(contentType)) {
			dataMasker = ApplicationMaskingConfiguration.getDefaultJsonMaskerInstance();
		}
		if (MediaType.APPLICATION_XML_VALUE.equals(contentType)) {
			dataMasker = ApplicationMaskingConfiguration.getDefaultXmlMaskerInstance();
		}
		return dataMasker;
	}
}
