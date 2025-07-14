package com.popcoadmin.exception.business;

import com.popcoadmin.exception.BusinessException;
import com.popcoadmin.exception.ErrorCode;

public class QuizOptionNotFoundException extends BusinessException {
    public QuizOptionNotFoundException() {
        super(ErrorCode.QUIZ_OPTION_NOT_FOUND);
    }

    public QuizOptionNotFoundException(String message) {
        super(ErrorCode.QUIZ_OPTION_NOT_FOUND, message);
    }
}
