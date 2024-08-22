package com.quizlier.auth.service;

import java.util.Optional;

import com.quizlier.auth.exceptions.AuthenticationFailedException;
import com.quizlier.auth.exceptions.DuplicateUserException;
import com.quizlier.auth.exceptions.UserNotFoundException;
import com.quizlier.common.dto.UserSignupRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.quizlier.auth.repository.UserRepository;
import com.quizlier.auth.utils.UserInfoDetails;
import com.quizlier.common.dto.UserLoginRequest;
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
	
	public User createPlayerUser(UserSignupRequest userRequest, String role) throws DuplicateUserException {
		try {
		Optional<User> userByEmail = userRepository.findUserByEmail(userRequest.getEmail());
		
		Optional<User> userByUsername = userRepository.findUserByUsername(userRequest.getUsername());
		
		if (userByEmail.isPresent()) {
			throw new DuplicateUserException(ServiceMessages.DUPLICATE_EMAIL);
		}
		if (userByUsername.isPresent()) {
			throw new DuplicateUserException(ServiceMessages.DUPLICATE_USERNAME);
		}
		
		User user = new User();
		UserRole userRole = role.equalsIgnoreCase("admin") ? UserRole.admin : UserRole.player;
		
		user.setEmail(userRequest.getEmail());
		user.setUsername(userRequest.getUsername());
		user.setFirstName(userRequest.getFirstName());
		user.setLastName(userRequest.getLastName());
		user.setPassword(encoder.encode(userRequest.getPassword()));
		user.setUserRole(userRole);
		user.setHighest_score(0);
		
		userRepository.save(user);
		
		return user;
		} catch (Exception e) {
			throw e;
		}
	}
	
	public User signInPlayer (UserLoginRequest userLoginRequest) throws UserNotFoundException, AuthenticationFailedException {
		try {
			Optional<User> userByEmail = userRepository.findUserByEmail(userLoginRequest.getEmail());
			
			if (!userByEmail.isPresent()) {
				throw new UserNotFoundException(ServiceMessages.INVALID_USER);
			} else {
				Boolean matched = encoder.matches(userLoginRequest.getPassword(), userByEmail.get().getPassword());
//				String hashedPassword = passwordEncryption.toHexString(passwordEncryption.getSHA(userLoginRequest.getPassword()));
				
				if (!matched) {
					throw new AuthenticationFailedException(ServiceMessages.CREDENTIALS_MISMATCH);
				} else {
					return userByEmail.get();
				}
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<User> user = userRepository.findUserByUsername(username);
		
		return user.map(UserInfoDetails::new).orElseThrow(() -> new UsernameNotFoundException("User not found " + username));
	}

	public User getUserByUsername(String username) {
		Optional<User> userByUsername = userRepository.findUserByUsername(username);

		return userByUsername.orElse(null);

	}

	public void updateHighscore(User user, String score) {

		user.setHighest_score(Integer.valueOf(score));

		userRepository.save(user);
	}
}
