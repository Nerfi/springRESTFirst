package com.example.cashcard.tutorials;
import org.springframework.data.annotation.Id;

public record Tutorials(@Id Long id, String title, String description, boolean published, String owner) {
}
