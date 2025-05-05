package com.stock.controller;

import com.stock.mapper.NewsMapper;
import com.stock.model.News;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class NewsController {
    private static final Logger log = LoggerFactory.getLogger(NewsController.class);
    private final NewsMapper newsMapper;

@GetMapping("/api/get_news")
public Map<String, Object> getNews(@RequestParam String startTime, @RequestParam String endTime, @RequestParam(defaultValue = "5") int count, @RequestParam(defaultValue = "day") String mode) {
    java.time.LocalDateTime start = java.time.LocalDateTime.parse(startTime.replace(" ", "T"));
    java.time.LocalDateTime end = java.time.LocalDateTime.parse(endTime.replace(" ", "T"));
    java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd");
    log.info("getNews参数：startTime={}, endTime={}, count={}, mode={}", start.format(formatter), end.format(formatter), count, mode);
    List<News> newsList = newsMapper.findNewsByTimeRange(start, end, count);
    for (int i = 0; i < count; i++) {
        News n = new News();
        n.setTitle("股票市场重要新闻点点滴滴的弟弟");
        n.setTime(LocalDateTime.now());
        n.setContent("这是第" + (i + 1) + "条新闻的详细内容，包含市场分析和预测。Get sentiment scores for one or more financial instruments (stocks, ETFs, crypto). Sentiment scores are calculated from both news and social media, normalized on a scale from -1 (very negative) to 1 (very positive).\n" +
                "\nGet sentiment scores for one or more financial instruments (stocks, ETFs, crypto). Sentiment scores are calculated from both news and social media, normalized on a scale from -1 (very negative) to 1 (very positive).\n" +
                "\nGet sentiment scores for one or more financial instruments (stocks, ETFs, crypto). Sentiment scores are calculated from both news and social media, normalized on a scale from -1 (very negative) to 1 (very positive).\n" +
                "\n");
        n.setSource("财经新闻");
        newsList.add(n);
    }
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
