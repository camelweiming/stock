package com.stock.service;

import com.stock.model.StockPrice;

import java.time.LocalDate;
import java.util.List;

/**
 * @author weiming
 * @date 2025/5/5
 */
public interface StockService {
    List<StockPrice> getStockData(String symbol, LocalDate startDate);

    List<StockPrice> getStockDataByView(String symbol, LocalDate startDate, String view);

    // 获取某一天的详情
    StockPrice getStockDetailByDay(String symbol, LocalDate date);

    // 获取某一周的详情（取该周最后一天）
    StockPrice getStockDetailByWeek(String symbol, LocalDate date);

    // 获取某一月的详情（取该月最后一天）
    StockPrice getStockDetailByMonth(String symbol, LocalDate date);
}
