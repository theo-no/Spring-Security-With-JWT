package com.theono.securitywithjwt.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ErrorResponse {

    private String errorCode;
    private String errorMessage;
}
