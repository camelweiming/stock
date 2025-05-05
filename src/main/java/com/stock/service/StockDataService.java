package com.stock.service;

/**
 * @author weiming
 * @date 2025/5/5
 */
public interface StockDataService {
    void initializeHistoricalData();

    void incrementalUpdate();
}
