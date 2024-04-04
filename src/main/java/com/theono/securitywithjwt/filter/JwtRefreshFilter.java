package com.theono.securitywithjwt.filter;

import com.theono.securitywithjwt.constant.ErrorCase;
import com.theono.securitywithjwt.constant.TokenExpirationTime;
import com.theono.securitywithjwt.exception.ErrorStatusException;
import com.theono.securitywithjwt.model.dto.RefreshToken;
import com.theono.securitywithjwt.repository.RedisRepository;
import com.theono.securitywithjwt.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@AllArgsConstructor
public class JwtRefreshFilter extends OncePerRequestFilter {

    private final AntPathRequestMatcher requestMatcher =
            new AntPathRequestMatcher("/reissue", "GET");
    private final RedisRepository redisRepository;
    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (requestMatcher.matches(request)) {
            String userId = request.getHeader("userId");
            if (userId == null) throw new ErrorStatusException(ErrorCase._401_USERID_NULL);

            Optional<RefreshToken> result = redisRepository.findById(userId);
            if (result.isEmpty())
                throw new ErrorStatusException(ErrorCase._401_NOT_FOUND_REFRESH_TOKEN);

            String refreshToken = result.get().getRefreshToken();
            if (refreshToken == null)
                throw new ErrorStatusException(ErrorCase._401_REFRESH_TOKEN_NULL);

            try {
                jwtUtil.isExpired(refreshToken);
            } catch (ExpiredJwtException e) {
                throw new ErrorStatusException(ErrorCase._401_REFRESH_TOKEN_EXPIRED);
            }

            String category = jwtUtil.getCategory(refreshToken);
            if (!category.equals("refresh"))
                throw new ErrorStatusException(ErrorCase._401_INVALID_REFRESH_TOKEN);

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

            response.setHeader("Authorization", "Bearer " + newAccess);

        } else {
            filterChain.doFilter(request, response);
        }
    }
}
