package com.fullStack.expenseTracker.dto.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UpdateUserRequestDto(
        @NotBlank(message = "Email is required!")
        @Email(message = "Email is not in valid format!")
        String email,
        
        String phone,
        
        String gender,
        
        String firstName,
        
        String lastName,
        
        String dateOfBirth,
        
        String address
) {
}