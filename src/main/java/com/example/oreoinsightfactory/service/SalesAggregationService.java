package com.example.oreoinsightfactory.service;

import com.example.oreoinsightfactory.model.Sale;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SalesAggregationService {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SalesAggregates {
        private int totalUnits;
        private double totalRevenue;
        private String topSku;
        private String topBranch;
    }

    public SalesAggregates calculateAggregates(List<Sale> sales) {
        if (sales == null || sales.isEmpty()) {
            return new SalesAggregates(0, 0.0, null, null);
        }

        int totalUnits = sales.stream().mapToInt(Sale::getUnits).sum();
        double totalRevenue = sales.stream().mapToDouble(s -> s.getUnits() * s.getPrice()).sum();
        totalRevenue = Math.round(totalRevenue * 100.0) / 100.0;

        // Top SKU por unidades vendidas
        Map<String, Integer> skuUnitsMap = sales.stream()
                .collect(Collectors.groupingBy(Sale::getSku, Collectors.summingInt(Sale::getUnits)));
        String topSku = skuUnitsMap.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        // Top Branch por unidades vendidas
        Map<String, Integer> branchUnitsMap = sales.stream()
                .collect(Collectors.groupingBy(Sale::getBranch, Collectors.summingInt(Sale::getUnits)));
        String topBranch = branchUnitsMap.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        return new SalesAggregates(totalUnits, totalRevenue, topSku, topBranch);
    }
}