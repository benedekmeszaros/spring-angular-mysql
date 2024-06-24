package com.pmf.awp.project.dto;

import org.hibernate.validator.constraints.Length;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Validated
public class RegisterDTO {
    @NotBlank(message = "Email address can not be emtpty")
    @Pattern(regexp = "^[a-z](?!.*\\.\\.)[a-z0-9\\.]{2,}@[a-z0-9\\.]{2,}\\.[a-z]{2,3}$", message = "Invalid email address")
    @Length(max = 100, message = "Email address must be shorter than 100 characters")
    private String email;
    @NotBlank(message = "Full name can not be emtpty.")
    @Pattern(regexp = "^[\\w\\d\\.]{3,24}$", message = "Invalid username")
    private String fullName;
    @NotBlank(message = "Password can not be emtpty.")
    @Pattern(regexp = "^(?=.*[A-Z]+).*$", message = "Password must contain a capital letter")
    @Pattern(regexp = "^(?=.*[0-9]+).*$", message = "Password must contain a number")
    @Length(min = 8, message = "Password must be at least 8 characters long")
    private String password;
}
