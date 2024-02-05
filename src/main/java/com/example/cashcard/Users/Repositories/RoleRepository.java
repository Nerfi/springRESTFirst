package com.example.cashcard.Users.Repositories;


import com.example.cashcard.Users.ERole;
import com.example.cashcard.Users.Role;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RoleRepository extends CrudRepository<Role, Long> {
    Optional<Role> findByName(ERole role);
}
