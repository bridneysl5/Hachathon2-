package com.example.oreoinsightfactory.service;

import com.example.oreoinsightfactory.service.SalesAggregationService.SalesAggregates;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class AiService {

    @Value("${github.models.token}")
    private String githubToken;

    @Value("${github.models.url}")
    private String apiUrl;

    @Value("${github.models.model}")
    private String modelName;

    private final RestTemplate restTemplate = new RestTemplate();

    public String generateInsightSummary(SalesAggregates agg, String branch) {
        if (githubToken == null || githubToken.isBlank()) {
            return String.format(
                    "Durante el periodo evaluado para %s, se alcanzaron %d unidades vendidas generando un total de $%.2f. El SKU con mayor demanda fue %s.",
                    branch != null ? branch : "todas las sucursales",
                    agg.getTotalUnits(),
                    agg.getTotalRevenue(),
                    agg.getTopSku() != null ? agg.getTopSku() : "N/A"
            );
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(githubToken);

            String prompt = String.format(
                    "Con estos datos: totalUnits=%d, totalRevenue=%.2f, topSku=%s, topBranch=%s. Devuelve un resumen <=120 palabras para enviar por email.",
                    agg.getTotalUnits(),
                    agg.getTotalRevenue(),
                    agg.getTopSku() != null ? agg.getTopSku() : "N/A",
                    agg.getTopBranch() != null ? agg.getTopBranch() : (branch != null ? branch : "Todas")
            );

            Map<String, Object> body = Map.of(
                    "model", modelName,
                    "messages", List.of(
                            Map.of("role", "system", "content", "Eres un analista que escribe resúmenes breves y claros para emails corporativos."),
                            Map.of("role", "user", "content", prompt)
                    ),
                    "max_tokens", 200
            );

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(apiUrl, entity, Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
                if (choices != null && !choices.isEmpty()) {
                    Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                    return (String) message.get("content");
                }
            }
        } catch (Exception e) {
            System.err.println("Advertencia al consultar GitHub Models: " + e.getMessage());
        }

        return String.format(
                "Resumen: %d unidades vendidas, ingresos por $%.2f y producto lider %s.",
                agg.getTotalUnits(), agg.getTotalRevenue(), agg.getTopSku()
        );
    }
}