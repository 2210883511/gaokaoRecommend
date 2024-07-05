package com.zzuli.gaokao.task;


import com.zzuli.gaokao.bean.UserFavorite;
import com.zzuli.gaokao.bean.UserLike;
import com.zzuli.gaokao.service.Impl.CFRecommendServiceImpl;
import com.zzuli.gaokao.service.UserFavoriteService;
import com.zzuli.gaokao.service.UserLikeService;
import com.zzuli.gaokao.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

@Component
@Slf4j
public class MyTask {


    @Autowired
    private UserLikeService likeService;

    @Autowired
    private UserFavoriteService favoriteService;


    @Autowired
    private UserService userService;

    @Autowired
    private CFRecommendServiceImpl cfRecommendService;


    @Value("${gaokao.path.cf-path}")
    String cfPath;


    // 一个小时更新一次
    @Scheduled(fixedDelay = 1000 * 60 * 60)
    public void updateCFModel(){

        List<UserLike> likeList = likeService.list();
        List<UserFavorite> favoriteList = favoriteService.list();
        HashSet<Integer> userIds = new HashSet<>();
        for (UserLike userLike : likeList) {
            userIds.add(userLike.getUserId());
        }
        for (UserFavorite userFavorite : favoriteList) {
            userIds.add(userFavorite.getUserId());
        }
        // 得到去重的用户id
        HashMap<Integer, ArrayList<Map<Integer,Integer>>> currentUserMap = new HashMap<>();
        for (Integer userId : userIds) {
            ArrayList<Map<Integer, Integer>> list = new ArrayList<>();
            for (UserFavorite userFavorite : favoriteList) {
                if(userId.equals(userFavorite.getUserId())){
                    HashMap<Integer, Integer> map = new HashMap<>();
                    map.put(userFavorite.getEntityId(),5);
                    list.add(map);
                }
            }
            currentUserMap.put(userId,list);
        }
        for (Integer userId : userIds) {
            for (UserLike userLike : likeList) {
                // 如果
                if(userId.equals(userLike.getUserId())){
                    ArrayList<Map<Integer, Integer>> list = currentUserMap.get(userId);
                    for (Map<Integer, Integer> schoolMap : list) {
                        // 如果当前map中存在这个key --> 高校id 更新value
                        if(schoolMap.containsKey(userLike.getEntityId())){
                            Integer key = userLike.getEntityId();
                            Integer value = schoolMap.get(key);
                            // 如果是推荐 value + 5
                            if(userLike.getRecommend()==1){
                                value += 5;
                                schoolMap.put(key,value);
                            }
                            // 如果是不推荐 value - 5
                            else {
                                schoolMap.put(key,value);
                            }
                            break;
                        }
                    }
                }
            }
        }
        try( BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(cfPath));) {
            for (Integer userId : currentUserMap.keySet()) {
                ArrayList<Map<Integer, Integer>> list = currentUserMap.get(userId);
                for (Map<Integer, Integer> map : list) {
                    for (Integer schoolId : map.keySet()) {
                        Integer value = map.get(schoolId);
                        bufferedWriter.write(userId + "," + schoolId + "," + value + "\n");
                        bufferedWriter.flush();
                    }
                }
            }
            if(cfRecommendService.updateModel()){
                for (Integer userId : userIds) {
                    cfRecommendService.updateList(userId);
                }
            }
        } catch (IOException e) {
            log.warn("协同过滤数据写入失败！{}",e.getMessage());
        }

    }



}
