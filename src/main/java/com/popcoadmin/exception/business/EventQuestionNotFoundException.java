package com.popcoadmin.exception.business;

import com.popcoadmin.exception.BaseException;
import com.popcoadmin.exception.ErrorCode;

public class EventQuestionNotFoundException extends BaseException {
    public EventQuestionNotFoundException() {
        super(ErrorCode.EVENT_QUESTION_NOT_FOUND);
    }
}
