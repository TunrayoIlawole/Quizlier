package com.quizlier.auth.service;

import com.quizlier.auth.utils.JwtService;
import com.quizlier.common.dto.AuthRequest;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Test
    void authenticateUser() {
        AuthRequest authRequest = new AuthRequest();
        authRequest.setUsername(RandomStringUtils.randomAlphabetic(8));
        authRequest.setPassword(RandomStringUtils.randomAlphanumeric(8, 18));

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword());

        when(authenticationManager.authenticate(usernamePasswordAuthenticationToken).isAuthenticated()).thenReturn(true);

        var response = authService.authenticateUser(authRequest);

        assertNotNull(response);
    }

    @Test
    void authenticateUser_invalidUser() {
        AuthRequest authRequest = new AuthRequest();
        authRequest.setUsername(RandomStringUtils.randomAlphabetic(8));
        authRequest.setPassword(RandomStringUtils.randomAlphanumeric(8, 18));

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword());

        when(authenticationManager.authenticate(usernamePasswordAuthenticationToken).isAuthenticated()).thenReturn(false);

        var response = assertThrows(UsernameNotFoundException.class, () -> authService.authenticateUser(authRequest));

        assertEquals("Invalid user request!", response.getMessage());
    }

    @Test
    void validateToken() {
        String token = RandomStringUtils.randomAlphabetic(24);
        String username = RandomStringUtils.randomAlphabetic(8);

        when(jwtService.extractUsername(token)).thenReturn(username);
//        when(jwtService.validateToken(token, username)).thenReturn(true);

        var response = authService.validateToken(token);

        assertNotNull(response);
        assertEquals(true, response);
    }
}