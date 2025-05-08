package com.stock.task;

import com.stock.service.StockDataService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author weiming
 * @date 2025/5/8
 */
@Slf4j
@Component
public class StockIncrementRunner {
    @Resource
    private StockDataService dataService;

    @Scheduled(cron = "0 10 4 * * ?")
    public void doIncrementalUpdate() {
        log.info("BEGIN doIncrementalUpdate" + java.time.LocalDateTime.now());
        dataService.incrementalUpdate();
        // 这里编写你要执行的程序逻辑
        log.info("FINISH doIncrementalUpdate" + java.time.LocalDateTime.now());
    }
}
