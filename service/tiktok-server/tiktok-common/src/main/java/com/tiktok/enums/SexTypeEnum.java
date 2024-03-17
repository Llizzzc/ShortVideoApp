package com.tiktok.enums;

/**
 * 性别枚举类.
 *
 * @author lzc
 * @version 1.0
 */
public enum SexTypeEnum {
    woman(0, "女"),
    man(1, "男"),
    secret(2, "保密");

    public final Integer type;
    public final String value;

    SexTypeEnum(Integer type, String value) {
        this.type = type;
        this.value = value;
    }
}