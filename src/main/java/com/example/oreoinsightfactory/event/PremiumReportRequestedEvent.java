package com.example.oreoinsightfactory.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.Instant;

@Getter
@AllArgsConstructor
public class PremiumReportRequestedEvent {
    private final String requestId;
    private final String branch;
    private final Instant from;
    private final Instant to;
    private final String emailTo;
    private final boolean includeCharts;
    private final boolean attachPdf;
}