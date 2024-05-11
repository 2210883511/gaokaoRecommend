package com.zzuli.gaokao.Utils;


import com.zzuli.gaokao.bean.User;
import org.springframework.stereotype.Component;

@Component
public class HostHolder {
        
    /*
     * @Description: 
     * @Date:   2024/5/10 21:08
     * @Param:  
     * @Return: 持有用户信息，用于代替session对象
     */
    ThreadLocal<User> users = new ThreadLocal<User>();
    public void setUser(User user){
        users.set(user);
    }
    public User getUser(){
        return users.get();
    }
    public void clear(){
        users.remove();
    }
}
