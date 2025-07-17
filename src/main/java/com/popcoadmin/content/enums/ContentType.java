package com.popcoadmin.content.enums;

import lombok.Getter;

@Getter
public enum ContentType {
    MOVIE("영화"),
    SERIES("시리즈");

    private final String displayName;

    ContentType(String displayName) {
        this.displayName = displayName;
    }
}
