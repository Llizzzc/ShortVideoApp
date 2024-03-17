package com.tiktok.grace.result;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;

/**
 * 自定义响应数据类型.
 *
 * @author lzc
 * @version V1.0
 */
@Data
@NoArgsConstructor
public class GraceJSONResult {

    // 响应业务状态码
    private Integer status;
    // 响应消息
    private String msg;
    // 是否成功
    private Boolean success;
    // 响应数据
    private Object data;

    public GraceJSONResult(ResponseStatusEnum responseStatusEnum, Object data) {
        this.status = responseStatusEnum.status();
        this.msg = responseStatusEnum.msg();
        this.success = responseStatusEnum.success();
        this.data = data;
    }

    public GraceJSONResult(ResponseStatusEnum responseStatusEnum) {
        this(responseStatusEnum, null);
    }

    /**
     * 成功返回, 带有数据的静态方法.
     *
     * @param data 需要返回的数据
     * @return 包含data的GraceJSONResult对象
     */
    public static GraceJSONResult ok(Object data) {
        return new GraceJSONResult(ResponseStatusEnum.SUCCESS, data);
    }

    /**
     * 成功返回, 不带有数据的静态方法.
     *
     * @return 不包含data的GraceJSONResult对象
     */
    public static GraceJSONResult ok() {
        return new GraceJSONResult(ResponseStatusEnum.SUCCESS);
    }

    /**
     * 成功返回, 需要自己传入响应类型.
     *
     * @param responseStatusEnum 响应结果枚举对象
     * @return GraceJSONResult对象
     */
    public static GraceJSONResult okCustom(ResponseStatusEnum responseStatusEnum) {
        return new GraceJSONResult(responseStatusEnum);
    }

    /**
     * 成功返回, 需要自己传入响应类型以及数据.
     *
     * @param responseStatusEnum 响应结果枚举对象
     * @return GraceJSONResult对象
     */
    public static GraceJSONResult okCustom(ResponseStatusEnum responseStatusEnum, Object data) {
        return new GraceJSONResult(responseStatusEnum, data);
    }

    /**
     * 错误返回.
     *
     * @return GraceJSONResult对象
     */
    public static GraceJSONResult error() {
        return new GraceJSONResult(ResponseStatusEnum.FAILED);
    }

    /**
     * 错误返回, 需要自己传入响应类型.
     *
     * @param responseStatusEnum 响应结果枚举对象
     * @return GraceJSONResult对象
     */
    public static GraceJSONResult errorCustom(ResponseStatusEnum responseStatusEnum) {
        return new GraceJSONResult(responseStatusEnum);
    }

    /**
     * 错误返回, map中包含了多条错误信息, 可以用于表单验证, 把错误统一的全部返回出去.
     *
     * @param map 映射对象
     * @return GraceJSONResult对象
     */
    public static GraceJSONResult errorMap(Map map) {
        return new GraceJSONResult(ResponseStatusEnum.FAILED, map);
    }
}