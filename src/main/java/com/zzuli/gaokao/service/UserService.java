package com.zzuli.gaokao.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zzuli.gaokao.bean.User;
import com.zzuli.gaokao.common.Result;

public interface UserService extends IService<User> {

    /*
     * @Description: 用户登录功能
     * @Date:   2024/3/24 18:59
     * @Param:  [username, password]
     * @Return: boolean
     */
    boolean login(String username,String password);


    /*
     * @Description: 添加用户
     * @Date:   2024/3/30 17:08
     * @Param:  [user]
     * @Return: boolean
     */
    boolean addUser(User user);








}
