package com.macys.uop.foundation.core.utils.eventlog;

/**
 * Enumeration for Channel Type Values
 *
 */
public enum ChannelTypeEnum {
	INBOUND("Inbound"),
    OUTBOUND("Outbound"),
    INTERNAL("Internal");

	public final String channel;

    private ChannelTypeEnum(String channel) {
        this.channel = channel;
    }
}
