package com.quizlier.auth.service;

import com.quizlier.auth.AbstractMockitoJUnitTest;
import com.quizlier.auth.exceptions.AuthenticationFailedException;
import com.quizlier.auth.exceptions.DuplicateUserException;
import com.quizlier.auth.exceptions.UserNotFoundException;
import com.quizlier.auth.mappers.UserMapper;
import com.quizlier.auth.repository.UserRepository;
import com.quizlier.common.dto.*;
import com.quizlier.common.entity.User;
import com.quizlier.common.entity.UserRole;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class UserServiceTest extends AbstractMockitoJUnitTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthService authService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

//    @Test
    void createPlayerUser() {
        UserSignupRequest signupRequest = new UserSignupRequest();
        signupRequest.setEmail("testemail@gmail.com");
        signupRequest.setPassword(RandomStringUtils.randomAlphanumeric(8, 20));
        signupRequest.setUsername(RandomStringUtils.randomAlphanumeric(8));
        signupRequest.setFirstName(RandomStringUtils.randomAlphabetic(7));
        signupRequest.setLastName(RandomStringUtils.randomAlphabetic(10));

        String role = "admin";

        when(userRepository.findUserByUsername(signupRequest.getUsername())).thenReturn(Optional.empty());
        when(userRepository.findUserByEmail(signupRequest.getEmail())).thenReturn(Optional.empty());

        String encodedPassword = passwordEncoder.encode(signupRequest.getPassword());
        when(passwordEncoder.encode(anyString())).thenReturn(encodedPassword);

        UserSignupResponse userSignupResponse = new UserSignupResponse();
        userSignupResponse.setId("4");
        userSignupResponse.setUserRole(UserRole.ADMIN.name());
        userSignupResponse.setEmail(signupRequest.getEmail());
        userSignupResponse.setUsername(signupRequest.getUsername());
        userSignupResponse.setFirstName(signupRequest.getFirstName());
        userSignupResponse.setLastName(signupRequest.getLastName());

        when(userMapper.userToUsersignupresponse(any(User.class))).thenReturn(userSignupResponse);

        var response = userService.createPlayerUser(signupRequest, role);

        assertNotNull(response);
        assertEquals(signupRequest.getUsername(), response.getUsername());
        assertEquals(UserRole.ADMIN.name(), response.getUserRole());

    }

//    @Test
    void createPlayerUser_userExisting() {
        UserSignupRequest signupRequest = new UserSignupRequest();
        signupRequest.setEmail("testexistingemail@gmail.com");
        signupRequest.setPassword(RandomStringUtils.randomAlphanumeric(7, 15));
        signupRequest.setUsername(RandomStringUtils.randomAlphanumeric(8));
        signupRequest.setFirstName(RandomStringUtils.randomAlphabetic(7));
        signupRequest.setLastName(RandomStringUtils.randomAlphabetic(10));

        String role = "admin";

        User user = new User();
        user.setId(1L);
        user.setUsername(signupRequest.getUsername());
        user.setPassword(RandomStringUtils.randomAlphanumeric(8, 60));
        user.setUserRole(UserRole.PLAYER);

        when(userRepository.findUserByUsername(signupRequest.getUsername())).thenReturn(Optional.of(user));

        var response = assertThrows(DuplicateUserException.class, () -> userService.createPlayerUser(signupRequest, role));

        assertEquals("That username is already taken. Please try another one", response.getMessage());
    }

//    @Test
    void signInPlayer() {
        UserLoginRequest loginRequest = new UserLoginRequest();
        loginRequest.setEmail("testemail@gmail.com");
        loginRequest.setPassword(RandomStringUtils.randomAlphanumeric(8, 20));

        User user = new User();
        user.setId(1L);
        user.setEmail(loginRequest.getEmail());
        user.setUsername(RandomStringUtils.randomAlphanumeric(8));
        user.setPassword(RandomStringUtils.randomAlphanumeric(8, 20));
        user.setUserRole(UserRole.ADMIN);


        when(userRepository.findUserByEmail(loginRequest.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())).thenReturn(true);

        AuthRequest authRequest = new AuthRequest();
        authRequest.setUsername(user.getUsername());
        authRequest.setPassword(user.getPassword());

        when(authService.authenticateUser(any(AuthRequest.class))).thenReturn(RandomStringUtils.randomAlphabetic(28));

        String token = authService.authenticateUser(authRequest);
        when(authService.authenticateUser(any(AuthRequest.class))).thenReturn(token);

        UserloginResponse userloginResponse = new UserloginResponse();
        userloginResponse.setUserId("1");
        userloginResponse.setUsername(user.getUsername());
        userloginResponse.setEmail(user.getEmail());
        userloginResponse.setToken(token);

        when(userMapper.userToUserLoginResponse(any(User.class))).thenReturn(userloginResponse);


        var response = userService.signInPlayer(loginRequest);

        assertNotNull(response);
        assertNotNull(response.getToken());
    }

//    @Test
    void signInPlayer_userNotFound() {
        UserLoginRequest loginRequest = new UserLoginRequest();
        loginRequest.setEmail("testnullemail@gmail.com");
        loginRequest.setPassword(RandomStringUtils.randomAlphanumeric(8, 20));

        when(userRepository.findUserByEmail(loginRequest.getEmail())).thenReturn(Optional.empty());

        var response = assertThrows(UserNotFoundException.class, () -> userService.signInPlayer(loginRequest));

        assertEquals("That email address does not exist in our database. Please sign up" , response.getMessage());
    }

//    @Test
    void signInPlayer_authenticationFailed() {
        UserLoginRequest loginRequest = new UserLoginRequest();
        loginRequest.setEmail("testfalseemail@gmail.com");
        loginRequest.setPassword(RandomStringUtils.randomAlphanumeric(8, 20));

        User user = new User();
        user.setId(5L);
        user.setEmail(loginRequest.getEmail());
        user.setUsername(RandomStringUtils.randomAlphanumeric(8));
        user.setPassword(RandomStringUtils.randomAlphanumeric(8, 20));
        user.setUserRole(UserRole.PLAYER);

        when(userRepository.findUserByEmail(loginRequest.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())).thenReturn(false);

        var response = assertThrows(AuthenticationFailedException.class, () -> userService.signInPlayer(loginRequest));

        assertEquals("The email and password provided do not match. Please try again", response.getMessage());
    }
}