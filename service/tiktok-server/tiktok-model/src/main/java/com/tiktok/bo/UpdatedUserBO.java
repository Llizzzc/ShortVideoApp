package com.tiktok.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.util.Date;

/**
 * 更新用户中, 对前端数据映射的实体类.
 *
 * @author lzc
 * @version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UpdatedUserBO {
    @NotBlank(message = "用户id不能为空!")
    @Length(min = 16, max = 32, message = "用户id长度不正确!")
    private String id;

    @Length(min = 5, max = 16, message = "昵称长度错误!")
    private String nickname;

    @Length(min = 5, max = 16, message = "视频号长度错误!")
    private String tiktokNum;

    @Length(min = 10, max = 128, message = "头像路径长度超出限制!")
    private String face;

    @Min(value = 0, message = "性别取值错误!")
    @Max(value = 2, message = "性别取值错误!")
    private Integer sex;

    @Length(min = 11, max = 11, message = "手机号长度错误!")
    private String mobile;
    private Date birthday;

    @Length(min = 2, max = 16, message = "国家长度超出限制!")
    private String country;

    @Length(min = 2, max = 16, message = "省份长度超出限制!")
    private String province;

    @Length(min = 2, max = 16, message = "市长度超出限制!")
    private String city;

    @Length(min = 2, max = 16, message = "区县长度超出限制!")
    private String district;

    @Length(max = 16, message = "简介长度超出限制!")
    private String description;

    @Length(min = 10, max = 128, message = "背景路径长度超出限制!")
    private String bgImg;

    @Min(value = 0, message = "是否允许更新视频号取值错误!")
    @Max(value = 1, message = "是否允许更新视频号取值错误!")
    private Integer canTiktokNumBeUpdated;

    @Length(min = 5, max = 16, message = "密码长度错误!")
    private String password;

    @Length(min = 5, max = 16, message = "密码长度错误!")
    private String newPassword;

    @Length(min = 16, max = 64, message = "盐长度错误!")
    private String salt;
}