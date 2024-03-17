package com.tiktok.enums;

/**
 * 用户信息修改枚举类.
 *
 * @author lzc
 * @version 1.0
 */
public enum UserInfoModifyType {
    NICKNAME(1, "昵称"),
    TIKTOKNUM(2, "视频号"),
    SEX(3, "性别"),
    BIRTHDAY(4, "生日"),
    LOCATION(5, "所在地"),
    DESC(6, "简介"),
    PASSWORD(7, "密码");

    public final Integer type;
    public final String value;

    UserInfoModifyType(Integer type, String value) {
        this.type = type;
        this.value = value;
    }

    public static boolean checkUserInfoTypeIsRight(Integer type) {
        return type.equals(NICKNAME.type) ||
                type.equals(TIKTOKNUM.type) ||
                type.equals(SEX.type) ||
                type.equals(BIRTHDAY.type) ||
                type.equals(LOCATION.type) ||
                type.equals(DESC.type) ||
                type.equals(PASSWORD.type);
    }
}