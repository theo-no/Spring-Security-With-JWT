package com.theono.securitywithjwt.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@EnableRedisRepositories
@Configuration
public class RedisConfig {

    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private int port;

    @Bean
    RedisConnectionFactory redisConnectionFactory() {
        // redis 연결 정보(host, port, password)를 지정한다.
        RedisStandaloneConfiguration redisStandaloneConfiguration =
                new RedisStandaloneConfiguration(host, port);
        return new LettuceConnectionFactory(redisStandaloneConfiguration);
    }
}
