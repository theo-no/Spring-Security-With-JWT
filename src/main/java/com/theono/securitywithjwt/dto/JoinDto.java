package com.theono.securitywithjwt.dto;


import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JoinDto {

    private String username;
    private String password;
}
