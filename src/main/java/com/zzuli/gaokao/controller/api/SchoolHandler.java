package com.zzuli.gaokao.controller.api;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zzuli.gaokao.Utils.HostHolder;
import com.zzuli.gaokao.annotation.LoginRequired;
import com.zzuli.gaokao.annotation.MaxLimit;
import com.zzuli.gaokao.bean.*;
import com.zzuli.gaokao.common.Result;
import com.zzuli.gaokao.service.*;
import com.zzuli.gaokao.service.Impl.UniversityServiceImpl;
import com.zzuli.gaokao.service.Impl.UniversityTagsServiceImpl;
import com.zzuli.gaokao.vo.UniversityDetailVo;
import com.zzuli.gaokao.vo.UniversityVo;
import com.zzuli.gaokao.vo.UserHistoryVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api")
public class SchoolHandler {

    @Autowired
    private UniversityServiceImpl universityService;

    @Autowired
    private UniversityTagsServiceImpl universityTagsService;

    @Autowired
    private ProvincesService provincesService;

    @Autowired
    private UniversityInfoService infoService;

    @Autowired
    private UniversityTagsService tagsService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserActionService actionService;

    @Autowired
    private UserFavoriteService favoriteService;

    @Autowired
    private UserLikeService likeService;






    /*
     * @Description: 获取高校列表 可以根据 985 || 211 || 双一流进行查询 或者 高校类型 军事类 || 农林类等
     * @Date:   2024/5/1 16:59
     * @Param:  [page, size, f985, f211, dualClassName, typeName]
     * @Return: com.zzuli.gaokao.common.Result
     */
    @MaxLimit
    @GetMapping("/schoolList")
    public Result getSchoolList(Integer page, Integer size,Integer f985,Integer f211,String dualClassName,String typeName,
    String schoolName,Integer provinceId){
        if(page == null || size == null){
            return Result.error("参数错误！page或size为空!");
        }
        Page<UniversityVo> voPage = new Page<>(page,size);
        Page<UniversityVo> universityVoPage = universityService.selectCustom(voPage, f985, f211, dualClassName, typeName, schoolName, provinceId);
        List<UniversityVo> list = universityVoPage.getRecords();
        long total = universityVoPage.getTotal();
        HashMap<String, Object> map = new HashMap<>();
        map.put("list",list);
        map.put("total",total);
        return  Result.success(map);
    }



    @GetMapping("/schoolDetail")
    @Transactional
    public Result getSchoolDetail(Integer schoolId){
        if(schoolId == null){
            return Result.error("参数错误！");
        }
        User user = hostHolder.getUser();
        // 表明用户登录了，将访问记录存到mysql中
        if(user != null){
            UserAction one = actionService.getOne(new QueryWrapper<UserAction>()
                    .eq("user_id", user.getId())
                    .eq("entity_type", 1)
                    .eq("entity_id", schoolId));
            if(one != null){
               actionService.update(new UpdateWrapper<UserAction>()
                       .set("create_time",new Date().getTime())
                       .eq("user_id",user.getId())
                       .eq("entity_type",1)
                       .eq("entity_id",schoolId));
            }else {
                UserAction userAction = new UserAction();
                userAction.setUserId(user.getId());
                userAction.setEntityType(1);
                userAction.setEntityId(schoolId);
                userAction.setCreateTime(new Date().getTime());
                actionService.save(userAction);
            }

        }
        UniversityDetailVo vo = new UniversityDetailVo();
        University university = universityService.getOne(new QueryWrapper<University>()
                .eq("school_id", schoolId));
        UniversityTags tags = universityTagsService.getOne(new QueryWrapper<UniversityTags>()
                .eq("school_id", schoolId));
        UniversityInfo info = infoService.getOne(new QueryWrapper<UniversityInfo>()
                .eq("school_id", schoolId));
        if(university != null){
            vo.setUniversity(university);
            Provinces provinces = provincesService.getById(university.getProvinceId());
            if(provinces != null){
                vo.setProvinceName(provinces.getName());
            }
        }
        if (info != null){
            vo.setInfo(info);
        }
        if(tags != null){
            vo.setTag(tags);
        }

        HashMap<String, Object> map = new HashMap<>();
        map.put("vo",vo);
        return Result.success(map);
    }


