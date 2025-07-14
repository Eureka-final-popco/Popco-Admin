package com.popcoadmin.exception.business;

import com.popcoadmin.exception.BusinessException;
import com.popcoadmin.exception.ErrorCode;

public class DuplicateQuestionOrderException extends BusinessException {
    public DuplicateQuestionOrderException() {
        super(ErrorCode.DUPLICATE_QUESTION_ORDER);
    }
    public DuplicateQuestionOrderException(String message) {
        super(ErrorCode.DUPLICATE_QUESTION_ORDER, message);
    }
}
