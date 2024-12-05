package com.macys.uop.order.ordercollectorchestrator.enums;

import lombok.Getter;

@Getter
public enum DeliveryMethod {

    GROUND("GROUND"),
    TWODAYAIR("TWODAYAIR"),
    OVERNIGHT("OVERNIGHT"),
    EMAIL("EMAIL"),
    SAMEDAY("SAMEDAY"),
    GROUND_SHIPPING("GROUND SHIPPING"),
    EXPEDITE("EXPEDITE"),
    PREMIUM("PREMIUM");

    private final String value;
    DeliveryMethod(String value) {
        this.value = value;
    }

    public static boolean isValid(String value) {
        for (DeliveryMethod enumValue : DeliveryMethod.values()) {
            if (enumValue.getValue().equalsIgnoreCase(value)) {
                return true;
            }
        }
        return false;
    }
}
