package com.example.TradingProject1.job;

import com.example.TradingProject1.service.BestPricingJobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class BestPricingScheduler {

    private final Random random = new Random();
    private final BestPricingJobService bestPricingJobService;

    @Autowired
    public BestPricingScheduler(BestPricingJobService bestPricingJobService) {
        this.bestPricingJobService = bestPricingJobService;
    }

    @Async
    @Scheduled(fixedRate = 10000) // Runs every 10 seconds
    public void updatePrices() {
        bestPricingJobService.fetchAndSaveCryptoAggregation();

    }
}
