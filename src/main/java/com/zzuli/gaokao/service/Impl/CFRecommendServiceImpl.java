package com.zzuli.gaokao.service.Impl;

import com.zzuli.gaokao.Utils.RedisUtil;
import com.zzuli.gaokao.bean.CFRecommend;
import lombok.extern.slf4j.Slf4j;
import org.apache.mahout.cf.taste.impl.recommender.svd.SVDRecommender;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CFRecommendServiceImpl {


    @Autowired
    private CFRecommend cfRecommend;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    
    /*
     * @Description: 从redis获取推荐列表，如果不存在就重新计算并存入redis
     * @Date:   2024/4/24 12:46
     * @Param:  [userId]
     * @Return: java.util.List<java.lang.Integer>
     */
    public List<Object> getRecommendByUserId(Integer userId){
        List<Object> universityIds = null;
        String cfRecommendKey = RedisUtil.getCfRecommendKey(userId);
        Boolean hasKay = redisTemplate.hasKey(cfRecommendKey);
        ListOperations<String, Object> listOperations = redisTemplate.opsForList();
        if(Boolean.TRUE.equals(hasKay)){
            log.info("redis存在推荐列表");
            // 获取推荐列表的长度
            Long size = listOperations.size(cfRecommendKey);
            universityIds = listOperations.range(cfRecommendKey, 0, size - 1);
            return universityIds;
        }else {
            log.info("redis不存在推荐列表");
            SVDRecommender recommender = cfRecommend.getRecommender();
            List<RecommendedItem> recommendedItems = null;
            try {
                recommendedItems = recommender.recommend(userId, 20);

                // 把推荐id从recommendedItems里拿出来
                universityIds = recommendedItems.stream()
                        .map((recommendedItem -> (Object) (recommendedItem.getItemID())))
                        .collect(Collectors.toList());
                // 把推荐列表存入redis
                listOperations.rightPushAll(cfRecommendKey,universityIds);
            } catch (Exception e) {
                log.error("获取推荐失败！{},该用户可能不存在！",e.getMessage());
            }
            return universityIds;
        }

    }

    public boolean updateAll(){


        return false;
    }
}
