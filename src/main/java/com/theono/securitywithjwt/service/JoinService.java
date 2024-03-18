package com.theono.securitywithjwt.service;

import com.theono.securitywithjwt.dto.JoinDto;
import com.theono.securitywithjwt.entity.UserEntity;
import com.theono.securitywithjwt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JoinService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public void joinProcess(JoinDto joinDto){

        String userId = joinDto.getUserId();
        String password = joinDto.getPassword();

        Boolean isExist = userRepository.existsByUserId(userId);

        if(isExist){
            return;
        }

        userRepository.save(UserEntity.builder()
                .userId(userId)
                .password(bCryptPasswordEncoder.encode(password))
                .role("ROLE_USER")
                .build());
    }


}
