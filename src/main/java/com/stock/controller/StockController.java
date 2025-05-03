package com.stock.controller;

import com.stock.model.StockPrice;
import com.stock.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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
    public List<StockPrice> getStockData(@RequestParam String symbol, 
                                        @RequestParam(required = false) String months) {
        LocalDate startDate = LocalDate.now().minusMonths(months != null ? Long.parseLong(months) : 6);
        return stockService.getStockData(symbol, startDate);
    }

    @GetMapping("/api/symbols")
    @ResponseBody
    public List<String> getSymbols() {
        return stockService.getAllSymbols();
    }
} 