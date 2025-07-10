package com.popcoadmin.exception.business;

import com.popcoadmin.exception.BusinessException;
import com.popcoadmin.exception.ErrorCode;

public class EventOptionNotFoundException extends BusinessException {
    public EventOptionNotFoundException() {
        super(ErrorCode.EVENT_OPTION_NOT_FOUND);
    }

    public EventOptionNotFoundException(String message) {
        super(ErrorCode.EVENT_OPTION_NOT_FOUND, message);
    }
}
