package com.macys.uop.order.ordercollectorchestrator.enums;

import lombok.Getter;

@Getter
public enum OrderChannelDivision {
    MCOM("71"),
    BCOM("72");
    private final String value;
    OrderChannelDivision(String value) {
        this.value = value;
    }

    public static boolean isValid(String value) {
        for (OrderChannelDivision enumValue : OrderChannelDivision.values()) {
            if (enumValue.getValue().equalsIgnoreCase(value)) {
                return true;
            }
        }
        return false;
    }
}
