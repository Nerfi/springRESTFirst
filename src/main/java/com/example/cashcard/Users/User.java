package com.example.cashcard.Users;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.Id;

// en el tutorial esto es el model, no si si es la forma correcta

public record User(@Id Long id, @NotBlank @Size(max = 20) String username, @NotBlank @Size(max = 50) String email, @NotBlank @Size(max = 120)String password) {
}
