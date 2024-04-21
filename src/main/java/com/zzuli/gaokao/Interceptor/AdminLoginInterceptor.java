package com.zzuli.gaokao.Interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zzuli.gaokao.Utils.JwtUtil;
import com.zzuli.gaokao.Utils.RedisUtil;
import com.zzuli.gaokao.common.Result;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;


@Component
public class AdminLoginInterceptor implements HandlerInterceptor {

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(request.getMethod().equals("OPTIONS")){
            return true;
        }
        String token = request.getHeader("Authorization");
        if(!StringUtils.isBlank(token)){
           if(JwtUtil.verifyToken(token)){
               String username = JwtUtil.getSubject(token);
               String tokenKey = RedisUtil.getTokenKey(username);
               Object o = redisTemplate.opsForValue().get(tokenKey);
               if(o != null){
                   return true;
               }
               Result error = Result.error(401,"token无效！");
               sendError(error,response);
               return false;
           }
            Result error = Result.error(401,"token无效！");
            sendError(error,response);
        }else {
            Result error = Result.error(401,"token不能为空");
            sendError(error,response);
        }
        return false;
    }



    private void sendError(Result result,HttpServletResponse response) throws IOException {
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
