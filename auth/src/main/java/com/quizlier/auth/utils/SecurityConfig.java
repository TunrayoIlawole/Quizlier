package com.quizlier.auth.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.quizlier.auth.service.UserService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
	
	@Autowired
	private JwtAuthFilter authFilter;
	
	
	@Bean
	public UserDetailsService userDetailsService() {
		return new UserService();
	}
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		
		return http.csrf(csrf -> csrf.disable())
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("api/v1/auth/generateToken", "api/v1/auth/signup/*", "api/v1/auth/signin", "api/v1/auth/validateToken", "api/v1/auth/getUsername", "api/v1/auth/roles", "api/v1/auth/userdetails", "api/v1/auth/welcome", "core/core/api/v1/*", "auth/auth/api/v1/*").permitAll()
						.requestMatchers("api/v1/auth/adminprofile", "api/v1/auth/playerprofile").authenticated()
						)
				.sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authenticationProvider(authenticationProvider())
				.addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class)
				.build();
				
//		return http.csrf().disable()
//				.authorizeHttpRequests()
//				.requestMatchers("api/v1/auth/generateToken", "api/v1/auth/signup", "api/v1/auth/signin", "api/v1/auth/welcome").permitAll()
//				.and()
//				.authorizeHttpRequests().requestMatchers("api/v1/auth/adminprofile", "api/v1/auth/playerprofile").authenticated()
//				.and()
//				.sessionManagement()
//				.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//				.and()
//				.authenticationProvider(authenticationProvider())
//				.addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class)
//				.build();
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(userDetailsService());
		authenticationProvider.setPasswordEncoder(passwordEncoder());
		return authenticationProvider;
	}
	
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}

}
