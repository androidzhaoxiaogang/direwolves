package com.edgar.direwolves.definition;

public enum RateLimitBy {

    /**
     * user
     */
    USER("user"),

    /**
     * token
     */
    TOKEN("token"),

    /**
     * App
     */
    APP_KEY("app_key");

    private final String value;

    RateLimitBy(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}