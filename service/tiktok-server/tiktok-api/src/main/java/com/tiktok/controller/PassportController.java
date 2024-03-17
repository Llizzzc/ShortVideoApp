package com.tiktok.controller;

import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tiktok.base.BasePropertiesInfo;
import com.tiktok.bo.LoginWithPasswordBO;
import com.tiktok.bo.RegisterLoginBO;
import com.tiktok.grace.result.GraceJSONResult;
import com.tiktok.grace.result.ResponseStatusEnum;
import com.tiktok.pojo.Users;
import com.tiktok.service.UsersService;
import com.tiktok.utils.IPUtil;
import com.tiktok.utils.JWTUtils;
import com.tiktok.utils.SMSUtils;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * 通信认证控制器.
 * 包括登陆, 注册, 退出功能.
 *
 * @author lzc
 * @version 1.0
 */
@RestController
@RequestMapping("passport")
@Slf4j
@Tag(name = "通信认证接口模块")
public class PassportController extends BasePropertiesInfo {
    @Resource
    private SMSUtils smsUtils;
    @Resource
    private UsersService usersService;
    @Resource
    private PasswordEncoder passwordEncoder;

    @Operation(description = "getSMSCode - 这是获取短信路由")
    @GetMapping("getSMSCode")
    @Parameters(value = {
            @Parameter(name = "mobile", required = true, description = "用户手机号")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "230", description = "短信发送成功"),
            @ApiResponse(responseCode = "531", description = "手机号为空"),
            @ApiResponse(responseCode = "532", description = "非11位手机号"),
            @ApiResponse(responseCode = "533", description = "短信发送太频繁"),
            @ApiResponse(responseCode = "535", description = "短信发送失败")})
    public GraceJSONResult getSMSCode(String mobile, HttpServletRequest request) throws TencentCloudSDKException {

        if (StringUtils.isBlank(mobile)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.MOBILE_EMPTY_ERROR);
        }

        if (mobile.length() != 11 || mobile.charAt(0) == '0') {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.MOBILE_FORMAT_ERROR);
        }

        // 获取用户ip
        String userIp = IPUtil.getRequestIp(request);
        // 对ip进行60s的限制
        redisOperator.setnx60s(MOBILE_SMSCODE + ":" + userIp, userIp);
        // 生成6位验证码
        String code = (int)((Math.random() * 9 + 1) * 100000) + "";
        log.info(code);
        smsUtils.sendSMS(mobile, code);

        redisOperator.set(MOBILE_SMSCODE + ":" + mobile, code, 60 * 10);
        return GraceJSONResult.okCustom(ResponseStatusEnum.SMS_SENT_SUCCESS);
    }

    @Operation(description = "login - 这是登陆路由")
    @PostMapping("login")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "210", description = "登陆成功"),
            @ApiResponse(responseCode = "534", description = "验证码过期或不匹配")})
    public GraceJSONResult login(@Valid @RequestBody RegisterLoginBO registerLoginBO)  {
        String mobile = registerLoginBO.getMobile();
        String code = registerLoginBO.getSmsCode();

        // 从redis中获得验证码进行校验是否匹配
        String redisCode = redisOperator.get(MOBILE_SMSCODE + ":" + mobile);
        if (StringUtils.isBlank(redisCode) || !redisCode.equalsIgnoreCase(code)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.SMS_CODE_ERROR);
        }

        Users user = usersService.queryIsMobileExist(mobile);
        if (user == null) {
            user = usersService.createUser(mobile);
        }

        // 如果不为空, 可以继续下方业务, 可以保存用户会话信息
        String userToken = JWTUtils.createJWT(user.getId(), user.getPassword());
        log.info(userToken);
        redisOperator.set(REDIS_USER_TOKEN + ":" + user.getId(), userToken, 24 * 3600);

        // 用户登录注册成功以后, 删除redis中的短信验证码
        redisOperator.del(MOBILE_SMSCODE + ":" + mobile);

        // 返回用户信息, 包含token令牌
        UsersVO userVO = new UsersVO();
        BeanUtils.copyProperties(user, userVO);
        userVO.setUserToken(userToken);
        return GraceJSONResult.okCustom(ResponseStatusEnum.USER_LOGIN_SUCCESS, userVO);
    }

    @Operation(description = "loginWithPassword - 这是账号登陆路由")
    @PostMapping("loginWithPassword")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "210", description = "登陆成功"),
            @ApiResponse(responseCode = "515", description = "用户未注册"),
            @ApiResponse(responseCode = "516", description = "密码错误")})
    public GraceJSONResult loginWithPassword(@Valid @RequestBody LoginWithPasswordBO loginWithPasswordBO) {
        String tiktokNum = loginWithPasswordBO.getTiktokNum();
        String password = loginWithPasswordBO.getPassword();

        // 查询数据库, 判断用户是否存在
        Users user = usersService.queryIsAccountExist(tiktokNum);
        if (user == null) {
           return GraceJSONResult.errorCustom(ResponseStatusEnum.USER_NOT_REGISTER_ERROR);
        }

        // 如果不为空, 可以继续下方业务, 可以保存用户会话信息
        if (!passwordEncoder.matches(password, user.getPassword())) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.USER_PASSWORD_ERROR);
        }

        String userToken = JWTUtils.createJWT(user.getId(), user.getPassword());
        log.info(userToken);
        redisOperator.set(REDIS_USER_TOKEN + ":" + user.getId(), userToken, 24 * 3600);
        UsersVO userVO = new UsersVO();
        BeanUtils.copyProperties(user, userVO);
        userVO.setUserToken(userToken);
        return GraceJSONResult.okCustom(ResponseStatusEnum.USER_LOGIN_SUCCESS, userVO);
    }

    @Operation(description = "logout - 这是退出路由")
    @Parameters(value = {
            @Parameter(name = "userId", required = true, description = "用户id")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "211", description = "退出成功"),
            @ApiResponse(responseCode = "5192", description = "用户id为空"),
            @ApiResponse(responseCode = "515", description = "用户未注册")})
    @GetMapping("logout")
    public GraceJSONResult logout(String userId) {
        if (StringUtils.isBlank(userId)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.USER_ID_EMPTY_ERROR);
        }

        Users user = usersService.getUser(userId);
        if (user == null) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.USER_NOT_REGISTER_ERROR);
        }

        // 后端只需要清除用户的token信息即可, 前端也需要清除, 清除本地app中的用户信息和token会话信息
        if (redisOperator.keyIsExist(REDIS_USER_TOKEN + ":" + userId)) {
            redisOperator.del(REDIS_USER_TOKEN + ":" + userId);
        }
        return GraceJSONResult.okCustom(ResponseStatusEnum.USER_LOGOUT_SUCCESS);
    }
}