package com.example.TradingProject1.controller;

import com.example.TradingProject1.entity.TradingDataAggregation;
import com.example.TradingProject1.model.BestPricingResponse;
import com.example.TradingProject1.service.TradingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/trading")
public class TradingController {

    private final TradingService tradingService;

    @Autowired
    public TradingController(TradingService tradingService) {
        this.tradingService = tradingService;
    }

    @GetMapping(path = "/best", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<TradingDataAggregation> getBestPrice() {
        return tradingService.getLatestBestAggregatedData();
    }
}
