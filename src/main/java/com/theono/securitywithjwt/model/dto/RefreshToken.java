package com.theono.securitywithjwt.model.dto;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.Date;

@Getter
@Builder
@RedisHash(value = "refreshToken", timeToLive = 3 * 60)
public class RefreshToken {

    @Id private String key;
    private String refreshToken;
    private Date expirationTime;
}
