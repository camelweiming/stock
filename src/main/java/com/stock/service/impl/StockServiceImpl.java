package com.stock.service.impl;

import com.stock.mapper.StockPriceMapper;
import com.stock.model.StockPrice;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.time.temporal.IsoFields;
import java.util.*;

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
        List<StockPrice> list = stockPriceMapper.findBySymbolAndDateRange(symbol, startDate);
        switch (view) {
            case "week":
                return buildDayData(list, new WeekKeyBuilder());
            case "month":
                return buildDayData(list, new MonthKeyBuilder());
            default: // "day"
                return list;
        }
    }

    public static List<StockPrice> buildDayData(List<StockPrice> prices, KeyBuilder keyBuilder) {
        List<StockPrice> weeks = new ArrayList<>();
        Map<String, StockPrice> mapping = new HashMap<>();
        for (StockPrice stockPrice : prices) {
            String key = keyBuilder.buildKey(stockPrice);
            StockPrice base = mapping.get(key);
            if (base == null) {
                base = new StockPrice();
                base.setOpenPrice(stockPrice.getOpenPrice());
                base.setHighPrice(stockPrice.getHighPrice());
                base.setLowPrice(stockPrice.getLowPrice());
                base.setSymbol(stockPrice.getSymbol());
                base.setTradeDate(stockPrice.getTradeDate());
                base.setClosePrice(stockPrice.getClosePrice());
                base.setCreatedAt(stockPrice.getCreatedAt());
                base.setUpdatedAt(stockPrice.getUpdatedAt());
                base.setId(stockPrice.getId());
                base.setVolume(stockPrice.getVolume());
                mapping.put(key, base);
                continue;
            }
            mergeStockPrice(base, stockPrice);
        }
        weeks.addAll(mapping.values());
        Collections.sort(weeks, Comparator.comparing(StockPrice::getTradeDate));
        return weeks;
    }

    public static void mergeStockPrice(StockPrice base, StockPrice current) {
        if (base.getHighPrice().compareTo(current.getHighPrice()) < 0) {
            base.setHighPrice(current.getHighPrice());
        }
        if (base.getLowPrice().compareTo(current.getLowPrice()) > 0) {
            base.setLowPrice(current.getLowPrice());
        }
        if (base.getTradeDate().compareTo(current.getTradeDate()) < 0) {
            base.setTradeDate(current.getTradeDate());
            base.setClosePrice(current.getClosePrice());
            base.setCreatedAt(current.getCreatedAt());
            base.setUpdatedAt(current.getUpdatedAt());
            base.setClosePrice(current.getClosePrice());
            base.setId(current.getId());
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

    public interface KeyBuilder {
        String buildKey(StockPrice stockPrice);
    }

    public static class WeekKeyBuilder implements KeyBuilder {

        @Override
        public String buildKey(StockPrice stockPrice) {
            return stockPrice.getTradeDate().get(ChronoField.YEAR) + "_" + stockPrice.getTradeDate().get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
        }
    }

    public static class MonthKeyBuilder implements KeyBuilder {

        @Override
        public String buildKey(StockPrice stockPrice) {
            return stockPrice.getTradeDate().get(ChronoField.YEAR) + "_" + stockPrice.getTradeDate().get(ChronoField.MONTH_OF_YEAR);
        }
    }
} 