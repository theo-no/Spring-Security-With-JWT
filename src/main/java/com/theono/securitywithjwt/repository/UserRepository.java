package com.theono.securitywithjwt.repository;

import com.theono.securitywithjwt.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Boolean existsByUserId(String userId);

    UserEntity findByUserId(String userId);
}
