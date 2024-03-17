package com.tiktok.interceptor;

import com.tiktok.base.BasePropertiesInfo;
import com.tiktok.exceptions.GraceException;
import com.tiktok.grace.result.ResponseStatusEnum;
import com.tiktok.utils.IPUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 短信发送拦截器, 限制60s.
 *
 * @author lzc
 * @version 1.0
 */
@Slf4j
@Component
public class PassportInterceptor extends BasePropertiesInfo implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取用户ip
        String userIp = IPUtil.getRequestIp(request);
        // 存在该ip则返回一个异常
        if (redisOperator.keyIsExist(MOBILE_SMSCODE + ":" + userIp)) {
            GraceException.display(ResponseStatusEnum.SMS_NEED_WAIT_ERROR);
            return false;
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}