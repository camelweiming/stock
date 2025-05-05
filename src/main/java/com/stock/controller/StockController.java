package com.stock.controller;

import com.stock.model.StockPrice;
import com.stock.service.StockService;
import com.stock.service.SymbolService;
import com.stock.service.impl.StockServiceImpl;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
public class StockController {
    @Resource
    private StockService stockService;
    @Resource
    private SymbolService symbolService;
    private static final Logger log = LoggerFactory.getLogger(StockController.class);

    @GetMapping("/api/stock/data")
    public Map<String, Object> getStockData(@RequestParam String symbol, @RequestParam(required = false) String view, @RequestParam String startDate, @RequestParam String endDate) {
        LocalDate start = YearMonth.parse(startDate).atDay(1);
        LocalDate end = YearMonth.parse(endDate).atEndOfMonth();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        log.info("getStockData：startTime={}, endTime={}, view={}", start.format(formatter), end.format(formatter), view);
        List<StockPrice> stockData = new ArrayList<>();
        if (view != null) {
            stockData.addAll(stockService.getStockDataByView(symbol, start, view));
        } else {
            stockData.addAll(stockService.getStockData(symbol, start));
        }

        // 过滤掉结束日期之后的数据
        List<StockPrice> filteredData = stockData.stream().filter(price -> !price.getTradeDate().isAfter(end)).collect(Collectors.toList());
        Map<String, Object> response = new HashMap<>();
        response.put("stockData", filteredData);
        return response;
    }

    @GetMapping("/api/symbols")
    public Map<String, Object> getSymbols() {
        List<String> symbols = symbolService.getAllSymbols();
        Map<String, Object> response = new HashMap<>();
        response.put("symbols", symbols);
        response.put("defaultSymbol", symbols.isEmpty() ? "" : symbols.get(0));
        return response;
    }

    @GetMapping("/api/getDetail")
    public Map<String, Object> getDetail(@RequestParam String symbol, @RequestParam String date, @RequestParam String view) {
        // 解析日期
        LocalDate tradeDate = LocalDate.parse(date);
        // 根据视图类型获取数据
        StockPrice detail = null;
        if ("day".equalsIgnoreCase(view)) {
            detail = stockService.getStockDetailByDay(symbol, tradeDate);
        } else if ("week".equalsIgnoreCase(view)) {
            detail = stockService.getStockDetailByWeek(symbol, tradeDate);
        } else if ("month".equalsIgnoreCase(view)) {
            detail = stockService.getStockDetailByMonth(symbol, tradeDate);
        }
        Map<String, Object> response = new HashMap<>();
        if (detail != null) {
            response.put("symbol", detail.getSymbol());
            response.put("date", detail.getTradeDate().toString());
            response.put("openPrice", detail.getOpenPrice());
            response.put("closePrice", detail.getClosePrice());
            response.put("highPrice", detail.getHighPrice());
            response.put("lowPrice", detail.getLowPrice());
            // 可补充更多字段
        }
        return response;
    }
} 