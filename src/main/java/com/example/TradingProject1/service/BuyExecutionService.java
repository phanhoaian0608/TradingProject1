package com.example.TradingProject1.service;

import com.example.TradingProject1.dto.request.TradingRequest;
import com.example.TradingProject1.entity.TradingDataAggregation;
import com.example.TradingProject1.entity.TradingTransactionHistory;
import com.example.TradingProject1.entity.UserWallet;
import com.example.TradingProject1.enums.CryptoType;
import com.example.TradingProject1.enums.TradingMethod;
import com.example.TradingProject1.enums.TradingStatus;
import com.example.TradingProject1.service.interfaces.TradingExecutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class BuyExecutionService implements TradingExecutionService<TradingRequest> {

    private final TradingTransactionHistoryService tradingTransactionHistoryService;
    private final UserWalletService userWalletService;
    private final GetTradeDataService getTradeDataService;
    private final UserService userService;

    @Autowired
    public BuyExecutionService(TradingTransactionHistoryService tradingTransactionHistoryService,
                               UserWalletService userWalletService, GetTradeDataService getTradeDataService,
                               UserService userService) {
        this.tradingTransactionHistoryService = tradingTransactionHistoryService;
        this.userWalletService = userWalletService;
        this.getTradeDataService = getTradeDataService;
        this.userService = userService;
    }

    @Override
    public ResponseEntity executeTrade(TradingRequest tradingRequest) {
        UserWallet originalWalletState = null;

        try {
            // Get current wallet information
            UserWallet userWallet = userWalletService.getCurrentUserWallet();
            originalWalletState = keepCurrentStateOfWallet(userWallet);

            // Get best aggregated pricing
            TradingDataAggregation aggregatedData = getTradeDataService.getDataByType(tradingRequest.getType());

            if (aggregatedData == null) {
                return ResponseEntity.status(404).body("No matching record found for type: " + tradingRequest.getType());
            }

            if (tradingRequest.getBuyQuantity() == null || tradingRequest.getBuyQuantity() > aggregatedData.getSellQuantity()) {
                return ResponseEntity.status(400).body("Invalid request to buy");
            }

            // Save transaction with status INITIALIZED
            TradingTransactionHistory transaction = initializeNewTradingTransaction(userWallet, tradingRequest, aggregatedData);
            tradingTransactionHistoryService.saveTransaction(transaction);

            // Update wallet balance and crypto quantity
            double totalMoneyNeeded = tradingRequest.getBuyQuantity() * aggregatedData.getSellPrice();
            if (userWallet.getBalance() < totalMoneyNeeded) {
                tradingTransactionHistoryService.updateTransactionStatus(transaction, TradingStatus.FAILURE);
                return ResponseEntity.status(400).body("Insufficient balance for transaction");
            }
            userWallet = setWalletData(userWallet, tradingRequest, totalMoneyNeeded);
            userWalletService.updateWallet(userWallet);

            // Save transaction with status IN_PROGRESS
            transaction.setPrice(aggregatedData.getSellPrice());
            transaction.setRemainingBalance(userWallet.getBalance());
            tradingTransactionHistoryService.updateTransactionStatus(transaction, TradingStatus.IN_PROGRESS);

            // Mock third-party service call - assume that we have 3rd party to call to trade
            boolean isTransactionSuccessful = mockThirdPartyBuyService();
            TradingStatus tradingStatus = isTransactionSuccessful ? TradingStatus.SUCCESS : TradingStatus.FAILURE;
            tradingTransactionHistoryService.updateTransactionStatus(transaction, tradingStatus);

            if (!isTransactionSuccessful) {
                throw new RuntimeException("Transaction failed when called to third-party service");
            }

            return ResponseEntity.ok("Transaction successful");
        } catch (Exception e) {
            // Rollback wallet changes
            if (originalWalletState != null) {
                userWalletService.updateWallet(originalWalletState);
            }
            return ResponseEntity.status(500).body("Transaction failed: " + e.getMessage());
        }
    }

    private boolean mockThirdPartyBuyService() {
        return true; // Simulate success
    }

    private UserWallet keepCurrentStateOfWallet(UserWallet userWallet) {
        UserWallet currentState = new UserWallet();
        currentState.setId(userWallet.getId());
        currentState.setBalance(userWallet.getBalance());
        currentState.setETHUSDT(userWallet.getETHUSDT());
        currentState.setBTCUSDT(userWallet.getBTCUSDT());
        return currentState;
    }

    private TradingTransactionHistory initializeNewTradingTransaction(UserWallet userWallet,
                                                                      TradingRequest tradingRequest,
                                                                      TradingDataAggregation tradingDataAggregation) {
        TradingTransactionHistory transaction = new TradingTransactionHistory();
        transaction.setUserId(userService.getCurrentUserId()); // Replace with actual user ID
        transaction.setCryptoType(CryptoType.valueOf(tradingRequest.getType()));
        transaction.setTradingType(TradingMethod.BUY);
        transaction.setQuantity(tradingRequest.getBuyQuantity());
        transaction.setPrice(tradingDataAggregation.getBuyPrice());
        transaction.setRemainingBalance(userWallet.getBalance());
        transaction.setTransactionTimestamp(LocalDateTime.now());
        transaction.setStatus(TradingStatus.INITIALIZED);

        return transaction;
    }

    private UserWallet setWalletData(UserWallet userWallet, TradingRequest tradingRequest, Double totalMoneyNeeded) {

        userWallet.setBalance(userWallet.getBalance() - totalMoneyNeeded);

        if (CryptoType.ETHUSDT.name().equalsIgnoreCase(tradingRequest.getType())) {
            userWallet.setETHUSDT(userWallet.getETHUSDT() + tradingRequest.getBuyQuantity());
        } else if (CryptoType.BTCUSDT.name().equalsIgnoreCase(tradingRequest.getType())) {
            userWallet.setBTCUSDT(userWallet.getBTCUSDT() + tradingRequest.getBuyQuantity());
        }

        return userWallet;
    }
}