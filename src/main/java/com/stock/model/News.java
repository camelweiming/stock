package com.stock.model;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class News {
    private Long id;
    private String title;
    private LocalDateTime time;
    private String content;
    private String source;
    private LocalDate date;
} 