package com.example.oreoinsightfactory.dto;

import com.example.oreoinsightfactory.model.Role;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {
    private String token;
    private Long expiresIn;
    private Role role;
    private String branch;
}