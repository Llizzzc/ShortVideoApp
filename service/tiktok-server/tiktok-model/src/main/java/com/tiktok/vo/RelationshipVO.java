package com.tiktok.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 返回给前端的关系对象.
 *
 * @author lzc
 * @version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RelationshipVO {
    private boolean follow = false;
    private boolean fan = false;
    private boolean isMe = false;
}