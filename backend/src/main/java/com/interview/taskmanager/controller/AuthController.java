package com.interview.taskmanager.controller;

import com.interview.taskmanager.dto.AuthRequest;
import com.interview.taskmanager.dto.AuthResponse;
import com.interview.taskmanager.dto.RegisterRequest;
import com.interview.taskmanager.entity.User;
import com.interview.taskmanager.repository.UserRepository;
import com.interview.taskmanager.security.CustomUserDetailsService;
import com.interview.taskmanager.security.JwtUtil;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private CustomUserDetailsService userDetailsService;
    
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        logger.info("Registration attempt for username: {}", request.getUsername());
        
        if (userRepository.existsByUsername(request.getUsername())) {
            logger.warn("Registration failed: Username already exists - {}", request.getUsername());
            return ResponseEntity.badRequest().body("Username already exists");
        }
        
        if (userRepository.existsByEmail(request.getEmail())) {
            logger.warn("Registration failed: Email already exists - {}", request.getEmail());
            return ResponseEntity.badRequest().body("Email already exists");
        }
        
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        
        userRepository.save(user);
        logger.info("User registered successfully: {}", request.getUsername());
        
        return ResponseEntity.ok("User registered successfully");
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequest request) {
        logger.info("Login attempt for username: {}", request.getUsername());
        
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
            
            UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
            String jwt = jwtUtil.generateToken(userDetails);
            
            User user = userRepository.findByUsername(request.getUsername()).orElseThrow();
            
            logger.info("Login successful for username: {}", request.getUsername());
            
            return ResponseEntity.ok(new AuthResponse(jwt, user.getId(), user.getUsername(), user.getEmail()));
            
        } catch (Exception e) {
            logger.error("Login failed for username: {} - {}", request.getUsername(), e.getMessage());
            return ResponseEntity.status(401).body("Invalid username or password");
        }
    }
}
