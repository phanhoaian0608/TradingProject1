package com.example.TradingProject1.controller;


import com.example.TradingProject1.dto.request.TradingRequest;
import com.example.TradingProject1.entity.UserWallet;
import com.example.TradingProject1.factory.TradingServiceFactory;
import com.example.TradingProject1.service.interfaces.GetDataService;
import com.example.TradingProject1.service.interfaces.TradingExecutionService;
import com.example.TradingProject1.service.TradingTransactionHistoryService;
import com.example.TradingProject1.service.UserWalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/trading")
public class TradingController {

    private final TradingServiceFactory tradingServiceFactory;
    private final UserWalletService userWalletService;
    private final TradingTransactionHistoryService tradingTransactionHistoryService;

    @Autowired
    public TradingController(TradingServiceFactory tradingServiceFactory, UserWalletService userWalletService,
                             TradingTransactionHistoryService tradingTransactionHistoryService) {
        this.userWalletService = userWalletService;
        this.tradingServiceFactory = tradingServiceFactory;
        this.tradingTransactionHistoryService = tradingTransactionHistoryService;
    }

    @GetMapping(path = "/best", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getBestPrice() {
        GetDataService tradingExecutionService = tradingServiceFactory.getTradingDataService();
        return tradingExecutionService.getData();
    }

    @PostMapping(path = "/trade", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity tradeBasedOnBestPrice(@RequestBody TradingRequest tradingRequest) {
        TradingExecutionService tradingExecutionService = tradingServiceFactory.getTradingExecutionService(tradingRequest.getTradingMethod());
        return tradingExecutionService.executeTrade(tradingRequest);

    }

    @GetMapping(path = "/wallet", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getUserWallet() {
        UserWallet userWallet = userWalletService.getCurrentUserWallet();
        return ResponseEntity.ok(userWallet);
    }

    @GetMapping(path = "/history", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getTradingHistory() {
        return tradingTransactionHistoryService.getUserTradingHistory();
    }
}
