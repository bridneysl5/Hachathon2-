package com.example.oreoinsightfactory.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class WeeklySummaryRequest {
    private String from; // Formato YYYY-MM-DD
    private String to;   // Formato YYYY-MM-DD
    private String branch;

    @NotBlank(message = "emailTo es obligatorio")
    @Email(message = "Formato de email inválido")
    private String emailTo;
}