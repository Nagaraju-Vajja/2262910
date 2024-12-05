package com.macys.uop.foundation.core.utils.masking;

/**
 * All data masking implementations should implement this Interface.

 * @see {@link JsonMasker} {@link XmlMasker} {@link CreditCardNumberMasker}
 *
 */
public interface IDataMasker {
	/**
	 * Masks data. 
	 * <p>
	 * Data can be Json, Xml, plain text etc
	 * 
	 * @param data input
	 * 
	 * @return masked string
	 */
	String maskData(String data);
}
