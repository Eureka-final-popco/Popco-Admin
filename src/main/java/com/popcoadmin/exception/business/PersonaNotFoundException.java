package com.popcoadmin.exception.business;

import com.popcoadmin.exception.BusinessException;
import com.popcoadmin.exception.ErrorCode;

public class PersonaNotFoundException extends BusinessException {
    public PersonaNotFoundException() {
        super(ErrorCode.PERSONA_NOT_FOUND);
    }

    public PersonaNotFoundException(String message) {
        super(ErrorCode.PERSONA_NOT_FOUND, message);
    }
}
