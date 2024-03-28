package com.theono.securitywithjwt.repository;

import com.theono.securitywithjwt.model.dto.RefreshToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RedisRepository extends CrudRepository<RefreshToken, String> {}
