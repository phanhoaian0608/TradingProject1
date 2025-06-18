package com.example.TradingProject1.repository;

import com.example.TradingProject1.entity.TradingTransactionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TradingTransactionHistoryRepository extends JpaRepository<TradingTransactionHistory, Long> {
    List<TradingTransactionHistory> findByUserId(Integer userId);

}