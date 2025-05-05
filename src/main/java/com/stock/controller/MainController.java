package com.stock.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author weiming
 * @date 2025/5/5
 */
@RestController
@RequiredArgsConstructor
public class MainController {
    @GetMapping("/")
    public String index() {
        return "index";
    }

}
