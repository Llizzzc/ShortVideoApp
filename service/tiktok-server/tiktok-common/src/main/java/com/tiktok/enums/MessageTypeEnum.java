package com.tiktok.enums;

/**
 * 消息类型枚举类.
 *
 * @author lzc
 * @version 1.0
 */
public enum MessageTypeEnum {
    FOLLOW_YOU(1, "关注"),
    LIKE_VLOG(2, "点赞视频"),
    COMMENT_VLOG(3, "评论视频"),
    REPLY_YOU(4, "回复评论"),
    LIKE_COMMENT(5, "点赞评论");

    public final Integer type;
    public final String value;

    MessageTypeEnum(Integer type, String value) {
        this.type = type;
        this.value = value;
    }
}