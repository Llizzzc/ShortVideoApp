package com.tiktok.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.util.Date;

/**
 * 返回给前端的评论对象.
 *
 * @author lzc
 * @version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CommentVO {
    private String id;
    private String commentId;
    private String vlogerId;
    private String fatherCommentId;
    private String vlogId;
    private String commentUserId;
    private String commentUserNickname;
    private String commentUserFace;
    private String content;
    private Integer likeCounts;
    private String replyedUserNickname;
    private Date createTime;
    private Integer isLike = 0;
}