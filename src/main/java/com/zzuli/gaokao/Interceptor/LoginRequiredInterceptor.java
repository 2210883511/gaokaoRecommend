package com.zzuli.gaokao.Interceptor;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zzuli.gaokao.Utils.HostHolder;
import com.zzuli.gaokao.Utils.JwtUtil;
import com.zzuli.gaokao.Utils.RedisUtil;
import com.zzuli.gaokao.annotation.LoginRequired;
import com.zzuli.gaokao.bean.User;
import com.zzuli.gaokao.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;

@Component
public class LoginRequiredInterceptor implements HandlerInterceptor {

    @Autowired
    HostHolder hostHolder;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 判断handler是否是HandlerMethod的一个实例
        if(handler instanceof HandlerMethod){
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            LoginRequired loginRequired = method.getAnnotation(LoginRequired.class);
            // 如果没有token并且访问的是需要登录的方法就拦截
            if(loginRequired != null && hostHolder.getUser() == null){
                Result error = Result.error(403, "请先登录！");
                sendError(error,response);
                return false;
            }
        }
        return true;
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



