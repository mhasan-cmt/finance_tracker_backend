package com.fullStack.expenseTracker.dto.requests;

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