package com.popcoadmin.exception.business;

import com.popcoadmin.exception.BaseException;
import com.popcoadmin.exception.ErrorCode;

public class EventNotFoundException extends BaseException {
    public EventNotFoundException() {
        super(ErrorCode.EVENT_NOT_FOUND);
    }
}
