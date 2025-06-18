package com.example.TradingProject1.dto.request;


import com.example.TradingProject1.enums.TradingMethod;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TradingRequest {
    private String type;
    private Double sellQuantity;
    private Double buyQuantity;
    private TradingMethod tradingMethod;


}
