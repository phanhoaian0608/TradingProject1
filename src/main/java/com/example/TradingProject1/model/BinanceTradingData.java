package com.example.TradingProject1.model;

import lombok.Data;
import lombok.Getter;

@Getter
public class BinanceTradingData {
    private String symbol;
    private Double bidPrice;
    private Double askPrice;
    private Double bidQty;
    private Double askQty;

}
