package com.stock.service.impl;

import com.stock.service.SymbolService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author weiming
 * @date 2025/5/5
 */
@Service
@Slf4j
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "stock")
public class SymbolServiceImpl implements SymbolService {
    private List<String> symbols;

    @PostConstruct
    public void init() {
        log.info("StockService initialized with symbols: {}", symbols);
    }

    public void setSymbols(List<String> symbols) {
        this.symbols = symbols;
        log.info("Symbols set to: {}", symbols);
    }

    @Override
    public List<String> getAllSymbols() {
        log.info("Getting all symbols: {}", symbols);
        return symbols != null ? symbols : new ArrayList<>();
    }
}
