package com.example.oreoinsightfactory.controller;

import com.example.oreoinsightfactory.dto.*;
import com.example.oreoinsightfactory.model.Role;
import com.example.oreoinsightfactory.model.User;
import com.example.oreoinsightfactory.repository.UserRepository;
import com.example.oreoinsightfactory.service.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El username ya existe");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El email ya existe");
        }

        if (request.getRole() == Role.BRANCH && (request.getBranch() == null || request.getBranch().isBlank())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Branch es obligatorio para el rol BRANCH");
        }

        String finalBranch = (request.getRole() == Role.CENTRAL) ? null : request.getBranch();

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .branch(finalBranch)
                .build();

        User saved = userRepository.save(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                UserResponse.builder()
                        .id(saved.getId())
                        .username(saved.getUsername())
                        .email(saved.getEmail())
                        .role(saved.getRole())
                        .branch(saved.getBranch())
                        .createdAt(saved.getCreatedAt())
                        .build()
        );
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas");
        }

        String token = jwtService.generateToken(user);

        return ResponseEntity.ok(
                AuthResponse.builder()
                        .token(token)
                        .expiresIn(3600L)
                        .role(user.getRole())
                        .branch(user.getBranch())
                        .build()
        );
    }
}