package com.zzuli.gaokao.controller.admin;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zzuli.gaokao.bean.Dic;
import com.zzuli.gaokao.bean.Provinces;
import com.zzuli.gaokao.bean.UniversityMasterPlan;
import com.zzuli.gaokao.bean.UniversityMasterScore;
import com.zzuli.gaokao.common.Result;
import com.zzuli.gaokao.service.DicService;
import com.zzuli.gaokao.service.ProvincesService;
import com.zzuli.gaokao.service.UniversityMasterPlanService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class MasterPlanController {

    @Autowired
    private UniversityMasterPlanService masterPlanService;

    @Autowired
    private DicService dicService;

    @Autowired
    private ProvincesService provincesService;


    /*
     * @Description: 获取年份 2023 || 2022 || 2021
     * @Date:   2024/4/22 12:40
     * @Param:  [schoolId]
     * @Return: com.zzuli.gaokao.common.Result
     */
    @GetMapping(value={"/admin/masterPlan/getYears","/api/masterPlan/getYears"})
    public Result getYears(Integer schoolId){
        if(schoolId == null){
            return Result.error("参数错误！");
        }
        List<UniversityMasterPlan> list = masterPlanService.list(new QueryWrapper<UniversityMasterPlan>()
                .select("distinct year")
                .eq("school_id", schoolId));
        if(list.isEmpty()){
            return Result.error("该高校没有数据！");
        }

        List<Integer> years = list.stream()
                .map(UniversityMasterPlan::getYear)
                .collect(Collectors.toList());
        List<Dic> dicList = dicService.list(new QueryWrapper<Dic>()
                .in("name", years)
                .orderByDesc("name"));
        HashMap<String, Object> map = new HashMap<>();
        map.put("years",dicList);
        return Result.success(map);
    }



    /*
     * @Description: 获取省份 河南 || 北京
     * @Date:   2024/4/20 22:49
     * @Param:  [schoolId, year]
     * @Return: com.zzuli.gaokao.common.Result
     */
    @GetMapping(value={"/admin/masterPlan/getProvinces","/api/masterPlan/getProvinces"})
    public Result getProvinces(Integer schoolId,Integer year){
        if(schoolId == null || year == null){
            return Result.error("参数错误！");
        }
        List<UniversityMasterPlan> list = masterPlanService.list(new QueryWrapper<UniversityMasterPlan>()
                .select("distinct province_id")
                .eq("school_id", schoolId)
                .eq("year",year));
        List<Integer> ids = list.stream()
                .map(UniversityMasterPlan::getProvinceId)
                .collect(Collectors.toList());

        List<Provinces> provinces = provincesService.list(new QueryWrapper<Provinces>()
                .in("id", ids)
                .orderByAsc("id"));
        HashMap<String, Object> map = new HashMap<>();
        map.put("provinces",provinces);
        return Result.success(map);
    }


    /*
     * @Description: 获取类型 文科 || 理科 || 综合
     * @Date:   2024/4/20 22:48
     * @Param:  [schoolId, provinceId, year]
     * @Return: com.zzuli.gaokao.common.Result
     */
    @GetMapping(value={"/admin/masterPlan/getTypes","/api/masterPlan/getTypes"})
    public Result getType(Integer schoolId,Integer provinceId,Integer year){

        if(schoolId == null || provinceId == null || year == null){
            return Result.error("参数错误！");
        }
        List<UniversityMasterPlan> list = masterPlanService.list(new QueryWrapper<UniversityMasterPlan>()
                .select("distinct type")
                .eq("school_id", schoolId)
                .eq("province_id",provinceId)
                .eq("year",year));
        List<Integer> ids = list.stream()
                .map(UniversityMasterPlan::getType)
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
    @GetMapping(value={"/admin/masterPlan/getBatch","/api/masterPlan/getBatch"})
    public Result getBatch(Integer schoolId,Integer provinceId,String year,Integer type){

        if(schoolId == null || provinceId == null || StringUtils.isBlank(year) || type == null) {
            return Result.error("参数错误！");
        }
        List<UniversityMasterPlan> list = masterPlanService.list(new QueryWrapper<UniversityMasterPlan>()
                .select("distinct local_batch_name")
                .eq("school_id", schoolId)
                .eq("province_id", provinceId)
                .eq("year",year)
                .eq("type",type));
        List<String> strings = list.stream()
                .map(UniversityMasterPlan::getLocalBatchName)
                .collect(Collectors.toList());
        List<Dic> dicList  = null;
        if(!strings.isEmpty()){
            dicList = dicService.list(new QueryWrapper<Dic>()
                    .in("name", strings));
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("batchList",dicList);
        return Result.success(map);
    }


    /*
     * @Description: 查询高校专业分数线 /schoolId/provinceId/year
     * @Date:   2024/4/22 23:49
     * @Param:  [current, size, type, batch, provinceId, schoolId, year]
     * @Return: com.zzuli.gaokao.common.Result
     */
    @GetMapping(value={"/admin/masterPlan/getMasterPlan/{schoolId}/{provinceId}/{year}","/api/masterPlan/getMasterPlan/{schoolId}/{provinceId}/{year}"})
    public Result getMasterScore(Integer current, Integer size, Integer type, String batch, @PathVariable String provinceId, @PathVariable String schoolId, @PathVariable String year){
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
        if(StringUtils.isBlank(batch)){
            return Result.error("批次不能为空！");
        }
        Page<UniversityMasterPlan> uvmPlan = new Page<>(current, size);
        QueryWrapper<UniversityMasterPlan> wrapper = new QueryWrapper<>();
        wrapper.eq("school_id",schoolId)
                .eq("province_id",provinceId)
                .eq("year",year)
                .eq("local_batch_name",batch)
                .eq("type",type);
        Page<UniversityMasterPlan> page = masterPlanService.page(uvmPlan, wrapper);
        List<UniversityMasterPlan> records = page.getRecords();
        long total = page.getTotal();
        HashMap<String, Object> map = new HashMap<>();
        map.put("total",total);
        map.put("uvmPlanList",records);
        return Result.success(map);
    }


    /*
     * @Description: 根据省份id来查询类型 文科 || 理科 || 综合（新高考） || 物理类 || 历史类
     * @Date:   2024/4/22 23:48
     * @Param:  [provinceId]
     * @Return: com.zzuli.gaokao.common.Result
     */
    @GetMapping("/admin/masterPlan/getAllType")
    public Result getAllTypeByProvinceId(Integer provinceId){
        if(provinceId == null){
            return Result.error("省份id为空！");
        }
        List<UniversityMasterPlan> list = masterPlanService.list(new QueryWrapper<UniversityMasterPlan>()
                .select("distinct type")
                .eq("province_id", provinceId));
        List<Integer> types = list.stream()
                .map(UniversityMasterPlan::getType)
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
    @GetMapping("/admin/masterPlan/getAllBatch")
    public Result getAllBatch(Integer type){
        if(type == null){
            return Result.error("type参数为空！");
        }
        List<UniversityMasterPlan> list = masterPlanService.list(new QueryWrapper<UniversityMasterPlan>()
                .eq("type", type)
                .select("distinct local_batch_name"));
        List<String> strings = list.stream()
                .map(UniversityMasterPlan::getLocalBatchName)
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
     * @Description: 添加招生计划
     * @Date:   2024/4/25 13:07
     * @Param:  [uvMasterScore]
     * @Return: com.zzuli.gaokao.common.Result
     */
    @PostMapping("/admin/masterPlan/addUvmPlan")
    public Result addUvmScore(@RequestBody UniversityMasterPlan masterPlan){
        Integer year = masterPlan.getYear();
        String spname = masterPlan.getSpname();
        String localBatchName = masterPlan.getLocalBatchName();
        Integer type = masterPlan.getType();
        Integer provinceId = masterPlan.getProvinceId();
        Integer specialId = masterPlan.getSpecialId();
        Integer schoolId = masterPlan.getSchoolId();
        String num = masterPlan.getNum();
        if(schoolId == null){
            return Result.error("高校id不能为空！");
        }
        if(year == null || type == null){
            return Result.error("年份和类型不能为空！");
        }

        if(provinceId == null || specialId == null){
            return Result.error("省份和专业id不能为空！");
        }

        if(StringUtils.isBlank(spname)){
            return Result.error("专业名称不能为空！");
        }
        if(StringUtils.isBlank(localBatchName)){
            return Result.error("批次不能为空！");
        }
        if(StringUtils.isBlank(num)){
            return Result.error("招生人数不能为空！");
        }
        UniversityMasterPlan one = masterPlanService.getOne(new QueryWrapper<UniversityMasterPlan>()
                .eq("school_id",schoolId)
                .eq("province_id",provinceId)
                .eq("year", year)
                .eq("spname", spname)
                .eq("type",type));

        if(one != null){
            return Result.error("该招生计划在该省份的"+year+"年已经添加过了！");
        }
        masterPlan.setStatus(1);

        masterPlanService.save(masterPlan);

        return Result.success("添加成功！");
    }

    /*
     * @Description: 根据id删除招生计划
     * @Date:   2024/4/25 13:07
     * @Param:  [id]
     * @Return: com.zzuli.gaokao.common.Result
     */

    @PostMapping("/admin/masterPlan/deleteUvmPlan")
    public Result deleteUvmScore(Integer id){
        masterPlanService.removeById(id);
        return Result.success("删除成功！");
    }

    /*
     * @Description: 更新专业分数线
     * @Date:   2024/4/25 13:07
     * @Param:  [uvMasterScore]
     * @Return: com.zzuli.gaokao.common.Result
     */
    @PostMapping("/admin/masterPlan/updateUvmPlan")
    public Result updateUvmScore(@RequestBody UniversityMasterPlan masterPlan){
        Integer year = masterPlan.getYear();
        String spname = masterPlan.getSpname();
        String localBatchName = masterPlan.getLocalBatchName();
        Integer type = masterPlan.getType();
        Integer provinceId = masterPlan.getProvinceId();
        Integer specialId = masterPlan.getSpecialId();
        Integer schoolId = masterPlan.getSchoolId();
        String num = masterPlan.getNum();

        if(schoolId == null){
            return Result.error("高校id不能为空！");
        }
        if(year == null || type == null){
            return Result.error("年份和类型不能为空！");
        }

        if(provinceId == null || specialId == null){
            return Result.error("省份和专业id不能为空！");
        }

        if(StringUtils.isBlank(spname)){
            return Result.error("专业名称不能为空！");
        }

        if(StringUtils.isBlank(localBatchName)){
            return Result.error("批次不能为空！");
        }
        if(StringUtils.isBlank(num)){
            return Result.error("招生人数不能为空！");
        }
        UniversityMasterPlan one = masterPlanService.getOne(new QueryWrapper<UniversityMasterPlan>()
                .eq("school_id",schoolId)
                .eq("province_id",provinceId)
                .eq("year", year)
                .eq("spname", spname)
                .eq("type",type));
        if(one != null && !one.getId().equals(masterPlan.getId())){
            return Result.error("记录已经存在了，请重新选择");
        }
        masterPlanService.updateById(masterPlan);
        return Result.success("更新成功！");

    }



}
