package com.zzuli.gaokao.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzuli.gaokao.Utils.RedisUtil;
import com.zzuli.gaokao.bean.User;
import com.zzuli.gaokao.common.Result;
import com.zzuli.gaokao.mapper.UserMapper;
import com.zzuli.gaokao.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;


@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {


    @Autowired
    private UserMapper mapper;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Override
    public boolean login(String username, String password) {
        User user = mapper.selectOne(new QueryWrapper<User>().select("username,password,salt").eq("username", username));
        if(user !=  null){
            password = DigestUtils.md5DigestAsHex((password + user.getSalt()).getBytes(StandardCharsets.UTF_8));
            return password.equals(user.getPassword());
        }
        return false;
    }

    @Override
    public boolean addUser(User user) {
        int insert = mapper.insert(user);
        return insert > 0;
    }

}
