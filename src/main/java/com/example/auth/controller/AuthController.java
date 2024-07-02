package com.example.auth.controller;

import com.example.auth.model.User;
import com.example.auth.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // Change to PasswordEncoder interface

    private final String SECRET_KEY = "secret";

    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody User user) {
        Optional<User> existingUser = userRepository.findByUsername(user.getUsername());
        if (existingUser.isPresent()) {
            return ResponseEntity.status(409).body("Username already exists");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword())); // Use passwordEncoder
        userRepository.save(user);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/signin")
    public ResponseEntity<?> signIn(@RequestBody Map<String, String> user) {
        Optional<User> optionalUser = userRepository.findByUsername(user.get("username"));

        if (optionalUser.isPresent()) {
            User foundUser = optionalUser.get();
            if (passwordEncoder.matches(user.get("password"), foundUser.getPassword())) { // Use passwordEncoder
                String token = Jwts.builder()
                        .setSubject(foundUser.getUsername())
                        .claim("roles", "user")
                        .setIssuedAt(new Date())
                        .setExpiration(new Date(System.currentTimeMillis() + 864000000)) // 10 days
                        .signWith(SignatureAlgorithm.HS512, SECRET_KEY.getBytes())
                        .compact();
                Map<String, String> response = new HashMap<>();
                response.put("token", token);
                return ResponseEntity.ok(response);
            }
        }

        return ResponseEntity.status(401).body("Invalid username or password");
    }
}
