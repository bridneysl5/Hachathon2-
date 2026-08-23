package com.example.oreoinsightfactory.dto;

import com.example.oreoinsightfactory.model.Role;
import lombok.Builder;
import lombok.Data;
import java.time.Instant;

@Data
@Builder
public class UserResponse {
    private String id;
    private String username;
    private String email;
    private Role role;
    private String branch;
    private Instant createdAt;
}