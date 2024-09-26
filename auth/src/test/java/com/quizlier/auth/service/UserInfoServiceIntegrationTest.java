package com.quizlier.auth.service;

import com.quizlier.auth.AbstractIntegrationTest;
import com.quizlier.auth.repository.UserRepository;
import com.quizlier.common.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class UserInfoServiceIntegrationTest extends AbstractIntegrationTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserInfoService userInfoService;

    @Test
    @Sql(value = {"classpath:/sql/init_script.sql", "classpath:/sql/clean_scripts.sql", "classpath:/sql/insert_users.sql"})
    void loadUserByUsername_success() throws UsernameNotFoundException {
        String username = "johnDoe";

        UserDetails userDetails = userInfoService.loadUserByUsername(username);

        assertNotNull(userDetails);

        Optional<User> user = userRepository.findUserByUsername(username);
        assertTrue(user.isPresent());
        assertEquals("johnDoe", userDetails.getUsername());
    }
}
