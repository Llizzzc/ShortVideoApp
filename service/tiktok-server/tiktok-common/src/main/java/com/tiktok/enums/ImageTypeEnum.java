package com.tiktok.enums;

/**
 * 图片后缀枚举类.
 *
 * @author lzc
 * @version 1.0
 */
public enum ImageTypeEnum {

    JPEG(1, "jpeg"),
    JPG(2, "jpg"),
    PNG(3, "png"),
    HEIC(4, "heic");

    public final Integer type;
    public final String name;

    ImageTypeEnum(Integer type, String name) {
        this.type = type;
        this.name = name;
    }

    public static boolean checkImageType(String name) {
        return name.equalsIgnoreCase(JPEG.name) || name.equalsIgnoreCase(JPG.name) || name.equalsIgnoreCase(PNG.name)
                || name.equalsIgnoreCase(HEIC.name);
    }
}