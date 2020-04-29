package com.andriusdgt.thedots.backend.config;

import com.andriusdgt.thedots.core.model.ApiError;
import com.andriusdgt.thedots.core.exception.DuplicatePointException;
import com.andriusdgt.thedots.core.exception.TooManyPointsException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ValidationException;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
final class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({ValidationException.class, DuplicatePointException.class, TooManyPointsException.class})
    ResponseEntity<ApiError> handleValidationException(Exception ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiError(ex.getMessage()));
    }

}
