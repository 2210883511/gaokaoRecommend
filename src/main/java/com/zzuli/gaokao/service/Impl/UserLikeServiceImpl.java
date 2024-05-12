package com.zzuli.gaokao.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzuli.gaokao.bean.UserLike;
import com.zzuli.gaokao.common.Result;
import com.zzuli.gaokao.mapper.UserLikeMapper;
import com.zzuli.gaokao.service.UserLikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;

@Service
public class UserLikeServiceImpl extends ServiceImpl<UserLikeMapper, UserLike> implements UserLikeService {


    @Autowired
    private UserLikeMapper likeMapper;

    /*
     * @Description: 实现用户的点赞功能
     * @Date:   2024/5/12 0:57
     * @Param:  [userId, entityType, entityId, recommend]
     * @Return: boolean
     */
    @Override
    public Result like(Integer userId, Integer entityType, Integer entityId, Integer recommend) {

        UserLike one = likeMapper.selectOne(new QueryWrapper<UserLike>()
                .eq("user_id", userId)
                .eq("entity_type", entityType)
                .eq("entity_id", entityId));
        HashMap<String, Object> map = new HashMap<>();
        if(one == null && recommend == 1){
            UserLike userLike = new UserLike(null,userId,entityType,entityId,recommend,new Date().getTime());
            likeMapper.insert(userLike);
            map.put("likeState",1);
            map.put("dislikeState",2);
            return Result.success("推荐成功",map);
        }else if(one == null && recommend == 2){
            UserLike userLike = new UserLike(null,userId,entityType,entityId,recommend,new Date().getTime());
            likeMapper.insert(userLike);
            map.put("likeState",2);
            map.put("dislikeState",1);
            return Result.success("不推荐成功",map);
        }else if(one != null && one.getRecommend().equals(recommend) && recommend == 1){
            likeMapper.delete(new QueryWrapper<>(one));
            map.put("likeState",2);
            map.put("dislikeState",2);
            return Result.success("取消推荐",map);
        }else if(one != null && one.getRecommend().equals(recommend) && recommend == 2){
            likeMapper.delete(new QueryWrapper<>(one));
            map.put("likeState",2);
            map.put("dislikeState",2);
            return Result.success("取消不推荐",map);
        }else if(one != null &&  !one.getRecommend().equals(recommend) &&  recommend == 2 ){
            likeMapper.update(null,new UpdateWrapper<UserLike>()
                    .set("recommend",recommend)
                    .eq("user_id",userId)
                    .eq("entity_type",entityType)
                    .eq("entity_id",entityId));
            map.put("likeState",2);
            map.put("dislikeState",1);
            return Result.success("不推荐成功",map);
        }else {
            likeMapper.update(null,new UpdateWrapper<UserLike>()
                    .set("recommend",recommend)
                    .eq("user_id",userId)
                    .eq("entity_type",entityType)
                    .eq("entity_id",entityId));
            map.put("likeState",1);
            map.put("dislikeState",2);
            return Result.success("推荐成功",map);
        }
    }

    @Override
    public Result likeState(Integer userId, Integer entityType, Integer entityId) {

        UserLike one = likeMapper.selectOne(new QueryWrapper<UserLike>()
                .eq("user_id", userId)
                .eq("entity_type", entityType)
                .eq("entity_id", entityId));
        HashMap<String, Object> map = new HashMap<>();
        if(one == null){

            map.put("likeState",2);
            map.put("dislikeState",2);
            return Result.success(map);
        }else if(one.getRecommend() == 1){
            map.put("likeState",1);
            map.put("dislikeState",2);
            return Result.success(map);
        }else {
            map.put("likeState",2);
            map.put("dislikeState",1);
            return Result.success(map);
        }
    }


}
