package com.popcoadmin.exception.business;

import com.popcoadmin.exception.BusinessException;
import com.popcoadmin.exception.ErrorCode;

public class DuplicateSortOrderException extends BusinessException {
    public DuplicateSortOrderException() {
        super(ErrorCode.DUPLICATE_SORT_ORDER);
    }
    public DuplicateSortOrderException(String message) {
        super(ErrorCode.DUPLICATE_SORT_ORDER, message);
    }
}
