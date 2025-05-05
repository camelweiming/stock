package com.stock.test;

import com.stock.model.StockPrice;

import java.text.DateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.IsoFields;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

/**
 * @author weiming
 * @date 2025/5/5
 */
public class Test {
    public static void main(String[] args) {
        LocalDateTime startDateTimeOfMonth = YearMonth.parse("2023-01").atDay(1).atStartOfDay();
        LocalDateTime endDateTimeOfMonth = YearMonth.parse("2023-01").atEndOfMonth().atTime(LocalTime.MAX);

        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM");
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//        LocalDate start = LocalDate.parse("2023-01", dateFormat);
        //.withDayOfMonth(1).with(LocalTime.MIN)
//        LocalDate end = LocalDate.parse("2023-01", dateFormat).with(TemporalAdjusters.lastDayOfMonth()).with(LocalTime.MIN);
        System.out.println(startDateTimeOfMonth.format(df));

        System.out.println(endDateTimeOfMonth.format(df));
//        System.out.println(end.format(df));
        // 创建一个 LocalDate 对象
        LocalDate date = LocalDate.of(2025, 1, 5);

        // 获取一年中的第几周
        int weekOfYear = date.get(ChronoField.MONTH_OF_YEAR);

        // 输出结果
        System.out.println(date.get(ChronoField.YEAR) + "_" + weekOfYear);
    }




}
