package com.theono.securitywithjwt.service;

import com.theono.securitywithjwt.constant.AuthErrorCode;
import com.theono.securitywithjwt.constant.TokenExpirationTime;
import com.theono.securitywithjwt.model.dto.RefreshToken;
import com.theono.securitywithjwt.repository.RedisRepository;
import com.theono.securitywithjwt.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ReissueService {

    private final JwtUtil jwtUtil;
    private final RedisRepository redisRepository;

    public ReissueService(JwtUtil jwtUtil, RedisRepository redisRepository) {
        this.jwtUtil = jwtUtil;
        this.redisRepository = redisRepository;
    }

    public ResponseEntity<?> reissue(HttpServletRequest request) {
        // TODO Redis 접근할 수 있는 key가 userId -> 추후 암호화 예정
        String userId = request.getHeader("userId");

        if (userId == null) {
            // 유저 정보 null
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .header("errorCode", AuthErrorCode.USERID_NULL.getErrorCode())
                    .build();
        }

        Optional<RefreshToken> result = redisRepository.findById(userId);

        if (result.isEmpty()) {
            // redis에 없음
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .header("errorCode", AuthErrorCode.NOT_FOUND_REFRESH_TOKEN.getErrorCode())
                    .build();
        }

        String refreshToken = result.get().getRefreshToken();
        if (refreshToken == null) {
            // response status code
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .header("errorCode", AuthErrorCode.REFRESH_TOKEN_NULL.getErrorCode())
                    .build();
        }

        // expired check
        try {
            jwtUtil.isExpired(refreshToken);
        } catch (ExpiredJwtException e) {
            // 여기서 redis에 있는 refresh token 삭제
            redisRepository.deleteById(userId);
            // response status code
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .header("errorCode", AuthErrorCode.REFRESH_TOKEN_EXPIRED.getErrorCode())
                    .build();
        }

        // 토큰이 refresh인지 확인 (발급시 페이로드에 명시)
        String category = jwtUtil.getCategory(refreshToken);

        if (!category.equals("refresh")) {

            // response status code
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .header("errorCode", AuthErrorCode.INVALID_REFRESH_TOKEN.getErrorCode())
                    .build();
        }

        userId = jwtUtil.getUserId(refreshToken);
        String role = jwtUtil.getRole(refreshToken);

        // make new JWT
        String newAccess =
                jwtUtil.createJwt(
                        "access",
                        userId,
                        role,
                        TokenExpirationTime.ACCESS_TOKEN_EXPIRATION_TIME.getExpirationTime());
        String newRefresh =
                jwtUtil.createJwt(
                        "refresh",
                        userId,
                        role,
                        TokenExpirationTime.REFRESH_TOKEN_EXPIRATION_TIME.getExpirationTime());

        // redis에 refresh token 저장
        redisRepository.save(
                RefreshToken.builder()
                        .key(userId)
                        .refreshToken(newRefresh)
                        .expirationTime(
                                jwtUtil.createExpireDate(
                                        TokenExpirationTime.REFRESH_TOKEN_EXPIRATION_TIME
                                                .getExpirationTime()))
                        .build());

        return ResponseEntity.ok().header("Authorization", "Bearer " + newAccess).build();
    }
}
