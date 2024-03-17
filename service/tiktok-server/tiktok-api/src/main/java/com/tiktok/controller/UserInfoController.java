package com.tiktok.controller;

import com.tiktok.configration.MinIOConfig;
import com.tiktok.base.BasePropertiesInfo;
import com.tiktok.bo.UpdatedUserBO;
import com.tiktok.enums.FileTypeEnum;
import com.tiktok.enums.ImageTypeEnum;
import com.tiktok.enums.UserInfoModifyType;
import com.tiktok.grace.result.GraceJSONResult;
import com.tiktok.grace.result.ResponseStatusEnum;
import com.tiktok.pojo.Users;
import com.tiktok.service.UsersService;
import com.tiktok.utils.MinIOUtils;
import com.tiktok.vo.UsersVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * 用户信息控制器.
 * 包括查询, 修改功能.
 *
 * @author lzc
 * @version 1.0
 */
@Slf4j
@Tag(name = "用户信息接口模块")
@RequestMapping("userInfo")
@RestController
public class UserInfoController extends BasePropertiesInfo {

    @Resource
    private UsersService usersService;
    @Resource
    private MinIOConfig minIOConfig;

    @GetMapping("query")
    @Operation(description = "query - 查询用户信息路由")
    @Parameters(value = {
            @Parameter(name = "userId", required = true, description = "用户id")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "2190", description = "获取用户信息成功"),
            @ApiResponse(responseCode = "5192", description = "用户id为空"),
            @ApiResponse(responseCode = "515", description = "用户未注册")})
    public GraceJSONResult query(String userId) {

        if (StringUtils.isBlank(userId)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.USER_ID_EMPTY_ERROR);
        }

        Users user = usersService.getUser(userId);
        if (user == null) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.USER_NOT_REGISTER_ERROR);
        }

        UsersVO usersVO = new UsersVO();
        BeanUtils.copyProperties(user, usersVO);

        // 我的关注总数
        String myFollowsCountsStr = redisOperator.get(REDIS_MY_FOLLOWS_COUNTS + ":" + userId);
        // 我的粉丝总数
        String myFansCountsStr = redisOperator.get(REDIS_MY_FANS_COUNTS + ":" + userId);
        // 点赞总和
        String likedVlogerCountsStr = redisOperator.get(REDIS_VLOGER_BE_LIKED_COUNTS + ":" + userId);


        Integer myFollowsCounts = 0;
        Integer myFansCounts = 0;
        Integer likedVlogerCounts = 0;

        if (StringUtils.isNotBlank(myFollowsCountsStr)) {
            myFollowsCounts = Integer.valueOf(myFollowsCountsStr);
        }
        if (StringUtils.isNotBlank(myFansCountsStr)) {
            myFansCounts = Integer.valueOf(myFansCountsStr);
        }
        if (StringUtils.isNotBlank(likedVlogerCountsStr)) {
            likedVlogerCounts = Integer.valueOf(likedVlogerCountsStr);
        }

        usersVO.setMyFollowsCounts(myFollowsCounts);
        usersVO.setMyFansCounts(myFansCounts);
        usersVO.setTotalLikeMeCounts(likedVlogerCounts);
        return GraceJSONResult.okCustom(ResponseStatusEnum.USER_INFO_GET_SUCCESS, usersVO);
    }

    @PostMapping("modifyUserInfo")
    @Operation(description = "modifyUserInfo - 修改用户信息路由")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "212", description = "修改用户信息成功"),
            @ApiResponse(responseCode = "511", description = "类型参数错误"),
            @ApiResponse(responseCode = "512", description = "昵称已经存在"),
            @ApiResponse(responseCode = "513", description = "视频号已经存在"),
            @ApiResponse(responseCode = "514", description = "视频号超出修改次数"),
            @ApiResponse(responseCode = "515", description = "用户未注册"),
            @ApiResponse(responseCode = "516", description = "密码错误"),
            @ApiResponse(responseCode = "517", description = "新旧密码相同"),
            @ApiResponse(responseCode = "510", description = "信息修改失败"),
            @ApiResponse(responseCode = "518", description = "请登陆"),
            @ApiResponse(responseCode = "530", description = "token异常")})
    public GraceJSONResult modifyUserInfo(@Valid @RequestBody UpdatedUserBO updatedUserBO, Integer type) {

        if (type == null || !UserInfoModifyType.checkUserInfoTypeIsRight(type)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.USER_INFO_TYPE_ERROR);
        }

        Users user = usersService.getUser(updatedUserBO.getId());
        if (user == null) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.USER_NOT_REGISTER_ERROR);
        }

        Users newUser = usersService.updateUserInfo(updatedUserBO, type);
        UsersVO usersVO = new UsersVO();
        BeanUtils.copyProperties(newUser, usersVO);
        return GraceJSONResult.okCustom(ResponseStatusEnum.USER_UPDATE_INFO_SUCCESS, usersVO);
    }

    @PostMapping("modifyImage")
    @Operation(description = "modifyImage - 修改用户头像或背景路由")
    @Parameters(value = {
            @Parameter(name = "userId", required = true, description = "用户id"),
            @Parameter(name = "type", required = true, description = "修改图片类型"),
            @Parameter(name = "file", required = true, description = "图片文件")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "212", description = "修改用户信息成功"),
            @ApiResponse(responseCode = "5192", description = "用户id为空"),
            @ApiResponse(responseCode = "540", description = "文件为空"),
            @ApiResponse(responseCode = "541", description = "文件类型参数错误"),
            @ApiResponse(responseCode = "542", description = "图片格式不支持"),
            @ApiResponse(responseCode = "543", description = "图片大小超过限制"),
            @ApiResponse(responseCode = "518", description = "请登陆"),
            @ApiResponse(responseCode = "515", description = "用户未注册"),
            @ApiResponse(responseCode = "530", description = "token异常")})
    public GraceJSONResult modifyImage(String userId, Integer type, MultipartFile file) throws Exception {

        if (StringUtils.isBlank(userId)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.USER_ID_EMPTY_ERROR);
        }

        Users thatUser = usersService.getUser(userId);
        if (thatUser == null) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.USER_NOT_REGISTER_ERROR);
        }

        // 检查文件类型
        if (type == null || !FileTypeEnum.checkFileTypeRights(type)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.FILE_TYPE_ERROR);
        }

        // 检查文件是否为空
        if (file == null || file.isEmpty()) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.FILE_UPLOAD_NULL_ERROR);
        }

        // 检查文件后缀
        String fileName = file.getOriginalFilename();
        String postArr = "";
        for (int i = 0; i < fileName.length(); i ++) {
            if (fileName.charAt(i) == '.') {
                postArr = fileName.substring(i + 1);
            }
        }
        if (!ImageTypeEnum.checkImageType(postArr)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.FILE_FORMAT_ERROR);
        }

        // 图片上传
        MinIOUtils.uploadFile(minIOConfig.getBucketName(), fileName, file.getInputStream());

        // 图片访问地址
        String imgUrl = minIOConfig.getFileHost()
                + "/"
                + minIOConfig.getBucketName()
                + "/"
                + fileName;

        // 将图片地址存入数据库
        UpdatedUserBO updatedUserBO = new UpdatedUserBO();
        updatedUserBO.setId(userId);

        if (type.equals(FileTypeEnum.BGIMG.type)) {
            updatedUserBO.setBgImg(imgUrl);
        } else {
            updatedUserBO.setFace(imgUrl);
        }

        Users user = usersService.updateUserInfo(updatedUserBO);
        UsersVO usersVO = new UsersVO();
        BeanUtils.copyProperties(user, usersVO);
        return GraceJSONResult.okCustom(ResponseStatusEnum.USER_UPDATE_INFO_SUCCESS, usersVO);
    }
}