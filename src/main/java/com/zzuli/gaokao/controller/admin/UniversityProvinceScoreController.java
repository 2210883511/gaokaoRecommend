package com.zzuli.gaokao.controller.admin;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zzuli.gaokao.bean.Dic;
import com.zzuli.gaokao.bean.Provinces;
import com.zzuli.gaokao.bean.UniversityMasterScore;
import com.zzuli.gaokao.bean.UniversityProvinceScore;
import com.zzuli.gaokao.common.Result;
import com.zzuli.gaokao.service.DicService;
import com.zzuli.gaokao.service.ProvincesService;
import com.zzuli.gaokao.service.UniversityProvinceScoreService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
public class UniversityProvinceScoreController {


    @Autowired
    private UniversityProvinceScoreService provinceScoreService;

    @Autowired
    private DicService dicService;

    @Autowired
    private ProvincesService provincesService;

    /*
     * @Description: 根据学校id来获取年份，然后去重构建年份新数组 2022 || 2023
     * @Date:   2024/4/25 15:33
     * @Param:  [schoolId]
     * @Return: com.zzuli.gaokao.common.Result
     */
    @GetMapping(value={"/admin/provinceScore/getYears","/api/provinceScore/getYears"})
    public Result getYear(Integer schoolId){

        if(schoolId == null){
            return Result.error("高校id不能为空！");
        }
        List<UniversityProvinceScore> list = provinceScoreService.list(new QueryWrapper<UniversityProvinceScore>()
                .select("distinct year")
                .eq("school_id", schoolId)
                .orderByDesc("year"));
        if(list.isEmpty()){
            return Result.success();
        }
        List<Integer> years = list.stream()
                .map(UniversityProvinceScore::getYear)
                .collect(Collectors.toList());
        List<Dic> dicList = dicService.list(new QueryWrapper<Dic>()
                .in("name", years));
        HashMap<String, Object> map = new HashMap<>();
        map.put("years",dicList);
        return Result.success(map);
    }

    /*
     * @Description: 获取省份 北京 || 上海 前提条件 先获取年份
     * @Date:   2024/4/25 15:41
     * @Param:  [schoolId, year]
     * @Return: com.zzuli.gaokao.common.Result
     */
    @GetMapping(value={"/admin/provinceScore/getProvinces","/api/provinceScore/getProvinces"})
    public Result getProvinces(Integer schoolId,Integer year){
        if(schoolId == null || year == null){
            return Result.error("高校id或者年份参数为空！");
        }
        List<UniversityProvinceScore> list = provinceScoreService.list(new QueryWrapper<UniversityProvinceScore>()
                .select("distinct province_id")
                .eq("school_id", schoolId)
                .eq("year",year)
                .orderByDesc("province_id"));
        List<Integer> provinceIds = list.stream()
                .map(UniversityProvinceScore::getProvinceId)
                .collect(Collectors.toList());
        List<Provinces> provinces = provincesService.list(new QueryWrapper<Provinces>()
                .in("id", provinceIds));
        HashMap<String, Object> map = new HashMap<>();
        map.put("provinces",provinces);
        return Result.success(map);
    }

    /*
     * @Description:  获取类型 文科 || 理科 || 综合 前提条件 年份、省份
     * @Date:   2024/4/25 15:43
     * @Param:  [schoolId, provinceId, year]
     * @Return: com.zzuli.gaokao.common.Result
     */
    @GetMapping(value={"/admin/provinceScore/getTypes","/api/provinceScore/getTypes"})
    public Result getType(Integer schoolId,Integer provinceId,Integer year){

        if(schoolId == null || provinceId == null || year == null){
            return Result.error("参数错误！");
        }
        List<UniversityProvinceScore> list = provinceScoreService.list(new QueryWrapper<UniversityProvinceScore>()
                .select("distinct type")
                .eq("school_id", schoolId)
                .eq("province_id",provinceId)
                .eq("year",year));
        List<Integer> ids = list.stream()
                .map(UniversityProvinceScore::getType)
                .collect(Collectors.toList());
        List<Dic> types = dicService.list(new QueryWrapper<Dic>()
                .in("id", ids));
        HashMap<String, Object> map = new HashMap<>();
        map.put("types",types);
        return Result.success(map);
    }

