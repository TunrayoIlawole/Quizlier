package com.quizlier.core.service;

import com.quizlier.auth.utils.UserInfoDetails;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final RestTemplate restTemplate;

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

    public int getHighestScore(String username) {
        try {
            ResponseEntity<Integer> response = restTemplate.getForEntity(AUTH_URL + "/highscore?username=" + username, Integer.class);
            return response.getBody() != null ? response.getBody() : 0;
        } catch (Exception e) {
            return 0;
        }
    }

    public void sendUserHighscore(String username, String highScore) {
        try {
            restTemplate.postForEntity(AUTH_URL + "/highscore?username=" + username + "&score=" + highScore, null, String.class);
        } catch (Exception e) {
            throw e;
        }
    }
}
