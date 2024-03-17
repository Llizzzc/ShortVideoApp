package com.tiktok.controller;

import com.tiktok.grace.result.GraceJSONResult;
import com.tiktok.grace.result.ResponseStatusEnum;
import com.tiktok.pojo.Users;
import com.tiktok.service.FansService;
import com.tiktok.service.UsersService;
import com.tiktok.base.BasePropertiesInfo;
import com.tiktok.utils.PagedGridResult;
import com.tiktok.vo.RelationshipVO;
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

/**
 * 朋友相关控制器.
 * 包括关注, 取关, 查询朋友列表等功能.
 *
 * @author lzc
 * @version 1.0
 */
@Slf4j
@RequestMapping("fans")
@Tag(name = "朋友接口模块")
@RestController
public class FansController extends BasePropertiesInfo {

    @Resource
    private UsersService usersService;

    @Resource
    private FansService fansService;

    @PostMapping("follow")
    @Operation(description = "follow - 关注博主路由")
    @Parameters(value = {
            @Parameter(name = "myId", required = true, description = "用户id"),
            @Parameter(name = "vlogerId", required = true, description = "博主id")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "213", description = "关注成功"),
            @ApiResponse(responseCode = "5192", description = "用户id为空"),
            @ApiResponse(responseCode = "519", description = "不能关注自己"),
            @ApiResponse(responseCode = "515", description = "用户未注册"),
            @ApiResponse(responseCode = "518", description = "请登陆"),
            @ApiResponse(responseCode = "530", description = "token异常")})
    public GraceJSONResult follow(String myId, String vlogerId) {

        if (StringUtils.isBlank(myId) || StringUtils.isBlank(vlogerId)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.USER_ID_EMPTY_ERROR);
        }

        if (myId.equalsIgnoreCase(vlogerId)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.USER_CAN_NOT_FOLLOW_SELF_ERROR);
        }

        Users vloger = usersService.getUser(vlogerId);
        Users myInfo = usersService.getUser(myId);

        if (myInfo == null || vloger == null) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.USER_NOT_REGISTER_ERROR);
        }

        // 保存粉丝关系到数据库
        fansService.doFollow(myId, vlogerId);

        // 博主的粉丝+1, 我的关注+1
        redisOperator.increment(REDIS_MY_FOLLOWS_COUNTS + ":" + myId, 1);
        redisOperator.increment(REDIS_MY_FANS_COUNTS + ":" + vlogerId, 1);

        // 我和博主的关联关系, 依赖redis, 不要存储数据库, 避免db的性能瓶颈
        redisOperator.set(REDIS_FANS_AND_VLOGGER_RELATIONSHIP + ":" + myId + ":" + vlogerId, "1");
        return GraceJSONResult.okCustom(ResponseStatusEnum.USER_FOLLOW_SUCCESS);
    }

    @PostMapping("cancel")
    @Operation(description = "cancel - 取关博主路由")
    @Parameters(value = {
            @Parameter(name = "myId", required = true, description = "用户id"),
            @Parameter(name = "vlogerId", required = true, description = "博主id")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "214", description = "取关成功"),
            @ApiResponse(responseCode = "5192", description = "用户id为空"),
            @ApiResponse(responseCode = "5190", description = "不能取关自己"),
            @ApiResponse(responseCode = "518", description = "请登陆"),
            @ApiResponse(responseCode = "515", description = "用户未注册"),
            @ApiResponse(responseCode = "530", description = "token异常")})
    public GraceJSONResult cancel(String myId, String vlogerId) {

        if (StringUtils.isBlank(myId) || StringUtils.isBlank(vlogerId)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.USER_ID_EMPTY_ERROR);
        }

        if (myId.equalsIgnoreCase(vlogerId)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.USER_CAN_NOT_CANCEL_FOLLOW_SELF_ERROR);
        }

        Users vloger = usersService.getUser(vlogerId);
        Users myInfo = usersService.getUser(myId);

        if (myInfo == null || vloger == null) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.USER_NOT_REGISTER_ERROR);
        }

        // 删除业务的执行
        fansService.doCancel(myId, vlogerId);

        // 博主的粉丝-1, 我的关注-1
        redisOperator.decrement(REDIS_MY_FOLLOWS_COUNTS + ":" + myId, 1);
        redisOperator.decrement(REDIS_MY_FANS_COUNTS + ":" + vlogerId, 1);

        // 我和博主的关联关系, 依赖redis, 不要存储数据库, 避免db的性能瓶颈
        redisOperator.del(REDIS_FANS_AND_VLOGGER_RELATIONSHIP + ":" + myId + ":" + vlogerId);
        return GraceJSONResult.okCustom(ResponseStatusEnum.USER_CANCEL_FOLLOW_SUCCESS);
    }

    @GetMapping("queryRelationship")
    @Operation(description = "queryDoIFollowVloger - 判断用户之间关系路由")
    @Parameters(value = {
            @Parameter(name = "myId", required = true, description = "用户id"),
            @Parameter(name = "vlogerId", required = true, description = "博主id")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "2192", description = "查询关系成功"),
            @ApiResponse(responseCode = "5192", description = "用户id为空"),
            @ApiResponse(responseCode = "5193", description = "不能查询自己和自己的关系"),
            @ApiResponse(responseCode = "515", description = "用户未注册")})
    public GraceJSONResult queryRelationship(String myId, String vlogerId) {

        if (StringUtils.isBlank(myId) || StringUtils.isBlank(vlogerId)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.USER_ID_EMPTY_ERROR);
        }

        if (myId.equalsIgnoreCase(vlogerId)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.USER_CAN_NOT_QUERY_SELF_TO_SELF_RELATIONSHIP_ERROR);
        }

        Users vloger = usersService.getUser(vlogerId);
        Users myInfo = usersService.getUser(myId);

        if (myInfo == null || vloger == null) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.USER_NOT_REGISTER_ERROR);
        }

        RelationshipVO relationshipVO = new RelationshipVO();
        String follow = redisOperator.get(REDIS_FANS_AND_VLOGGER_RELATIONSHIP + ":" + myId + ":" + vlogerId);
        String fan = redisOperator.get(REDIS_FANS_AND_VLOGGER_RELATIONSHIP + ":" + vlogerId + ":" + myId);
        if (follow != null &&follow.equalsIgnoreCase("1")) {
            relationshipVO.setFollow(true);
        }
        if (fan != null && fan.equalsIgnoreCase("1")) {
            relationshipVO.setFan(true);
        }
        return GraceJSONResult.okCustom(ResponseStatusEnum.USER_RELATIONSHIP_QUERY_SUCCESS, relationshipVO);
    }

    @Operation(description = "queryMyFollows - 查询关注列表路由")
    @Parameters(value = {
            @Parameter(name = "vlogerId", required = true, description = "博主id"),
            @Parameter(name = "currentUserId", required = true, description = "当前用户id"),
            @Parameter(name = "page", description = "页码"),
            @Parameter(name = "pageSize", description = "每页数量")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "215", description = "获取关注列表成功"),
            @ApiResponse(responseCode = "5192", description = "用户id为空"),
            @ApiResponse(responseCode = "515", description = "用户未注册")})
    @GetMapping("queryMyFollows")
    public GraceJSONResult queryMyFollows(String vlogerId, String currentUserId, Integer page, Integer pageSize) {

        if (StringUtils.isBlank(vlogerId) || StringUtils.isBlank(currentUserId)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.USER_ID_EMPTY_ERROR);
        }

        Users vloger = usersService.getUser(vlogerId);
        Users current = usersService.getUser(currentUserId);

        if (vloger == null || current == null) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.USER_NOT_REGISTER_ERROR);
        }

        if (page == null) {
            page = COMMON_START_PAGE;
        }

        if (pageSize == null) {
            pageSize = COMMON_PAGE_SIZE;
        }
        PagedGridResult pagedGridResult = fansService.queryMyFollows(vlogerId, currentUserId, page, pageSize);
        return GraceJSONResult.okCustom(ResponseStatusEnum.USER_GET_FOLLOWS_SUCCESS, pagedGridResult);
    }

    @Operation(description = "queryMyFans - 查询粉丝列表路由")
    @Parameters(value = {
            @Parameter(name = "vlogerId", required = true, description = "用户id"),
            @Parameter(name = "currentUserId", required = true, description = "当前用户id"),
            @Parameter(name = "page", description = "页码"),
            @Parameter(name = "pageSize", description = "每页数量")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "216", description = "获取粉丝列表成功"),
            @ApiResponse(responseCode = "5192", description = "用户id为空"),
            @ApiResponse(responseCode = "515", description = "用户未注册")})
    @GetMapping("queryMyFans")
    public GraceJSONResult queryMyFans(String vlogerId, String currentUserId, Integer page, Integer pageSize) {

        if (StringUtils.isBlank(vlogerId) || StringUtils.isBlank(currentUserId)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.USER_ID_EMPTY_ERROR);
        }

        Users vloger = usersService.getUser(vlogerId);
        Users current = usersService.getUser(currentUserId);

        if (vloger == null || current == null) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.USER_NOT_REGISTER_ERROR);
        }

        if (page == null) {
            page = COMMON_START_PAGE;
        }

        if (pageSize == null) {
            pageSize = COMMON_PAGE_SIZE;
        }
        PagedGridResult pagedGridResult = fansService.queryMyFans(vlogerId, currentUserId, page, pageSize);
        return GraceJSONResult.okCustom(ResponseStatusEnum.USER_GET_FANS_SUCCESS, pagedGridResult);
    }
}