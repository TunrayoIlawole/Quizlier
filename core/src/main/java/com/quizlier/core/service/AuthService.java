package com.quizlier.core.service;

import com.quizlier.auth.utils.UserInfoDetails;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AuthService {

    @Autowired
    private RestTemplate restTemplate;

    private static final String AUTH_URL = "http://auth/auth/api/v1/auth";

    public boolean validateToken(String token) {
        try {
            ResponseEntity<Boolean> response = restTemplate.getForEntity(AUTH_URL + "/validateToken?token=" + token, Boolean.class);

            System.out.println(response.getBody());
            return response.getBody() != null && response.getBody();
        } catch (Exception e) {
            return false;
        }
    }

    public String getUsername(String token) {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(AUTH_URL + "/getUsername?token=" + token, String.class);
            return response.getBody() != null ? response.getBody() : null;
        } catch (Exception e) {
            return null;
        }
    }

    public String getUserRole(String username) {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(AUTH_URL + "/roles?username=" + username, String.class);

            return response.getBody() != null ? response.getBody() : null;
        } catch (Exception e) {
            return null;
        }
    }
}
