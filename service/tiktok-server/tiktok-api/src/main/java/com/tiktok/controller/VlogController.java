package com.tiktok.controller;

import com.tiktok.configration.MinIOConfig;
import com.tiktok.bo.VlogBO;
import com.tiktok.base.BasePropertiesInfo;
import com.tiktok.enums.ImageTypeEnum;
import com.tiktok.enums.ListTypeEnum;
import com.tiktok.grace.result.GraceJSONResult;
import com.tiktok.grace.result.ResponseStatusEnum;
import com.tiktok.pojo.Users;
import com.tiktok.pojo.Vlog;
import com.tiktok.service.MyLikedVlogService;
import com.tiktok.service.UsersService;
import com.tiktok.service.VlogService;
import com.tiktok.utils.MinIOUtils;
import com.tiktok.utils.PagedGridResult;
import com.tiktok.vo.IndexVlogVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * 视频相关控制器.
 * 包括创建, 查询, 点赞等功能.
 *
 * @author lzc
 * @version 1.0
 */
@Slf4j
@Tag(name = "短视频相关业务功能的接口模块")
@RequestMapping("vlog")
@RestController
public class VlogController extends BasePropertiesInfo {

    @Resource
    private VlogService vlogService;

    @Resource
    private UsersService usersService;

    @Resource
    private MyLikedVlogService myLikedVlogService;

    @Resource
    private MinIOConfig minIOConfig;

