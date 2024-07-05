package com.zzuli.gaokao.controller.api;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zzuli.gaokao.Utils.HostHolder;
import com.zzuli.gaokao.annotation.LoginRequired;
import com.zzuli.gaokao.bean.User;
import com.zzuli.gaokao.bean.UserLike;
import com.zzuli.gaokao.common.Result;
import com.zzuli.gaokao.service.UserLikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;



@RestController
@RequestMapping("/api/like")
public class LikeHandler {

    @Autowired
    private UserLikeService likeService;

    @Autowired
    private HostHolder hostHolder;


    @PostMapping("/setLike")
    @LoginRequired
    public Result like(Integer entityType,Integer entityId,Integer recommend){

        User user = hostHolder.getUser();
        Integer userId = user.getId();
        if(userId == null || entityType == null || entityId == null || recommend == null){
            return Result.error("点赞参数错误！");
        }
        return likeService.like(userId, entityType, entityId, recommend);
    }
    @GetMapping("/getLike")
    @LoginRequired
    public Result likeState(Integer entityType,Integer entityId){
        User user = hostHolder.getUser();
        Integer userId = user.getId();
        if(userId == null || entityType == null || entityId == null){
            return Result.error("点赞参数错误！");
        }
        return likeService.likeState(userId,entityType,entityId);
    }

    @GetMapping("/getStatistic")
    public Result getStatistic(Integer entityType,Integer entityId){

        if(entityType == null || entityId == null){
            return Result.success("统计数据参数错误！");
        }
        List<UserLike> likeList = likeService.list(new QueryWrapper<UserLike>()
                .select("recommend")
                .eq("entity_type", entityType)
                .eq("entity_id", entityId));
        int total = likeList.size();
        long likeTotal = 0;
        int percent = 0;
        if(total != 0){
            List<Integer> list = likeList.stream()
                    .map(UserLike::getRecommend)
                    .collect(Collectors.toList());
            likeTotal = list.stream()
                    .filter(recommend -> recommend == 1)
                    .count();
            percent = (int) (((likeTotal * 1.0) / total) * 100);
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("percent",percent);
        map.put("total",total);
        return Result.success(map);
    }

}
