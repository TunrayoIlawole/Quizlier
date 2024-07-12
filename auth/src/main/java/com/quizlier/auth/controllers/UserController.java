package com.quizlier.auth.controllers;

import com.quizlier.auth.exceptions.AuthenticationFailedException;
import com.quizlier.auth.exceptions.DuplicateUserException;
import com.quizlier.auth.exceptions.UserNotFoundException;
import com.quizlier.auth.utils.UserInfoDetails;
import com.quizlier.common.entity.User;
import com.quizlier.common.vo.ResponseData;
import com.quizlier.common.vo.ServiceMessages;
import com.quizlier.common.vo.ServiceStatusCodes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import com.quizlier.auth.service.UserService;
import com.quizlier.auth.utils.JwtService;
import com.quizlier.common.dto.AuthRequest;
import com.quizlier.common.dto.UserLoginRequest;
import com.quizlier.common.dto.UserRequest;

import java.util.List;

@RestController
@RequestMapping("api/v1/auth")
public class UserController {

	public static String PLAYER = "player";
	public static String ADMIN = "admin";
	
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
	
	@PostMapping("/signup/player")
	public ResponseEntity signUpPlayer(@RequestBody UserRequest userRequest) {
		ResponseData<User> response = new ResponseData<>(ServiceStatusCodes.ERROR, ServiceMessages.GENERAL_ERROR_MESSAGE);
		try {
			User user = userService.createPlayerUser(userRequest, PLAYER);

			response.setStatus(ServiceStatusCodes.SUCCESS);
			response.setMessage(ServiceMessages.SUCCESS_RESPONSE);
			response.setData(user);

			return ResponseEntity.ok().body(response);
		} catch (DuplicateUserException ex) {
			response.setMessage(ex.getMessage());
			return ResponseEntity.badRequest().body(response);
		} catch (Exception e) {
			return ResponseEntity.internalServerError().body(e.getMessage());
		}
	}

	@PostMapping("/signup/admin")
	public ResponseEntity signUpAdmin(@RequestBody UserRequest userRequest) {
		ResponseData<User> response = new ResponseData<>(ServiceStatusCodes.ERROR, ServiceMessages.GENERAL_ERROR_MESSAGE);
		try {
			User user = userService.createPlayerUser(userRequest, ADMIN);

			response.setStatus(ServiceStatusCodes.SUCCESS);
			response.setMessage(ServiceMessages.SUCCESS_RESPONSE);
			response.setData(user);

			return ResponseEntity.ok().body(response);
		} catch (DuplicateUserException ex) {
			response.setMessage(ex.getMessage());
			return ResponseEntity.badRequest().body(response);
		} catch (Exception e) {
			return ResponseEntity.internalServerError().body(e.getMessage());
		}
	}
	
	@PostMapping("/signin")
	public ResponseEntity signIn(@RequestBody UserLoginRequest userLoginRequest) {
		ResponseData response = new ResponseData<>(ServiceStatusCodes.ERROR, ServiceMessages.GENERAL_ERROR_MESSAGE);

		try {
			User user = userService.signInPlayer(userLoginRequest);
			AuthRequest authRequest = new AuthRequest();
			authRequest.setUsername(user.getUsername());
			authRequest.setPassword(userLoginRequest.getPassword());

			Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));

			if (authentication.isAuthenticated()) {
				response.setData(jwtService.generateToken(authRequest.getUsername()));
				response.setMessage("User authenticated successfully");
				return ResponseEntity.ok().body(response);
			} else {
				throw new UsernameNotFoundException("Invalid user reques t!");
			}
		} catch (UserNotFoundException | UsernameNotFoundException ex) {
			response.setMessage(ex.getMessage());
			return ResponseEntity.status(HttpStatusCode.valueOf(404)).body(response);
		} catch (AuthenticationFailedException ex) {
			response.setMessage(ex.getMessage());
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
		} catch (Exception e) {
			return ResponseEntity.internalServerError().body(e.getMessage());

		}
	}

	@GetMapping("/validateToken")
	public ResponseEntity<Boolean> validateToken(@RequestParam("token") String token) {
		String username = jwtService.extractUsername(token);
		boolean isValid = jwtService.validateToken(token, username);

		return ResponseEntity.ok(isValid);
	}

	@GetMapping("/getUsername")
	public ResponseEntity<String> fetchUsername(@RequestParam("token") String token) {
		String username = jwtService.extractUsername(token);

		return ResponseEntity.ok(username);
	}



	@GetMapping("/roles")
	public ResponseEntity<String> getUserRoles(@RequestParam("username") String username) {
		User user = userService.getUserByUsername(username);

		if (user != null) {
			String userRole = user.getUserRole().name();

			return ResponseEntity.ok(userRole);
		} else {
			return ResponseEntity.notFound().build();
		}
	}
	
//	public String authenticateAndGetToken(@RequestBody AuthRequest authRequest) {
//		Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
//
//		if (authentication.isAuthenticated()) {
//			return jwtService.generateToken(authRequest.getUsername());
//		} else {
//			throw new UsernameNotFoundException("Invalid user request!");
//		}
//	}

}
