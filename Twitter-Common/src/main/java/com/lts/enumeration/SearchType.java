package com.lts.enumeration;

public enum SearchType {
    USER("user"),
    TWEET("tweet"),
    LIVE("live"),
    LIST("list");

    private String value;

    SearchType(String value) {
        this.value = value;
    }
    public String getValue() {
        return value;
    }
    public static SearchType fromValue(String value) {
        for (SearchType type : SearchType.values()) {
            if (type.getValue().equals(value)) {
                return type;
            }
        }
        return  null;
    }
}