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
public class SellExecutionService implements TradingExecutionService<TradingRequest> {

    private final TradingTransactionHistoryService tradingTransactionHistoryService;
    private final UserWalletService userWalletService;
    private final GetTradeDataService getTradeDataService;
    private final UserService userService;


    @Autowired
    public SellExecutionService(TradingTransactionHistoryService tradingTransactionHistoryService,
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
            //Get current wallet information
            UserWallet userWallet = userWalletService.getCurrentUserWallet();
            originalWalletState = keepCurrentStateOfWallet(userWallet);


            boolean isValidSellTransaction = isValidRequestToSell(userWallet, tradingRequest);
            if (!isValidSellTransaction) {
                return ResponseEntity.status(400).body("Invalid request to sell");
            }

            // Get best aggregated pricing
            TradingDataAggregation aggregatedData = getTradeDataService.getDataByType(tradingRequest.getType());

            if (aggregatedData == null) {
                return ResponseEntity.status(404).body("No matching record found for type: " + tradingRequest.getType());
            }

            // Save transaction with status INITIALIZED
            TradingTransactionHistory transaction = initializeNewTradingTransaction(userWallet, tradingRequest, aggregatedData);
            tradingTransactionHistoryService.saveTransaction(transaction);

            //  Update wallet balance and crypto quantity
            double totalMoneyNeeded = tradingRequest.getSellQuantity() * aggregatedData.getBuyPrice();
            setWalletData(userWallet, tradingRequest, totalMoneyNeeded);
            userWalletService.updateWallet(userWallet);

            // Save transaction with status IN_PROGRESS
            transaction.setPrice(aggregatedData.getBuyPrice());
            transaction.setRemainingBalance(userWallet.getBalance());
            tradingTransactionHistoryService.updateTransactionStatus(transaction, TradingStatus.IN_PROGRESS);

            // Mock third-party service call - assume that we have 3rd party to call to trade
            boolean isTransactionSuccessful = mockThirdPartySellService();
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

    private boolean mockThirdPartySellService() {
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

    private TradingTransactionHistory initializeNewTradingTransaction(UserWallet userWallet, TradingRequest tradingRequest,
                                                                      TradingDataAggregation tradingDataAggregation) {
        TradingTransactionHistory transaction = new TradingTransactionHistory();
        transaction.setUserId(userService.getCurrentUserId()); // Replace with actual user ID
        transaction.setCryptoType(CryptoType.valueOf(tradingRequest.getType()));
        transaction.setTradingType(TradingMethod.SELL);
        transaction.setQuantity(tradingRequest.getSellQuantity());
        transaction.setPrice(tradingDataAggregation.getSellPrice());
        transaction.setRemainingBalance(userWallet.getBalance());
        transaction.setTransactionTimestamp(LocalDateTime.now());
        transaction.setStatus(TradingStatus.INITIALIZED);

        return transaction;
    }

    private void setWalletData(UserWallet userWallet, TradingRequest tradingRequest, Double totalMoneyNeeded) {

        userWallet.setBalance(userWallet.getBalance() + totalMoneyNeeded);

        if (CryptoType.ETHUSDT.name().equalsIgnoreCase(tradingRequest.getType())) {
            userWallet.setETHUSDT(userWallet.getETHUSDT() - tradingRequest.getSellQuantity());
        } else if (CryptoType.BTCUSDT.name().equalsIgnoreCase(tradingRequest.getType())) {
            userWallet.setBTCUSDT(userWallet.getBTCUSDT() - tradingRequest.getSellQuantity());
        }

    }

    private boolean isValidRequestToSell(UserWallet userWallet, TradingRequest tradingRequest) {
        if (tradingRequest.getSellQuantity() == null) {
            return false;
        } else if (CryptoType.ETHUSDT.name().equalsIgnoreCase(tradingRequest.getType())) {
            return userWallet.getETHUSDT() >= tradingRequest.getSellQuantity();
        } else if (CryptoType.BTCUSDT.name().equalsIgnoreCase(tradingRequest.getType())) {
            return userWallet.getBTCUSDT() >= tradingRequest.getSellQuantity();
        }
        return false;
    }

}