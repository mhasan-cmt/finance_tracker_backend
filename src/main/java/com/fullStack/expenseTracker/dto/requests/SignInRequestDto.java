package com.fullStack.expenseTracker.dto.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

public record SignInRequestDto(
        @NotBlank(message = "Email is required!")
        String email,

        @NotBlank(message = "Password is required!")
        String password

) {
}
