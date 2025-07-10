package com.popcoadmin.exception.business;

import com.popcoadmin.exception.BaseException;
import com.popcoadmin.exception.ErrorCode;

public class EventOptionNotFoundException extends BaseException {
    public EventOptionNotFoundException() {
        super(ErrorCode.EVENT_OPTION_NOT_FOUND);
    }
}
