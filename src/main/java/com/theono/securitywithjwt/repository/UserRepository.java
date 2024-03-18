package com.theono.securitywithjwt.repository;

import com.theono.securitywithjwt.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Boolean existsByUserId(String userId);
}
