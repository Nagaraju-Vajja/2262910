package com.macys.uop.foundation.core.utils.eventlog;

import java.util.Map;

/**
 * Interface contract for publishing {@link EventLogMessage}
 *
 */
public interface EventLogMessagePublisher {
	/**
	 * Method that helps in publishing {@link EventLogMessage} synchronously
	 * 
	 * @param channelName where event log message will be published
	 * @param eventlogMessage {@link EventLogMessage}
	 * @param headers
	 * 
	 * @return published messageid
	 */
	String publishEventLogMessage(String channelName, EventLogMessage eventlogMessage, Map<String, String> headers);
}
