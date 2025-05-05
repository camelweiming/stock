package com.stock.mapper;

import com.stock.model.News;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface NewsMapper {
    @Select("SELECT * FROM news WHERE time >= #{startTime} AND time <= #{endTime} ORDER BY time DESC LIMIT #{count}")
    List<News> findNewsByTimeRange(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime, @Param("count") int count);


    @Insert("INSERT INTO news (title, time, content, source, date, link, md5) " +
            "VALUES (#{title}, #{time}, #{content}, #{source}, #{date}, #{link}, #{md5})")
    void insertIgnore(News news);
} 