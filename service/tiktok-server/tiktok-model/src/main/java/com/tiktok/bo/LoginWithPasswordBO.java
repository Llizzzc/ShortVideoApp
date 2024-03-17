package com.tiktok.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.NotBlank;

/**
 * 密码登陆中, 对前端数据映射的实体类.
 *
 * @author lzc
 * @version  1.0
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class LoginWithPasswordBO {

    @NotBlank(message = "账号不能为空!")
    @Length(min = 5, max = 16, message = "账号格式不正确!")
    private String tiktokNum;

    @NotBlank(message = "密码不能为空!")
    @Length(min = 5, max = 16, message = "密码格式不正确!")
    private String password;
}