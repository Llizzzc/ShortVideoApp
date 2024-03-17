package com.tiktok.pojo;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

/**
 * 点赞短视频关联表.
 *
 * @author lzc
 * @version 1.0
 */
@TableName(value ="my_liked_vlog")
@Data
public class MyLikedVlog implements Serializable {

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    private String userId;
    private String vlogId;

    @TableField(value = "created_time", fill = FieldFill.INSERT)
    private Date createdTime;
}