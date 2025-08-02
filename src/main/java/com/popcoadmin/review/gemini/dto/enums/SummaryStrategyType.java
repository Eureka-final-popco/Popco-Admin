package com.popcoadmin.review.gemini.dto.enums;

import lombok.Getter;

@Getter
public enum SummaryStrategyType {
    INITIAL("전체"),
    UPDATE_PARTIAL("업데이트");

    private final String displayName;

    SummaryStrategyType(String displayName) {
        this.displayName = displayName;
    }
}
