package com.theono.securitywithjwt.filter;

import com.theono.securitywithjwt.constant.ErrorCase;
import com.theono.securitywithjwt.constant.TokenExpirationTime;
import com.theono.securitywithjwt.exception.ErrorStatusException;
import com.theono.securitywithjwt.model.dto.CustomUserDetails;
import com.theono.securitywithjwt.model.dto.RefreshToken;
import com.theono.securitywithjwt.model.request.LoginRequest;
import com.theono.securitywithjwt.repository.RedisRepository;
import com.theono.securitywithjwt.util.JwtUtil;
import com.theono.securitywithjwt.util.RequestResponseUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

@AllArgsConstructor
@Slf4j
public class LoginAuthenticationFilter extends OncePerRequestFilter {

    private final AntPathRequestMatcher requestMatcher =
            new AntPathRequestMatcher("/login", "POST");
    private AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final RedisRepository redisRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (requestMatcher.matches(request)) {
            LoginRequest loginRequest =
                    RequestResponseUtil.jsonToObject(request.getInputStream(), LoginRequest.class);
            String userId = (loginRequest.getUserId() == null) ? "" : loginRequest.getUserId();
            String password =
                    (loginRequest.getPassword() == null) ? "" : loginRequest.getPassword();

            Authentication authResult = attemptAuthentication(userId, password);
            if (authResult == null) {
                throw new ErrorStatusException(ErrorCase._401_AUTHENTICATION_FAIL);
            }
            successfulAuthentication(response, authResult);
        } else {
            filterChain.doFilter(request, response);
        }
    }

    private Authentication attemptAuthentication(String userId, String password)
            throws ErrorStatusException {
        if (userId.isEmpty() || password.isEmpty()) {
            throw new ErrorStatusException(ErrorCase._400_BAD_LOGIN_REQUEST);
        }
        UsernamePasswordAuthenticationToken authRequest =
                UsernamePasswordAuthenticationToken.unauthenticated(userId, password);
        try {
            return authenticationManager.authenticate(authRequest);
        } catch (AuthenticationException ex) {
            throw new ErrorStatusException(ErrorCase._401_AUTHENTICATION_FAIL);
        }
    }

    private void successfulAuthentication(HttpServletResponse response, Authentication authResult)
            throws ErrorStatusException, IOException {
        CustomUserDetails userDetails = (CustomUserDetails) authResult.getPrincipal();
        String userId = userDetails.getUserEntity().getUserId();
        Collection<? extends GrantedAuthority> authorities = authResult.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        // 토큰 생성 TODO 각자 함수로 만들자 JWTUtil로
        String access =
                jwtUtil.createJwt(
                        "access",
                        userId,
                        role,
                        TokenExpirationTime.ACCESS_TOKEN_EXPIRATION_TIME.getExpirationTime());
        String refresh =
                jwtUtil.createJwt(
                        "refresh",
                        userId,
                        role,
                        TokenExpirationTime.REFRESH_TOKEN_EXPIRATION_TIME.getExpirationTime());

        // redis에 refresh token 저장
        redisRepository.save(
                RefreshToken.builder()
                        .key(userId)
                        .refreshToken(refresh)
                        .expirationTime(
                                jwtUtil.createExpireDate(
                                        TokenExpirationTime.REFRESH_TOKEN_EXPIRATION_TIME
                                                .getExpirationTime()))
                        .build());

        // JSON 응답 생성 TODO 이거 RequestResponseUtil로 옮겨라
        JSONObject jsonResponse = new JSONObject();
        try {
            jsonResponse.put("isSuccess", true);
            jsonResponse.put("userId", userId);
            jsonResponse.put("role", role);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        // 응답 설정
        log.info("header 설정 시작");
        response.addHeader("Authorization", "Bearer " + access);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(jsonResponse.toString());
        response.setStatus(HttpStatus.OK.value());
    }
}
