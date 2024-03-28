package com.theono.securitywithjwt.service;

import com.theono.securitywithjwt.model.dto.UserDto;
import com.theono.securitywithjwt.model.entity.UserEntity;
import com.theono.securitywithjwt.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public ResponseEntity<UserDto> getUser(String userId) {
        UserEntity userEntity = userRepository.findByUserId(userId);
        if (userEntity == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        return ResponseEntity.ok(UserDto.builder().userId(userEntity.getUserId()).build());
    }
}
