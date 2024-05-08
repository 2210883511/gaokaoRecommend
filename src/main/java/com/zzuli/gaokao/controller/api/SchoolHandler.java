package com.zzuli.gaokao.controller.api;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zzuli.gaokao.bean.Provinces;
import com.zzuli.gaokao.bean.University;
import com.zzuli.gaokao.bean.UniversityInfo;
import com.zzuli.gaokao.bean.UniversityTags;
import com.zzuli.gaokao.common.Result;
import com.zzuli.gaokao.service.Impl.UniversityServiceImpl;
import com.zzuli.gaokao.service.Impl.UniversityTagsServiceImpl;
import com.zzuli.gaokao.service.ProvincesService;
import com.zzuli.gaokao.service.UniversityInfoService;
import com.zzuli.gaokao.vo.UniversityDetailVo;
import com.zzuli.gaokao.vo.UniversityVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
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


    /*
     * @Description: 获取高校列表 可以根据 985 || 211 || 双一流进行查询 或者 高校类型 军事类 || 农林类等
     * @Date:   2024/5/1 16:59
     * @Param:  [page, size, f985, f211, dualClassName, typeName]
     * @Return: com.zzuli.gaokao.common.Result
     */
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
    public Result getSchoolDetail(Integer schoolId){
        if(schoolId == null){
            return Result.error("参数错误！");
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





}
