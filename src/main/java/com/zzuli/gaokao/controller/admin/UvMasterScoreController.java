package com.zzuli.gaokao.controller.admin;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zzuli.gaokao.bean.Dic;
import com.zzuli.gaokao.bean.Provinces;
import com.zzuli.gaokao.bean.UniversityMasterScore;
import com.zzuli.gaokao.common.Result;
import com.zzuli.gaokao.service.DicService;
import com.zzuli.gaokao.service.ProvincesService;
import com.zzuli.gaokao.service.UniversityMasterScoreService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/uvmScore")
public class UvMasterScoreController {

    @Autowired
    private UniversityMasterScoreService service;

    @Autowired
    private DicService dicService;

    @Autowired
    private ProvincesService provincesService;


    /*
     * @Description: 获取类型 文科 || 理科 || 综合
     * @Date:   2024/4/20 22:48
     * @Param:  [schoolId, provinceId, year]
     * @Return: com.zzuli.gaokao.common.Result
     */
    @GetMapping("/getTypes")
    public Result getType(Integer schoolId,Integer provinceId,String year){

        if(schoolId == null || provinceId == null){
            return Result.error("参数错误！");
        }
        List<UniversityMasterScore> list = service.list(new QueryWrapper<UniversityMasterScore>()
                .select("distinct type")
                .eq("school_id", schoolId)
                .eq("province_id",provinceId)
                .eq("year",year));
        List<Integer> ids = list.stream()
                .map(UniversityMasterScore::getType)
                .collect(Collectors.toList());
        List<Dic> types = dicService.list(new QueryWrapper<Dic>()
                .in("id", ids));
        HashMap<String, Object> map = new HashMap<>();
        map.put("types",types);
        return Result.success(map);
    }
    /*
     * @Description: 获取批次 本科一批 || 本科二批
     * @Date:   2024/4/20 22:49
     * @Param:  [schoolId, provinceId, year]
     * @Return: com.zzuli.gaokao.common.Result
     */
    @GetMapping("/getBatch")
    public Result getBatch(Integer schoolId,Integer provinceId,String year){

        if(schoolId == null || provinceId == null || StringUtils.isBlank(year)){
            return Result.error("参数错误！");
        }
        List<UniversityMasterScore> list = service.list(new QueryWrapper<UniversityMasterScore>()
                .select("distinct local_batch_name")
                .eq("school_id", schoolId)
                .eq("province_id", provinceId)
                .eq("year",year));
        List<String> strings = list.stream()
                .map(UniversityMasterScore::getLocalBatchName)
                .collect(Collectors.toList());
        List<Dic> dicList = dicService.list(new QueryWrapper<Dic>()
                .in("name", strings));
        HashMap<String, Object> map = new HashMap<>();
        map.put("batchList",dicList);
        return Result.success(map);
    }

    /*
     * @Description: 获取省份 河南 || 北京
     * @Date:   2024/4/20 22:49
     * @Param:  [schoolId, year]
     * @Return: com.zzuli.gaokao.common.Result
     */
    @GetMapping("/getProvinces")
    public Result getProvinces(Integer schoolId,String year){
        if(schoolId == null || StringUtils.isBlank(year)){
            return Result.error("参数错误！");
        }
        List<UniversityMasterScore> list = service.list(new QueryWrapper<UniversityMasterScore>()
                .select("distinct province_id")
                .eq("school_id", schoolId)
                .eq("year",year));
        List<Integer> ids = list.stream()
                .map(UniversityMasterScore::getProvinceId)
                .collect(Collectors.toList());

        List<Provinces> provinces = provincesService.list(new QueryWrapper<Provinces>()
                .in("id", ids)
                .orderByAsc("id"));
        HashMap<String, Object> map = new HashMap<>();
        map.put("provinces",provinces);
        return Result.success(map);
    }

    @GetMapping("/getMasterScores/{schoolId}/{provinceId}/{year}")
    public Result getMasterScore(Integer current, Integer size,Integer type, @PathVariable String provinceId, @PathVariable String schoolId, @PathVariable String year){
        if(current == null || size == null){
            return Result.error("参数错误！");
        }
        if(provinceId == null){
            return Result.error("省份不能为空！");
        }
        if(schoolId == null){
            return Result.error("高校id不能为空！");
        }
        if(StringUtils.isBlank(year)){
            return Result.error("年份不能为空！");
        }
        Page<UniversityMasterScore> uvmScorePage = new Page<>(current, size);
        QueryWrapper<UniversityMasterScore> wrapper = new QueryWrapper<>();
        wrapper.eq("school_id",schoolId)
                .eq("province_id",provinceId)
                .eq("year",year)
                .eq("type",type);
        Page<UniversityMasterScore> page = service.page(uvmScorePage, wrapper);
        List<UniversityMasterScore> records = page.getRecords();
        long total = page.getTotal();
        HashMap<String, Object> map = new HashMap<>();
        map.put("total",total);
        map.put("uvmScoreList",records);
        return Result.success(map);
    }

}
