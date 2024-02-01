package com.example.cashcard.Users.Repositories;

import com.example.cashcard.Users.User;
import org.springframework.data.repository.CrudRepository;


import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);
}




//https://thorben-janssen.com/ultimate-guide-derived-queries-with-spring-data-jpa/