package com.example.oreoinsightfactory.service;

import com.example.oreoinsightfactory.service.SalesAggregationService.SalesAggregates;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.Instant;

@Service
public class PdfService {

    public byte[] generateSalesReportPdf(SalesAggregates agg, String branch, String aiSummary) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4, 36, 36, 36, 36);
            PdfWriter.getInstance(document, out);
            document.open();

            // Título
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.BLUE);
            Paragraph title = new Paragraph("Reporte Ejecutivo de Ventas - Oreo Factory", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            Paragraph date = new Paragraph("Generado el: " + Instant.now().toString(), FontFactory.getFont(FontFactory.HELVETICA, 10, Color.GRAY));
            date.setAlignment(Element.ALIGN_CENTER);
            date.setSpacingAfter(20);
            document.add(date);

            // Tabla de métricas
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10);
            table.setSpacingAfter(20);

            addCell(table, "Sucursal / Alcance", branch != null ? branch : "Todas las sucursales");
            addCell(table, "Unidades Totales Vendidas", String.valueOf(agg.getTotalUnits()));
            addCell(table, "Facturacion Total", String.format("$%.2f", agg.getTotalRevenue()));
            addCell(table, "Producto Lider (Top SKU)", agg.getTopSku() != null ? agg.getTopSku() : "N/A");
            addCell(table, "Sucursal Lider (Top Branch)", agg.getTopBranch() != null ? agg.getTopBranch() : "N/A");

            document.add(table);

            // Resumen IA
            Paragraph summaryTitle = new Paragraph("Analisis Ejecutivo (IA):", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12));
            document.add(summaryTitle);

            Paragraph summaryContent = new Paragraph(aiSummary, FontFactory.getFont(FontFactory.HELVETICA, 11));
            summaryContent.setSpacingBefore(5);
            document.add(summaryContent);

            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            System.err.println("Error creando PDF: " + e.getMessage());
            return new byte[0];
        }
    }

    private void addCell(PdfPTable table, String header, String value) {
        PdfPCell cell1 = new PdfPCell(new Phrase(header, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10)));
        cell1.setBackgroundColor(new Color(240, 240, 240));
        cell1.setPadding(6);
        PdfPCell cell2 = new PdfPCell(new Phrase(value, FontFactory.getFont(FontFactory.HELVETICA, 10)));
        cell2.setPadding(6);
        table.addCell(cell1);
        table.addCell(cell2);
    }
}