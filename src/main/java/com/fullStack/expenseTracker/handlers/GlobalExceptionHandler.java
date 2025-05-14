package com.fullStack.expenseTracker.handlers;

import com.fullStack.expenseTracker.dto.reponses.ApiResponseDto;
import com.fullStack.expenseTracker.enums.ApiResponseStatus;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = ConstraintViolationException.class)
    public ResponseEntity<ApiResponseDto<?>> ConstraintViolationExceptionHandler(ConstraintViolationException exception) {
        List<String> errorMessages = new ArrayList<>();
        
        exception.getConstraintViolations().forEach(violation -> {
            String propertyPath = violation.getPropertyPath().toString();
            String field = propertyPath.contains(".") ? 
                    propertyPath.substring(propertyPath.lastIndexOf('.') + 1) : 
                    propertyPath;
            
            errorMessages.add(field + ": " + violation.getMessage());
        });
        
        return ResponseEntity
                .badRequest()
                .body(
                        new ApiResponseDto<>(ApiResponseStatus.FAILED, HttpStatus.BAD_REQUEST, errorMessages.toString())
                );
    }
}