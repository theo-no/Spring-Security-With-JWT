package com.theono.securitywithjwt.model.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class LoginResponse {
    private boolean isSuccess;
    private String errorMessage;
    private String userId;
    private String role;
}
