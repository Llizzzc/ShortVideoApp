package com.tiktok.enums;

/**
 * 视频列表枚举类.
 *
 * @author lzc
 * @version 1.0
 */
public enum ListTypeEnum {

    PUBLIC(0, "public"),
    PRIVATE(1, "private"),
    LIKED(2, "liked");

    public final Integer type;
    public final String name;

    ListTypeEnum(Integer type, String name) {
        this.type = type;
        this.name = name;
    }

    public static boolean checkListType(Integer type) {
        return type.equals(PUBLIC.type) || type.equals(PRIVATE.type) || type.equals(LIKED.type);
    }
}