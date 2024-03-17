package com.tiktok.grace.result;

/**
 * 响应结果枚举, 用于提供给GraceJSONResult, 向前端返回结果.
 *
 * @author lzc
 * @version 1.0
 */
public enum ResponseStatusEnum {

    // 20x
    SUCCESS(200, true, "操作成功!"),

    // 21x
    USER_LOGIN_SUCCESS(210,true,"登陆成功!"),
    USER_LOGOUT_SUCCESS(211,true,"退出成功!"),
    USER_UPDATE_INFO_SUCCESS(212,true,"修改信息成功!"),
    USER_FOLLOW_SUCCESS(213,true,"关注成功!"),
    USER_CANCEL_FOLLOW_SUCCESS(214,true,"取关成功!"),
    USER_GET_FOLLOWS_SUCCESS(215,true,"获取关注列表成功!"),
    USER_GET_FANS_SUCCESS(216, true,"获取粉丝列表成功!"),
    USER_LIKE_VLOG_SUCCESS(217,true,"点赞视频成功!"),
    USER_UNLIKE_VLOG_SUCCESS(218,true,"取消点赞视频成功!"),
    USER_GET_FRIEND_VLOG_LIST_SUCCESS(219,true,"获取朋友视频列表成功!"),
    USER_INFO_GET_SUCCESS(2190,true, "用户信息获取成功!"),
    USER_GET_FOLLOW_LIST_SUCCESS(2191,true, "获取关注视频列表成功!"),
    USER_RELATIONSHIP_QUERY_SUCCESS(2192, true, "查询关系成功!"),
    USER_LIKE_COMMENT_SUCCESS(2193, true, "点赞评论成功!"),
    USER_UNLIKE_COMMENT_SUCCESS(2194, true, "取消点赞评论成功!"),

    // 22x
    VLOG_PUBLISH_SUCCESS(220,true,"视频发布成功!"),
    VLOG_LIST_GET_SUCCESS(221,true,"获取视频列表成功!"),
    VLOG_GET_SUCCESS(222,true,"获取该视频成功!"),
    VLOG_GET_TOTAL_LIKE_SUCCESS(223,true,"获取视频点赞总数成功!"),
    VLOG_INFO_UPDATE_SUCCESS(224,true,"视频信息更新成功!"),

    // 23x
    SMS_SENT_SUCCESS(230,true,"短信发送成功!"),

    // 24x
    COMMENT_GET_TOTAL_LIKE_SUCCESS(240, true,"获取评论总数成功!"),
    COMMENT_PUBLISH_SUCCESS(241,true,"发布评论成功!"),
    COMMENT_DELETE_SUCCESS(242, true, "删除评论成功!"),
    COMMENT_LIST_GET_SUCCESS(243, true,"获取评论列表成功!"),

    // 50x
    FAILED(500, false, "操作失败!"),

    // 51x
    USER_UPDATE_INFO_ERROR(510,false,"信息修改失败!"),
    USER_INFO_TYPE_ERROR(511,false,"修改信息参数错误!"),
    USER_INFO_UPDATED_NICKNAME_EXIST_ERROR(512,false,"昵称已经存在!"),
    USER_INFO_UPDATED_TIKTOKNUM_EXIST_ERROR(513,false,"视频号已经存在!"),
    USER_INFO_CANT_UPDATED_TIKTOKNUM_ERROR(514,false,"超出修改次数!"),
    USER_NOT_REGISTER_ERROR(515,false,"用户未注册!"),
    USER_PASSWORD_ERROR(516,false,"密码错误!"),
    USER_NEWPASSWORD_SAME_ERROR(517,false,"新旧密码相同!"),
    USER_UN_LOGIN_ERROR(518,false,"未登陆!"),
    USER_CAN_NOT_FOLLOW_SELF_ERROR(519, false,"不能关注自己!"),
    USER_CAN_NOT_CANCEL_FOLLOW_SELF_ERROR(5190, false,"不能取关自己!"),
    USER_ALREADY_LIKE_THIS_VLOG_ERROR(5191,false,"已经点赞过啦!"),
    USER_ID_EMPTY_ERROR(5192,false,"用户id为空!"),
    USER_CAN_NOT_QUERY_SELF_TO_SELF_RELATIONSHIP_ERROR(5193, false, "不能查询自己和自己的关系!"),

    // 52x
    VLOG_NOT_EXIST_ERROR(520,false,"视频不存在!"),
    VLOG_ID_EMPTY_ERROR(521,false,"视频id为空!"),
    VLOG_TYPE_ERROR(522,false,"视频类型参数错误!"),

    // 53x
    TOKEN_INVALID_ERROR(530,false,"Token异常!"),
    MOBILE_EMPTY_ERROR(531, false, "输入的号码为空!"),
    MOBILE_FORMAT_ERROR(532, false, "您输入的号码格式有误!"),
    SMS_NEED_WAIT_ERROR(533,false,"短信发送太频繁!"),
    SMS_CODE_ERROR(534,false,"验证码过期或不匹配!"),
    SMS_SENT_FAILED_ERROR(535,false,"短信发送失败!"),

    // 54x
    FILE_UPLOAD_NULL_ERROR(540,false,"文件为空!"),
    FILE_TYPE_ERROR(541,false,"文件参数错误!"),
    FILE_FORMAT_ERROR(542,false,"文件图片格式不支持!"),
    FILE_MAX_SIZE_2MB_ERROR(543,false,"仅支持2MB大小以下的图片上传!"),

    // 55x
    COMMENT_ID_EMPTY_ERROR(550, false, "评论id为空!"),
    COMMENT_NOT_EXIST_ERROR(551, false, "评论不存在!");

    // 响应业务状态
    private Integer status;
    // 调用是否成功
    private Boolean success;
    // 响应消息
    private String msg;

    ResponseStatusEnum(Integer status, Boolean success, String msg) {
        this.status = status;
        this.success = success;
        this.msg = msg;
    }
    public Integer status() {
        return status;
    }
    public Boolean success() {
        return success;
    }
    public String msg() {
        return msg;
    }
}