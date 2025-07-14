package com.popcoadmin.exception.business;

import com.popcoadmin.exception.BusinessException;
import com.popcoadmin.exception.ErrorCode;

public class QuizNotFoundException extends BusinessException {
  public QuizNotFoundException() {
    super(ErrorCode.QUIZ_NOT_FOUND);
  }

  public QuizNotFoundException(String message) {
    super(ErrorCode.QUIZ_NOT_FOUND, message);
  }
}
