package com.tiktok.exceptions;

import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tiktok.grace.result.GraceJSONResult;
import com.tiktok.grace.result.ResponseStatusEnum;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 对controller进行统一异常拦截处理, 可以针对异常的类型进行捕获, 然后返回json信息到前端.
 *
 * @author lzc
 * @version 1.0
 */
@ControllerAdvice
public class GraceExceptionHandler {

    @ExceptionHandler(MyCustomException.class)
    @ResponseBody
    public GraceJSONResult returnMyException(MyCustomException e) {
        e.printStackTrace();
        return GraceJSONResult.errorCustom(e.getResponseStatusEnum());
    }

    @ExceptionHandler(TencentCloudSDKException.class)
    @ResponseBody
    public GraceJSONResult returnTencentCloudSDK(TencentCloudSDKException e) {
        e.printStackTrace();
        return GraceJSONResult.errorCustom(ResponseStatusEnum.SMS_SENT_FAILED_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public GraceJSONResult returnMethodArgumentNotValid(MethodArgumentNotValidException e) {
        e.printStackTrace();
        BindingResult result = e.getBindingResult();
        Map<String, String> map = getErrors(result);
        return GraceJSONResult.errorMap(map);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseBody
    public GraceJSONResult returnMaxUploadSize(MaxUploadSizeExceededException e) {
        e.printStackTrace();
        return GraceJSONResult.errorCustom(ResponseStatusEnum.FILE_MAX_SIZE_2MB_ERROR);
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public GraceJSONResult returnException(Exception e) {
        e.printStackTrace();
        return GraceJSONResult.errorCustom(ResponseStatusEnum.FAILED);
    }

    /**
     * 返回错误集.
     *
     * @param result 结果信息
     * @return 错误集
     */
    public Map<String, String> getErrors(BindingResult result) {
        Map<String, String> map = new HashMap<>();
        List<FieldError> errorList = result.getFieldErrors();
        for (FieldError ff : errorList) {
            // 错误所对应的属性字段名
            String field = ff.getField();
            // 错误的信息
            String msg = ff.getDefaultMessage();
            map.put(field, msg);
        }
        return map;
    }
}