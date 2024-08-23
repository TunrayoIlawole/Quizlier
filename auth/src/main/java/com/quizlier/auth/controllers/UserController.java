package com.quizlier.auth.controllers;

import com.quizlier.common.dto.*;
import com.quizlier.common.entity.User;
import com.quizlier.common.vo.ResponseData;
import com.quizlier.common.vo.ServiceMessages;
import com.quizlier.common.vo.ServiceStatusCodes;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;

import com.quizlier.auth.service.UserService;
import com.quizlier.auth.utils.JwtService;


@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {

	public static String PLAYER = "player";
	public static String ADMIN = "admin";

	private final AuthenticationManager authenticationManager;

	private final JwtService jwtService;
	
	private final UserService userService;
	
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
	public ResponseEntity signUpPlayer(@RequestBody UserSignupRequest userRequest) {
		User user = userService.createPlayerUser(userRequest, PLAYER);

		UserSignupResponse userSignupResponse = new UserSignupResponse();
		userSignupResponse.setEmail(user.getEmail());
		userSignupResponse.setUsername(user.getUsername());
		userSignupResponse.setFirstName(user.getFirstName());
		userSignupResponse.setLastName(user.getLastName());
		userSignupResponse.setId(user.getId().toString());
		userSignupResponse.setUserRole(user.getUserRole().name());

		ResponseData<UserSignupResponse> response = new ResponseData<>(ServiceStatusCodes.SUCCESS, ServiceMessages.SUCCESS_RESPONSE);
		response.setData(userSignupResponse);

		return ResponseEntity.ok().body(response);
	}

	@PostMapping("/signup/admin")
	public ResponseEntity signUpAdmin(@RequestBody UserSignupRequest userRequest) {
		User user = userService.createPlayerUser(userRequest, ADMIN);

		UserSignupResponse userSignupResponse = new UserSignupResponse();
		userSignupResponse.setEmail(user.getEmail());
		userSignupResponse.setUsername(user.getUsername());
		userSignupResponse.setFirstName(user.getFirstName());
		userSignupResponse.setLastName(user.getLastName());
		userSignupResponse.setId(user.getId().toString());
		userSignupResponse.setUserRole(user.getUserRole().name());

		ResponseData<UserSignupResponse> response = new ResponseData<>(ServiceStatusCodes.SUCCESS, ServiceMessages.SUCCESS_RESPONSE);
		response.setData(userSignupResponse);

		return ResponseEntity.ok().body(response);
	}
	
	@PostMapping("/signin")
	public ResponseEntity signIn(@RequestBody UserLoginRequest userLoginRequest) {

		User user = userService.signInPlayer(userLoginRequest);
		AuthRequest authRequest = new AuthRequest();
		authRequest.setUsername(user.getUsername());
		authRequest.setPassword(userLoginRequest.getPassword());

		String token = userService.authenticateUser(authRequest);

		UserloginResponse userloginResponse = new UserloginResponse();
		userloginResponse.setEmail(user.getEmail());
		userloginResponse.setUserId(user.getId().toString());
		userloginResponse.setUsername(user.getUsername());
		userloginResponse.setToken(token);

		ResponseData<UserloginResponse> response = new ResponseData<>(ServiceStatusCodes.SUCCESS, ServiceMessages.AUTH_SUCCESS);

		response.setData(userloginResponse);
		return ResponseEntity.ok().body(response);
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

	@GetMapping("/highscore")
	public ResponseEntity<Integer> getHighScore(@RequestParam("username") String username) {
		User user = userService.getUserByUsername(username);

		if (user != null) {
			int highestScore = user.getHighest_score();

			return ResponseEntity.ok(highestScore);
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	@PostMapping("/highscore")
	public void updateHighscore(@RequestParam("username") String username, @RequestParam("score") String score) {
		User user = userService.getUserByUsername(username);

		if (user != null) {
			userService.updateHighscore(user, score);
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
