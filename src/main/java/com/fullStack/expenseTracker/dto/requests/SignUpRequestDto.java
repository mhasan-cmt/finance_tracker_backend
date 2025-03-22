package com.fullStack.expenseTracker.dto.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record SignUpRequestDto(
        @NotBlank(message = "Username is required!")
        @Size(min = 3, message = "Username must have atleast 3 characters!")
        @Size(max = 20, message = "Username can have have atmost 20 characters!")
        String userName,

        @Email(message = "Email is not in valid format!")

        @NotBlank(message = "Email is required!")
        String email,

        @NotBlank(message = "Password is required!")
        @Size(min = 8, message = "Password must have atleast 8 characters!")
        @Size(max = 20, message = "Password can have have atmost 20 characters!")
        String password,

        Set<String> roles
) {

}