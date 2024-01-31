package com.example.cashcard.Users;

import org.springframework.data.annotation.Id;
// creo que podria considerar esta clase como un modelo
public record Role(@Id Long id, String name) {
}
