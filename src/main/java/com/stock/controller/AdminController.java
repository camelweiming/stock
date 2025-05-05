package com.stock.controller;

import com.stock.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author weiming
 * @date 2025/5/5
 */
@RestController
@RequiredArgsConstructor
public class AdminController {
    private final StockService stockService;
    @PostMapping("/api/admin/initialize")
    public String initializeData() {
        stockService.initializeHistoricalData();
        return "Historical data initialization started";
    }

    @PostMapping("/api/admin/incremental-update")
    public String incrementalUpdate() {
        stockService.incrementalUpdate();
        return "Incremental update started";
    }
}
