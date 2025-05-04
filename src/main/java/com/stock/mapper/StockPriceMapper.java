package com.stock.mapper;

import com.stock.model.StockPrice;
import org.apache.ibatis.annotations.*;
import java.time.LocalDate;
import java.util.List;

@Mapper
public interface StockPriceMapper {
    
    @Insert("INSERT INTO stock_price (symbol, trade_date, open_price, high_price, low_price, close_price, volume) " +
            "VALUES (#{symbol}, #{tradeDate}, #{openPrice}, #{highPrice}, #{lowPrice}, #{closePrice}, #{volume}) " +
            "ON DUPLICATE KEY UPDATE " +
            "open_price = #{openPrice}, high_price = #{highPrice}, low_price = #{lowPrice}, " +
            "close_price = #{closePrice}, volume = #{volume}")
    void insertOrUpdate(StockPrice stockPrice);

    @Select("SELECT * FROM stock_price WHERE symbol = #{symbol} AND trade_date >= #{startDate} " +
            "ORDER BY trade_date")
    List<StockPrice> findBySymbolAndDateRange(@Param("symbol") String symbol, 
                                             @Param("startDate") LocalDate startDate);

    @Select("SELECT DISTINCT symbol FROM stock_price")
    List<String> findAllSymbols();

    @Select("SELECT MAX(trade_date) FROM stock_price WHERE symbol = #{symbol}")
    LocalDate findLatestTradeDate(@Param("symbol") String symbol);

    @Select("SELECT * FROM stock_price WHERE symbol = #{symbol} AND trade_date >= #{startDate} " +
            "AND DAYOFWEEK(trade_date) = 6 " + // 周五
            "ORDER BY trade_date")
    List<StockPrice> findWeeklyData(@Param("symbol") String symbol, 
                                   @Param("startDate") LocalDate startDate);

    @Select("SELECT * FROM stock_price WHERE symbol = #{symbol} AND trade_date >= #{startDate} " +
            "AND DAY(trade_date) = DAY(LAST_DAY(trade_date)) " + // 每月最后一天
            "ORDER BY trade_date")
    List<StockPrice> findMonthlyData(@Param("symbol") String symbol, 
                                    @Param("startDate") LocalDate startDate);
} 