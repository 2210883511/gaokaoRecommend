package com.zzuli.gaokao.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zzuli.gaokao.bean.UserFavorite;
import com.zzuli.gaokao.common.Result;


public interface UserFavoriteService extends IService<UserFavorite> {


    /*
     * @Description: 用户收藏功能
     * @Date:   2024/5/11 23:11
     * @Param:  [userId, entityType, entityId]
     * @Return: boolean
     */
    boolean favorite(Integer userId,Integer entityType,Integer entityId);
    
    /*
     * @Description: 查询收藏状态
     * @Date:   2024/5/12 0:04
     * @Param:  [userId, entityType, entityId]
     * @Return: com.zzuli.gaokao.common.Result
     */
    Integer getFavoriteState(Integer userId,Integer entityType,Integer entityId);

}
