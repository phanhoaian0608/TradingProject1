package com.example.TradingProject1.factory;

import com.example.TradingProject1.enums.TradingMethod;
import com.example.TradingProject1.service.BuyExecutionService;
import com.example.TradingProject1.service.GetTradeDataService;
import com.example.TradingProject1.service.SellExecutionService;
import com.example.TradingProject1.service.interfaces.GetDataService;
import com.example.TradingProject1.service.interfaces.TradingExecutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TradingServiceFactory {

    private final BuyExecutionService buyService;
    private final SellExecutionService sellService;
    private final GetTradeDataService getTradeDataService;

    @Autowired
    public TradingServiceFactory(BuyExecutionService buyService, SellExecutionService sellService, GetTradeDataService getTradeDataService) {
        this.buyService = buyService;
        this.sellService = sellService;
        this.getTradeDataService = getTradeDataService;
    }

    public TradingExecutionService getTradingExecutionService(TradingMethod tradingMethod) {
        if (TradingMethod.BUY.equals(tradingMethod)) {
            return buyService;
        } else if (TradingMethod.SELL.equals(tradingMethod)) {
            return sellService;
        }
        throw new IllegalArgumentException("Invalid trading method: " + tradingMethod);
    }

    public GetDataService getTradingDataService() {
        return getTradeDataService;

    }
}