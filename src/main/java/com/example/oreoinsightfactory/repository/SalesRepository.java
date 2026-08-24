package com.example.oreoinsightfactory.repository;

import com.example.oreoinsightfactory.model.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface SalesRepository extends JpaRepository<Sale, String> {
    List<Sale> findByBranch(String branch);

    @Query("SELECT s FROM Sale s WHERE (:branch IS NULL OR s.branch = :branch) " +
            "AND (CAST(:from AS string) IS NULL OR s.soldAt >= :from) " +
            "AND (CAST(:to AS string) IS NULL OR s.soldAt <= :to)")
    List<Sale> findFilteredSales(@Param("branch") String branch,
                                 @Param("from") Instant from,
                                 @Param("to") Instant to);
}