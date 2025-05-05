CREATE DATABASE IF NOT EXISTS stock_analysis;
USE stock_analysis;

CREATE TABLE IF NOT EXISTS stock_price (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    symbol VARCHAR(10) NOT NULL,
    trade_date DATE NOT NULL,
    open_price DECIMAL(10, 2),
    high_price DECIMAL(10, 2),
    low_price DECIMAL(10, 2),
    close_price DECIMAL(10, 2),
    volume BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_symbol_date (symbol, trade_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `news` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `title` varchar(255) DEFAULT NULL,
    `link` varchar(128) DEFAULT NULL,
    `time` datetime DEFAULT NULL,
    `content` text,
    `source` varchar(100) DEFAULT NULL,
    `date` date DEFAULT NULL,
    `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
    `md5` varchar(64) DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY uk_md5 (md5),
    KEY(date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;