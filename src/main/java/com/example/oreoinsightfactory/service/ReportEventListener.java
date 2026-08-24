package com.example.oreoinsightfactory.service;

import com.example.oreoinsightfactory.event.PremiumReportRequestedEvent;
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
    private final PdfService pdfService;

    @Async("taskExecutor")
    @EventListener
    public void handleReportRequest(ReportRequestedEvent event) {
        System.out.println("Procesando reporte regular en hilo: " + Thread.currentThread().getName());

        List<Sale> sales = salesRepository.findFilteredSales(event.getBranch(), event.getFrom(), event.getTo());
        var aggregates = aggregationService.calculateAggregates(sales);
        String summaryText = aiService.generateInsightSummary(aggregates, event.getBranch());

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

    @Async("taskExecutor")
    @EventListener
    public void handlePremiumReportRequest(PremiumReportRequestedEvent event) {
        System.out.println("Procesando reporte PREMIUM en hilo: " + Thread.currentThread().getName());

        List<Sale> sales = salesRepository.findFilteredSales(event.getBranch(), event.getFrom(), event.getTo());
        var aggregates = aggregationService.calculateAggregates(sales);
        String summaryText = aiService.generateInsightSummary(aggregates, event.getBranch());

        String chartUrl = String.format(
                "https://quickchart.io/chart?c={type:'bar',data:{labels:['Unidades','Ingresos($)'],datasets:[{label:'Desempeno',data:[%d,%.2f],backgroundColor:['%%2336A2EB','%%23FF6384']}]}}",
                aggregates.getTotalUnits(),
                aggregates.getTotalRevenue()
        );

        String html = String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; background-color: #f4f6f8; padding: 20px; }
                    .container { background-color: #ffffff; padding: 25px; border-radius: 8px; max-width: 600px; margin: auto; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
                    .header { background: #004B93; color: white; padding: 15px; border-radius: 6px; text-align: center; }
                    .metric { display: inline-block; width: 45%%; background: #eef3fc; margin: 5px; padding: 12px; border-radius: 5px; text-align: center; }
                    .summary { margin-top: 20px; padding: 15px; background: #fdf6e2; border-left: 4px solid #f39c12; border-radius: 4px; }
                    .chart { text-align: center; margin-top: 20px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h2>🍪 Oreo Premium Insights Report</h2>
                    </div>
                    <h3>Alcance: %s</h3>
                    <div>
                        <div class="metric"><strong>Total Unidades:</strong><br/>%d</div>
                        <div class="metric"><strong>Total Revenue:</strong><br/>$%.2f</div>
                        <div class="metric"><strong>Top SKU:</strong><br/>%s</div>
                        <div class="metric"><strong>Top Sucursal:</strong><br/>%s</div>
                    </div>
                    <div class="summary">
                        <strong>🤖 Analisis IA:</strong>
                        <p>%s</p>
                    </div>
                    <div class="chart">
                        <h4>Metricas Visuales:</h4>
                        <img src="%s" width="450" alt="Grafico de Ventas" style="max-width:100%%;"/>
                    </div>
                </div>
            </body>
            </html>
            """,
                event.getBranch() != null ? event.getBranch() : "Global",
                aggregates.getTotalUnits(),
                aggregates.getTotalRevenue(),
                aggregates.getTopSku() != null ? aggregates.getTopSku() : "N/A",
                aggregates.getTopBranch() != null ? aggregates.getTopBranch() : "N/A",
                summaryText,
                chartUrl
        );

        byte[] pdfBytes = null;
        if (event.isAttachPdf()) {
            pdfBytes = pdfService.generateSalesReportPdf(aggregates, event.getBranch(), summaryText);
        }

        emailService.sendHtmlEmailWithAttachment(
                event.getEmailTo(),
                "🍪 Reporte Semanal Oreo Premium con Graficos y PDF",
                html,
                pdfBytes
        );
    }
}