package com.stock.controller;

import com.stock.service.StockDataService;
import com.stock.service.StockService;
import com.stock.service.impl.StockServiceImpl;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author weiming
 * @date 2025/5/5
 */
@RestController
public class AdminController {
    @Resource
    private StockDataService stockDataService;

    @PostMapping("/api/admin/initialize")
    public String initializeData() {
        stockDataService.initializeHistoricalData();
        return "Historical data initialization started";
    }

    @PostMapping("/api/admin/incremental-update")
    public String incrementalUpdate() {
        stockDataService.incrementalUpdate();
        return "Incremental update started";
    }
}
