package com.theono.securitywithjwt.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCase {
    _400_BAD_LOGIN_REQUEST(HttpStatus.BAD_REQUEST, "400-000", "bad login request"),
    _401_AUTHENTICATION_FAIL(HttpStatus.UNAUTHORIZED, "401-000", "authentication fail"),
    _401_ACCESS_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "401-001", "access token is expired"),
    _401_INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "401-002", "invalid access token"),
    _401_REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "401-003", "refresh token is expired"),
    _401_INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "401-004", "invalid refresh token"),
    _401_NOT_FOUND_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "401-005", "not found refresh token"),
    _401_REFRESH_TOKEN_NULL(HttpStatus.UNAUTHORIZED, "401-006", "refresh token null"),
    _401_USERID_NULL(HttpStatus.UNAUTHORIZED, "401-007", "userid null");

    private final HttpStatus status;
    private final String errorCode;
    private final String errorMessage;
}
