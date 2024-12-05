package com.macys.uop.order.ordercollectorchestrator.enums;

import lombok.Getter;

@Getter
public enum DeliveryType {

    PICKUP("PICKUP"),
    SHIPPING("SHIPPING"),
    ELECTRONIC("ELECTRONIC");

    private final String value;
    DeliveryType(String value) {
        this.value = value;
    }

    public static boolean isValid(String value) {
        for (DeliveryType enumValue : DeliveryType.values()) {
            if (enumValue.getValue().equalsIgnoreCase(value)) {
                return true;
            }
        }
        return false;
    }
}
