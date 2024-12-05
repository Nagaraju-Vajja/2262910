package com.macys.uop.order.ordercollectorchestrator.enums;

import lombok.Getter;

@Getter
public enum FulfillmentChannel {

    STR1("STR1"),
    EXT("EXT"),
    POOL("POOL"),
    DROPSHIP("DROPSHIP"),
    FACS("FACS"),
    EMAIL("EMAIL"),
    ORDD("ORDD"),
    SPECIAL("SPECIAL"),
    SPEC("SPEC"),
    ORDR("ORDR");

    private final String value;
    FulfillmentChannel(String value) {
        this.value = value;
    }

    public static boolean isValid(String value) {
        for (FulfillmentChannel enumValue : FulfillmentChannel.values()) {
            if (enumValue.getValue().equalsIgnoreCase(value)) {
                return true;
            }
        }
        return false;
    }
}
