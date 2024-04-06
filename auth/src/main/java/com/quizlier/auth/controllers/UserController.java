package com.quizlier.auth.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.quizlier.auth.service.UserService;
import com.quizlier.auth.utils.JwtService;
import com.quizlier.common.dto.AuthRequest;
import com.quizlier.common.dto.UserLoginRequest;
import com.quizlier.common.dto.UserRequest;

@RestController
@RequestMapping("api/v1/auth")
public class UserController {
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private JwtService jwtService;
	
	private final UserService userService;
	
	@Autowired
	public UserController(UserService userService, AuthenticationManager authenticationManager) {
		this.userService = userService;
		this.authenticationManager = authenticationManager;
	}
	
	@GetMapping("/welcome")
	public void test() {
		System.out.println("testing!");
	}
	
	// Test authentication
	@GetMapping("/adminprofile")
	@PreAuthorize("hasAuthority('ROLE_admin')")
	public String adminProfile() {
		return "Welcome to Admin Profile";
	}
	
	// Test authentication
	@GetMapping("/playerprofile")
	@PreAuthorize("hasAuthority('ROLE_player')")
	public String playerProfile() {
		return "Welcome to Player Profile";
	}
	
	@PostMapping("/signup")
	public ResponseEntity signUp(@RequestBody UserRequest userRequest) {
		return userService.createUser(userRequest);
	}
	
	@PostMapping("/signin")
	public ResponseEntity signIn(@RequestBody UserLoginRequest userLoginRequest) {
		return userService.signInUser(userLoginRequest);
	}
	
	@PostMapping("/generateToken")
	public String authenticateAndGetToken(@RequestBody AuthRequest authRequest) {
		Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
		
		if (authentication.isAuthenticated()) {
			return jwtService.generateToken(authRequest.getUsername());
		} else {
			throw new UsernameNotFoundException("Invalid user request!");
		}
	}

}
