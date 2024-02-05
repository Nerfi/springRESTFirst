package com.example.cashcard.Users.controllers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


import com.example.cashcard.Users.ERole;
import com.example.cashcard.Users.Login.Payload.LoginRequest;
import com.example.cashcard.Users.Login.Payload.MessageResponse;
import com.example.cashcard.Users.Login.Payload.SignupRequest;
import com.example.cashcard.Users.Login.Payload.UserInfoResponse;
import com.example.cashcard.Users.Repositories.RoleRepository;
import com.example.cashcard.Users.Repositories.UserRepository;
import com.example.cashcard.Users.Role;
import com.example.cashcard.Users.User;
import com.example.cashcard.Users.security.jwt.JwtUtils;
import com.example.cashcard.Users.security.services.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);

        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .body(new UserInfoResponse(
                        userDetails.getId(),
                        userDetails.getUsername(),
                        userDetails.getEmail(),
                        roles
                ));
    }
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest){
        if (userRepository.existsByUsername(signupRequest.getUsername())) {
            return ResponseEntity.badRequest().body(
                    new MessageResponse("Error: username already exists")
            );
        }

        if(userRepository.existsByEmail(signupRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse(("Error: Email already taken")));
        }

        // create new user account

        User user = new User( signupRequest.getUsername(), signupRequest.getEmail(), encoder.encode(signupRequest.getPassword()));


        Set<String> strRoles = signupRequest.getRole();
        System.out.println(strRoles +  " los roles deberia ir aqui");

        System.out.println("LOLOLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL");

        Set<Role> roles = new HashSet<>();

     if(strRoles == null) {
         Role userRole = roleRepository.findByName(ERole.ROLE_USER).orElseThrow(() -> new RuntimeException("Error: Role is not found"));
         roles.add(userRole);

     } else {
         strRoles.forEach(role -> {
             switch (role) {

                 case "admin":
                     Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN).orElseThrow(() -> new RuntimeException("Error: role  admin is not found"));
                     roles.add(adminRole);

                     break;

                 case "mod":
                     Role modRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
                             .orElseThrow(() -> new RuntimeException("Error: role mod is not found"));
                     roles.add(modRole);

                     break;

                 default:
                     Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                             .orElseThrow(() -> new RuntimeException("Error: role user is not found"));
                     roles.add(userRole);
             }
         });
     }

        user.setRoles(roles); // esto me estan dadno problemas: Unknown column 'user' in 'field list'
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

    @PostMapping("/signout")
    public ResponseEntity<?> logoutUser() {
        ResponseCookie cookie = jwtUtils.getCleanJwtCookie();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new MessageResponse("You've been signed out!"));
    }


}
