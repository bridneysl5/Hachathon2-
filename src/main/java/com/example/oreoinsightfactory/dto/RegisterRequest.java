package com.example.oreoinsightfactory.dto;

import com.example.oreoinsightfactory.model.Role;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "Username es obligatorio")
    @Pattern(regexp = "^[a-zA-Z0-9._]{3,30}$", message = "Username debe tener 3-30 caracteres alfanuméricos, . o _")
    private String username;

    @NotBlank(message = "Email es obligatorio")
    @Email(message = "Formato de email inválido")
    private String email;

    @NotBlank(message = "Password es obligatorio")
    @Size(min = 8, message = "Password debe tener mínimo 8 caracteres")
    private String password;

    @NotNull(message = "Role es obligatorio (CENTRAL o BRANCH)")
    private Role role;

    private String branch;
}