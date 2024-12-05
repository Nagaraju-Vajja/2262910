package com.macys.uop.foundation.core.utils.validation;

import java.util.Map;

/**
 * Interface contract for message duplication check through Spanner Table directly.
 *
 */
public interface ISpannerDBBasedDuplicationCheck {
	
	/**
	 * Method to be implemented by {@link ISpannerDBBasedDuplicationCheck} implementation classes to check message duplication.
	 * 
	 * @param payload message payload extracted from {@link PubsubMessage}
	 * @param headers message headers extracted from {@link PubsubMessage}
	 * 
	 * @return true if message is duplicate else false
	 */
	boolean isDuplicate(String payload, Map<String,String> headers);

}
