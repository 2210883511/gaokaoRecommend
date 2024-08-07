package com.zzuli.gaokao.controller.admin;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zzuli.gaokao.bean.Master;
import com.zzuli.gaokao.bean.University;
import com.zzuli.gaokao.bean.UniversityMaster;
import com.zzuli.gaokao.bean.UniversityTags;
import com.zzuli.gaokao.common.Result;
import com.zzuli.gaokao.service.MasterService;
import com.zzuli.gaokao.service.UniversityMasterService;
import com.zzuli.gaokao.service.UniversityService;
import com.zzuli.gaokao.service.UniversityTagsService;
import com.zzuli.gaokao.vo.UniversityMasterVo;
import com.zzuli.gaokao.vo.UniversityVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
public class UvMasterController {

    @Autowired
    private UniversityService universityService;

    @Autowired
    private UniversityTagsService tagsService;

    @Autowired
    private UniversityMasterService uvMasterService;

    @Autowired
    private MasterService masterService;


    /*
     * @Description: 获取高校列表
     * @Date:   2024/4/17 23:19
     * @Param:  [current, size, provinceId, schoolName]
     * @Return: com.zzuli.gaokao.common.Result
     */
    @GetMapping("/admin/uvMaster/getVoList")
    public Result getUniversityVo(Integer current,Integer size,Integer provinceId,String schoolName){
        Page<University> page = new Page<>(current, size);
        QueryWrapper<University> wrapper = new QueryWrapper<>();
        wrapper.select("school_id","school_name","header_url","town_name","city_name");
        if(StringUtils.isNotBlank(schoolName)){
            wrapper.like("school_name",schoolName);
        }
        if(provinceId != null){
            wrapper.eq("province_Id",provinceId);
        }
        Page<University> universityPage = universityService.page(page, wrapper);
        List<University> uvList = universityPage.getRecords();
        long total = universityPage.getTotal();
        // 获取ids
        List<Integer> ids = uvList.stream()
                .map(University::getSchoolId)
                .collect(Collectors.toList());
        List<UniversityTags> tagsList = tagsService.list(new QueryWrapper<UniversityTags>()
                .in("school_id", ids));

        List<UniversityVo> voList = uvList.stream()
                .map((university) -> {
                    UniversityVo vo = null;
                    for (UniversityTags tags : tagsList) {
                        if (university.getSchoolId().equals(tags.getSchoolId())) {
                            vo = new UniversityVo();
                            vo.setSchoolId(university.getSchoolId());
                            vo.setSchoolName(university.getSchoolName());
                            vo.setHeaderUrl(university.getHeaderUrl());
                            vo.setCityName(university.getCityName());
                            vo.setTownName(university.getTownName());
                            vo.setF211(tags.getF211());
                            vo.setF985(tags.getF985());
                            vo.setDualClassName(tags.getDualClassName());
                            vo.setTypeName(tags.getTypeName());
                            break;
                        }
                    }
                    return vo;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        HashMap<String, Object> map = new HashMap<>();
        map.put("universityVoList",voList);
        map.put("total",total);
        return Result.success(map);
    }


    /*
     * @Description: 获取高校专业详情列表
     * @Date:   2024/4/18 11:10
     * @Param:  []
     * @Return: com.zzuli.gaokao.common.Result
     */
    @GetMapping(value = {"/admin/uvMaster/getUvMasterVoList","/api/uvMaster/getUvMasterVoList"})
    public Result getUvMasterVo(Integer current,Integer size,Integer schoolId,String masterName,String level1Name,String type,String typeDetail){

        QueryWrapper<UniversityMaster> wrapper = new QueryWrapper<UniversityMaster>()
                .eq("school_id", schoolId);
        if(StringUtils.isNotBlank(masterName)){
            wrapper.like("special_name",masterName);
        }
        List<UniversityMaster> uvList = uvMasterService.list(wrapper);
        if(uvList.isEmpty())
            return Result.success();
        List<Integer> masterIds = uvList.stream()
                .map(UniversityMaster::getSpecialId)
                .collect(Collectors.toList());
        List<Master> masterList = masterService.list(new QueryWrapper<Master>()
                        .select("type","type_detail","level1_name","limit_year","id","name")
                        .in("id", masterIds));
        List<UniversityMasterVo> universityMasterVoList = uvList.stream()
                .map(uvMaster -> {
                    UniversityMasterVo universityMasterVo = null;
                    for (Master master : masterList) {

                        if (uvMaster.getSpecialId().equals(master.getId())) {
                            universityMasterVo = new UniversityMasterVo();
                            universityMasterVo.setUniversityMaster(uvMaster);
                            universityMasterVo.setMaster(master);
                        }
                    }
                    return universityMasterVo;
                })
                .filter(Objects::nonNull)
                .sorted(Comparator.comparingInt(UniversityMasterVo::getNationFeature))
                .collect(Collectors.toList());

        Integer total = null;
        List<UniversityMasterVo> realVoList = null;
        if(StringUtils.isBlank(type) && StringUtils.isBlank(typeDetail)  && StringUtils.isBlank(level1Name)){
            total = universityMasterVoList.size();
            realVoList = universityMasterVoList
                    .stream()
                    .skip((long) (current - 1) * size)
                    .limit(size)
                    .sorted(Comparator.comparingInt(UniversityMasterVo::getNationFeature))
                    .collect(Collectors.toList());
        }else{
            List<UniversityMasterVo> tmpList = universityMasterVoList
                    .stream()
                    .filter(vo -> vo.getLevel1Name().equals(level1Name)
                            && vo.getType().equals(type)
                            && vo.getTypeDetail().equals(typeDetail))
                    .sorted(Comparator.comparingInt(UniversityMasterVo::getNationFeature))
                    .collect(Collectors.toList());
            total  = tmpList.size();
            realVoList = tmpList.stream()
                    .skip((long) (current - 1) * size)
                    .limit(size)
                    .collect(Collectors.toList());
        }

        HashMap<String, Object> map = new HashMap<>();
        map.put("total",total);
        map.put("universityMasterVoList",realVoList);
        return Result.success(map);

    }




    @PostMapping("/admin/uvMaster/delete/{id}")
    public Result deleteUniversityMaster(@PathVariable Integer id){
        uvMasterService.removeById(id);
        return Result.success("删除成功！");
    }



    @PostMapping("/admin/uvMaster/addUniversityMaster")
    public Result addUniversityMaster(@RequestBody UniversityMaster uvMaster){
        Integer specialId = uvMaster.getSpecialId();
        String specialName = uvMaster.getSpecialName();
        Integer schoolId = uvMaster.getSchoolId();

        if(specialId == null){
            return Result.error("一级专业不能为空");
        }
        if(StringUtils.isBlank(specialName)){
            return Result.error("二级专业名称不能为空！");
        }
        if (uvMaster.getNationFeature() == null) {
            return Result.error("参数错误，nationFeature !");
        }
        if(uvMaster.getProvinceFeature() == null){
            return Result.error("参数错误，provinceFeature !");
        }
        UniversityMaster one = uvMasterService.getOne(new QueryWrapper<UniversityMaster>()
                .eq("school_id", schoolId)
                .eq("special_name", specialName));
        if(one != null){
            return Result.error("二级专业名称重复！");
        }
        uvMaster.setStatus(1);
        uvMasterService.save(uvMaster);
        return Result.success("添加成功！");
    }
    
    /*
     * @Description: 更新高校专业
     * @Date:   2024/4/24 19:00
     * @Param:  [uvMaster]
     * @Return: com.zzuli.gaokao.common.Result
     */
    @PostMapping("/admin/uvMaster/updateUniversityMaster")
    public Result updateUniversityMaster(@RequestBody UniversityMaster uvMaster){
        Integer schoolId = uvMaster.getSchoolId();
        Integer specialId = uvMaster.getSpecialId();
        String specialName = uvMaster.getSpecialName();
        Integer id = uvMaster.getId();

        if(StringUtils.isBlank(specialName)){
            return Result.error("二级专业名称不能为空！");
        }

        if(specialId == null){
            return Result.error("一级专业不能为空");
        }

        if(schoolId == null){
            return Result.error("参数错误，schoolId为空！");
        }
        if(id == null){
            return Result.error("id不能为空！");
        }

        UniversityMaster one = uvMasterService.getOne(new QueryWrapper<UniversityMaster>()
                .eq("school_id", schoolId)
                .eq("special_name", specialName));
        if(one != null && !one.getId().equals(id)){
            return Result.error("二级专业名称重复！");
        }

        uvMasterService.updateById(uvMaster);
        return Result.success("更新成功！");
    }

    /*
     * @Description: 根据学校id查询高校包含的专业
     * @Date:   2024/4/22 21:26
     * @Param:  [schoolId]
     * @Return: com.zzuli.gaokao.common.Result
     */
    @GetMapping("/admin/uvMaster/getUvMasters")
    public Result getUvMasterBySchoolId(Integer schoolId){

        List<UniversityMaster> masters = uvMasterService.list(new QueryWrapper<UniversityMaster>()
                .eq("school_id", schoolId)
                .select("id,special_name,special_id"));
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("masters",masters);
        return Result.success(map);
    }
    
    /*
     * @Description: 根据学校id 和名称来获取高校专业id
     * @Date:   2024/4/24 19:06
     * @Param:  [specialName, schoolId]
     * @Return: com.zzuli.gaokao.common.Result
     */
    @GetMapping("/admin/uvMaster/getUvMasterBySpecialName")
    public Result getUvMasterBySpecialName(String specialName,Integer schoolId){
        if(StringUtils.isBlank(specialName) || schoolId == null){
            return Result.error("参数错误！");
        }
        UniversityMaster one = uvMasterService.getOne(new QueryWrapper<UniversityMaster>()
                .select("id")
                .eq("school_id", schoolId)
                .eq("special_name", specialName));
        HashMap<String, Object> map = new HashMap<>();
        map.put("specialId",one.getId());
        return Result.success(map);
    }

    








}
