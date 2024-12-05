package com.macys.uop.foundation.core.utils.eventlog;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Class that captures Event Detail information
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventDetail {
	private String referenceId;
	private String referenceType;
}
