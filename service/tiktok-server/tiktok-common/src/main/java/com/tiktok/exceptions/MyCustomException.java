package com.tiktok.exceptions;

import com.tiktok.grace.result.ResponseStatusEnum;
import lombok.Getter;
import lombok.Setter;

/**
 * 自定义异常.
 *
 * @author lzc
 * @version 1.0
 */
@Getter
@Setter
public class MyCustomException extends RuntimeException {
    private ResponseStatusEnum responseStatusEnum;  // 响应结果
    public MyCustomException(ResponseStatusEnum responseStatusEnum) {
        super("异常状态码为: " + responseStatusEnum.status()
                + "; 具体异常信息为: " + responseStatusEnum.msg());
        this.responseStatusEnum = responseStatusEnum;
    }
}