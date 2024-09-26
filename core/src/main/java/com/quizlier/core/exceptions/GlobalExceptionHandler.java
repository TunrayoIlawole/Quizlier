package com.quizlier.core.exceptions;

import com.quizlier.common.vo.ResponseData;
import com.quizlier.common.vo.ServiceMessages;
import com.quizlier.common.vo.ServiceStatusCodes;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateEntityException.class)
    public ResponseEntity handleDuplicateEntityException(DuplicateEntityException ex) {
        ResponseData response = new ResponseData<>(ServiceStatusCodes.ERROR, ServiceMessages.GENERAL_ERROR_MESSAGE);

        response.setMessage(ex.getMessage());
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(MaximumEntityException.class)
    public ResponseEntity handleMaximumEntityException(MaximumEntityException ex) {
        ResponseData response = new ResponseData<>(ServiceStatusCodes.ERROR, ServiceMessages.GENERAL_ERROR_MESSAGE);

        response.setMessage(ex.getMessage());
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(InvalidEntityException.class)
    public ResponseEntity handleInvalidEntityException(InvalidEntityException ex) {
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
