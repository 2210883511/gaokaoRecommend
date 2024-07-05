package com.zzuli.gaokao.controller.api;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hankcs.hanlp.mining.word2vec.DocVectorModel;
import com.zzuli.gaokao.Utils.HostHolder;
import com.zzuli.gaokao.annotation.LoginRequired;
import com.zzuli.gaokao.bean.*;
import com.zzuli.gaokao.common.Result;
import com.zzuli.gaokao.service.*;
import com.zzuli.gaokao.vo.TfIdfVo;
import com.zzuli.gaokao.vo.UniversityVo;
import com.zzuli.gaokao.vo.UserHistoryVo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/content")
public class ContentRecommendHandler {

    @Autowired
    private DocVectorModel model;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserActionService userActionService;


    @Autowired
    private UniversityService universityService;

    @Autowired
    private UniversityTagsService tagsService;

    @Autowired
    private UserService userService;

    @Autowired
    private ProvincesService provincesService;


    @GetMapping("/getStyleRecommendList")
    @LoginRequired
    public Result getStyleRecommendList() {
        User user = hostHolder.getUser();
        Integer userId = user.getId();
        if (userId == null) {
            return Result.error("用户参数错误！");
        }
        // 查询用户最近5条的浏览记录 倒叙排列
        Page<UserAction> page = userActionService.page(new Page<UserAction>(1, 5), new QueryWrapper<UserAction>()
                .eq("user_id", userId)
                .orderByDesc("create_time"));
        List<UserAction> actionList = page.getRecords();
        List<Integer> ids = actionList.stream()
                .map(UserAction::getEntityId)
                .collect(Collectors.toList());
        List<University> universityList = null;
        ArrayList<UserHistoryVo> voList = new ArrayList<>();
        ArrayList<UniversityVo> recommendList = new ArrayList<>();

        // 获取最近10条访问过的学校
        if (!ids.isEmpty()) {
            universityList = universityService.list(new QueryWrapper<University>()
                    .select("school_name", "school_id")
                    .in("school_id", ids));
            // 构造用户历史访问记录对象
            for (UserAction userAction : actionList) {
                for (University university : universityList) {
                    if (userAction.getEntityId().equals(university.getSchoolId())) {
                        UserHistoryVo vo = new UserHistoryVo();
                        vo.setUserId(userId);
                        vo.setSchoolId(university.getSchoolId());
                        vo.setCreateTime(new Date(userAction.getCreateTime()));
                        vo.setSchoolName(university.getSchoolName());
                        voList.add(vo);
                        break;
                    }
                }
            }
            // 根据用户历史访问记录来进行推荐
            for (UserHistoryVo vo : voList) {
                List<Map.Entry<Integer, Float>> nearest = model.nearest(vo.getSchoolId(), 2);
                for (Map.Entry<Integer, Float> entry : nearest) {
                    UniversityVo tmp = new UniversityVo();
                    // 获取被推荐的高校id和推荐的高校名称
                    tmp.setSchoolId(entry.getKey());
                    tmp.setRecommendBy(vo.getSchoolName());
                    recommendList.add(tmp);
                }
            }


        }
        User one = userService.getById(userId);
        String profile = one.getProfile();
        if(profile != null){
            ObjectMapper objectMapper = new ObjectMapper();
            ArrayList<TfIdfVo> tfIdfVos = null;
            try {
                tfIdfVos = objectMapper.readValue(profile, new TypeReference<ArrayList<TfIdfVo>>() {});
                for (TfIdfVo tfIdfVo : tfIdfVos) {
                    List<Map.Entry<Integer, Float>> nearest = model.nearest(tfIdfVo.getName(), 5);
                    for (Map.Entry<Integer, Float> entry : nearest) {
                        UniversityVo  vo = new UniversityVo();
                        vo.setSchoolId(entry.getKey());
                        vo.setRecommendBy(tfIdfVo.getName());
                        recommendList.add(vo);
                    }
                }

            } catch (JsonProcessingException e) {
                log.warn("用户画像解析失败");
            }
        }
        if (ids.isEmpty() && profile == null) {
            Provinces provinces = provincesService.getById(one.getProvinceId());
            if(provinces != null){
                String name = provinces.getName();
                List<Map.Entry<Integer, Float>> nearest = model.nearest(name, 20);
                for (Map.Entry<Integer, Float> entry : nearest) {
                    UniversityVo vo = new UniversityVo();
                    vo.setSchoolId(entry.getKey());
                    vo.setRecommendBy(name);
                    recommendList.add(vo);
                }
            }
        }
        List<Integer> schoolIds = recommendList.stream()
                .map(UniversityVo::getSchoolId)
                .collect(Collectors.toList());
        //构造填充vo对象
        if(!schoolIds.isEmpty()){
            List<University> list = universityService.list(new QueryWrapper<University>()
                    .in("school_id", schoolIds));
            List<UniversityTags> tagsList = tagsService.list(new QueryWrapper<UniversityTags>()
                    .in("school_id", schoolIds));;
            for (UniversityVo vo : recommendList) {
                for (University university : list) {
                    if(vo.getSchoolId().equals(university.getSchoolId())){
                        vo.setUniversity(university);
                    }
                }
            }
            // 不能break;
            for (UniversityVo vo : recommendList) {
                for (UniversityTags tags : tagsList) {
                    if(vo.getSchoolId().equals(tags.getSchoolId())){
                        vo.setTag(tags);
                    }
                }
            }
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("recommendList",recommendList);
        return Result.success(map);
    }


}
