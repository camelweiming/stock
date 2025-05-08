package com.stock.test;

import com.stock.utils.HttpClientUtils;
import org.apache.hc.core5.http.ParseException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * @author weiming
 * @date 2025/5/8
 */
public class TestPostNews {
    private static DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static void main(String[] args) throws IOException, ParseException {
        Map<String,String> m = new HashMap<>();
        m.put("date", LocalDateTime.now().format(df));
        m.put("title","BIG NEWS CONFERENCE TOMORROW");
        m.put("content","TRUMP: BIG NEWS CONFERENCE TOMORROW 10AM EST IN OVAL, MAJOR TRADE DEAL WITH A BIG & HIGHLY RESPECTED COUNTRY");
        HttpClientUtils.doPost("http://13.239.254.225/api/postNews",m);
    }
}
