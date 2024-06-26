package com.theono.securitywithjwt.config;

import com.theono.securitywithjwt.filter.HandleErrorStatusExceptionFilter;
import com.theono.securitywithjwt.filter.JwtAuthenticationFilter;
import com.theono.securitywithjwt.filter.JwtRefreshFilter;
import com.theono.securitywithjwt.filter.LoginAuthenticationFilter;
import com.theono.securitywithjwt.repository.RedisRepository;
import com.theono.securitywithjwt.service.CustomUserDetailsService;
import com.theono.securitywithjwt.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    // AuthenticationManager가 인자로 받을 AuthenticationConfiguraion 객체 생성자 주입
    private final JwtUtil jwtUtil;
    private final RedisRepository redisRepository;
    private final CustomUserDetailsService userDetailsService;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.sessionManagement(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                        request -> {
                            request.requestMatchers(HttpMethod.GET)
                                    .permitAll()
                                    .requestMatchers(HttpMethod.GET, "/user/info")
                                    .hasAnyRole("USER")
                                    .requestMatchers(HttpMethod.POST, "/login")
                                    .permitAll();
                        })
                .with(
                        new CustomDsl(),
                        new Customizer<CustomDsl>() {
                            @Override
                            public void customize(CustomDsl customDsl) {}
                        });

        //                .addFilterAt(
        //                        new IdPasswordAuthenticationFilter(
        //                                http.getSharedObject(AuthenticationManager.class),
        //                                jwtUtil,
        //                                redisRepository),
        //                        UsernamePasswordAuthenticationFilter.class)
        //                .addFilterAfter(
        //                        new JwtAuthenticationFilter(jwtUtil, userDetailsService),
        //                        IdPasswordAuthenticationFilter.class);

        return http.build();
    }

    public class CustomDsl extends AbstractHttpConfigurer<CustomDsl, HttpSecurity> {

        @Override
        public void configure(HttpSecurity http) throws Exception {
            http.addFilterAt(
                            new HandleErrorStatusExceptionFilter(),
                            UsernamePasswordAuthenticationFilter.class)
                    .addFilterAfter(
                            new LoginAuthenticationFilter(
                                    http.getSharedObject(AuthenticationManager.class),
                                    jwtUtil,
                                    redisRepository),
                            HandleErrorStatusExceptionFilter.class)
                    .addFilterAfter(
                            new JwtAuthenticationFilter(jwtUtil, userDetailsService),
                            LoginAuthenticationFilter.class)
                    .addFilterAfter(
                            new JwtRefreshFilter(redisRepository, jwtUtil),
                            JwtAuthenticationFilter.class);
        }
    }
}
