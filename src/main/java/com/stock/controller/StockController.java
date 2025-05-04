package com.stock.controller;

import com.stock.model.StockPrice;
import com.stock.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.stock.model.News;
import com.stock.mapper.NewsMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;
    private final NewsMapper newsMapper;
    private static final Logger log = LoggerFactory.getLogger(StockController.class);

    @GetMapping("/")
    public String index(Model model) {
        List<String> symbols = stockService.getAllSymbols();
        model.addAttribute("symbols", symbols);
        return "index";
    }

    @PostMapping("/initialize")
    @ResponseBody
    public String initializeData() {
        stockService.initializeHistoricalData();
        return "Historical data initialization started";
    }

    @PostMapping("/incremental-update")
    @ResponseBody
    public String incrementalUpdate() {
        stockService.incrementalUpdate();
        return "Incremental update started";
    }

    @GetMapping("/api/stock-data")
    @ResponseBody
    public List<StockPrice> getStockData(@RequestParam String[] symbols, 
                                        @RequestParam(required = false) String view,
                                        @RequestParam String startDate,
                                        @RequestParam String endDate) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        List<StockPrice> result = new ArrayList<>();
        
        for (String symbol : symbols) {
            if (view != null) {
                result.addAll(stockService.getStockDataByView(symbol, start, view));
            } else {
                result.addAll(stockService.getStockData(symbol, start));
            }
        }
        
        // 过滤掉结束日期之后的数据
        return result.stream()
                .filter(price -> !price.getTradeDate().isAfter(end))
                .collect(Collectors.toList());
    }

    @GetMapping("/api/symbols")
    @ResponseBody
    public List<String> getSymbols() {
        return stockService.getAllSymbols();
    }

    @GetMapping("/api/get_news")
    @ResponseBody
    public Map<String, Object> getNews(
            @RequestParam String startTime,
            @RequestParam String endTime,
            @RequestParam(defaultValue = "5") int count,
            @RequestParam(defaultValue = "day") String mode) {
        java.time.LocalDateTime start = java.time.LocalDateTime.parse(startTime.replace(" ", "T"));
        java.time.LocalDateTime end = java.time.LocalDateTime.parse(endTime.replace(" ", "T"));
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd");
        log.info("getNews参数：startTime={}, endTime={}, count={}, mode={}", start.format(formatter), end.format(formatter), count, mode);
        List<News> newsList = newsMapper.findNewsByTimeRange(start, end, count);

        Map<String, Object> response = new HashMap<>();
        response.put("code", 0);
        response.put("message", "success");
        response.put("data", newsList);
        response.put("mode", mode);
        response.put("startTime", startTime);
        response.put("endTime", endTime);
        return response;
    }
} 