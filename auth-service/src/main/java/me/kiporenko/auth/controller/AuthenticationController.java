package me.kiporenko.auth.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.kiporenko.auth.domain.dto.AuthenticationRequest;
import me.kiporenko.auth.domain.dto.AuthenticationResponece;
import me.kiporenko.auth.domain.dto.RegisterRequest;
import me.kiporenko.auth.service.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin
@Slf4j
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponece> register(
            @RequestBody RegisterRequest request
    ){
        log.info("Registering new user: {}", request);
        return ResponseEntity.ok(authenticationService.register(request));

    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponece> register(
            @RequestBody AuthenticationRequest request
    ){
        log.info("Authentication of user: {}", request);
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }

}
