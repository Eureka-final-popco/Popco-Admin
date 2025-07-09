package com.popcoadmin.exception.business;

import com.popcoadmin.exception.BaseException;
import com.popcoadmin.exception.ErrorCode;

public class PersonaNotFoundException extends BaseException {
    public PersonaNotFoundException() {
        super(ErrorCode.PERSONA_NOT_FOUND);
    }

    public PersonaNotFoundException(String message) {
        super(ErrorCode.PERSONA_NOT_FOUND, message);
    }
}
