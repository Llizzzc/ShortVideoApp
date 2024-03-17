package com.tiktok.controller;

import com.tiktok.bo.CommentBO;
import com.tiktok.enums.MessageTypeEnum;
import com.tiktok.grace.result.GraceJSONResult;
import com.tiktok.grace.result.ResponseStatusEnum;
import com.tiktok.pojo.Comment;
import com.tiktok.pojo.Users;
import com.tiktok.pojo.Vlog;
import com.tiktok.service.CommentService;
import com.tiktok.service.MsgService;
import com.tiktok.service.UsersService;
import com.tiktok.service.VlogService;
import com.tiktok.utils.PagedGridResult;
import com.tiktok.vo.CommentVO;
import com.tiktok.base.BasePropertiesInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * 评论控制器.
 * 包括发布, 删除, 点赞等功能.
 *
 * @author lzc
 * @version 1.0
 */
@Slf4j
@Tag(name = "评论接口模块")
@RequestMapping("comment")
@RestController
public class CommentController extends BasePropertiesInfo {

    @Resource
    private CommentService commentService;
    @Resource
    private MsgService msgService;
    @Resource
    private VlogService vlogService;
    @Resource
    private UsersService usersService;

    @Operation(description = "create - 发布评论路由")
    @Parameters(value = {
            @Parameter(name = "commentBO", required = true, description = "评论信息")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "241", description = "发布评论成功"),
            @ApiResponse(responseCode = "518", description = "请登陆"),
            @ApiResponse(responseCode = "530", description = "token异常")})
    @PostMapping("create")
    public GraceJSONResult create(@RequestBody @Valid CommentBO commentBO) {
        CommentVO commentVO = commentService.createComment(commentBO);
        return GraceJSONResult.okCustom(ResponseStatusEnum.COMMENT_PUBLISH_SUCCESS, commentVO);
    }

    @Operation(description = "counts - 获取评论总数路由")
    @Parameters(value = {
            @Parameter(name = "vlogId", required = true, description = "视频id")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "240", description = "操作成功"),
            @ApiResponse(responseCode = "521", description = "视频id为空"),
            @ApiResponse(responseCode = "520", description = "视频不存在")})
    @GetMapping("counts")
    public GraceJSONResult counts(String vlogId) {

        if (StringUtils.isBlank(vlogId)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.VLOG_ID_EMPTY_ERROR);
        }

        Vlog vlog = vlogService.getVlog(vlogId);
        if (vlog == null) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.VLOG_NOT_EXIST_ERROR);
        }

        String countsStr = redisOperator.get(REDIS_VLOG_COMMENT_COUNTS + ":" + vlogId);
        if (StringUtils.isBlank(countsStr)) {
            countsStr = "0";
        }
        return GraceJSONResult.okCustom(ResponseStatusEnum.COMMENT_GET_TOTAL_LIKE_SUCCESS, Integer.valueOf(countsStr));
    }

    @Operation(description = "list - 获取评论列表路由")
    @Parameters(value = {
            @Parameter(name = "vlogId", required = true, description = "视频id"),
            @Parameter(name = "userId", required = true, description = "用户id"),
            @Parameter(name = "page", required = true, description = "页码"),
            @Parameter(name = "pageSize", required = true, description = "每页个数")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "243", description = "获取评论列表成功"),
            @ApiResponse(responseCode = "5192", description = "用户id为空"),
            @ApiResponse(responseCode = "515", description = "用户未注册"),
            @ApiResponse(responseCode = "520", description = "视频不存在"),
            @ApiResponse(responseCode = "521", description = "视频id为空")})
    @GetMapping("list")
    public GraceJSONResult list(String vlogId, String userId, Integer page, Integer pageSize) {

        if (StringUtils.isBlank(vlogId)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.VLOG_ID_EMPTY_ERROR);
        }

        Vlog vlog = vlogService.getVlog(vlogId);
        if (vlog == null) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.VLOG_NOT_EXIST_ERROR);
        }

        if (StringUtils.isBlank(userId)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.USER_ID_EMPTY_ERROR);
        }

        Users user = usersService.getUser(userId);
        if (user == null) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.USER_NOT_REGISTER_ERROR);
        }

        if (page == null) {
            page = COMMON_START_PAGE;
        }
        if (pageSize == null) {
            pageSize = COMMON_PAGE_SIZE;
        }

        PagedGridResult pagedGridResult = commentService.queryVlogComments(vlogId, userId, page, pageSize);
        return GraceJSONResult.okCustom(ResponseStatusEnum.COMMENT_LIST_GET_SUCCESS, pagedGridResult);
    }

    @Operation(description = "delete - 删除评论路由")
    @Parameters(value = {
            @Parameter(name = "commentUserId", required = true, description = "评论用户id"),
            @Parameter(name = "commentId", required = true, description = "评论id"),
            @Parameter(name = "vlogId", required = true, description = "视频id")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "242", description = "删除评论成功"),
            @ApiResponse(responseCode = "5192", description = "用户id为空"),
            @ApiResponse(responseCode = "515", description = "用户未注册"),
            @ApiResponse(responseCode = "520", description = "视频不存在"),
            @ApiResponse(responseCode = "521", description = "视频id为空"),
            @ApiResponse(responseCode = "550", description = "评论id为空"),
            @ApiResponse(responseCode = "551", description = "评论不存在"),
            @ApiResponse(responseCode = "518", description = "请登陆"),
            @ApiResponse(responseCode = "530", description = "token异常")})
    @DeleteMapping("delete")
    public GraceJSONResult delete(String commentUserId, String commentId, String vlogId) {

        if (StringUtils.isBlank(commentUserId)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.USER_ID_EMPTY_ERROR);
        }

        if (StringUtils.isBlank(commentId)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.COMMENT_ID_EMPTY_ERROR);
        }

        if (StringUtils.isBlank(vlogId)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.VLOG_ID_EMPTY_ERROR);
        }

        Users user = usersService.getUser(commentUserId);
        Vlog vlog = vlogService.getVlog(vlogId);
        Comment comment = commentService.getComment(commentId);

       if (user == null) {
           return GraceJSONResult.errorCustom(ResponseStatusEnum.USER_NOT_REGISTER_ERROR);
       }

       if (vlog == null) {
           return GraceJSONResult.errorCustom(ResponseStatusEnum.VLOG_NOT_EXIST_ERROR);
       }

       if (comment == null) {
           return GraceJSONResult.errorCustom(ResponseStatusEnum.COMMENT_NOT_EXIST_ERROR);
       }

        commentService.deleteComment(commentUserId, commentId, vlogId);
        return GraceJSONResult.okCustom(ResponseStatusEnum.COMMENT_DELETE_SUCCESS);
    }

    @Operation(description = "like - 点赞评论路由")
    @Parameters(value = {
            @Parameter(name = "commentId", required = true, description = "评论id"),
            @Parameter(name = "userId", required = true, description = "用户id")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "2193", description = "点赞评论成功"),
            @ApiResponse(responseCode = "550", description = "评论id为空"),
            @ApiResponse(responseCode = "551", description = "评论不存在"),
            @ApiResponse(responseCode = "5192", description = "用户id为空"),
            @ApiResponse(responseCode = "515", description = "用户未注册"),
            @ApiResponse(responseCode = "518", description = "请登陆"),
            @ApiResponse(responseCode = "530", description = "token异常")})
    @PostMapping("like")
    public GraceJSONResult like(String commentId, String userId) {

        if (StringUtils.isBlank(commentId)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.COMMENT_ID_EMPTY_ERROR);
        }

        if (StringUtils.isBlank(userId)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.USER_ID_EMPTY_ERROR);
        }

        Comment comment = commentService.getComment(commentId);
        Users user = usersService.getUser(userId);

        if (comment == null) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.COMMENT_NOT_EXIST_ERROR);
        }

        if (user == null) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.USER_NOT_REGISTER_ERROR);
        }

        redisOperator.increment(REDIS_VLOG_COMMENT_LIKED_COUNTS + ":" + commentId, 1);
        redisOperator.set(REDIS_USER_LIKE_COMMENT + ":" + userId + ":" + commentId, "1");
        redisOperator.increment(REDIS_VLOGER_BE_LIKED_COUNTS + ":" + userId, 1);

        // 系统消息: 点赞评论
        Vlog vlog = vlogService.getVlog(comment.getVlogId());
        Map msgContent = new HashMap();
        msgContent.put("vlogId", vlog.getId());
        msgContent.put("vlogCover", vlog.getCover());
        msgContent.put("commentId", commentId);
        msgService.createMsg(userId, comment.getCommentUserId(), MessageTypeEnum.LIKE_COMMENT.type, msgContent);

        return GraceJSONResult.okCustom(ResponseStatusEnum.USER_LIKE_COMMENT_SUCCESS);
    }

    @Operation(description = "unlike - 取消点赞评论路由")
    @Parameters(value = {
            @Parameter(name = "commentId", required = true, description = "评论id"),
            @Parameter(name = "userId", required = true, description = "用户id")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "2194", description = "取消点赞评论成功"),
            @ApiResponse(responseCode = "550", description = "评论id为空"),
            @ApiResponse(responseCode = "551", description = "评论不存在"),
            @ApiResponse(responseCode = "5192", description = "用户id为空"),
            @ApiResponse(responseCode = "515", description = "用户未注册"),
            @ApiResponse(responseCode = "518", description = "请登陆"),
            @ApiResponse(responseCode = "530", description = "token异常")})
    @PostMapping("unlike")
    public GraceJSONResult unlike(String commentId, String userId) {

        if (StringUtils.isBlank(commentId)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.COMMENT_ID_EMPTY_ERROR);
        }

        if (StringUtils.isBlank(userId)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.USER_ID_EMPTY_ERROR);
        }

        Comment comment = commentService.getComment(commentId);
        Users user = usersService.getUser(userId);

        if (comment == null) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.COMMENT_NOT_EXIST_ERROR);
        }

        if (user == null) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.USER_NOT_REGISTER_ERROR);
        }

        redisOperator.decrement(REDIS_VLOG_COMMENT_LIKED_COUNTS + ":" + commentId, 1);
        redisOperator.decrement(REDIS_VLOGER_BE_LIKED_COUNTS + ":" + userId, 1);
        redisOperator.del(REDIS_USER_LIKE_COMMENT + ":" + userId + ":" + commentId);
        return GraceJSONResult.okCustom(ResponseStatusEnum.USER_UNLIKE_COMMENT_SUCCESS);
    }
}