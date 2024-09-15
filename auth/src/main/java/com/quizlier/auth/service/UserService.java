package com.quizlier.auth.service;

import java.util.Optional;

import com.quizlier.auth.exceptions.AuthenticationFailedException;
import com.quizlier.auth.exceptions.DuplicateUserException;
import com.quizlier.auth.exceptions.UserNotFoundException;
import com.quizlier.auth.mappers.UserMapper;
import com.quizlier.common.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.quizlier.auth.repository.UserRepository;
import com.quizlier.common.entity.User;
import com.quizlier.common.entity.UserRole;
import com.quizlier.common.vo.ServiceMessages;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;

	private final PasswordEncoder encoder;

	private final AuthService authService;

	private final UserMapper userMapper;
	
	public UserSignupResponse createPlayerUser(UserSignupRequest userRequest, String role) throws DuplicateUserException {
		Optional<User> userByEmail = userRepository.findUserByEmail(userRequest.getEmail());
		
		Optional<User> userByUsername = userRepository.findUserByUsername(userRequest.getUsername());
		
		if (userByEmail.isPresent()) {
			throw new DuplicateUserException(ServiceMessages.DUPLICATE_EMAIL);
		}
		if (userByUsername.isPresent()) {
			throw new DuplicateUserException(ServiceMessages.DUPLICATE_USERNAME);
		}
		
		User user = userMapper.userRequestToUser(userRequest);

		UserRole userRole = role.equalsIgnoreCase("admin") ? UserRole.ADMIN : UserRole.PLAYER;
		user.setPassword(encoder.encode(userRequest.getPassword()));
		user.setUserRole(userRole);
		user.setHighest_score(0);
		
		userRepository.save(user);

		UserSignupResponse userSignupResponse = userMapper.userToUsersignupresponse(user);
		
		return userSignupResponse;
	}
	
	public UserloginResponse signInPlayer (UserLoginRequest userLoginRequest) throws UserNotFoundException, AuthenticationFailedException {
			Optional<User> userByEmail = userRepository.findUserByEmail(userLoginRequest.getEmail());
			
			if (!userByEmail.isPresent()) {
				throw new UserNotFoundException(ServiceMessages.INVALID_USER);
			} else {
				Boolean matched = encoder.matches(userLoginRequest.getPassword(), userByEmail.get().getPassword());

				if (!matched) {
					throw new AuthenticationFailedException(ServiceMessages.CREDENTIALS_MISMATCH);
				} else {
					AuthRequest authRequest = new AuthRequest();
					authRequest.setUsername(userByEmail.get().getUsername());
					authRequest.setPassword(userLoginRequest.getPassword());

					String token = authService.authenticateUser(authRequest);
					UserloginResponse userloginResponse = userMapper.userToUserLoginResponse(userByEmail.get());
					userloginResponse.setToken(token);

					return userloginResponse;
				}
			}
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
