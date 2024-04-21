package com.zzuli.gaokao.controller.admin;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zzuli.gaokao.bean.*;
import com.zzuli.gaokao.common.Result;
import com.zzuli.gaokao.service.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/admin/master")
public class MasterController {

    @Autowired
    private MasterService masterService;

    @Autowired
    private TypeDetailService detailService;

    @Autowired
    private MasterTypeService masterTypeService;

    @Autowired
    private TagsService tagsService;


    @GetMapping("/getMasters")
    public Result getMasters(Integer current, Integer size, String masterName,String type,String typeDetail,String levelName){
        Page<Master> page = new Page<>(current, size);
        QueryWrapper<Master> wrapper = new QueryWrapper<>();
        wrapper.select("id","type_detail_id","name","code","limit_year","level1_name","degree","type","type_detail");
        if(StringUtils.isNotBlank(masterName)){
            wrapper.like("name",masterName);
        }
        if(StringUtils.isNotBlank(levelName) && StringUtils.isNotBlank(type) && StringUtils.isNotBlank(typeDetail)){
            wrapper.eq("level1_name",levelName);
            wrapper.eq("type",type);
            wrapper.eq("type_detail",typeDetail);
        }
        Page<Master> masterPage = masterService.page(page, wrapper);
        List<Master> records = masterPage.getRecords();
        long total = masterPage.getTotal();
        HashMap<String, Object> map = new HashMap<>();
        map.put("masters",records);
        map.put("total",total);
        return Result.success(map);
    }

    @GetMapping("/getTags")
    public Result getTags(String schoolType){
        List<String> levelNames = new ArrayList<>();
        if(StringUtils.isNotBlank(schoolType)){
            levelNames.add(schoolType);
        }else {
            levelNames.add("本科");
            levelNames.add("专科（高职）");
        }
        /* 查出所有的 type */
        List<MasterType> types = masterTypeService.list(new QueryWrapper<MasterType>()
                .select("type","level_name"));

        /* 查出所有的typeDetail*/
        List<TypeDetail> details = detailService.list(new QueryWrapper<TypeDetail>()
                .select("type_name", "type_detail"));

        ArrayList<Map<String, Object>> vo = new ArrayList<>();
        levelNames.forEach(levelName->{
            HashMap<String, Object> one = new HashMap<>();
            ArrayList<Map<String, Object>> oneChildren = new ArrayList<>();
            one.put("label",levelName);
            one.put("value",levelName);
            one.put("children",oneChildren);
            for (MasterType type : types) {
                if(levelName.equals(type.getLevelName())){
                    HashMap<String, Object> map = new HashMap<>();
                    ArrayList<Map<String, Object>> children = new ArrayList<>();
                    map.put("label",type.getType());
                    map.put("value",type.getType());
                    map.put("children",children);
                    for (TypeDetail detail : details) {
                        if(type.getType().equals(detail.getTypeName())){
                            HashMap<String, Object> tmp = new HashMap<>();
                            tmp.put("label",detail.getTypeDetail());
                            tmp.put("value",detail.getTypeDetail());
                            children.add(tmp);
                        }
                    }
                    oneChildren.add(map);
                }
            }
            vo.add(one);
        });
        return Result.success(vo);
    }


    @PostMapping("/addMaster")
    public Result addMaster(@RequestBody Master master){


        if(master == null){
            return Result.error("请求体不能为空！");
        }


        if(StringUtils.isBlank(master.getName())){
            return  Result.error("专业名称不能为空！");
        }


        if(StringUtils.isBlank(master.getCode())){
            return Result.error("专业代码不能为空！");
        }


        if(StringUtils.isBlank(master.getLimitYear())){
            return Result.error("专业年限不能为空！");
        }

        Master one = null;
        one = masterService.getOne(new QueryWrapper<Master>().eq("name", master.getName()));

        if(one != null){
            return Result.error("专业名称不能重复！");
        }

        one = masterService.getOne(new QueryWrapper<Master>().eq("code", master.getCode()));

        if(one != null){
            return Result.error("专业代码不能重复！");
        }
        String type = master.getType();
        String typeDetail = master.getTypeDetail();
        TypeDetail masterTypeDetail = detailService.getOne(new QueryWrapper<TypeDetail>()
                .eq("type_name", type)
                .eq("type_detail", typeDetail));
        master.setStatus(1);
        master.setTypeDetailId(masterTypeDetail.getId());
        master.setAddTime(new Date());
        masterService.save(master);
        return Result.success("保存成功！");
    }

    @PostMapping("/delete/{id}")
    public Result deleteMaster(@PathVariable("id") Integer id){
        masterService.removeById(id);
        return Result.success("删除成功！");
    }

    @PostMapping("/updateMaster")
    public Result updateMaster(@RequestBody Master master){
        String name = master.getName();
        String code = master.getCode();
        if(StringUtils.isBlank(name)){
            master.setName(null);
        }
        if(StringUtils.isBlank(code)){
            master.setCode(null);
        }
        if(StringUtils.isBlank(master.getDegree())){
            master.setDegree(null);
        }
        if(StringUtils.isBlank(master.getLimitYear())){
            master.setLimitYear(null);
        }
        Master one = masterService.getOne(new QueryWrapper<Master>().eq("name", name));
        if(one != null && !one.getId().equals(master.getId())){
            return Result.error("专业名称不能重复！");
        }
        one = masterService.getOne(new QueryWrapper<Master>().eq("code", code));
        if(one != null && !one.getId().equals(master.getId())){
            return Result.error("专业代码不能重复！");
        }

        masterService.updateById(master);
        return Result.success("保存成功！");
    }

    /*
     * @Description: 获取单个专业对象
     * @Date:   2024/4/13 13:51
     * @Param:  [id]
     * @Return: com.zzuli.gaokao.common.Result
     */
    @GetMapping("/getOne/{id}")
    public Result getOneMaster(@PathVariable("id") Integer id){

        Master master = masterService.getById(id);
        master.setAddTime(null);
        master.setStatus(null);
        master.setUpdateTime(null);
        HashMap<String, Object> map = new HashMap<>();
        map.put("master",master);
        return Result.success(map);
    }

    @GetMapping("/getSpecialList")
    public Result getOneMaster(String levelName,String type,String typeDetail){
        if(StringUtils.isBlank(levelName) && StringUtils.isBlank(type) && StringUtils.isBlank(typeDetail)){
            return Result.error("参数错误！");
        }
        List<Master> specialList = masterService.list(new QueryWrapper<Master>()
                .select("id","name")
                .eq("level1_name", levelName)
                .eq("type", type)
                .eq("type_detail", typeDetail));
        HashMap<String, Object> map = new HashMap<>();
        map.put("masters",specialList);
        return Result.success(map);
    }

}
