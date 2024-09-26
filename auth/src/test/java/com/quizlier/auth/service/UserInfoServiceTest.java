package com.quizlier.auth.service;

import com.quizlier.auth.AbstractMockitoJUnitTest;
import com.quizlier.auth.repository.UserRepository;
import com.quizlier.auth.utils.UserInfoDetails;
import com.quizlier.common.entity.User;
import com.quizlier.common.entity.UserRole;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


class UserInfoServiceTest extends AbstractMockitoJUnitTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserInfoService userInfoService;

//    @Test
    void loadUserByUsername() {
        String username = RandomStringUtils.randomAlphanumeric(7);

        User user = new User();
        user.setId(1L);
        user.setUsername(username);
        user.setPassword(RandomStringUtils.randomAlphanumeric(8, 20));
        user.setUserRole(UserRole.PLAYER);


        when(userRepository.findUserByUsername(username)).thenReturn(Optional.of(user));

        var response = userInfoService.loadUserByUsername(username);

        assertNotNull(response);
        assertEquals(username, response.getUsername());
        assertEquals("ROLE_PLAYER", response.getAuthorities().stream().map(GrantedAuthority::getAuthority).findFirst().get());

    }

//    @Test
    void loadUserByUsername_userNotFound() {
        String username = RandomStringUtils.randomAlphanumeric(7);
        when(userRepository.findUserByUsername(username)).thenReturn(Optional.empty());

        var response = assertThrows(UsernameNotFoundException.class, () -> userInfoService.loadUserByUsername(username));

        assertEquals("User not found " + username , response.getMessage());
    }
}