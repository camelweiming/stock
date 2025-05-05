package com.stock.service.impl;

import com.stock.mapper.NewsMapper;
import com.stock.model.News;
import com.stock.service.NewsService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author weiming
 * @date 2025/5/5
 */
@Service
public class NewsServiceImpl implements NewsService {
    @Resource
    private NewsMapper newsMapper;

    @Override
    public List<News> findNewsByTimeRange(LocalDateTime startTime, LocalDateTime endTime, int count) {
        return newsMapper.findNewsByTimeRange(startTime, endTime, count);
    }
}
