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

@Controller
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

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
    public Map<String, Object> getNews(@RequestParam String date, @RequestParam(defaultValue = "5") int count) {
        // 模拟数据
        List<Map<String, String>> newsList = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Map<String, String> news = new HashMap<>();
            news.put("title", "股票市场重要新闻 " + (i + 1));
            news.put("time", date + " " + String.format("%02d:00", i));
            news.put("content", "这是第" + (i + 1) + "条新闻的详细内容，包含市场分析和预测。");
            news.put("source", "财经新闻");
            newsList.add(news);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("code", 0);
        response.put("message", "success");
        response.put("data", newsList);
        return response;
    }
} 