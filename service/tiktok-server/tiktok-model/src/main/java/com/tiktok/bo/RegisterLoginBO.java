package com.tiktok.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.NotBlank;

/**
 * 登陆注册中, 对前端数据映射的实体类.
 *
 * @author lzc
 * @version  1.0
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class RegisterLoginBO {

    @NotBlank(message = "手机号不能为空!")
    @Length(min = 11, max = 11, message = "手机号位数为11!")
    private String mobile;

    @NotBlank(message = "验证码不能为空!")
    @Length(min = 6, max = 6, message = "验证码位数为6!")
    private String smsCode;
}