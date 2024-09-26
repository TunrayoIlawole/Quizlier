package com.quizlier.auth.service;

import com.quizlier.auth.utils.JwtService;
import com.quizlier.common.dto.AuthRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;

    public String authenticateUser(AuthRequest authRequest) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));

        if (authentication.isAuthenticated()) {
            String token = jwtService.generateToken(authRequest.getUsername());

            return token;

        } else {
            throw new UsernameNotFoundException("Invalid user request!");
        }
    }

    public boolean validateToken(String token) {
        String username = jwtService.extractUsername(token);
        boolean isValid = jwtService.validateToken(token, username);

        return isValid;
    }

    public String fetchUsername(String token) {
        String username = jwtService.extractUsername(token);

        return username;
    }
}
