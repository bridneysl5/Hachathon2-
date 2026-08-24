package com.example.oreoinsightfactory.dto;

import lombok.Builder;
import lombok.Data;
import java.time.Instant;

@Data
@Builder
public class SaleResponse {
    private String id;
    private String sku;
    private Integer units;
    private Double price;
    private String branch;
    private Instant soldAt;
    private String createdBy;
}