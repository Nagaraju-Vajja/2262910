package com.macys.uop.order.ordercollectorchestrator.enums;

import lombok.Getter;

@Getter
public enum FulfillmentType {

    STH("STH"),
    S2AP("S2AP"),
    BOPS("BOPS"),
    BOSS("BOSS"),
    SDD("SDD");

    private final String value;
    FulfillmentType(String value) {
        this.value = value;
    }

    public static boolean isValid(String value) {
        for (FulfillmentType enumValue : FulfillmentType.values()) {
            if (enumValue.getValue().equalsIgnoreCase(value)) {
                return true;
            }
        }
        return false;
    }
}
