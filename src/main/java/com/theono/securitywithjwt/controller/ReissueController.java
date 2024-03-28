package com.theono.securitywithjwt.controller;

import com.theono.securitywithjwt.service.ReissueService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/reissue")
public class ReissueController {

    private final ReissueService reissueService;

    public ReissueController(ReissueService reissueService) {
        this.reissueService = reissueService;
    }

    @GetMapping
    @ResponseBody
    public ResponseEntity<?> reissue(HttpServletRequest request) {
        return reissueService.reissue(request);
    }
}
