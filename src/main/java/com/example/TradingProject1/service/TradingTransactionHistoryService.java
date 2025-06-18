package com.example.TradingProject1.service;

import com.example.TradingProject1.entity.TradingTransactionHistory;
import com.example.TradingProject1.enums.TradingStatus;
import com.example.TradingProject1.repository.TradingTransactionHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TradingTransactionHistoryService {

    private final TradingTransactionHistoryRepository tradingTransactionHistoryRepository;
    private final UserService userService;

    @Autowired
    public TradingTransactionHistoryService(TradingTransactionHistoryRepository tradingTransactionHistoryRepository, UserService userService) {
        this.tradingTransactionHistoryRepository = tradingTransactionHistoryRepository;
        this.userService = userService;
    }

    public ResponseEntity getUserTradingHistory() {
        return ResponseEntity.ok(tradingTransactionHistoryRepository.findByUserId(userService.getCurrentUserId()));
    }
    @Transactional
    public void saveTransaction(TradingTransactionHistory transaction) {
        tradingTransactionHistoryRepository.save(transaction);
    }

    @Transactional
    public void updateTransactionStatus(TradingTransactionHistory transaction, TradingStatus tradingStatus) {
        transaction.setStatus(tradingStatus);
        tradingTransactionHistoryRepository.save(transaction);
    }
}