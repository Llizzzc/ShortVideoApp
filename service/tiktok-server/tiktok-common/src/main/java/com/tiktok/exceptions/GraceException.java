package com.tiktok.exceptions;

import com.tiktok.grace.result.ResponseStatusEnum;

/**
 * 优雅的处理异常, 统一封装.
 *
 * @author lzc
 * @version 1.0
 */
public class GraceException {
    public static void display(ResponseStatusEnum responseStatusEnum) {
        throw new MyCustomException(responseStatusEnum);
    }
}