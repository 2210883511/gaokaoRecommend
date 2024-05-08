package com.zzuli.gaokao.controller.api;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zzuli.gaokao.bean.UniversityImg;
import com.zzuli.gaokao.common.Result;
import com.zzuli.gaokao.service.Impl.UniversityImgServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/api/landscape")
public class ImageHandler {


    @Autowired
    private UniversityImgServiceImpl imgService;

    @GetMapping("/imageList")
    public Result getLandscapes(Integer schoolId){
        if(schoolId == null){
            return Result.error("高校id不能为空！");
        }
        List<UniversityImg> imgList = imgService.list(new QueryWrapper<UniversityImg>()
                .select("id", "url")
                .eq("school_id", schoolId)
                .orderByDesc("rank"));

        HashMap<String, Object> map = new HashMap<>();
        map.put("images",imgList);
        return Result.success(map);
    }


}
