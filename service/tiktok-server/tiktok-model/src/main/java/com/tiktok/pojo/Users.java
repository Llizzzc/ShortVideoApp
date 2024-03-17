package com.tiktok.pojo;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

/**
 * 用户表.
 *
 * @author lzc
 * @version 1.0
 */
@TableName(value = "users")
@Data
public class Users implements Serializable {
    // 自动生成32位数的id
    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    private String mobile;
    private String nickname;
    private String tiktokNum;
    private String face;
    private Integer sex;
    private Date birthday;
    private String country;
    private String province;
    private String city;
    private String district;
    private String description;
    private String bgImg;
    private Integer canTiktokNumBeUpdated;
    private String password;

    // 插入时自动更新时间
    @TableField(value = "created_time", fill = FieldFill.INSERT)
    private Date createdTime;

    // 插入修改时自动更新时间
    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    private Date updatedTime;

}