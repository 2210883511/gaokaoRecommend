package com.zzuli.gaokao.controller.user;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zzuli.gaokao.bean.University;
import com.zzuli.gaokao.bean.UniversityTags;
import com.zzuli.gaokao.common.Result;
import com.zzuli.gaokao.service.Impl.UniversityServiceImpl;
import com.zzuli.gaokao.service.Impl.UniversityTagsServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class SchoolController {

    @Autowired
    private UniversityServiceImpl universityService;

    @Autowired
    private UniversityTagsServiceImpl universityTagsService;

    @GetMapping("/test")
    public Result test(Integer page, Integer size){
        Long pre = System.currentTimeMillis();
        Page<University> pageUniversity = new Page<>(page,size);
        Page<UniversityTags> pageUvTags = new Page<>(page,size);
        Page<University> universityPage = universityService.page(pageUniversity,new QueryWrapper<University>()
                .select("school_id","school_name,header_url,city_name,town_name"));
        Page<UniversityTags> tagsPage = universityTagsService.page(pageUvTags);
        long total = universityPage.getTotal();
        List<University> uvList = universityPage.getRecords();
        List<UniversityTags> uvTagsList = tagsPage.getRecords();
        ArrayList<Map<String, Object>> array = new ArrayList<>();
        for (University university : uvList) {
            HashMap<String, Object> map = new HashMap<>();
            for (UniversityTags tags : uvTagsList) {
                if (university.getSchoolId().equals(tags.getSchoolId())){
                    map.put("schoolId",university.getSchoolId());
                    map.put("schoolName",university.getSchoolName());
                    map.put("headerUrl",university.getHeaderUrl());
                    map.put("cityName",university.getCityName());
                    map.put("townName",university.getTownName());
                    map.put("f_985",tags.getF985());
                    map.put("f_211",tags.getF211());
                    map.put("schoolTypeName",tags.getSchoolTypeName());
                    map.put("schoolNatureName",tags.getSchoolNatureName());
                    map.put("typeName",tags.getTypeName());
                    map.put("dualClassName",tags.getDualClassName());
                    array.add(map);
                    break;
                }
            }

        }
        Long end = System.currentTimeMillis();
        log.warn(end-pre + " ");
        HashMap<String, Object> data = new HashMap<>();
        data.put("list",array);
        data.put("total",total);
        return  Result.success(data);
    }

    @GetMapping("/test2")
    public Result test2(Integer page,Integer size){
        Page<Map<String, Object>> pagination = new Page<>(page,size);
        Long start = System.currentTimeMillis();
        List<Map<String, Object>> universityInfo = universityService.findUniversityInfo(pagination);
        Long end = System.currentTimeMillis();
        log.warn(end-start + " ");
        return Result.success(universityInfo);
    }
    @GetMapping("/test3")
    public Result test3(Integer page,Integer size){
        System.out.println(page);
        System.out.println(size);
        Page<Map<String, Object>> pagination = new Page<>(page,size);
        Long start = System.currentTimeMillis();
        List<Map<String, Object>> universityInfo = universityService.find(pagination);
        Long end = System.currentTimeMillis();
        log.warn(end-start + " ");
        return Result.success(universityInfo);
    }
}
