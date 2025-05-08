package com.stock.model;

import lombok.Data;

/**
 * @author weiming
 * @date 2025/5/8
 */
@Data
public class Result {
    public static final int CODE_SUCCESS = 200;
    public static final int CODE_SYS_ERROR = 500;
    public int code;
    public String msg;

    public Result() {
    }

    public Result(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
