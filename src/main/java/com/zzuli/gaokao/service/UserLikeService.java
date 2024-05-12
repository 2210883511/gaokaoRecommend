package com.zzuli.gaokao.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zzuli.gaokao.bean.UserLike;
import com.zzuli.gaokao.common.Result;

public interface UserLikeService extends IService<UserLike> {


    /*
     * @Description: 用户点赞功能
     * @Date:   2024/5/11 14:18
     * @Param:  [userId, entityType, entityId, recommend]
     * @Return: boolean
     */
    Result like(Integer userId, Integer entityType, Integer entityId, Integer recommend);

    
    /*
     * @Description: 查询点赞状态
     * @Date:   2024/5/12 17:57
     * @Param:  [userId, entityType, entityId]
     * @Return: com.zzuli.gaokao.common.Result
     */
    Result likeState(Integer userId,Integer entityType,Integer entityId);

}
