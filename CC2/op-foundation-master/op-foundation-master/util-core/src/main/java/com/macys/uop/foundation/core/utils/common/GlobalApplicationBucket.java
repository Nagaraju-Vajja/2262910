package com.macys.uop.foundation.core.utils.common;

/**
 * The purpose of this class is to keep parameter values 
 * that are required to be accessed by Non-Spring classes. <br> 
 * These values can be populated at application startup events. <br>
 * Use Cases : 
 * <ul>
 * <li>Value of a property defined in application.properties</li>
 * </ul>
 * 
 * @see ApplicationEventProcessor
 * @see com.macys.uop.foundation.core.utils.logging.LogMessage
 */
public class GlobalApplicationBucket {
	
	private GlobalApplicationBucket() { }
	
	private static int maxLogLineSizeInKb;

	public static int getMaxLogLineSizeInKb() {
		return maxLogLineSizeInKb;
	}

	public static void setMaxLogLineSizeInKb(int maxLogLineSizeInKb) {
		GlobalApplicationBucket.maxLogLineSizeInKb = maxLogLineSizeInKb;
	}
}
