package com.example.oreoinsightfactory.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PremiumSummaryRequest {
    private String from;
    private String to;
    private String branch;

    @NotBlank(message = "emailTo es obligatorio")
    @Email(message = "Email invalido")
    private String emailTo;

    private String format = "PREMIUM";
    private boolean includeCharts = true;
    private boolean attachPdf = true;
}