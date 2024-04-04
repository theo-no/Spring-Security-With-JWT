package com.theono.securitywithjwt.filter;

import com.theono.securitywithjwt.constant.TokenExpirationTime;
import com.theono.securitywithjwt.model.dto.CustomUserDetails;
import com.theono.securitywithjwt.model.dto.RefreshToken;
import com.theono.securitywithjwt.model.request.LoginRequest;
import com.theono.securitywithjwt.repository.RedisRepository;
import com.theono.securitywithjwt.util.JwtUtil;
import com.theono.securitywithjwt.util.RequestResponseUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import org.json.JSONException;
import org.json.JSONObject;

//public class IdPasswordAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
//
//    private final JwtUtil jwtUtil;
//    private final RedisRepository redisRepository;
//
//    private static final AntPathRequestMatcher DEFAULT_ANT_PATH_REQUEST_MATCHER =
//            new AntPathRequestMatcher("/login", "POST");
//
//    public IdPasswordAuthenticationFilter(
//            AuthenticationManager authenticationManager,
//            JwtUtil jwtUtil,
//            RedisRepository redisRepository) {
//        super(DEFAULT_ANT_PATH_REQUEST_MATCHER, authenticationManager);
//        this.jwtUtil = jwtUtil;
//        this.redisRepository = redisRepository;
//    }
//
//    @Override
//    public Authentication attemptAuthentication(
//            HttpServletRequest request, HttpServletResponse response)
//            throws AuthenticationException, IOException {
//
//        LoginRequest loginRequest = obtainBody(request);
//        if (loginRequest.getUserId() == null) {
//            loginRequest.setUserId("");
//        }
//        if (loginRequest.getPassword() == null) {
//            loginRequest.setPassword("");
//        }
//        System.out.println("null 이냐? " + this.getAuthenticationManager());
//        UsernamePasswordAuthenticationToken authRequest =
//                UsernamePasswordAuthenticationToken.unauthenticated(
//                        loginRequest.getUserId(), loginRequest.getPassword());
//
//        return this.getAuthenticationManager().authenticate(authRequest);
//    }
//
//    @Nullable
//    private LoginRequest obtainBody(HttpServletRequest request) throws IOException {
//        return RequestResponseUtil.jsonToObject(request.getInputStream(), LoginRequest.class);
//    }
//
//    @Override
//    protected void successfulAuthentication(
//            HttpServletRequest request,
//            HttpServletResponse response,
//            FilterChain chain,
//            Authentication authResult)
//            throws IOException {
//
//        CustomUserDetails userDetails = (CustomUserDetails) authResult.getPrincipal();
//        String userId = userDetails.getUserId();
//
//        Collection<? extends GrantedAuthority> authorities = authResult.getAuthorities();
//        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
//        GrantedAuthority auth = iterator.next();
//        String role = auth.getAuthority();
//        // 토큰 생성
//        String access =
//                jwtUtil.createJwt(
//                        "access",
//                        userId,
//                        role,
//                        TokenExpirationTime.ACCESS_TOKEN_EXPIRATION_TIME.getExpirationTime());
//        String refresh =
//                jwtUtil.createJwt(
//                        "refresh",
//                        userId,
//                        role,
//                        TokenExpirationTime.REFRESH_TOKEN_EXPIRATION_TIME.getExpirationTime());
//
//        // redis에 refresh token 저장
//        redisRepository.save(
//                RefreshToken.builder()
//                        .key(userId)
//                        .refreshToken(refresh)
//                        .expirationTime(
//                                jwtUtil.createExpireDate(
//                                        TokenExpirationTime.REFRESH_TOKEN_EXPIRATION_TIME
//                                                .getExpirationTime()))
//                        .build());
//
//        // JSON 응답 생성
//        JSONObject jsonResponse = new JSONObject();
//        try {
//            jsonResponse.put("isSuccess", true);
//            jsonResponse.put("userId", userId);
//            jsonResponse.put("role", role);
//        } catch (JSONException e) {
//            throw new RuntimeException(e);
//        }
//
//        // 응답 설정
//        response.addHeader("Authorization", "Bearer " + access);
//        response.setContentType("application/json");
//        response.setCharacterEncoding("UTF-8");
//        response.getWriter().write(jsonResponse.toString());
//        response.setStatus(HttpStatus.OK.value());
//    }
//
//    @Override
//    protected void unsuccessfulAuthentication(
//            HttpServletRequest request,
//            HttpServletResponse response,
//            AuthenticationException failed)
//            throws IOException {
//        // JSON 응답 생성
//        JSONObject jsonResponse = new JSONObject();
//        try {
//            jsonResponse.put("isSuccess", false);
//            jsonResponse.put("errorMessage", "다시 로그인 해주세요");
//        } catch (JSONException e) {
//            throw new RuntimeException(e);
//        }
//
//        // 응답 설정
//        response.setContentType("application/json");
//        response.setCharacterEncoding("UTF-8");
//        response.getWriter().write(jsonResponse.toString());
//        response.setStatus(HttpStatus.UNAUTHORIZED.value());
//    }
//}
