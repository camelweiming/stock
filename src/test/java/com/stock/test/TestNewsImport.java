package com.stock.test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Files;
import com.google.common.io.Resources;
import com.stock.StockAnalysisApplication;
import com.stock.model.News;
import com.stock.service.NewsService;
import jakarta.annotation.Resource;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author weiming
 * @date 2025/5/5
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = StockAnalysisApplication.class)
public class TestNewsImport {
    @Resource
    private NewsService newsService;

    @Test
    public void testImport() throws IOException {
        URL resource = Resources.getResource("news.json");
        String content = Resources.toString(resource, StandardCharsets.UTF_8);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(content);
        for (JsonNode articleNode : rootNode) {
            News news = new News();

            // 提取日期
            String date = articleNode.get("date").asText();
            news.setTime(LocalDateTime.parse(date, DateTimeFormatter.ISO_ZONED_DATE_TIME));
            news.setDate(news.getTime().toLocalDate());
            // 提取标题
            String title = articleNode.get("title").asText();
            news.setTitle(title);
            // 提取链接
            String link = articleNode.get("link").asText();
            news.setLink(link);
            news.setContent(articleNode.get("content").asText());
            System.out.println("日期: " + date);
            System.out.println("标题: " + title);
            System.out.println("链接: " + link);
//
//            // 提取 symbols 数组
//            JsonNode symbolsNode = articleNode.get("symbols");
//            System.out.print("股票代码: ");
//            for (JsonNode symbolNode : symbolsNode) {
//                System.out.print(symbolNode.asText() + " ");
//            }
//            System.out.println();

            // 提取 sentiment 对象
            JsonNode sentimentNode = articleNode.get("sentiment");
            double polarity = sentimentNode.get("polarity").asDouble();
            double neg = sentimentNode.get("neg").asDouble();
            double neu = sentimentNode.get("neu").asDouble();
            double pos = sentimentNode.get("pos").asDouble();

            System.out.println("情感极性: " + polarity);
            System.out.println("负面情感比例: " + neg);
            System.out.println("中性情感比例: " + neu);
            System.out.println("正面情感比例: " + pos);
            System.out.println("-------------------");
            newsService.save(news);
        }
//        newsService.findNewsByTimeRange(LocalDateTime.now(), LocalDateTime.now(), 10);
    }
}
