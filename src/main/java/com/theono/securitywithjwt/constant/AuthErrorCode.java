package com.theono.securitywithjwt.constant;

import lombok.Getter;

@Getter
public enum AuthErrorCode {
    ACCESS_TOKEN_EXPIRED("Auth-001"),
    INVALID_ACCESS_TOKEN("Auth-002"),
    REFRESH_TOKEN_EXPIRED("Auth-003"),
    INVALID_REFRESH_TOKEN("Auth-004"),
    NOT_FOUND_REFRESH_TOKEN("Auth-005"),
    REFRESH_TOKEN_NULL("Auth-006"),
    USERID_NULL("Auth-007");

    private final String errorCode;

    AuthErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}
