package com.tiktok.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.NotBlank;

/**
 * 评论的时候, 对前端数据映射的实体类.
 *
 * @author lzc
 * @version  1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CommentBO {

    @NotBlank(message = "博主id不能为空!")
    @Length(min = 16, max = 32, message = "用户id长度错误!")
    private String vlogerId;

    @NotBlank(message = "父评论id不能为空!")
    @Length(max = 32, message = "评论id长度错误!")
    private String fatherCommentId;

    @NotBlank(message = "视频id不能为空!")
    @Length(min = 16, max = 32, message = "视频id长度错误!")
    private String vlogId;

    @NotBlank(message = "评论用户id不能为空!")
    @Length(min = 16, max = 32, message = "用户id长度错误!")
    private String commentUserId;

    @NotBlank(message = "评论内容不能为空!")
    @Length(max = 64, message = "评论内容过长!")
    private String content;
}