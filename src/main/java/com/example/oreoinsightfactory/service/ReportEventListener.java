package com.example.oreoinsightfactory.service;

import com.example.oreoinsightfactory.event.ReportRequestedEvent;
import com.example.oreoinsightfactory.model.Sale;
import com.example.oreoinsightfactory.repository.SalesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ReportEventListener {

    private final SalesRepository salesRepository;
    private final SalesAggregationService aggregationService;
    private final AiService aiService;
    private final EmailService emailService;

    @Async("taskExecutor")
    @EventListener
    public void handleReportRequest(ReportRequestedEvent event) {
        System.out.println("Procesando evento asíncrono en hilo: " + Thread.currentThread().getName());

        // 1. Filtrar ventas por rango y sucursal
        List<Sale> sales = salesRepository.findFilteredSales(event.getBranch(), event.getFrom(), event.getTo());

        // 2. Calcular agregados
        var aggregates = aggregationService.calculateAggregates(sales);

        // 3. Llamar a GitHub Models para generar el resumen
        String summaryText = aiService.generateInsightSummary(aggregates, event.getBranch());

        // 4. Formatear correo y enviar
        String body = String.format(
                "%s\n\n--- Metricas Principales ---\nUnidades: %d\nIngresos: $%.2f\nTop SKU: %s\nTop Sucursal: %s",
                summaryText,
                aggregates.getTotalUnits(),
                aggregates.getTotalRevenue(),
                aggregates.getTopSku() != null ? aggregates.getTopSku() : "N/A",
                aggregates.getTopBranch() != null ? aggregates.getTopBranch() : "N/A"
        );

        emailService.sendEmail(
                event.getEmailTo(),
                "Reporte Semanal Oreo",
                body
        );
    }
}