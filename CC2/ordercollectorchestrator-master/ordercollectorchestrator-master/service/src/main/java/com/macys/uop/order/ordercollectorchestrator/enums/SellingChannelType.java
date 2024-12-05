package com.macys.uop.order.ordercollectorchestrator.enums;

import lombok.Getter;

@Getter
public enum SellingChannelType {
    MCOM("MCOM"),
    BCOM("BCOM"),
    ESEND("ESEND");

    private final String value;

    SellingChannelType(String value) {
        this.value = value;
    }

    public static boolean isValid(String value) {
        for (SellingChannelType enumValue : SellingChannelType.values()) {
            if (enumValue.getValue().equalsIgnoreCase(value)) {
                return true;
            }
        }
        return false;
    }
}
