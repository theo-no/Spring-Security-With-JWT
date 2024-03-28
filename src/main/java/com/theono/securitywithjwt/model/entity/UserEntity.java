package com.theono.securitywithjwt.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true)
    private String userId;
    private String password;
    private String role;

    public UserEntity(String userId, String password, String role) {
        this.userId = userId;
        this.password = password;
        this.role = role;
    }


}
