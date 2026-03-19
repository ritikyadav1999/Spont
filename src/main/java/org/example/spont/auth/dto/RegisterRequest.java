package org.example.spont.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.example.spont.user.entity.Gender;

public record RegisterRequest(
        @NotBlank
        @Size(min = 4,max = 100,message = "name should have at least 4 characters")
        String name,

        @NotBlank
        @Size(min = 10,max=15,message = "Phone should have at least 10 characters")
        String phone,

        @Email
        @NotBlank
        String email,

        @Size(min = 8, message = "password should have at least 8 characters")
        @NotBlank
        String password,

        @NotNull
        Gender gender
) {
}
