package com.theono.securitywithjwt.service;

import com.theono.securitywithjwt.model.dto.CustomUserDetails;
import com.theono.securitywithjwt.model.entity.UserEntity;
import com.theono.securitywithjwt.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserEntity userEntity = userRepository.findByUserId(username);
        if (userEntity != null) {
            return new CustomUserDetails(userEntity);
        }

        return null;
    }
}
