package com.maids.libms.auth.controller;

import com.maids.libms.auth.dto.*;
import com.maids.libms.auth.service.AuthenticationService;
import com.maids.libms.main.ResponseDto;
import com.maids.libms.patron.PatronService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    final AuthenticationService service;
    final PatronService patronService;

    @PostMapping("/register")
    public ResponseEntity<ResponseDto<String>> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        return service.register(request);
    }

    @PostMapping("/verify")
    public ResponseEntity<ResponseDto<VerifyResponse>> verify(
            HttpServletRequest request,
            HttpServletResponse response,
            @Valid @RequestBody VerifyRequest requestBody
    ) {
        return service.verify(requestBody, request, response);
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseDto<LoginResponse>> authenticate(
            @RequestBody @Valid LoginRequest request
    ) {
        return service.login(request);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ResponseDto<LoginResponse>> refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        return service.refreshToken(request, response);
    }
}
