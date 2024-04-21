package com.zzuli.gaokao.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zzuli.gaokao.Utils.JwtUtil;
import com.zzuli.gaokao.bean.User;
import com.zzuli.gaokao.common.Result;
import com.zzuli.gaokao.service.Impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
public class TestController {

    @Autowired
    private UserServiceImpl userService;

    @PostMapping("/login")
    public Result test(String username,String password){
        boolean login = userService.login(username, password);
        if(login){
            String token = JwtUtil.getToken(username);
            HashMap<String, Object> map = new HashMap<>();
            map.put("token",token);
            return Result.success(map);
        }else {
            return Result.error("用户名或者密码不正确!");
        }
    }

//    @GetMapping("/userInfo")
//    public Result userInfo(String token){
//
//        String subject = JwtUtil.getSubject(token);
//        User one = userService
//                .getOne(new QueryWrapper<User>()
//                        .select("username,nickname,roles,header_url")
//                        .eq("username",subject));
//
//        HashMap<String, Object> map = new HashMap<>();
//        String [] roles = {one.getRoles()};
//        map.put("username",one.getUsername());
//        map.put("nickname",one.getNickname());
//        map.put("roles",roles);
//        map.put("header_url",one.getHeaderUrl());
//        return Result.success(map);
//    }

}
