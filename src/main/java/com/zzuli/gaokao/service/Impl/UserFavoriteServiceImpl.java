package com.zzuli.gaokao.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzuli.gaokao.bean.UserFavorite;
import com.zzuli.gaokao.mapper.UserFavoriteMapper;
import com.zzuli.gaokao.service.UserFavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class UserFavoriteServiceImpl extends ServiceImpl<UserFavoriteMapper, UserFavorite> implements UserFavoriteService {

    @Autowired
    UserFavoriteMapper favoriteMapper;
    
    /*
     * @Description: 实现用户收藏功能
     * @Date:   2024/5/11 23:15
     * @Param:  [userId, entityType, entityId]
     * @Return: boolean
     */
    @Override
    public boolean favorite(Integer userId, Integer entityType, Integer entityId) {

        UserFavorite one = favoriteMapper.selectOne(new QueryWrapper<UserFavorite>()
                .eq("user_id", userId)
                .eq("entity_type", entityType)
                .eq("entity_id", entityId));
        // one为null 实现收藏功能,否则实现取消收藏功能
        if(one == null){
            UserFavorite userFavorite = new UserFavorite();
            userFavorite.setUserId(userId);
            userFavorite.setEntityType(entityType);
            userFavorite.setEntityId(entityId);
            userFavorite.setCreateTime(new Date().getTime());
            favoriteMapper.insert(userFavorite);
            return true;
        }else{
            favoriteMapper.delete(new QueryWrapper<UserFavorite>()
                    .eq("user_id", userId)
                    .eq("entity_type", entityType)
                    .eq("entity_id", entityId));
            return false;
        }
    }

    @Override
    public Integer getFavoriteState(Integer userId, Integer entityType, Integer entityId) {
        UserFavorite one = favoriteMapper.selectOne(new QueryWrapper<UserFavorite>()
                .eq("user_id", userId)
                .eq("entity_type", entityType)
                .eq("entity_id", entityId));
        // one为null 实现收藏功能,否则实现取消收藏功能
        if(one != null){
            return 1;
        }else {
            return 2;
        }
    }
}
