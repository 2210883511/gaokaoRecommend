package com.zzuli.gaokao.service.Impl;

import com.zzuli.gaokao.Utils.RedisUtil;
import com.zzuli.gaokao.bean.CFRecommend;
import lombok.extern.slf4j.Slf4j;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.recommender.svd.ALSWRFactorizer;
import org.apache.mahout.cf.taste.impl.recommender.svd.SVDRecommender;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
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

    public boolean updateList(Integer userId){
        log.info("更新用户:{}的推荐列表",userId);
        SVDRecommender recommender = cfRecommend.getRecommender();
        List<RecommendedItem> recommendedItems = null;
        try {
            recommendedItems = recommender.recommend(userId, 20);

            // 把推荐id从recommendedItems里拿出来
            List<Object> universityIds = recommendedItems.stream()
                    .map((recommendedItem -> (Object) (recommendedItem.getItemID())))
                    .collect(Collectors.toList());
            // 把推荐列表存入redis
            String cfRecommendKey = RedisUtil.getCfRecommendKey(userId);
            ListOperations<String, Object> listOperations = redisTemplate.opsForList();
            redisTemplate.delete(cfRecommendKey);
            listOperations.rightPushAll(cfRecommendKey,universityIds);
        } catch (Exception e) {
            log.error("获取推荐失败！{},该用户可能不存在！",e.getMessage());
            return false;
        }

        return true;
    }


    public boolean updateModel(){
        File file = null;
        FileDataModel model = null;
        ALSWRFactorizer factorizer = null;
        SVDRecommender recommender = null;
        try {
            file = new File("/cf.txt");
            model = new FileDataModel(file);
            factorizer = new ALSWRFactorizer(model,5,0.001,100);
            recommender = new SVDRecommender(model, factorizer);
            cfRecommend.setModel(model);
            cfRecommend.setRecommender(recommender);
        } catch (IOException e) {
            log.error("协同过滤数据集加载失败！{}",e.getMessage());
        } catch (TasteException e) {
            log.error("迭代失败！{}",e.getMessage());
        }
        log.info("协同过滤模型更新成功！");
        return true;
    }
}
