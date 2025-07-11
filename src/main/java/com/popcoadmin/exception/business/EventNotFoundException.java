package com.popcoadmin.exception.business;

import com.popcoadmin.exception.BusinessException;
import com.popcoadmin.exception.ErrorCode;

public class EventNotFoundException extends BusinessException {
  public EventNotFoundException() {
    super(ErrorCode.EVENT_NOT_FOUND);
  }

  public EventNotFoundException(String message) {
    super(ErrorCode.EVENT_NOT_FOUND, message);
  }
}