    @LoginRequired
    @GetMapping("/userHistory")
    public Result getUserHistory(){
        User user = hostHolder.getUser();
        Integer userId = user.getId();
        if(userId == null){
            return Result.error("用户id参数错误！");
        }
        // 查询用户最近10条的浏览记录 倒叙排列
        Page<UserAction> page = actionService.page(new Page<UserAction>(1, 10), new QueryWrapper<UserAction>()
                .eq("user_id", userId)
                .orderByDesc("create_time"));
        List<UserAction> actionList = page.getRecords();
        List<Integer> ids = actionList.stream()
                .map(UserAction::getEntityId)
                .collect(Collectors.toList());
        List<University> universityList = null;
        ArrayList<UserHistoryVo> voList = new ArrayList<>();
        if(!ids.isEmpty()){
            universityList = universityService.list(new QueryWrapper<University>()
                    .select("school_name", "header_url", "school_id")
                    .in("school_id", ids));
            for (UserAction userAction : actionList) {
                for (University university : universityList) {
                    if(userAction.getEntityId().equals(university.getSchoolId())){
                        UserHistoryVo vo = new UserHistoryVo();
                        vo.setUserId(userId);
                        vo.setSchoolId(university.getSchoolId());
                        vo.setHeaderUrl(university.getHeaderUrl());
                        vo.setCreateTime(new Date(userAction.getCreateTime()));
                        vo.setSchoolName(university.getSchoolName());
                        voList.add(vo);
                        break;
                    }
                }
            }
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("historyList",voList);
        return Result.success(map);
    }

    @LoginRequired
    @GetMapping("/getFavoriteList")
    public Result getSchoolList(Integer page,Integer size){
        User user = hostHolder.getUser();
        Integer userId = user.getId();
        if (page == null || size == null) {
            return Result.error("分页参数错误！");
        }
        Page<UserFavorite> favoritePage = new Page<>(page,size);
        Page<UserFavorite> userFavoritePage = favoriteService.page(favoritePage, new QueryWrapper<UserFavorite>()
                .eq("user_id", userId)
                .eq("entity_type", 1));
        long total = userFavoritePage.getTotal();
        List<UserFavorite> list = userFavoritePage.getRecords();

        List<Integer> schoolIds = list.stream()
                .map(UserFavorite::getEntityId)
                .distinct()
                .collect(Collectors.toList());
        ArrayList<UniversityVo> universityVos = new ArrayList<>();
        if(!schoolIds.isEmpty()){
            List<University> universityList = universityService.list(new QueryWrapper<University>()
                    .in("school_id", schoolIds));
            List<UniversityTags> tagsList = tagsService.list(new QueryWrapper<UniversityTags>()
                    .in("school_id", schoolIds));

            for (University university : universityList) {
                for (UniversityTags tags : tagsList) {
                    if(university.getSchoolId().equals(tags.getSchoolId())){
                        UniversityVo vo = new UniversityVo();
                        vo.setUniversity(university);
                        vo.setTag(tags);
                        universityVos.add(vo);
                        break;
                    }
                }
            }

        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("universityVoList",universityVos);
        map.put("total",total);
        return Result.success(map);
    }



    @LoginRequired
    @GetMapping("/getLikeList")
    public Result getLikeSchoolList(Integer page,Integer size){
        User user = hostHolder.getUser();
        Integer userId = user.getId();
        if (page == null || size == null) {
            return Result.error("分页参数错误！");
        }
        Page<UserLike> likePage = new Page<>(page,size);
        Page<UserLike> userLikePage = likeService.page(likePage, new QueryWrapper<UserLike>()
                .eq("user_id", userId)
                .eq("entity_type", 1)
                .orderByDesc("create_time"));
        long total = userLikePage.getTotal();
        List<UserLike> list = userLikePage.getRecords();
        List<Integer> schoolIds = list.stream()
                .map(UserLike::getEntityId)
                .distinct()
                .collect(Collectors.toList());
        ArrayList<UniversityVo> universityVos = new ArrayList<>();
        if(!schoolIds.isEmpty()){
            List<University> universityList = universityService.list(new QueryWrapper<University>()
                    .in("school_id", schoolIds));
            List<UniversityTags> tagsList = tagsService.list(new QueryWrapper<UniversityTags>()
                    .in("school_id", schoolIds));
            for (University university : universityList) {
                for (UniversityTags tags : tagsList) {
                    if(university.getSchoolId().equals(tags.getSchoolId())){
                        UniversityVo vo = new UniversityVo();
                        vo.setUniversity(university);
                        vo.setTag(tags);
                        universityVos.add(vo);
                        break;
                    }
                }
            }

        }
        for (UniversityVo universityVo : universityVos) {
            for (UserLike userLike : list) {
                if(universityVo.getSchoolId().equals(userLike.getEntityId())){
                    universityVo.setRecommend(userLike.getRecommend());
                    break;
                }
            }
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("universityVoList",universityVos);
        map.put("total",total);
        return Result.success(map);
    }





}
