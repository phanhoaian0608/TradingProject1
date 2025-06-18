package com.example.TradingProject1.service;

import com.example.TradingProject1.entity.TradingDataAggregation;
import com.example.TradingProject1.enums.CryptoType;
import com.example.TradingProject1.dto.*;
import com.example.TradingProject1.repository.TradingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class BestPricingJobService {
    private final String BINANCE_URL = "https://api.binance.com/api/v3/ticker/bookTicker";
    private final String HOUBI_URL = "https://api.huobi.pro/market/tickers";

    private final RestTemplate restTemplate;
    private final TradingRepository tradingRepository;

    @Autowired
    public BestPricingJobService(RestTemplate restTemplate, TradingRepository tradingRepository) {
        this.restTemplate = restTemplate;
        this.tradingRepository = tradingRepository;
    }

    public void fetchAndSaveCryptoAggregation() {
        System.out.println("fetch");
        List<BinanceTradingData> binanceTradingDataList = fetchBinanceData();
        HoubiTradingDataList houbiTradingDataList = fetchHoubiData();

        Map<String, BinanceTradingData> binanceTradingDataMap = binanceTradingDataList.parallelStream()
                .filter(binanceTradingData -> CryptoType.BTCUSDT.toString().equalsIgnoreCase(binanceTradingData.getSymbol())
                        || CryptoType.ETHUSDT.toString().equalsIgnoreCase(binanceTradingData.getSymbol()))
                .collect(Collectors.toMap(BinanceTradingData::getSymbol, Function.identity()));

        Map<String, HoubiTradingData> houbiTradingDataMap = houbiTradingDataList.getData().parallelStream()
                .filter(houbiTradingData -> CryptoType.BTCUSDT.toString().equalsIgnoreCase(houbiTradingData.getSymbol())
                        || CryptoType.ETHUSDT.toString().equalsIgnoreCase(houbiTradingData.getSymbol()))
                .collect(Collectors.toMap(houbiTradingData -> houbiTradingData.getSymbol().toUpperCase(), Function.identity()));

        List<TradingDataAggregation> tradingDataAggregationList = new ArrayList<>();
        tradingDataAggregationList.add(getBestPricingAggregation(CryptoType.BTCUSDT, binanceTradingDataMap, houbiTradingDataMap));
        tradingDataAggregationList.add(getBestPricingAggregation(CryptoType.ETHUSDT, binanceTradingDataMap, houbiTradingDataMap));

        tradingRepository.saveAll(tradingDataAggregationList);
    }

    private TradingDataAggregation getBestPricingAggregation(CryptoType cryptoType, Map<String, BinanceTradingData> binanceTradingDataMap, Map<String, HoubiTradingData> houbiTradingDataMap) {
        TradingDataAggregation data = new TradingDataAggregation();
        data.setType(cryptoType.name());
        if (binanceTradingDataMap.get(cryptoType.name()).getBidPrice() >= houbiTradingDataMap.get(cryptoType.name()).getBid()) {
            data.setBuyPrice(binanceTradingDataMap.get(cryptoType.name()).getBidPrice());
            data.setBuyQuantity(binanceTradingDataMap.get(cryptoType.name()).getBidQty());
        } else {
            data.setBuyPrice(houbiTradingDataMap.get(cryptoType.name()).getBid());
            data.setBuyQuantity(houbiTradingDataMap.get(cryptoType.name()).getBidSize());
        }

        if (binanceTradingDataMap.get(cryptoType.name()).getAskPrice() <= houbiTradingDataMap.get(cryptoType.name()).getAsk()) {
            data.setSellPrice(binanceTradingDataMap.get(cryptoType.name()).getAskPrice());
            data.setSellQuantity(binanceTradingDataMap.get(cryptoType.name()).getAskQty());
        } else {
            data.setSellPrice(houbiTradingDataMap.get(cryptoType.name()).getAsk());
            data.setSellQuantity(houbiTradingDataMap.get(cryptoType.name()).getAskSize());
        }

        return data;
    }

    private List<BinanceTradingData> fetchBinanceData() {
        BinanceTradingData[] binanceTradingData = restTemplate.getForObject(BINANCE_URL, BinanceTradingData[].class);
        if (binanceTradingData != null) {
            return Arrays.asList(binanceTradingData);
        }
        return new ArrayList<>();
    }

    private HoubiTradingDataList fetchHoubiData() {
        return restTemplate.getForObject(HOUBI_URL, HoubiTradingDataList.class);
    }


}
