package com.theono.securitywithjwt.constant;

import lombok.Getter;

@Getter
public enum TokenExpirationTime {
    ACCESS_TOKEN_EXPIRATION_TIME(60 * 1000L),
    REFRESH_TOKEN_EXPIRATION_TIME(3 * 60 * 1000L);

    private final long expirationTime;

    TokenExpirationTime(long expirationTime) {
        this.expirationTime = expirationTime;
    }
}
