package com.example.TradingProject1.entity;

import com.example.TradingProject1.enums.CryptoType;
import com.example.TradingProject1.enums.TradingMethod;
import com.example.TradingProject1.enums.TradingStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class TradingTransactionHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer transactionId;

    @Column(nullable = false)
    private Integer userId;

    @Column(nullable = false)
    private TradingMethod tradingType; //"BUY" or "SELL"

    @Column(nullable = false)
    private CryptoType cryptoType;

    @Column(nullable = false)
    private Double quantity;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private Double remainingBalance;

    @Column(nullable = false)
    private LocalDateTime transactionTimestamp;

    @Column(nullable = false)
    private TradingStatus status;
}