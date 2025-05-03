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
} 