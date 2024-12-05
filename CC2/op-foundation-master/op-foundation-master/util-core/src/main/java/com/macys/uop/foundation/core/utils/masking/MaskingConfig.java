package com.macys.uop.foundation.core.utils.masking;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * POJO which holds data masking configuration related attributes.
 * <br>
 * <pre>
 * 	[
 * 		{
 *   		"attributeName": "accountNumber",
 *   		"attributeType": "JSON",
 *   		"dataMaskerClassFQN": "com.macys.uop.foundation.core.utils.masking.CreditCardNumberMasker"
 * 		},
 * 		{
 *   		"attributeName": "accountNumber",
 *   		"attributeType": "XML",
 *   		"dataMaskerClassFQN": "com.macys.uop.foundation.core.utils.masking.CreditCardNumberMasker"
 * 		}
 *	]
 * </pre>
 *
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MaskingConfig {
	/**
	 * Name of Element/Attribute whose value should be masked.
	 */
	private String attributeName;
	
	/**
	 * Type of Element/Attribute whose value should be masked.
	 */
	private String attributeType;
	
	/**
	 * Fully Qualified Class Name of Data Masker instance which implements @link IDataMasker#maskData(String)},
	 * will be used to mask data. 
	 */
	private String dataMaskerClassFQN;
}
