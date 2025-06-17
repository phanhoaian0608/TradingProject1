package com.example.TradingProject1.repository;

import com.example.TradingProject1.entity.TradingDataAggregation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TradingRepository extends JpaRepository<TradingDataAggregation, String> {
}
