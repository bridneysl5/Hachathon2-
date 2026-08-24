package com.example.oreoinsightfactory.dto;

import lombok.Builder;
import lombok.Data;
import java.time.Instant;

@Data
@Builder
public class WeeklySummaryResponse {
    private String requestId;
    private String status;
    private String message;
    private String estimatedTime;
    private Instant requestedAt;
}