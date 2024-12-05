package com.macys.uop.order.ordercollectorchestrator.enums;

import lombok.Getter;

@Getter
public enum LineType {

    PRODUCT("PRODUCT"),
    EGC("EGC"),
    VGC("VGC"),
    CHARIT("CHARIT");

    private final String value;
    LineType(String value) {
        this.value = value;
    }

    public static boolean isValid(String value) {
        for (LineType enumValue : LineType.values()) {
            if (enumValue.getValue().equalsIgnoreCase(value)) {
                return true;
            }
        }
        return false;
    }
}
