package com.zzuli.gaokao.controller.api;

import com.alibaba.druid.sql.ast.SQLPartitionValue;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wf.captcha.SpecCaptcha;
import com.zzuli.gaokao.Utils.CommonUtils;
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
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/api/user")
public class UserHandler {

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    
    /*
     * @Description: 用户登录功能
     * @Date:   2024/5/10 20:17
     * @Param:  [user, captchaId, captcha]
     * @Return: com.zzuli.gaokao.common.Result
     */
    @PostMapping("/login")
    public Result login(@RequestBody User user,String captchaId,String captcha){
        String username = user.getUsername();
        String password = user.getPassword();
        if(StringUtils.isBlank(username) || StringUtils.isBlank(password))
            return Result.error("用户名或密码为空！");

        if(StringUtils.isBlank(captcha))
            return Result.error("验证码为空！");
        String userCaptchaKey = RedisUtil.getUserCaptchaKey(captchaId);
        Object text = redisTemplate.opsForValue().get(userCaptchaKey);
        if(text == null){
            return Result.error("验证码过期！");
        }
        if(!captcha.equalsIgnoreCase(text.toString())){
            return Result.error("验证码错误！");
        }

        if(!userService.login(username,password)){
            return Result.error("用户名或密码错误");
        }
        /* 登录完成之后应该生成token并返回给前端，把token放到result中*/
        User one = userService.getOne(new QueryWrapper<User>()
                .select("id")
                .eq("username", username));
        HashMap<String, Object> userMap = new HashMap<>();
        Integer id = one.getId();
        userMap.put("id",id);
        userMap.put("username",username);
        String token = JwtUtil.getToken(userMap);
        String userTokenKey = RedisUtil.getUserTokenKey(username);
        redisTemplate.opsForValue().set(userTokenKey,token, CommonUtils.TOKEN_EXPIRED, TimeUnit.MILLISECONDS);
        HashMap<String, Object> map = new HashMap<>();
        map.put("userToken",token);
        return Result.success(map);
    }

    /*
     * @Description: 用户退出功能
     * @Date:   2024/5/10 20:23
     * @Param:  [token]
     * @Return: com.zzuli.gaokao.common.Result
     */
    @PostMapping("/logout")
    public Result logout(){
        User user = hostHolder.getUser();
        if(user == null){
            return Result.error("参数错误！");
        }
        String userTokenKey = RedisUtil.getUserTokenKey(user.getUsername());
        System.out.println(userTokenKey);
        redisTemplate.delete(userTokenKey);
        return Result.success("退出成功！");
    }


    
    /*
     * @Description: 根据用户token来获取用户信息
     * @Date:   2024/5/10 20:16
     * @Param:  [token]
     * @Return: com.zzuli.gaokao.common.Result
     */
    @GetMapping("/userInfo")
    public Result userInfo(String token) {

        String subject = JwtUtil.getSubject(token);
        if(subject == null){
            log.warn("找不到该用户的信息");
            return Result.error("找不到该用户的信息");
        }
        User one = userService
                .getOne(new QueryWrapper<User>()
                        .select("username,nickname,roles,header_url,email,province_id,description,profile,id")
                        .eq("username",subject));
        HashMap<String, Object> userVo = new HashMap<>();
        String [] roles = {one.getRoles()};
        userVo.put("username",one.getUsername());
        userVo.put("nickname",one.getNickname());
        userVo.put("roles",roles);
        userVo.put("headerUrl",one.getHeaderUrl());
        userVo.put("email",one.getEmail());
        userVo.put("provinceId",one.getProvinceId());
        userVo.put("description",one.getDescription());
        userVo.put("id",one.getId());
        if(StringUtils.isNotBlank(one.getProfile())){
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                ArrayList arrayList = objectMapper.readValue(one.getProfile(), ArrayList.class);
                userVo.put("profile",arrayList);
            } catch (JsonProcessingException e) {
                log.warn("用户画像解析失败！{}",e.getMessage());
            }
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("user",userVo);
        return Result.success(map);
    }



    /*
     * @Description: 用户端生成验证码
     * @Date:   2024/5/10 20:16
     * @Param:  []
     * @Return: com.zzuli.gaokao.common.Result
     */
    @GetMapping("/captcha")
    public Result captcha()   {
        SpecCaptcha specCaptcha = new SpecCaptcha(100,40,4);
        String code = specCaptcha.text();
        String key = CommonUtils.generateUUID();
        String userCaptchaKey = RedisUtil.getUserCaptchaKey(key);
        redisTemplate.opsForValue().set(userCaptchaKey,code,CommonUtils.CAPTCHA_EXPIRED,TimeUnit.MINUTES);
        String base64 = specCaptcha.toBase64();
        HashMap<String, Object> map = new HashMap<>();
        map.put("id",key);
        map.put("url",base64);
        return Result.success(map);
    }


}
