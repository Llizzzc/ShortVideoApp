package com.tiktok.pojo;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

/**
 * 粉丝表.
 *
 * @author lzc
 * @version 1.0
 */
@TableName(value = "fans")
@Data
public class Fans implements Serializable {

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    private String vlogerId;
    private String fanId;
    private Integer isFanFriendOfMine;

    @TableField(value = "created_time", fill = FieldFill.INSERT)
    private Date createdTime;
}