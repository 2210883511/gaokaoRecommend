package com.zzuli.gaokao.controller.admin;


import com.zzuli.gaokao.common.Result;
import com.zzuli.gaokao.service.Impl.CFRecommendServiceImpl;
import com.zzuli.gaokao.service.UniversityService;
import com.zzuli.gaokao.vo.UniversityVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/admin/cfRecommend")
public class CFRecommendController {

    @Autowired
    private CFRecommendServiceImpl cfRecommendService;

    @Autowired
    private UniversityService universityService;


    @GetMapping("/recommend")
    public Result getRecommendByUserId(Integer userId){
        if(userId == null){
            return Result.error("用户id不能为空！");

        }
        List<Object> recommendIds = cfRecommendService.getRecommendByUserId(userId);
        List<UniversityVo> voList = universityService.getUniversityVoListById(recommendIds);
        HashMap<String, Object> map = new HashMap<>();
        map.put("recommendList",voList);
        return Result.success(map);
    }



}
