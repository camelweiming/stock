package com.stock.service.impl;

import com.stock.mapper.StockPriceMapper;
import com.stock.model.StockPrice;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
public class StockServiceImpl implements com.stock.service.StockService {
    @Resource
    private StockPriceMapper stockPriceMapper;

    @Override
    public List<StockPrice> getStockData(String symbol, LocalDate startDate) {
        log.info("Getting stock data for symbol: {} from date: {}", symbol, startDate);
        return stockPriceMapper.findBySymbolAndDateRange(symbol, startDate);
    }

    @Override
    public List<StockPrice> getStockDataByView(String symbol, LocalDate startDate, String view) {
        log.info("Getting {} view data for symbol: {} from date: {}", view, symbol, startDate);
        switch (view) {
            case "week":
                return stockPriceMapper.findWeeklyData(symbol, startDate);
            case "month":
                return stockPriceMapper.findMonthlyData(symbol, startDate);
            default: // "day"
                return stockPriceMapper.findBySymbolAndDateRange(symbol, startDate);
        }
    }

    // 获取某一天的详情
    @Override
    public StockPrice getStockDetailByDay(String symbol, LocalDate date) {
        List<StockPrice> list = stockPriceMapper.findBySymbolAndDateRange(symbol, date);
        return list.stream().filter(p -> p.getTradeDate().equals(date)).findFirst().orElse(null);
    }

    // 获取某一周的详情（取该周最后一天）
    @Override
    public StockPrice getStockDetailByWeek(String symbol, LocalDate date) {
        List<StockPrice> list = stockPriceMapper.findWeeklyData(symbol, date);
        return list.stream().filter(p -> p.getTradeDate().equals(date)).findFirst().orElse(null);
    }

    // 获取某一月的详情（取该月最后一天）
    @Override
    public StockPrice getStockDetailByMonth(String symbol, LocalDate date) {
        List<StockPrice> list = stockPriceMapper.findMonthlyData(symbol, date);
        return list.stream().filter(p -> p.getTradeDate().equals(date)).findFirst().orElse(null);
    }
} 