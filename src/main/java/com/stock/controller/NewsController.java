package com.stock.controller;

import com.stock.model.News;
import com.stock.service.NewsService;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author weiming
 * @date 2025/5/5
 */
@RestController
public class NewsController {
    private static final Logger log = LoggerFactory.getLogger(NewsController.class);
    @Resource
    private NewsService newsService;

    @GetMapping("/api/get_news")
    public Map<String, Object> getNews(@RequestParam String startTime, @RequestParam String endTime, @RequestParam(defaultValue = "5") int count, @RequestParam(defaultValue = "day") String mode) {
        java.time.LocalDateTime start = java.time.LocalDateTime.parse(startTime.replace(" ", "T"));
        java.time.LocalDateTime end = java.time.LocalDateTime.parse(endTime.replace(" ", "T")).plusDays(7);
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd");
        log.info("getNews参数：startTime={}, endTime={}, count={}, mode={}", start.format(formatter), end.format(formatter), count, mode);
        List<News> newsList = newsService.findNewsByTimeRange(start, end, count);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 0);
        response.put("message", "success");
        response.put("data", newsList);
        response.put("mode", mode);
        response.put("startTime", startTime);
        response.put("endTime", endTime);
        return response;
    }
}
