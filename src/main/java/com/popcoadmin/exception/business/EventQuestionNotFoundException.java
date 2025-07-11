package com.popcoadmin.exception.business;

import com.popcoadmin.exception.BusinessException;
import com.popcoadmin.exception.ErrorCode;

public class EventQuestionNotFoundException extends BusinessException {
    public EventQuestionNotFoundException() {
        super(ErrorCode.EVENT_QUESTION_NOT_FOUND);
    }

    public EventQuestionNotFoundException(String message) {
        super(ErrorCode.EVENT_QUESTION_NOT_FOUND, message);
    }
}
