package com.example.TradingProject1.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class TradingDataAggregation {
    @Id
    @Column( nullable = false, unique = true)
    private String type;

    private Double sellPrice;
    private Double sellQuantity;
    private Double buyPrice;
    private Double buyQuantity;
}
