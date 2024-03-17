package com.tiktok.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.*;

/**
 * 视频上传中, 对前端数据的映射类.
 *
 * @author lzc
 * @version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class VlogBO {
    private String id;

    @NotBlank(message = "用户id不能为空!")
    @Length(min = 16, max = 32, message = "用户id长度错误!")
    private String vlogerId;

    @NotBlank(message = "视频路径不能为空!")
    @Length(min = 10, max = 255, message = "视频路径超出长度!")
    private String url;

    @NotBlank(message = "封面路径不能为空!")
    @Length(min = 10, max = 356, message = "封面路径超出长度!")
    private String cover;

    @NotNull(message = "描述不能为空!")
    @Length(min = 5, max = 32, message = "描述超出长度!")
    private String title;

    @NotNull(message = "宽度不能为空!")
    @Max(value = 1500, message = "宽度太大!")
    @Min(value = 100, message = "宽度太小")
    private Integer width;

    @NotNull(message = "长度不能为空!")
    @Max(value = 2000, message = "长度太大!")
    @Min(value = 200, message = "长度太小")
    private Integer height;

    private Integer likeCounts;
    private Integer commentsCounts;
}