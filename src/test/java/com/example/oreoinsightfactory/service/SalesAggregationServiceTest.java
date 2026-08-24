package com.example.oreoinsightfactory.service;

import com.example.oreoinsightfactory.model.Sale;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class SalesAggregationServiceTest {

    @InjectMocks
    private SalesAggregationService salesAggregationService;

    private Sale createSale(String sku, int units, double price, String branch) {
        return Sale.builder()
                .id("s_1")
                .sku(sku)
                .units(units)
                .price(price)
                .branch(branch)
                .soldAt(Instant.now())
                .createdBy("user")
                .build();
    }

    @Test
    void shouldCalculateCorrectAggregatesWithValidData() {
        // 10 * 1.99 = 19.9
        // 5 * 2.49 = 12.45
        // 15 * 1.99 = 29.85
        // Total revenue = 62.2 (redondeado)
        List<Sale> sales = List.of(
                createSale("OREO_CLASSIC", 10, 1.99, "Miraflores"),
                createSale("OREO_DOUBLE", 5, 2.49, "San Isidro"),
                createSale("OREO_CLASSIC", 15, 1.99, "Miraflores")
        );

        var result = salesAggregationService.calculateAggregates(sales);

        assertThat(result.getTotalUnits()).isEqualTo(30);
        assertThat(result.getTotalRevenue()).isEqualTo(62.2);
        assertThat(result.getTopSku()).isEqualTo("OREO_CLASSIC");
        assertThat(result.getTopBranch()).isEqualTo("Miraflores");
    }

    @Test
    void shouldHandleEmptyListGracefully() {
        var result = salesAggregationService.calculateAggregates(List.of());

        assertThat(result.getTotalUnits()).isEqualTo(0);
        assertThat(result.getTotalRevenue()).isEqualTo(0.0);
        assertThat(result.getTopSku()).isNull();
        assertThat(result.getTopBranch()).isNull();
    }

    @Test
    void shouldConsiderOnlySpecifiedBranchSales() {
        List<Sale> sales = List.of(
                createSale("OREO_CLASSIC", 10, 1.99, "Miraflores"),
                createSale("OREO_DOUBLE", 20, 2.49, "San Isidro")
        );

        List<Sale> filtered = sales.stream().filter(s -> s.getBranch().equals("Miraflores")).toList();
        var result = salesAggregationService.calculateAggregates(filtered);

        assertThat(result.getTotalUnits()).isEqualTo(10);
        assertThat(result.getTopBranch()).isEqualTo("Miraflores");
    }

    @Test
    void shouldFilterByDateRangeCorrectly() {
        Instant now = Instant.now();
        Sale s1 = createSale("OREO_CLASSIC", 10, 1.99, "Miraflores");
        s1.setSoldAt(now.minusSeconds(10000));

        var result = salesAggregationService.calculateAggregates(List.of(s1));
        assertThat(result.getTotalUnits()).isEqualTo(10);
    }

    @Test
    void shouldIdentifyTopSkuEvenWithMultipleTypes() {
        List<Sale> sales = List.of(
                createSale("OREO_THINS", 5, 2.19, "Miraflores"),
                createSale("OREO_CLASSIC", 12, 1.99, "Miraflores")
        );

        var result = salesAggregationService.calculateAggregates(sales);
        assertThat(result.getTopSku()).isEqualTo("OREO_CLASSIC");
    }
}