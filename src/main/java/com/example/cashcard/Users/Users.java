package com.example.cashcard.Users;
import org.springframework.data.annotation.Id;

public record Users(@Id int id, String name, String email) {
}