    @PostMapping("publish")
    @Operation(description = "publish - 发布短视频路由")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "220", description = "发布成功"),
            @ApiResponse(responseCode = "515", description = "用户未注册"),
            @ApiResponse(responseCode = "518", description = "请登陆"),
            @ApiResponse(responseCode = "530", description = "token异常")})
    public GraceJSONResult publish(@Valid @RequestBody VlogBO vlogBO) {

        Users user = usersService.getUser(vlogBO.getVlogerId());
        if (user == null) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.USER_NOT_REGISTER_ERROR);
        }

        vlogService.createVlog(vlogBO);
        return GraceJSONResult.okCustom(ResponseStatusEnum.VLOG_PUBLISH_SUCCESS);
    }

    @PostMapping("updateCover")
    @Operation(description = "updateCover - 更改视频封面路由")
    @Parameters(value = {
            @Parameter(name = "file", required = true, description = "图片文件")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "224", description = "封面修改成功"),
            @ApiResponse(responseCode = "542", description = "图片格式不支持"),
            @ApiResponse(responseCode = "540", description = "文件为空"),
            @ApiResponse(responseCode = "543", description = "图片大小超过限制"),
            @ApiResponse(responseCode = "518", description = "请登陆"),
            @ApiResponse(responseCode = "530", description = "token异常")})
    public GraceJSONResult updateCover(MultipartFile file) throws Exception {

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

        return GraceJSONResult.okCustom(ResponseStatusEnum.VLOG_INFO_UPDATE_SUCCESS, imgUrl);
    }

    @Operation(description = "indexList - 查询视频详情路由")
    @Parameters(value = {
            @Parameter(name = "userId", required = true, description = "用户id"),
            @Parameter(name = "search", required = true, description = "搜索关键字"),
            @Parameter(name = "page", required = true, description = "页码"),
            @Parameter(name = "pageSize", required = true, description = "每页个数")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "221", description = "获取视频列表成功"),
            @ApiResponse(responseCode = "5192", description = "用户id为空"),
            @ApiResponse(responseCode = "515", description = "用户未注册")})
    @GetMapping("indexList")
    public GraceJSONResult indexList(String userId, String search, Integer page, Integer pageSize) {

        if (StringUtils.isBlank(userId)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.USER_ID_EMPTY_ERROR);
        }

        Users user = usersService.getUser(userId);
        if (user == null) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.USER_NOT_REGISTER_ERROR);
        }

        if (search == null) {
            search = "";
        }
        if (page == null) {
            page = COMMON_START_PAGE;
        }
        if (pageSize == null) {
            pageSize = COMMON_PAGE_SIZE;
        }

        PagedGridResult gridResult = vlogService.getIndexVlogList(userId, search, page, pageSize);
        return GraceJSONResult.okCustom(ResponseStatusEnum.VLOG_LIST_GET_SUCCESS, gridResult);
    }

    @Operation(description = "detail - 查询视频详情路由")
    @Parameters(value = {
            @Parameter(name = "userId", required = true, description = "用户id"),
            @Parameter(name = "vlogId", required = true, description = "视频id")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "222", description = "获取视频成功"),
            @ApiResponse(responseCode = "520", description = "视频不存在"),
            @ApiResponse(responseCode = "5192", description = "用户id为空"),
            @ApiResponse(responseCode = "515", description = "用户未注册"),
            @ApiResponse(responseCode = "521", description = "视频id为空")})
    @GetMapping("detail")
    public GraceJSONResult detail(String userId, String vlogId) {

        if (StringUtils.isBlank(userId)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.USER_ID_EMPTY_ERROR);
        }

        Users user = usersService.getUser(userId);
        if (user == null) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.USER_NOT_REGISTER_ERROR);
        }

        if (StringUtils.isBlank(vlogId)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.VLOG_ID_EMPTY_ERROR);
        }

        Vlog vlog = vlogService.getVlog(vlogId);
        if (vlog == null) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.VLOG_NOT_EXIST_ERROR);
        }

        IndexVlogVO indexVlogVO = vlogService.getVlogDetailById(userId, vlogId);
        return GraceJSONResult.okCustom(ResponseStatusEnum.VLOG_GET_SUCCESS, indexVlogVO);
    }

    @Operation(description = "changeToPrivateOrPublic - 更改视频类型路由")
    @Parameters(value = {
            @Parameter(name = "userId", required = true, description = "用户id"),
            @Parameter(name = "vlogId", required = true, description = "视频id")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "224", description = "修改视频信息成功"),
            @ApiResponse(responseCode = "520", description = "视频不存在"),
            @ApiResponse(responseCode = "5192", description = "用户id为空"),
            @ApiResponse(responseCode = "515", description = "用户未注册"),
            @ApiResponse(responseCode = "521", description = "视频id为空"),
            @ApiResponse(responseCode = "522", description = "视频类型参数错误"),
            @ApiResponse(responseCode = "518", description = "请登陆"),
            @ApiResponse(responseCode = "530", description = "token异常")})
    @PostMapping("changeToPrivateOrPublic")
    public GraceJSONResult changeToPrivate(String userId, String vlogId, Integer type) {

        if (StringUtils.isBlank(userId)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.USER_ID_EMPTY_ERROR);
        }

        Users user = usersService.getUser(userId);
        if (user == null) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.USER_NOT_REGISTER_ERROR);
        }

        if (StringUtils.isBlank(vlogId)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.VLOG_ID_EMPTY_ERROR);
        }

        Vlog vlog = vlogService.getVlog(vlogId);
        if (vlog == null) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.VLOG_NOT_EXIST_ERROR);
        }

        if (type == null || !ListTypeEnum.checkListType(type)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.VLOG_TYPE_ERROR);
        }

        vlogService.changeToPrivateOrPublic(userId, vlogId, type);
        return GraceJSONResult.okCustom(ResponseStatusEnum.VLOG_INFO_UPDATE_SUCCESS);
    }

    @Operation(description = "myList - 查询视频类型路由")
    @Parameters(value = {
            @Parameter(name = "userId", required = true, description = "用户id"),
            @Parameter(name = "type", required = true, description = "视频类型"),
            @Parameter(name = "page", required = true, description = "页码"),
            @Parameter(name = "pageSize", required = true, description = "每页个数")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "221", description = "获取视频成功"),
            @ApiResponse(responseCode = "5192", description = "用户id为空"),
            @ApiResponse(responseCode = "515", description = "用户未注册"),
            @ApiResponse(responseCode = "522", description = "视频类型错误")})
    @GetMapping("myList")
    public GraceJSONResult myList(String userId, Integer type, Integer page, Integer pageSize) {

        if (StringUtils.isBlank(userId)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.USER_ID_EMPTY_ERROR);
        }

        Users myInfo = usersService.getUser(userId);
        if (myInfo == null) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.USER_NOT_REGISTER_ERROR);
        }

        if (!ListTypeEnum.checkListType(type)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.VLOG_TYPE_ERROR);
        }

        if (page == null) {
            page = COMMON_START_PAGE;
        }

        if (pageSize == null) {
            pageSize = COMMON_PAGE_SIZE;
        }

        PagedGridResult pagedGridResult = vlogService.queryMyVlogList(userId, type, page, pageSize);
        return GraceJSONResult.okCustom(ResponseStatusEnum.VLOG_LIST_GET_SUCCESS, pagedGridResult);
    }

    @Operation(description = "like - 点赞路由")
    @Parameters(value = {
            @Parameter(name = "userId", required = true, description = "用户id"),
            @Parameter(name = "vlogerId", required = true, description = "博主id"),
            @Parameter(name = "vlogId", required = true, description = "视频id")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "217", description = "点赞视频成功"),
            @ApiResponse(responseCode = "5192", description = "用户id为空"),
            @ApiResponse(responseCode = "515", description = "用户未注册"),
            @ApiResponse(responseCode = "520", description = "视频不存在"),
            @ApiResponse(responseCode = "521", description = "视频id为空"),
            @ApiResponse(responseCode = "5191", description = "已经点赞过了"),
            @ApiResponse(responseCode = "518", description = "请登陆"),
            @ApiResponse(responseCode = "530", description = "token异常")})
    @PostMapping("like")
    public GraceJSONResult like(String userId, String vlogerId, String vlogId) {

        if (StringUtils.isBlank(userId) || StringUtils.isBlank(vlogerId)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.USER_ID_EMPTY_ERROR);
        }

        if (StringUtils.isBlank(vlogId)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.VLOG_ID_EMPTY_ERROR);
        }

        Users user = usersService.getUser(userId);
        Users vloger = usersService.getUser(vlogerId);
        Vlog vlog = vlogService.getVlog(vlogId);

        if (user == null || vloger == null) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.USER_NOT_REGISTER_ERROR);
        }

        if (vlog == null) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.VLOG_NOT_EXIST_ERROR);
        }

        if (vlogService.doILikeVlog(userId, vlogId)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.USER_ALREADY_LIKE_THIS_VLOG_ERROR);
        }

        // 我点赞的视频, 关联关系保存到数据库
        myLikedVlogService.userLikeVlog(userId, vlogId);

        // 点赞后, 视频和视频发布者的获赞都会 +1
        redisOperator.increment(REDIS_VLOGER_BE_LIKED_COUNTS + ":" + vlogerId, 1);
        redisOperator.increment(REDIS_VLOG_BE_LIKED_COUNTS + ":" + vlogId, 1);

        // 我点赞的视频, 需要在redis中保存关联关系
        redisOperator.set(REDIS_USER_LIKE_VLOG + ":" + userId + ":" + vlogId, "1");
        return GraceJSONResult.okCustom(ResponseStatusEnum.USER_LIKE_VLOG_SUCCESS);
    }

    @Operation(description = "unlike - 取消点赞路由")
    @Parameters(value = {
            @Parameter(name = "userId", required = true, description = "用户id"),
            @Parameter(name = "vlogerId", required = true, description = "博主id"),
            @Parameter(name = "vlogId", required = true, description = "视频id")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "218", description = "取消点赞视频成功"),
            @ApiResponse(responseCode = "5192", description = "用户id为空"),
            @ApiResponse(responseCode = "515", description = "用户未注册"),
            @ApiResponse(responseCode = "520", description = "视频不存在"),
            @ApiResponse(responseCode = "521", description = "视频id为空"),
            @ApiResponse(responseCode = "518", description = "请登陆"),
            @ApiResponse(responseCode = "530", description = "token异常")})
    @PostMapping("unlike")
    public GraceJSONResult unlike(String userId, String vlogerId, String vlogId) {

        if (StringUtils.isBlank(userId) || StringUtils.isBlank(vlogerId)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.USER_ID_EMPTY_ERROR);
        }

        if (StringUtils.isBlank(vlogId)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.VLOG_ID_EMPTY_ERROR);
        }

        Users user = usersService.getUser(userId);
        Users vloger = usersService.getUser(vlogerId);
        Vlog vlog = vlogService.getVlog(vlogId);

        if (user == null || vloger == null) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.USER_NOT_REGISTER_ERROR);
        }

        if (vlog == null) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.VLOG_NOT_EXIST_ERROR);
        }

        // 我取消点赞的视频, 关联关系删除
        myLikedVlogService.userUnLikeVlog(userId, vlogId);

        redisOperator.decrement(REDIS_VLOGER_BE_LIKED_COUNTS + ":" + vlogerId, 1);
        redisOperator.decrement(REDIS_VLOG_BE_LIKED_COUNTS + ":" + vlogId, 1);
        redisOperator.del(REDIS_USER_LIKE_VLOG + ":" + userId + ":" + vlogId);
        return GraceJSONResult.okCustom(ResponseStatusEnum.USER_UNLIKE_VLOG_SUCCESS);
    }

    @Operation(description = "totalLikedCounts - 获取视频点赞数路由")
    @Parameters(value = {
            @Parameter(name = "vlogId", required = true, description = "视频id")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "223", description = "获取视频点赞数成功"),
            @ApiResponse(responseCode = "520", description = "视频不存在"),
            @ApiResponse(responseCode = "521", description = "视频id为空"),
            @ApiResponse(responseCode = "523", description = "短视频不存在")})
    @PostMapping("totalLikedCounts")
    public GraceJSONResult totalLikedCounts(String vlogId) {

        if (StringUtils.isBlank(vlogId)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.VLOG_ID_EMPTY_ERROR);
        }

        Vlog vlog = vlogService.getVlog(vlogId);
        if (vlog == null) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.VLOG_NOT_EXIST_ERROR);
        }

        Integer vlogBeLikedCounts = vlogService.getVlogBeLikedCounts(vlogId);
        return GraceJSONResult.okCustom(ResponseStatusEnum.VLOG_GET_TOTAL_LIKE_SUCCESS, vlogBeLikedCounts);
    }

    @Operation(description = "followList - 获取关注视频列表路由")
    @Parameters(value = {
            @Parameter(name = "myId", required = true, description = "用户id"),
            @Parameter(name = "page", required = true, description = "页码"),
            @Parameter(name = "pageSize", required = true, description = "每页个数")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "2191", description = "获取关注视频列表成功"),
            @ApiResponse(responseCode = "5192", description = "用户id为空"),
            @ApiResponse(responseCode = "515", description = "用户未注册"),
            @ApiResponse(responseCode = "518", description = "请登陆"),
            @ApiResponse(responseCode = "530", description = "token异常")})
    @GetMapping("followList")
    public GraceJSONResult followList(String myId, Integer page, Integer pageSize) {

        if (StringUtils.isBlank(myId)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.USER_ID_EMPTY_ERROR);
        }

        Users user = usersService.getUser(myId);
        if (user == null) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.USER_NOT_REGISTER_ERROR);
        }

        if (page == null) {
            page = COMMON_START_PAGE;
        }
        if (pageSize == null) {
            pageSize = COMMON_PAGE_SIZE;
        }

        PagedGridResult gridResult = vlogService.getMyFollowVlogList(myId, page, pageSize);
        return GraceJSONResult.okCustom(ResponseStatusEnum.USER_GET_FOLLOW_LIST_SUCCESS, gridResult);
    }

    @Operation(description = "friendList - 获取朋友视频列表路由")
    @Parameters(value = {
            @Parameter(name = "myId", required = true, description = "用户id"),
            @Parameter(name = "page", required = true, description = "页码"),
            @Parameter(name = "pageSize", required = true, description = "每页个数")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "219", description = "获取朋友视频列表成功"),
            @ApiResponse(responseCode = "5192", description = "用户id为空"),
            @ApiResponse(responseCode = "515", description = "用户未注册"),
            @ApiResponse(responseCode = "518", description = "请登陆"),
            @ApiResponse(responseCode = "530", description = "token异常")})
    @GetMapping("friendList")
    public GraceJSONResult friendList(String myId, Integer page, Integer pageSize) {

        if (StringUtils.isBlank(myId)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.USER_ID_EMPTY_ERROR);
        }

        Users user = usersService.getUser(myId);
        if (user == null) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.USER_NOT_REGISTER_ERROR);
        }

        if (page == null) {
            page = COMMON_START_PAGE;
        }

        if (pageSize == null) {
            pageSize = COMMON_PAGE_SIZE;
        }

        PagedGridResult gridResult = vlogService.getMyFriendVlogList(myId, page, pageSize);
        return GraceJSONResult.okCustom(ResponseStatusEnum.USER_GET_FRIEND_VLOG_LIST_SUCCESS, gridResult);
    }
}