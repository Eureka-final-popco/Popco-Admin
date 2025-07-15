package com.popcoadmin.exception.business;

import com.popcoadmin.exception.BusinessException;
import com.popcoadmin.exception.ErrorCode;

public class QuizQuestionNotFoundException extends BusinessException {
    public QuizQuestionNotFoundException() {
        super(ErrorCode.QUIZ_QUESTION_NOT_FOUND);
    }

    public QuizQuestionNotFoundException(String message) {
        super(ErrorCode.QUIZ_QUESTION_NOT_FOUND, message);
    }
}
