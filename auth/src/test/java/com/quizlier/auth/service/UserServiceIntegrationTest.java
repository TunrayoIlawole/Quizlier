package com.quizlier.auth.service;

import com.quizlier.auth.exceptions.AuthenticationFailedException;
import com.quizlier.auth.exceptions.DuplicateUserException;
import com.quizlier.auth.exceptions.UserNotFoundException;
import com.quizlier.auth.repository.UserRepository;
import com.quizlier.common.dto.*;
import com.quizlier.common.entity.User;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class UserServiceIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthService authService;

    static PostgreSQLContainer container = new PostgreSQLContainer("postgres:latest");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", container::getJdbcUrl);
        registry.add("spring.datasource.username", container::getUsername);
        registry.add("spring.datasource.password", container::getPassword);
    }

    @BeforeAll
    static void beforeAll() {
        container.start();
    }

    @AfterAll
    static void afterAll() {
        container.stop();
    }

    @Test
    @Sql(value = {"classpath:/sql/init_script.sql", "classpath:/sql/clean_scripts.sql", "classpath:/sql/insert_users.sql"})
    void createUser_Success() throws DuplicateUserException {

        UserSignupRequest signupRequest = new UserSignupRequest();
        signupRequest.setFirstName("testFirstName");
        signupRequest.setLastName("testLastName");
        signupRequest.setUsername("testUserName");
        signupRequest.setEmail("test@gmail.com");
        signupRequest.setPassword("testPassword");

        UserSignupResponse signupResponse = userService.createPlayerUser(signupRequest, "player");

        assertNotNull(signupResponse);
        assertEquals("testUserName", signupRequest.getUsername());

        Optional<User> savedUser = userRepository.findUserByUsername(signupRequest.getUsername());
        assertTrue(savedUser.isPresent());
        assertEquals("testUserName", savedUser.get().getUsername());
    }

    @Test
    @Sql(value = {"classpath:/sql/init_script.sql", "classpath:/sql/clean_scripts.sql", "classpath:/sql/insert_users.sql"})
    void signInUser_Success() throws UserNotFoundException, AuthenticationFailedException {
        UserLoginRequest loginRequest = new UserLoginRequest();

        loginRequest.setEmail("test@gmail.com");
        loginRequest.setPassword("testPassword");

        Optional<User> userByEmail = userRepository.findUserByEmail(loginRequest.getEmail());

        AuthRequest authRequest = new AuthRequest();
        authRequest.setUsername(userByEmail.get().getUsername());
        authRequest.setPassword(authRequest.getPassword());

        String token = authService.authenticateUser(authRequest);

        assertNotNull(token);

        UserloginResponse userloginResponse = userService.signInPlayer(loginRequest);

        assertNotNull(userloginResponse);
        assertEquals(token, userloginResponse.getToken());
        assertEquals(userByEmail.get().getUsername(), userloginResponse.getUsername());
    }
}
