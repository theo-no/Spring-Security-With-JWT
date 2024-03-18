package com.theono.securitywithjwt.controller;

import com.theono.securitywithjwt.dto.JoinDto;
import com.theono.securitywithjwt.service.JoinService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
public class JoinController {

    private final JoinService joinService;

    @GetMapping("/join")
    public String joinP(){
        return "join";
    }

    @PostMapping("/join")
    @ResponseBody
    public String joinProcess(@RequestBody JoinDto joinDto){
        joinService.joinProcess(joinDto);
        return "ok";
    }

}
