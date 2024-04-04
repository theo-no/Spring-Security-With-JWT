package com.theono.securitywithjwt.exception;

import com.theono.securitywithjwt.constant.ErrorCase;
import lombok.Getter;

@Getter
public class ErrorStatusException extends RuntimeException {

    private final ErrorCase errorCase;

    public ErrorStatusException(ErrorCase errorCase) {
        super(errorCase.getErrorMessage());
        this.errorCase = errorCase;
    }
}
