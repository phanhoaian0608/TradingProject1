package com.example.TradingProject1.service;

import com.example.TradingProject1.entity.TradingDataAggregation;
import com.example.TradingProject1.repository.TradingRepository;
import com.example.TradingProject1.service.interfaces.GetDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetTradeDataService implements GetDataService {
    private final TradingRepository tradingRepository;

    @Autowired
    public GetTradeDataService(TradingRepository tradingRepository) {
        this.tradingRepository = tradingRepository;
    }

    @Override
    public ResponseEntity getData() {
        System.out.println("Fetching trade data...");
        List<TradingDataAggregation> tradingDataAggregations = tradingRepository.findAll();
        return ResponseEntity.ok(tradingDataAggregations);
    }

    public TradingDataAggregation getDataByType(String cryptoType) {
        System.out.println("Fetching trade data...");
        TradingDataAggregation tradingDataAggregation = tradingRepository.findByType(cryptoType);
        return tradingDataAggregation;
    }

}
