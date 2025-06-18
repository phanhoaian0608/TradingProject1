package com.example.TradingProject1.service.interfaces;

import org.springframework.http.ResponseEntity;

public interface TradingExecutionService<T> {
    ResponseEntity executeTrade(T tradingData);

}
