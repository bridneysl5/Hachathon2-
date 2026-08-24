package com.example.oreoinsightfactory.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.Instant;

@Data
public class SaleRequest {
    @NotBlank(message = "SKU es obligatorio")
    private String sku;

    @NotNull(message = "Units es obligatorio")
    @Min(value = 1, message = "Units debe ser al menos 1")
    private Integer units;

    @NotNull(message = "Price es obligatorio")
    @Positive(message = "Price debe ser mayor a 0")
    private Double price;

    @NotBlank(message = "Branch es obligatorio")
    private String branch;

    @NotNull(message = "SoldAt es obligatorio")
    private Instant soldAt;
}