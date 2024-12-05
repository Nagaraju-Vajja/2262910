package com.macys.uop.foundation.core.utils.common;

import java.time.Duration;
import java.time.Instant;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import com.fasterxml.jackson.core.io.JsonStringEncoder;

public interface StringUtil {
		
	/**
	 * Method that will quote text contents using JSON standard quoting and return results as a character array.
	 * 
	 * @param source Input
	 * @return new char[0] : In case of null value or empty string as input else computed result
	 * 
	 * @see {@link JsonStringEncoder#getInstance()#quoteAsString(String)}
	 */
	default char[] quoteAsString(String source) {
		char[] result=new char[0];
		if(!StringUtils.isAllBlank(source)) {
			result=JsonStringEncoder.getInstance().quoteAsString(source);
		}
		return result;
	}
	
	/**
	 * Utility method which constructs text for logging time taken during message processing 
	 * 
	 * @param startTime Instant
	 * @param endTime Instant
	 * 
	 * @return text for logging
	 */
	default String constructMsgProcessingDurationText4Logging(Instant startTime, Instant endTime, String origin) {
		
		Assert.notNull(startTime, "'startTime' must not be null");
		Assert.notNull(endTime, "'endTime' must not be null");
		Assert.hasText(origin, "'origin' must not be empty");
		
		long totalDuration= Duration.between(startTime, endTime).toMillis();
		
		StringBuilder msgBucket=new StringBuilder();
		msgBucket.append(origin+" Message Received At: "+startTime.toString()+" :: ");
		msgBucket.append(origin+" Message Processing Completed At: "+endTime.toString()+" :: ");
		msgBucket.append(origin+" Message Processing Time Taken In Millis: "+totalDuration);
		
		return msgBucket.toString();
	}
	
}
