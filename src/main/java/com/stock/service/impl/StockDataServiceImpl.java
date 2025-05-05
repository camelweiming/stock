package com.stock.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stock.mapper.StockPriceMapper;
import com.stock.model.StockPrice;
import com.stock.service.SymbolService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "data")
public class StockDataServiceImpl implements com.stock.service.StockDataService {
    @Resource
    private StockPriceMapper stockPriceMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private int initYears;
    private String alphaVantageApiKey;
    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 30000; // 30秒延迟
    private static final String ALPHA_VANTAGE_URL = "https://www.alphavantage.co/query";
    @Resource
    private SymbolService symbolService;

    @PostConstruct
    public void init() {
        log.info("Init years set to: {}", initYears);
    }

    public void setInitYears(int initYears) {
        this.initYears = initYears;
    }

    public void setAlphaVantageApiKey(String alphaVantageApiKey) {
        this.alphaVantageApiKey = alphaVantageApiKey;
    }

    @Override
    public void initializeHistoricalData() {
        log.info("Starting historical data initialization");
        Calendar from = Calendar.getInstance();
        from.add(Calendar.YEAR, -initYears);
        List<String> symbols = symbolService.getAllSymbols();
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


    @Override
    public void incrementalUpdate() {
        log.info("Starting incremental data update");
        List<String> symbols = symbolService.getAllSymbols();
        for (String symbol : symbols) {
            log.info("Processing symbol: {}", symbol);
            try {
                LocalDate latestDate = stockPriceMapper.findLatestTradeDate(symbol);
                if (latestDate == null) {
                    log.info("No existing data found for symbol: {}, performing full initialization", symbol);
                    initializeHistoricalData();
                    continue;
                }
                log.info("Latest trade date for {}: {}", symbol, latestDate);

                JsonNode timeSeriesData = getTimeSeriesWithRetry(symbol);
                if (timeSeriesData != null) {
                    JsonNode timeSeries = timeSeriesData.get("Time Series (Daily)");
                    if (timeSeries != null) {
                        int updatedCount = 0;
                        for (Map.Entry<String, JsonNode> entry : (Iterable<Map.Entry<String, JsonNode>>) timeSeries::fields) {
                            LocalDate date = LocalDate.parse(entry.getKey(), DateTimeFormatter.ISO_LOCAL_DATE);
                            if (date.isAfter(latestDate)) {
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
                                updatedCount++;

                                // 记录每条插入的数据
                                log.info("Inserted/Updated record - Symbol: {}, Date: {}, Open: {}, High: {}, Low: {}, Close: {}, Volume: {}",
                                        symbol,
                                        date,
                                        stockPrice.getOpenPrice(),
                                        stockPrice.getHighPrice(),
                                        stockPrice.getLowPrice(),
                                        stockPrice.getClosePrice(),
                                        stockPrice.getVolume());
//                                TimeUnit.MILLISECONDS.sleep(100);
                            }
                        }
                        log.info("Updated {} new records for symbol: {}", updatedCount, symbol);
                    } else {
                        log.warn("No time series data found for symbol: {}", symbol);
                    }
                } else {
                    log.error("Failed to retrieve data for symbol: {}", symbol);
                }
                TimeUnit.SECONDS.sleep(30);
            } catch (Exception e) {
                log.error("Error processing symbol {}: {}", symbol, e.getMessage(), e);
            }
        }
        log.info("Incremental data update completed");
    }
} 