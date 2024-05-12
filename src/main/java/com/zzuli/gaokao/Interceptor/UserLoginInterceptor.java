package com.zzuli.gaokao.Interceptor;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zzuli.gaokao.Utils.HostHolder;
import com.zzuli.gaokao.Utils.JwtUtil;
import com.zzuli.gaokao.Utils.RedisUtil;
import com.zzuli.gaokao.bean.User;
import com.zzuli.gaokao.common.Result;
import com.zzuli.gaokao.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Slf4j
@Component
public class UserLoginInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(request.getMethod().equals("OPTIONS")){
            return true;
        }
        String token = request.getHeader("UserToken");

        if(StringUtils.isNotBlank(token)){
            // 解析token
            if(JwtUtil.verifyToken(token)){

                String username = JwtUtil.getSubject(token);
                String userTokenKey = RedisUtil.getUserTokenKey(username);
                Object o = redisTemplate.opsForValue().get(userTokenKey);
                // redis验证通过
                if(o != null){
                    // 根据用户名查出该用户信息存入threadLocal
                    User one = userService.getOne(new QueryWrapper<User>()
                            .eq("username", username));
                    if (one != null) {
                        hostHolder.setUser(one);
                    }
                    return true;
                }else {
                    Result error = Result.error(401, "登录信息失效");
                    log.warn("token过期！");
                    sendError(error,response);
                    return false;
                }

            }
            // 解析失败 重新登陆
            else {
                Result error = Result.error(401, "登录信息失效");
                log.warn("token过期！");
                sendError(error,response);
                return false;
            }

        }

        // token为null也直接放行
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        hostHolder.clear();
    }


    private void sendError(Result result, HttpServletResponse response) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        String data = objectMapper.writeValueAsString(result);
        response.setStatus(200);
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        PrintWriter writer = response.getWriter();
        writer.write(data);
        writer.flush();
    }
}
