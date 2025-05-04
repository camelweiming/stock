package com.stock.test;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * @author weiming
 * @date 2025/5/4
 */
public class TestGetNews {
    public static String getNewsData(String apiKey, String fromDate, String toDate) throws IOException {
        StringBuilder result = new StringBuilder();
        String baseUrl = "https://eodhd.com/api/sentiments";
        Map<String, String> params = new HashMap<>();
        params.put("s", "spy.us");
        params.put("api_token", apiKey);
        params.put("from", fromDate);
        params.put("to", toDate);
        params.put("fmt", "json");

        StringBuilder queryString = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (queryString.length() > 0) {
                queryString.append("&");
            }
            queryString.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue()));
        }

        URL url = new URL(baseUrl + "?" + queryString.toString());
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            reader.close();
        } else {
            System.out.println("请求失败，状态码：" + responseCode);
        }
        connection.disconnect();

        return result.toString();
    }

    /**
     * date 日期	string (YYYY-MM-DD) 字符串 （YYYY-MM-DD）	The date of sentiment aggregation 情绪聚合的日期
     * count 计数	integer 整数	Number of articles used for sentiment on that day
     * 当天用于情绪的文章数
     * normalized 规范化	float 浮	Sentiment score between -1 (very negative) and 1 (very positive)
     * 情绪得分介于 -1（非常负面）和 1（非常积极）之间
     *
     *
     * "BTC-USD.CC": [
     *     {
     *       "date": "2022-04-22",
     *       "count": 31,
     *       "normalized": 0.1835
     *     }
     * ]
     *
     * @param args
     * @throws JSONException
     * @throws IOException
     */
    public static void main(String[] args) throws JSONException, IOException {
        // 替换为你的实际 API 密钥
        String apiKey = " 6817681fa9d630.78396422";
        // 指定时间，例如 2025-05-04，获取该时间前一周的新闻
        String toDate = "2025-05-04";
        String fromDate = "2025-04-27";

        String newsJson = getNewsData(apiKey, fromDate, toDate);
        if (!newsJson.isEmpty()) {
            JSONObject root = new JSONObject(newsJson);
            JSONArray jsonArray = root.getJSONArray("SPY.US");
            System.out.println(jsonArray);
//            JSONArray newsArray = new JSONArray(newsJson);
//            for (int i = 0; i < newsArray.length(); i++) {
//                JSONObject newsItem = newsArray.getJSONObject(i);
//                System.out.println("标题：" + newsItem.getString("title"));
//                System.out.println("内容：" + newsItem.getString("content"));
//                System.out.println("发布日期：" + newsItem.getString("date"));
//                System.out.println("相关股票代码：" + newsItem.getString("symbols"));
//                System.out.println("-" + "重复 50 次");
//            }
        }
    }
}
