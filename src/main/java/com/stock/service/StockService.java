package com.stock.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stock.mapper.StockPriceMapper;
import com.stock.model.StockPrice;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "stock")
public class StockService {
    private final StockPriceMapper stockPriceMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private List<String> symbols;
    private int initYears;
    private String alphaVantageApiKey;
    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 30000; // 30秒延迟
    private static final String ALPHA_VANTAGE_URL = "https://www.alphavantage.co/query";

    @PostConstruct
    public void init() {
        log.info("StockService initialized with symbols: {}", symbols);
    }

    public void setSymbols(List<String> symbols) {
        this.symbols = symbols;
        log.info("Symbols set to: {}", symbols);
    }

    public void setInitYears(int initYears) {
        this.initYears = initYears;
        log.info("Init years set to: {}", initYears);
    }

    public void setAlphaVantageApiKey(String alphaVantageApiKey) {
        this.alphaVantageApiKey = alphaVantageApiKey;
        log.info("Alpha Vantage API key set");
    }

    public void initializeHistoricalData() {
        log.info("Starting historical data initialization");
        Calendar from = Calendar.getInstance();
        from.add(Calendar.YEAR, -initYears);

        for (String symbol : symbols) {
            log.info("Processing symbol: {}", symbol);
            try {
                JsonNode timeSeriesData = getTimeSeriesWithRetry(symbol);
                if (timeSeriesData != null) {
                    JsonNode timeSeries = timeSeriesData.get("Time Series (Daily)");
                    if (timeSeries != null) {
                        log.info("Retrieved {} data points for symbol: {}", timeSeries.size(), symbol);
                        
                        for (Map.Entry<String, JsonNode> entry : (Iterable<Map.Entry<String, JsonNode>>) timeSeries::fields) {
                            LocalDate date = LocalDate.parse(entry.getKey(), DateTimeFormatter.ISO_LOCAL_DATE);
                            JsonNode quote = entry.getValue();
                            
                            StockPrice stockPrice = new StockPrice();
                            stockPrice.setSymbol(symbol);
                            stockPrice.setTradeDate(date);
                            stockPrice.setOpenPrice(new BigDecimal(quote.get("1. open").asText()));
                            stockPrice.setHighPrice(new BigDecimal(quote.get("2. high").asText()));
                            stockPrice.setLowPrice(new BigDecimal(quote.get("3. low").asText()));
                            stockPrice.setClosePrice(new BigDecimal(quote.get("4. close").asText()));
                            stockPrice.setVolume(Long.parseLong(quote.get("5. volume").asText()));
                            
                            stockPriceMapper.insertOrUpdate(stockPrice);
                            TimeUnit.MILLISECONDS.sleep(100); // 添加小延迟
                        }
                    }
                }
                TimeUnit.SECONDS.sleep(30); // 处理完一个股票后等待30秒
            } catch (Exception e) {
                log.error("Error processing symbol {}: {}", symbol, e.getMessage());
            }
        }
        log.info("Historical data initialization completed");
    }

    private JsonNode getTimeSeriesWithRetry(String symbol) {
        int retries = 0;
        while (retries < MAX_RETRIES) {
            try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                String url = String.format("%s?function=TIME_SERIES_DAILY&symbol=%s&outputsize=full&apikey=%s",
                        ALPHA_VANTAGE_URL, symbol, alphaVantageApiKey);
                HttpGet request = new HttpGet(url);
                
                return httpClient.execute(request, response -> {
                    String json = EntityUtils.toString(response.getEntity());
                    return objectMapper.readTree(json);
                });
            } catch (Exception e) {
                retries++;
                log.warn("Attempt {} failed for symbol {}: {}", retries, symbol, e.getMessage());
                if (retries < MAX_RETRIES) {
                    try {
                        TimeUnit.MILLISECONDS.sleep(RETRY_DELAY_MS);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }
        log.error("Failed to get time series data for symbol {} after {} attempts", symbol, MAX_RETRIES);
        return null;
    }

    public List<StockPrice> getStockData(String symbol, LocalDate startDate) {
        log.info("Getting stock data for symbol: {} from date: {}", symbol, startDate);
        return stockPriceMapper.findBySymbolAndDateRange(symbol, startDate);
    }

    public List<String> getAllSymbols() {
        log.info("Getting all symbols: {}", symbols);
        return symbols != null ? symbols : new ArrayList<>();
    }
} 