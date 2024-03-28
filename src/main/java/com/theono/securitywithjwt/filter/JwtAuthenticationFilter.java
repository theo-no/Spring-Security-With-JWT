package com.theono.securitywithjwt.filter;

import com.theono.securitywithjwt.constant.AuthErrorCode;
import com.theono.securitywithjwt.model.entity.UserEntity;
import com.theono.securitywithjwt.service.CustomUserDetailsService;
import com.theono.securitywithjwt.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private SecurityContextHolderStrategy securityContextHolderStrategy =
            SecurityContextHolder.getContextHolderStrategy();
    private SecurityContextRepository securityContextRepository =
            new RequestAttributeSecurityContextRepository();
    private CustomUserDetailsService userDetailsSService;
    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, CustomUserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsSService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if ("/reissue".equals(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        // 헤더에서 access키에 담긴 토큰을 꺼냄
        String authorization = request.getHeader("Authorization");
        log.info("Authorization info : {}", authorization);

        // 토큰이 없다면 다음 필터로 넘김
        if (authorization == null) {

            filterChain.doFilter(request, response);

            return;
        }

        String accessToken = authorization.substring(7);
        log.info("access token : {}", accessToken);

        if (accessToken.equals("null")) {

            filterChain.doFilter(request, response);

            return;
        }

        // 토큰 만료 여부 확인, 만료시 다음 필터로 넘기지 않음
        try {
            jwtUtil.isExpired(accessToken);
        } catch (ExpiredJwtException e) {

            // response header
            response.setHeader("errorCode", AuthErrorCode.ACCESS_TOKEN_EXPIRED.getErrorCode());

            // response status code
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // 토큰이 access인지 확인 (발급시 페이로드에 명시)
        String category = jwtUtil.getCategory(accessToken);

        if (!category.equals("access")) {

            // response header
            response.setHeader("errorCode", AuthErrorCode.INVALID_ACCESS_TOKEN.getErrorCode());

            // response status code
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // userId, role 값을 획득
        String userId = jwtUtil.getUserId(accessToken);
        String role = jwtUtil.getRole(accessToken);
        log.info("role : {}", role);

        UserEntity userEntity = UserEntity.builder().userId(userId).role(role).build();

        Authentication authResult = authenticate(userId);
        onSuccessfulAuthentication(authResult, request, response);

        filterChain.doFilter(request, response);
    }

    private Authentication authenticate(String userId) throws RuntimeException {
        try {
            UserDetails userDetails = userDetailsSService.loadUserByUsername(userId);

            return new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private void onSuccessfulAuthentication(
            Authentication authResult, HttpServletRequest request, HttpServletResponse response) {
        SecurityContext securityContext = securityContextHolderStrategy.createEmptyContext();
        securityContext.setAuthentication(authResult);
        securityContextRepository.saveContext(securityContext, request, response);
    }
}
