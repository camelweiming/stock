package com.stock;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;
import com.stock.service.impl.StockServiceImpl;

@SpringBootApplication
@EnableScheduling
@MapperScan("com.stock.mapper")
public class StockAnalysisApplication {
    public static void main(String[] args) {
        SpringApplication.run(StockAnalysisApplication.class, args);
    }
} 