package com.stock.controller;

import com.stock.model.StockPrice;
import com.stock.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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
} 