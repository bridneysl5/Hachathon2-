package com.example.oreoinsightfactory.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "sales")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sale {

    @Id
    private String id;

    @Column(nullable = false)
    private String sku;

    @Column(nullable = false)
    private Integer units;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private String branch;

    @Column(nullable = false)
    private Instant soldAt;

    @Column(nullable = false)
    private String createdBy;

    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = "s_" + System.currentTimeMillis();
        }
    }
}