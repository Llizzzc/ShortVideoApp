package com.tiktok.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 返回给前端的粉丝对象.
 *
 * @author lzc
 * @version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class FansVO {
    private String fanId;
    private String nickname;
    private String face;
    private boolean isFollow = false;
    private boolean isFan = false;
    private boolean isMe = false;
}