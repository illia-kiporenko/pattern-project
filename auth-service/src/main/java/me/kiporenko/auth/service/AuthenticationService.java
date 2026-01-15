package me.kiporenko.auth.service;

import me.kiporenko.auth.config.JwtService;
import me.kiporenko.auth.domain.dto.AuthenticationRequest;
import me.kiporenko.auth.domain.dto.AuthenticationResponece;
import me.kiporenko.auth.domain.dto.RegisterRequest;
import me.kiporenko.auth.domain.model.Role;
import me.kiporenko.auth.domain.model.User;
import me.kiporenko.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Autowired
    AuthenticationService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }


    public AuthenticationResponece register(RegisterRequest request) {
        var user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .role(Role.USER)
                .password(passwordEncoder.encode(request.getPassword()))
                .build();
        userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponece.builder()
                .token(jwtToken)
                .build();

    }

    public AuthenticationResponece authenticate(AuthenticationRequest request) {

        Authentication authResult = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        System.out.println("Authentication success: " + authResult.isAuthenticated());

        System.out.println("before search");
        var user = userRepository.findByUsername(request.getUsername()).orElseThrow();
        System.out.println("Encoded password in DB: " + user.getPassword());
        System.out.println("Password matches: " + passwordEncoder.matches(request.getPassword(), user.getPassword()));


        var jwtToken = jwtService.generateToken(user);
        System.out.println("JWT token: " + jwtToken);
        return AuthenticationResponece.builder()
                .token(jwtToken)
                .build();
    }
}