    /*
     * @Description: 根据省份id来查询类型 文科 || 理科 || 综合（新高考） || 物理类 || 历史类
     * @Date:   2024/4/22 23:48
     * @Param:  [provinceId]
     * @Return: com.zzuli.gaokao.common.Result
     */
    @GetMapping("/admin/provinceScore/getAllType")
    public Result getAllTypeByProvinceId(Integer provinceId){
        if(provinceId == null){
            return Result.error("省份id为空！");
        }
        List<UniversityProvinceScore> list = provinceScoreService.list(new QueryWrapper<UniversityProvinceScore>()
                .select("distinct type")
                .eq("province_id", provinceId));
        List<Integer> types = list.stream()
                .map(UniversityProvinceScore::getType)
                .collect(Collectors.toList());
        List<Dic> dicList = null;
        if(!types.isEmpty()){
            dicList = dicService.list(new QueryWrapper<Dic>()
                    .in("id", types));
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("types",dicList);
        return Result.success(map);
    }
    /*
     * @Description: 根据type从表中查询出所有招生批次
     * @Date:   2024/4/22 23:47
     * @Param:  [type]
     * @Return: com.zzuli.gaokao.common.Result
     */
    @GetMapping("/admin/provinceScore/getAllBatch")
    public Result getAllBatch(Integer type){
        if(type == null){
            return Result.error("type参数为空！");
        }
        List<UniversityProvinceScore> list = provinceScoreService.list(new QueryWrapper<UniversityProvinceScore>()
                .eq("type", type)
                .select("distinct local_batch_name"));
        List<String> strings = list.stream()
                .map(UniversityProvinceScore::getLocalBatchName)
                .collect(Collectors.toList());
        List<Dic> dicList = null;
        if(!strings.isEmpty()){
            dicList = dicService.list(new QueryWrapper<Dic>()
                    .in("name", strings));
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("batchList",dicList);
        return Result.success(map);
    }

    /*
     * @Description: 根据type从分数表中查出选科要求 去重查询
     * @Date:   2024/4/22 23:45
     * @Param:  [type]
     * @Return: com.zzuli.gaokao.common.Result
     */
    @GetMapping("/admin/provinceScore/getAllSgInfo")
    public Result getAllSgInfo(Integer type){
        List<UniversityProvinceScore> list = provinceScoreService.list(new QueryWrapper<UniversityProvinceScore>()
                .select("distinct sg_info")
                .ne("sg_info","")
                .eq("type", type));
        ArrayList<Map<String, Object>> maps = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("id",i);
            map.put("name",list.get(i).getSgInfo());
            maps.add(map);
        }
        HashMap<String, Object> data = new HashMap<>();
        data.put("sgInfos",maps);
        return Result.success(data);
    }



    /*
     * @Description: 根据高校id 来获取招生类型，每个高校的招生类型不同 普通类 || 中外合作办学 || 与濮阳工学院联办等
     * @Date:   2024/4/28 15:58
     * @Param:  [schoolId]
     * @Return: com.zzuli.gaokao.common.Result
     */
    @GetMapping("/admin/provinceScore/getZslxName")
    public Result getZslxName(Integer schoolId){
        List<UniversityProvinceScore> list = provinceScoreService.list(new QueryWrapper<UniversityProvinceScore>()
                .eq("school_id", schoolId)
                .select("distinct zslx_name"));
        if(list.isEmpty()){
            list = provinceScoreService.list(new QueryWrapper<UniversityProvinceScore>()
                    .select("distinct zslx_name"));
        }
        List<String> zslxNames = list.stream()
                .map(UniversityProvinceScore::getZslxName)
                .collect(Collectors.toList());
        HashMap<String, Object> map = new HashMap<>();
        map.put("zslxList",zslxNames);
        return Result.success(map);
    }

    /*
     * @Description: 查询高校省份分数线 /schoolId/provinceId/year
     * @Date:   2024/4/22 23:49
     * @Param:  [current, size, type, batch, provinceId, schoolId, year]
     * @Return: com.zzuli.gaokao.common.Result
     */
    @GetMapping(value={"/admin/provinceScore/getProvinceScore/{schoolId}/{provinceId}/{year}",
            "/api/provinceScore/getProvinceScore/{schoolId}/{provinceId}/{year}"})

    public Result getProvinceScore(Integer current, Integer size, Integer type, @PathVariable String provinceId, @PathVariable String schoolId, @PathVariable String year){
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
        if(type == null){
            return Result.error("类型不能为空！");
        }

        Page<UniversityProvinceScore> uvpScorePage = new Page<>(current, size);
        QueryWrapper<UniversityProvinceScore> wrapper = new QueryWrapper<>();
        wrapper.eq("school_id",schoolId)
                .eq("province_id",provinceId)
                .eq("year",year)
                .eq("type",type);
        Page<UniversityProvinceScore> page = provinceScoreService.page(uvpScorePage, wrapper);
        List<UniversityProvinceScore> records = page.getRecords();
        long total = page.getTotal();
        HashMap<String, Object> map = new HashMap<>();
        map.put("total",total);
        map.put("uvpScoreList",records);
        return Result.success(map);
    }
    
    /*
     * @Description: 添加省份分数线
     * @Date:   2024/4/28 15:40
     * @Param:  [uvProvinceScore]
     * @Return: com.zzuli.gaokao.common.Result
     */
    @PostMapping("/admin/provinceScore/addUvProvinceScore")
    public Result addUvProvinceScore(@RequestBody UniversityProvinceScore uvProvinceScore){
        Integer provinceId = uvProvinceScore.getProvinceId();
        Integer year = uvProvinceScore.getYear();
        Integer type = uvProvinceScore.getType();
        Integer schoolId = uvProvinceScore.getSchoolId();
        String localBatchName = uvProvinceScore.getLocalBatchName();
        String zslxName = uvProvinceScore.getZslxName();
        String proscore = uvProvinceScore.getProscore();
        if(schoolId == null){
            return Result.error("高校id不能为空！");
        }
        if(provinceId == null || year == null || type == null){
            return Result.error("必要参数为空！");
        }
        if(StringUtils.isBlank(localBatchName)){
            return Result.error("批次不能为空！");
        }
        if(StringUtils.isBlank(zslxName)){
            return Result.error("招生类型不能为空！");
        }
        if(StringUtils.isBlank(proscore)){
            return Result.error("省控线不能为空！");
        }
        UniversityProvinceScore one = provinceScoreService.getOne(new QueryWrapper<UniversityProvinceScore>()
                .eq("school_id", schoolId)
                .eq("province_id", provinceId)
                .eq("type", type)
                .eq("local_batch_name", localBatchName)
                .eq("zslx_name", zslxName)
                .eq("sg_info", uvProvinceScore.getSgInfo()));
        if(one != null){
            return Result.error("重复添加！");
        }
        uvProvinceScore.setStatus(1);

        provinceScoreService.save(uvProvinceScore);
        return Result.success("添加成功！");
    }


    @PostMapping("/admin/provinceScore/updateProvinceScore")
    public Result updateProvinceScore(@RequestBody UniversityProvinceScore uvProvinceScore){
        Integer provinceId = uvProvinceScore.getProvinceId();
        Integer year = uvProvinceScore.getYear();
        Integer type = uvProvinceScore.getType();
        Integer schoolId = uvProvinceScore.getSchoolId();
        String localBatchName = uvProvinceScore.getLocalBatchName();
        String zslxName = uvProvinceScore.getZslxName();
        String proscore = uvProvinceScore.getProscore();
        if(schoolId == null){
            return Result.error("高校id不能为空！");
        }
        if(provinceId == null || year == null || type == null){
            return Result.error("必要参数为空！");
        }
        if(StringUtils.isBlank(localBatchName)){
            return Result.error("批次不能为空！");
        }
        if(StringUtils.isBlank(zslxName)){
            return Result.error("招生类型不能为空！");
        }
        if(StringUtils.isBlank(proscore)){
            return Result.error("省控线不能为空！");
        }
        UniversityProvinceScore one = provinceScoreService.getOne(new QueryWrapper<UniversityProvinceScore>()
                .eq("school_id", schoolId)
                .eq("province_id", provinceId)
                .eq("type", type)
                .eq("local_batch_name", localBatchName)
                .eq("zslx_name", zslxName)
                .eq("sg_info", uvProvinceScore.getSgInfo()));
        if(one != null && !one.getId().equals(uvProvinceScore.getId())){
            return Result.error("重复添加！");
        }
        provinceScoreService.updateById(uvProvinceScore);
        return Result.success("更新成功！");
    }

    @PostMapping("/admin/provinceScore/deleteProvinceScore")
    public Result deleteProvinceScore(Integer id){

        provinceScoreService.removeById(id);
        return Result.success("删除成功！");
    }








    



}
