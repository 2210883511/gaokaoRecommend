package com.zzuli.gaokao.controller.admin;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wf.captcha.SpecCaptcha;
import com.zzuli.gaokao.Utils.CommonUtils;
import com.zzuli.gaokao.Utils.JwtUtil;
import com.zzuli.gaokao.Utils.RedisUtil;
import com.zzuli.gaokao.bean.User;
import com.zzuli.gaokao.common.Result;
import com.zzuli.gaokao.service.Impl.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/admin")
public class LoginController {


    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private RedisTemplate<String ,Object> redisTemplate;

    @PostMapping("/login")
    public Result login(String username, String password,String captchaId,String code){

        if(StringUtils.isBlank(username) || StringUtils.isBlank(password))
            return Result.error("用户名或密码为空！");

        if(StringUtils.isBlank(code))
            return Result.error("验证码为空！");
        String captchaKey = RedisUtil.getCaptchaKey(captchaId);
        Object text = redisTemplate.opsForValue().get(captchaKey);
        if(text == null){
            return Result.error("验证码过期！");
        }
        if(!code.equalsIgnoreCase(text.toString())){
            return Result.error("验证码错误！");
        }

        if(!userService.login(username,password)){
            return Result.error("用户名或密码错误");
        }
        /* 登录完成之后应该生成token并返回给前端，把token放到result中*/
        String token = JwtUtil.getToken(username);
        String tokenKey = RedisUtil.getTokenKey(username);
        redisTemplate.opsForValue().set(tokenKey,token,CommonUtils.TOKEN_EXPIRED, TimeUnit.MILLISECONDS);
        HashMap<String, Object> map = new HashMap<>();
        map.put("token",token);
        return Result.success(map);
    }


    @GetMapping("/userInfo")
    public Result userInfo(String token){

        String subject = JwtUtil.getSubject(token);
        User one = userService
                .getOne(new QueryWrapper<User>()
                        .select("username,nickname,roles,header_url")
                        .eq("username",subject));
        HashMap<String, Object> map = new HashMap<>();
        String [] roles = {one.getRoles()};
        map.put("username",one.getUsername());
        map.put("nickname",one.getNickname());
        map.put("roles",roles);
        map.put("header_url",one.getHeaderUrl());
        return Result.success(map);
    }


    @PostMapping("/logout")
    public Result logout(HttpServletRequest request){
        String token = request.getHeader("Authorization");
        String subject = JwtUtil.getSubject(token);
        String tokenKey = RedisUtil.getTokenKey(subject);
        redisTemplate.delete(tokenKey);
        return Result.success("退出成功！");
    }



    @GetMapping("/captcha")
    public Result captcha()   {
        SpecCaptcha specCaptcha = new SpecCaptcha(100,49,4);
        String code = specCaptcha.text();
        String key = CommonUtils.generateUUID();
        String captchaKey = RedisUtil.getCaptchaKey(key);
        redisTemplate.opsForValue().set(captchaKey,code,CommonUtils.CAPTCHA_EXPIRED,TimeUnit.MINUTES);
        String base64 = specCaptcha.toBase64();
        HashMap<String, Object> map = new HashMap<>();
        map.put("key",key);
        map.put("img",base64);
        return Result.success(map);
    }

}
