package com.zzuli.gaokao.controller.api;


import com.zzuli.gaokao.Utils.HostHolder;
import com.zzuli.gaokao.annotation.LoginRequired;
import com.zzuli.gaokao.bean.User;
import com.zzuli.gaokao.common.Result;
import com.zzuli.gaokao.service.UserFavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequestMapping("/api/favorite")
public class FavoriteHandler {

    @Autowired
    private UserFavoriteService favoriteService;

    @Autowired
    HostHolder hostHolder;


    @PostMapping("/setFavorite")
    @LoginRequired
    public Result favorite(Integer entityType,Integer entityId){
        User user = hostHolder.getUser();
        Integer userId = user.getId();
        if (userId == null || entityType == null || entityId == null) {
            return Result.error("收藏参数错误！");
        }
        boolean favorite = favoriteService.favorite(userId, entityType, entityId);
        HashMap<String, Object> map = new HashMap<>();
        if(favorite){
            map.put("favoriteState",1);
            return Result.success("收藏成功",map);
        }else {
            map.put("favoriteState",2);
            return Result.success("取消收藏",map);
        }
    }



    @GetMapping("/getFavorite")
    @LoginRequired
    public Result getFavoriteState(Integer entityType,Integer entityId){
        User user = hostHolder.getUser();
        Integer favoriteState = favoriteService.getFavoriteState(user.getId(), entityType, entityId);
        HashMap<String, Object> map = new HashMap<>();
        if(favoriteState == 1){
            map.put("favoriteState",1);
            return Result.success(map);
        }
        else{
            map.put("favoriteState",2);
            return Result.success(map);
        }
    }

}
