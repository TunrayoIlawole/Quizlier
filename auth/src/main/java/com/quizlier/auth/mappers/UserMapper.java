package com.quizlier.auth.mappers;

import com.quizlier.common.dto.UserSignupRequest;
import com.quizlier.common.dto.UserSignupResponse;
import com.quizlier.common.dto.UserloginResponse;
import com.quizlier.common.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserSignupResponse userToUsersignupresponse(User user);
    User userRequestToUser(UserSignupRequest userSignupRequest);

    @Mapping(source = "id", target = "userId")
    UserloginResponse userToUserLoginResponse(User user);
}
