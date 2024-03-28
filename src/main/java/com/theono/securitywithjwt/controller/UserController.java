package com.theono.securitywithjwt.controller;

import com.theono.securitywithjwt.model.dto.UserDto;
import com.theono.securitywithjwt.service.UserService;
import com.theono.securitywithjwt.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public UserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping
    public String userP() {
        return "login/user";
    }

    @GetMapping("/info")
    @ResponseBody
    public ResponseEntity<UserDto> getUser(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        // 토큰이 없다면 다음 필터로 넘김
        if (authorization == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        String accessToken = authorization.substring(7);
        if (accessToken.equals("null")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        return userService.getUser(jwtUtil.getUserId(accessToken));
    }
}
