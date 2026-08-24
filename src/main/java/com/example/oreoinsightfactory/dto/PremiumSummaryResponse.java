package com.example.oreoinsightfactory.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class PremiumSummaryResponse {
    private String requestId;
    private String status;
    private String message;
    private String estimatedTime;
    private List<String> features;
}