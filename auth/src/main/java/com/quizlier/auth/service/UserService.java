package com.quizlier.auth.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.quizlier.auth.repository.UserRepository;
import com.quizlier.auth.utils.PasswordEncryption;
import com.quizlier.auth.utils.UserInfoDetails;
import com.quizlier.common.dto.UserLoginRequest;
import com.quizlier.common.dto.UserRequest;
import com.quizlier.common.entity.User;
import com.quizlier.common.entity.UserRole;
import com.quizlier.common.vo.ResponseData;
import com.quizlier.common.vo.ServiceMessages;
import com.quizlier.common.vo.ServiceStatusCodes;

@Service
public class UserService implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private PasswordEncoder encoder;
	
//	@Autowired
//	public UserService(UserRepository userRepository, PasswordEncryption passwordEncryption) {
//		this.userRepository = userRepository;
//		this.passwordEncryption = passwordEncryption;
//	}
	
	public ResponseEntity createPlayerUser(UserRequest userRequest) {
		try {
		ResponseData<User> response = new ResponseData<>(ServiceStatusCodes.ERROR, ServiceMessages.GENERAL_ERROR_MESSAGE);
		
		Optional<User> userByEmail = userRepository.findUserByEmail(userRequest.getEmail());
		
		Optional<User> userByUsername = userRepository.findUserByUsername(userRequest.getUsername());
		
		if (userByEmail.isPresent()) {
			response.setMessage(ServiceMessages.DUPLICATE_EMAIL);
			return ResponseEntity.badRequest().body(response);
		}
		
		if (userByUsername.isPresent()) {
			response.setMessage(ServiceMessages.DUPLICATE_USERNAME);
			return ResponseEntity.badRequest().body(response);
		}
		
		User user = new User();
		
		user.setEmail(userRequest.getEmail());
		user.setUsername(userRequest.getUsername());
		user.setFirstName(userRequest.getFirstName());
		user.setLastName(userRequest.getLastName());
		user.setPassword(encoder.encode(userRequest.getPassword()));
		user.setUserRole(UserRole.player);
		
		userRepository.save(user);
		
		response.setStatus(ServiceStatusCodes.SUCCESS);
		response.setMessage(ServiceMessages.SUCCESS_RESPONSE);
		response.setData(user);
		
		return ResponseEntity.ok().body(response);
		
		
		} catch (Exception e) {
			// TODO: handle exception
			return ResponseEntity.internalServerError().body(e.getMessage());
		}
	}
	
	public ResponseEntity signInPlayer (UserLoginRequest userLoginRequest) {
		try {
			ResponseData<User> response = new ResponseData<>(ServiceStatusCodes.ERROR, ServiceMessages.GENERAL_ERROR_MESSAGE);
			
			Optional<User> userByEmail = userRepository.findUserByEmail(userLoginRequest.getEmail());
			
			if (!userByEmail.isPresent()) {
				response.setMessage(ServiceMessages.INVALID_USER);
				return ResponseEntity.status(HttpStatusCode.valueOf(400)).body(response);
			} else {
				Boolean matched = encoder.matches(userLoginRequest.getPassword(), userByEmail.get().getPassword());
//				String hashedPassword = passwordEncryption.toHexString(passwordEncryption.getSHA(userLoginRequest.getPassword()));
				
				if (!matched) {
					response.setMessage(ServiceMessages.CREDENTIALS_MISMATCH);
					return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
				} else {
					response.setMessage("Sign in successful");
					response.setData(userByEmail.get());
					return ResponseEntity.ok().body(response);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			return ResponseEntity.internalServerError().body(e.getMessage());
		}
	}
	
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<User> user = userRepository.findUserByUsername(username);
		
		return user.map(UserInfoDetails::new).orElseThrow(() -> new UsernameNotFoundException("User not found " + username));
	}
}
