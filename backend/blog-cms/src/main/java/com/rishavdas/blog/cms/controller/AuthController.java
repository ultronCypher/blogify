package com.rishavdas.blog.cms.controller;

import com.rishavdas.blog.cms.dto.LoginDTO;
import com.rishavdas.blog.cms.dto.RegisterDTO;
import com.rishavdas.blog.cms.dto.UserSummaryDTO;
import com.rishavdas.blog.cms.model.Role;
import com.rishavdas.blog.cms.model.User;
import com.rishavdas.blog.cms.repository.UserRepository;
import com.rishavdas.blog.cms.security.JwtUtil;
import com.rishavdas.blog.cms.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//
//    @Autowired
//    private AuthenticationManager authenticationManager;
//
//    @Autowired
//    private JwtUtil jwtUtil;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserService userService;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserService userService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterDTO dto){
        if(userRepository.existsByUsername(dto.getUsername())){
            return ResponseEntity.badRequest().body("Username already exists");
        }
        if(userRepository.existsByEmail(dto.getEmail())){
            return ResponseEntity.badRequest().body("User already registered with the given email id");
        }

        User user=new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(Role.USER);
        userRepository.save(user);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO dto) {
        try {
            // Authenticate the user
            var auth = authenticationManager.authenticate(
                    new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                            dto.getUsername(), dto.getPassword()
                    )
            );

            var userDetails = (org.springframework.security.core.userdetails.UserDetails) auth.getPrincipal();
            String token = jwtUtil.generateToken(userDetails.getUsername());
            return ResponseEntity.ok(java.util.Map.of("token", token));

        } catch (org.springframework.security.authentication.BadCredentialsException e) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
    }

    @GetMapping("/me")
    public ResponseEntity<UserSummaryDTO> me(Authentication authentication){
        String username = authentication.getName();
        User user = userService.findByUsername(username);
        return ResponseEntity.ok(UserSummaryDTO.from(user));
    }
}
