package com.macys.uop.foundation.core.utils.validation;

import java.util.Map;

/**
 * Interface contract for message duplication check through REST API.
 *
 */
public interface IRestBasedDuplicationCheck {
	
	/**
	 * Method to be implemented by {@link IRestBasedDuplicationCheck} implementation classes to check message duplication.
	 * 
	 * @param serviceURL REST API Url
	 * @param payload message payload extracted from {@link PubsubMessage} to be sent
	 * @param headers message headers extracted from {@link PubsubMessage} to be sent
	 * 
	 * @return true if message is duplicate else false
	 */
	boolean isDuplicate(String serviceURL, String payload, Map<String,String> headers);
}
