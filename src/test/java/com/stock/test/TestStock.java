package com.stock.test;

import com.stock.model.StockPrice;
import com.stock.service.impl.StockServiceImpl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author weiming
 * @date 2025/5/5
 */
public class TestStock {
    public static void main(String[] args) {
        List<StockPrice> list = new ArrayList<>();
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        StockPrice p1 = new StockPrice();
        p1.setHighPrice(BigDecimal.valueOf(100));
        p1.setLowPrice(BigDecimal.valueOf(10));
        p1.setCreatedAt(LocalDateTime.now());
        p1.setOpenPrice(BigDecimal.valueOf(0));
        p1.setClosePrice(BigDecimal.valueOf(0));
        p1.setTradeDate(LocalDate.parse("2025-05-05", df));
        list.add(p1);


        StockPrice p2 = new StockPrice();
        p2.setHighPrice(BigDecimal.valueOf(600));
        p2.setLowPrice(BigDecimal.valueOf(40));
        p2.setCreatedAt(LocalDateTime.now());
        p2.setOpenPrice(BigDecimal.valueOf(0));
        p2.setClosePrice(BigDecimal.valueOf(0));
        p2.setTradeDate(LocalDate.parse("2025-05-06", df));
        list.add(p2);

        StockPrice p3 = new StockPrice();
        p3.setHighPrice(BigDecimal.valueOf(300));
        p3.setLowPrice(BigDecimal.valueOf(30));
        p3.setCreatedAt(LocalDateTime.now());
        p3.setOpenPrice(BigDecimal.valueOf(0));
        p3.setClosePrice(BigDecimal.valueOf(0));
        p3.setTradeDate(LocalDate.parse("2025-05-07", df));
        list.add(p3);

        StockPrice p4 = new StockPrice();
        p4.setHighPrice(BigDecimal.valueOf(800));
        p4.setLowPrice(BigDecimal.valueOf(3));
        p4.setCreatedAt(LocalDateTime.now());
        p4.setOpenPrice(BigDecimal.valueOf(0));
        p4.setClosePrice(BigDecimal.valueOf(0));
        p4.setTradeDate(LocalDate.parse("2025-05-12", df));
        list.add(p4);
        List<StockPrice> ws = StockServiceImpl.buildDayData(list, new StockServiceImpl.WeekKeyBuilder());
        ws.forEach(stockPrice -> {
            System.out.println("date:" + stockPrice.getTradeDate().format(df) + " high:" + stockPrice.getHighPrice() + " low:" + stockPrice.getLowPrice());
        });
    }


}
