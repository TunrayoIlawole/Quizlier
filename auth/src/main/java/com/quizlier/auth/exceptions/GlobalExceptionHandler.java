package com.quizlier.auth.exceptions;

import com.quizlier.common.vo.ResponseData;
import com.quizlier.common.vo.ServiceMessages;
import com.quizlier.common.vo.ServiceStatusCodes;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AuthenticationFailedException.class)
    public ResponseEntity handleAuthenticationFailedEntityException(AuthenticationFailedException ex) {
        ResponseData response = new ResponseData<>(ServiceStatusCodes.ERROR, ServiceMessages.GENERAL_ERROR_MESSAGE);

        response.setMessage(ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(DuplicateUserException.class)
    public ResponseEntity duplicateUserEntityException(DuplicateUserException ex) {
        ResponseData response = new ResponseData<>(ServiceStatusCodes.ERROR, ServiceMessages.GENERAL_ERROR_MESSAGE);

        response.setMessage(ex.getMessage());
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity handleUserNotFoundEntityException(UserNotFoundException ex) {
        ResponseData response = new ResponseData<>(ServiceStatusCodes.ERROR, ServiceMessages.GENERAL_ERROR_MESSAGE);

        response.setMessage(ex.getMessage());
        return ResponseEntity.status(HttpStatusCode.valueOf(404)).body(response);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity handleUsernameNotFoundEntityException(UsernameNotFoundException ex) {
        ResponseData response = new ResponseData<>(ServiceStatusCodes.ERROR, ServiceMessages.GENERAL_ERROR_MESSAGE);

        response.setMessage(ex.getMessage());
        return ResponseEntity.status(HttpStatusCode.valueOf(404)).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity handleOtherException(Exception ex) {
        ResponseData response = new ResponseData<>(ServiceStatusCodes.ERROR, ServiceMessages.GENERAL_ERROR_MESSAGE);

        return ResponseEntity.internalServerError().body(ex.getMessage());
    }

}
