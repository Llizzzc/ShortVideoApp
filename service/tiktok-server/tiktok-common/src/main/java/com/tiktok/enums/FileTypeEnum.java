package com.tiktok.enums;

/**
 * 文件类型枚举.
 *
 * @author lzc
 * @version 1.0
 */
public enum FileTypeEnum {
    BGIMG(1, "用户背景图"),
    FACE(2, "用户头像"),
    COVER(3, "视频封面");

    public final Integer type;
    public final String value;

    FileTypeEnum(Integer type, String value) {
        this.type = type;
        this.value = value;
    }

    public static boolean checkFileTypeRights(Integer type) {
        return type == BGIMG.type || type == FACE.type || type == COVER.type;
    }
}