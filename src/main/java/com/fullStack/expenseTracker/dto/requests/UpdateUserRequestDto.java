package com.fullStack.expenseTracker.dto.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UpdateUserRequestDto(
        String email,
        
        String phone,
        
        String gender,
        
        String firstName,
        
        String lastName,
        
        String dateOfBirth,
        
        String address
) {
}