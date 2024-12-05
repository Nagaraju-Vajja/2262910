package com.macys.uop.foundation.core.utils.epf;

import java.util.Map;

import com.macys.uop.foundation.core.utils.exception.Error;

/**
 * Interface contract for publishing non retriable EPF message.
 *
 */
public interface EPFMessagePublisher {

	/**
	 * Method helps in publishing non retriable EPF message asynchronously
	 * 
	 * @param error {@link Error}
	 * @param statusCode EPF status Code
	 * @param headers Map http/message headers
	 * @param additionalEPFInfo Map that contains additional information for populating EPF message. 
	 */
	void publishEPFMessage(Error error, Integer statusCode, Map<String,String> headers, Map<String,String> additionalEPFInfo);
}
