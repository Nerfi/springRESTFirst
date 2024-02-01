package com.example.cashcard.Users;

import org.springframework.data.annotation.Id;
// creo que podria considerar esta clase como un modelo
public record Role(@Id Long id, String name) {
}

// https://www.bezkoder.com/spring-boot-login-example-mysql/
// lo hemos dejado en el parte de crear el Model User
// https://www.sivalabs.in/using-java-records-with-spring-boot-3/