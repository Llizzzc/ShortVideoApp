package com.tiktok.interceptor;

import com.tiktok.base.BasePropertiesInfo;
import com.tiktok.exceptions.GraceException;
import com.tiktok.grace.result.ResponseStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 用户token拦截器.
 *
 * @author lzc
 * @version 1.0
 */
@Slf4j
@Component
public class UserTokenInterceptor extends BasePropertiesInfo implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // 从header中获得用户id和token
        String userId = request.getHeader("headerUserId");
        String userToken = request.getHeader("headerUserToken");

        // 判断header中用户id和token不能为空
        if (StringUtils.isNotBlank(userId) && StringUtils.isNotBlank(userToken)) {
            String redisToken = redisOperator.get(REDIS_USER_TOKEN + ":" + userId);
            if (StringUtils.isBlank(redisToken)) {
                GraceException.display(ResponseStatusEnum.USER_UN_LOGIN_ERROR);
                return false;
            } else {
                // 比较token是否一致, 如果不一致, 表示用户在别的手机端登录
                if (!redisToken.equalsIgnoreCase(userToken)) {
                    GraceException.display(ResponseStatusEnum.TOKEN_INVALID_ERROR);
                    return false;
                }
            }
        } else {
            GraceException.display(ResponseStatusEnum.USER_UN_LOGIN_ERROR);
            return false;
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    }
}