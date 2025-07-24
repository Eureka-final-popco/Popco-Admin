package com.popcoadmin.content.entity.enums;

public enum BatchContentType {
    ALL(null),
    MOVIE("movie"),
    TV("tv");

    private final String value;

    BatchContentType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
