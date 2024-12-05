package com.macys.uop.foundation.core.utils.masking;

/**
 * Data masking implementation which masks credit card number. 
 *
 */
public class CreditCardNumberMasker implements IDataMasker {
	
	private static final int CONSTANT_VALUE_4=4;
	private static final int CONSTANT_VALUE_15=15;
	private static final int CONSTANT_VALUE_18=18;
	
	private final String[] masks = {"","*","**","***","****",
			"*****","******","*******","********","*********",
			"**********","***********","************","*************","**************",
			"***************"};
	
	/**
	 * Method which masks element value. Can be any text.
	 *  
	 * {@link IDataMasker#maskData(String)} 
	 */
	@Override
	public String maskData(String data) {
		if (data == null) {
			return "";
		}
		String ret = data.trim();
		if (ret.length() <= CONSTANT_VALUE_4) {
			return masks[ret.length()];
		}
		int scrabblerDigits = (ret.length() <= CONSTANT_VALUE_18) ? ret.length() - CONSTANT_VALUE_4 : CONSTANT_VALUE_15;
		return masks[scrabblerDigits]+ret.substring(ret.length() - CONSTANT_VALUE_4);
	}

}
