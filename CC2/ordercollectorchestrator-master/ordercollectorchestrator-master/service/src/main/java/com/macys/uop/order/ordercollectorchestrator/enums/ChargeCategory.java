package com.macys.uop.order.ordercollectorchestrator.enums;

import lombok.Getter;

@Getter
public enum ChargeCategory {

    CHARGE("CHARGE"),
    DISCOUNT("DISCOUNT");

    private final String value;
    ChargeCategory(String value) {
        this.value = value;
    }
    public static boolean isValid(String value) {
        for (ChargeCategory enumValue : ChargeCategory.values()) {
            if (enumValue.getValue().equalsIgnoreCase(value)) {
                return true;
            }
        }
        return false;
    }
}
