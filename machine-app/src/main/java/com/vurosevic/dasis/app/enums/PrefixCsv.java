package com.vurosevic.dasis.app.enums;

public enum PrefixCsv {

    FEATURE("feature"),
    LABEL("label");

    private String prefix;

    PrefixCsv(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }
}
