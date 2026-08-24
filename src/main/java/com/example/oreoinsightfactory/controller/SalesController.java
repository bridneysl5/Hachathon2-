package com.example.oreoinsightfactory.controller;

import com.example.oreoinsightfactory.dto.SaleRequest;
import com.example.oreoinsightfactory.dto.SaleResponse;
import com.example.oreoinsightfactory.model.Role;
import com.example.oreoinsightfactory.model.Sale;
import com.example.oreoinsightfactory.model.User;
import com.example.oreoinsightfactory.repository.SalesRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/sales")
@RequiredArgsConstructor
public class SalesController {

    private final SalesRepository salesRepository;

    @PostMapping
    public ResponseEntity<SaleResponse> createSale(@Valid @RequestBody SaleRequest request, @AuthenticationPrincipal User currentUser) {
        if (currentUser.getRole() == Role.BRANCH && !currentUser.getBranch().equals(request.getBranch())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No puedes crear ventas para otra sucursal");
        }

        Sale sale = Sale.builder()
                .sku(request.getSku())
                .units(request.getUnits())
                .price(request.getPrice())
                .branch(request.getBranch())
                .soldAt(request.getSoldAt())
                .createdBy(currentUser.getUsername())
                .build();

        Sale saved = salesRepository.save(sale);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToResponse(saved));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SaleResponse> getSaleById(@PathVariable String id, @AuthenticationPrincipal User currentUser) {
        Sale sale = salesRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Venta no encontrada"));

        if (currentUser.getRole() == Role.BRANCH && !sale.getBranch().equals(currentUser.getBranch())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acceso denegado a otra sucursal");
        }

        return ResponseEntity.ok(mapToResponse(sale));
    }

    @GetMapping
    public ResponseEntity<List<SaleResponse>> listSales(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
            @RequestParam(required = false) String branch,
            @AuthenticationPrincipal User currentUser
    ) {
        String queryBranch = branch;
        if (currentUser.getRole() == Role.BRANCH) {
            queryBranch = currentUser.getBranch();
        }

        List<Sale> sales = salesRepository.findFilteredSales(queryBranch, from, to);
        List<SaleResponse> response = sales.stream().map(this::mapToResponse).collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SaleResponse> updateSale(@PathVariable String id, @Valid @RequestBody SaleRequest request, @AuthenticationPrincipal User currentUser) {
        Sale sale = salesRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Venta no encontrada"));

        if (currentUser.getRole() == Role.BRANCH && (!sale.getBranch().equals(currentUser.getBranch()) || !request.getBranch().equals(currentUser.getBranch()))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No puedes modificar ventas de otra sucursal");
        }

        sale.setSku(request.getSku());
        sale.setUnits(request.getUnits());
        sale.setPrice(request.getPrice());
        sale.setBranch(request.getBranch());
        sale.setSoldAt(request.getSoldAt());

        Sale updated = salesRepository.save(sale);
        return ResponseEntity.ok(mapToResponse(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSale(@PathVariable String id) {
        Sale sale = salesRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Venta no encontrada"));
        salesRepository.delete(sale);
        return ResponseEntity.noContent().build();
    }

    private SaleResponse mapToResponse(Sale sale) {
        return SaleResponse.builder()
                .id(sale.getId())
                .sku(sale.getSku())
                .units(sale.getUnits())
                .price(sale.getPrice())
                .branch(sale.getBranch())
                .soldAt(sale.getSoldAt())
                .createdBy(sale.getCreatedBy())
                .build();
    }
}