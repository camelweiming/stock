package com.stock.service.impl;

import com.google.common.hash.Hashing;
import com.stock.mapper.NewsMapper;
import com.stock.model.News;
import com.stock.service.NewsService;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.nio.charset.StandardCharsets;
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

    @Override
    public void save(News news) {
        if (StringUtils.isBlank(news.getTitle()) || StringUtils.isBlank(news.getContent())) {
            throw new RuntimeException("Empty title or content");
        }
        if (news.getLink() != null && news.getLink().length() > 100) {
            news.setLink(StringUtils.substring(news.getLink(), 100));
        }
        if (news.getTitle() != null && news.getTitle().length() > 200) {
            news.setTitle(StringUtils.substring(news.getTitle(), 200));
        }
        if (news.getSource() != null && news.getSource().length() > 50) {
            news.setSource(StringUtils.substring(news.getSource(), 50));
        }
        String md5Hash = Hashing.md5().hashString(news.getTitle(), StandardCharsets.UTF_8).toString();
        news.setMd5(md5Hash);
        newsMapper.insertIgnore(news);
    }
}
