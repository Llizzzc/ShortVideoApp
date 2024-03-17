package com.tiktok.pojo;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

/**
 * 评论表.
 *
 * @author lzc
 * @version 1.0
 */
@TableName(value ="comment")
@Data
public class Comment implements Serializable {

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    private String vlogerId;
    private String fatherCommentId;
    private String vlogId;
    private String commentUserId;
    private String content;
    private Integer likeCounts;

    @TableField(value = "created_time", fill = FieldFill.INSERT)
    private Date createTime;
}