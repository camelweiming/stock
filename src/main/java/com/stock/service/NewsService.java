package com.stock.service;

import com.stock.model.News;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author weiming
 * @date 2025/5/5
 */
public interface NewsService {
    List<News> findNewsByTimeRange(LocalDateTime startTime, LocalDateTime endTime, int count);
}
