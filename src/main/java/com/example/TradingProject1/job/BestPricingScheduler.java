package com.example.TradingProject1.job;

import com.example.TradingProject1.entity.TradingDataAggregation;
import com.example.TradingProject1.model.BinanceTradingData;
import com.example.TradingProject1.service.TradingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Random;

@Component
public class BestPricingScheduler {

    private final Random random = new Random();
    private final TradingService tradingService;

    @Autowired
    public BestPricingScheduler(TradingService tradingService) {
        this.tradingService = tradingService;
    }

    @Scheduled(fixedRate = 10000) // Runs every 10 seconds
    public void updatePrices() {
        List<TradingDataAggregation> tradingDataAggregations = tradingService.fetchCryptoAggregation();

        tradingService.saveNewAggregatedData(tradingDataAggregations);

    }
}
