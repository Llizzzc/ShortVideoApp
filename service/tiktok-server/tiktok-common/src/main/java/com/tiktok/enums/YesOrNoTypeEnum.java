package com.tiktok.enums;

/**
 * yes or no枚举类.
 *
 * @author lzc
 * @version 1.0
 */
public enum YesOrNoTypeEnum {
    NO(0, "否"),
    YES(1, "是");

    public final Integer type;
    public final String value;
    YesOrNoTypeEnum(Integer type, String value) {
        this.type = type;
        this.value = value;
    }
}