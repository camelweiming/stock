package com.stock.controller;

import com.stock.model.StockPrice;
import com.stock.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.stock.model.News;
import com.stock.mapper.NewsMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;
    private final NewsMapper newsMapper;
    private static final Logger log = LoggerFactory.getLogger(StockController.class);

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @PostMapping("/initialize")
    public String initializeData() {
        stockService.initializeHistoricalData();
        return "Historical data initialization started";
    }

    @PostMapping("/incremental-update")
    public String incrementalUpdate() {
        stockService.incrementalUpdate();
        return "Incremental update started";
    }

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

        // 获取新闻数据
        LocalDateTime startTime = start.atStartOfDay();
        LocalDateTime endTime = end.atTime(23, 59, 59);
        List<News> newsList = newsMapper.findNewsByTimeRange(startTime, endTime, 5);

        Map<String, Object> response = new HashMap<>();
        response.put("stockData", filteredData);
        response.put("newsList", newsList);
        return response;
    }

    @GetMapping("/api/symbols")
    public Map<String, Object> getSymbols() {
        List<String> symbols = stockService.getAllSymbols();
        Map<String, Object> response = new HashMap<>();
        response.put("symbols", symbols);
        response.put("defaultSymbol", symbols.isEmpty() ? "" : symbols.get(0));
        return response;
    }

    @GetMapping("/api/get_news")
    public Map<String, Object> getNews(@RequestParam String startTime, @RequestParam String endTime, @RequestParam(defaultValue = "5") int count, @RequestParam(defaultValue = "day") String mode) {
        java.time.LocalDateTime start = java.time.LocalDateTime.parse(startTime.replace(" ", "T"));
        java.time.LocalDateTime end = java.time.LocalDateTime.parse(endTime.replace(" ", "T"));
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd");
        log.info("getNews参数：startTime={}, endTime={}, count={}, mode={}", start.format(formatter), end.format(formatter), count, mode);
        List<News> newsList = newsMapper.findNewsByTimeRange(start, end, count);
        for (int i = 0; i < count; i++) {
            News n = new News();
            n.setTitle("股票市场重要新闻 " + (i + 1));
            n.setTime(LocalDateTime.now());
            n.setContent("这是第" + (i + 1) + "条新闻的详细内容，包含市场分析和预测。Get sentiment scores for one or more financial instruments (stocks, ETFs, crypto). Sentiment scores are calculated from both news and social media, normalized on a scale from -1 (very negative) to 1 (very positive).\n" +
                    "\nGet sentiment scores for one or more financial instruments (stocks, ETFs, crypto). Sentiment scores are calculated from both news and social media, normalized on a scale from -1 (very negative) to 1 (very positive).\n" +
                    "\nGet sentiment scores for one or more financial instruments (stocks, ETFs, crypto). Sentiment scores are calculated from both news and social media, normalized on a scale from -1 (very negative) to 1 (very positive).\n" +
                    "\n");
            n.setSource("财经新闻");
            newsList.add(n);
        }
        Map<String, Object> response = new HashMap<>();
        response.put("code", 0);
        response.put("message", "success");
        response.put("data", newsList);
        response.put("mode", mode);
        response.put("startTime", startTime);
        response.put("endTime", endTime);
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