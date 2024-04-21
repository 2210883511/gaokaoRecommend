package com.zzuli.gaokao.controller.admin;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zzuli.gaokao.bean.UniversityTags;
import com.zzuli.gaokao.common.Result;
import com.zzuli.gaokao.service.Impl.UniversityTagsServiceImpl;
import it.unimi.dsi.fastutil.Hash;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController

@RequestMapping("/admin/tags")
public class TagsController {

    @Autowired
    private UniversityTagsServiceImpl tagsService;

    @GetMapping("/getTags")
    public Result getTags(Integer page,Integer size,Integer provinceId,String schoolName){

        Page<Map<String, Object>> mapPage = new Page<>(page, size);
        Page<Map<String, Object>> tags = null;
        if(StringUtils.isBlank(schoolName)){
             tags = tagsService.findTags(mapPage, provinceId, null);
        }else {
             tags = tagsService.findTags(mapPage, provinceId, schoolName);
        }
        List<Map<String, Object>> records = tags.getRecords();
        long total = tags.getTotal();
        HashMap<String, Object> map = new HashMap<>();
        map.put("tags",records);
        map.put("total",total);
        return Result.success(map);
    }

    @GetMapping("/delete/{id}")
    public Result deleteTags(@PathVariable("id") Integer schoolId){

        tagsService.remove(new QueryWrapper<UniversityTags>()
                .eq("school_id",schoolId));

        return Result.success("删除成功！");
    }
    
    /*
     * @Description: 根据学校id进行更新
     * @Date:   2024/4/7 20:59
     * @Param:  [universityTags, schoolId]
     * @Return: com.zzuli.gaokao.common.Result
     */
    @PostMapping("/updateTags/{schoolId}")
    public Result updateTags(@RequestBody UniversityTags universityTags,@PathVariable("schoolId") Integer schoolId){

        if(universityTags == null){
            return Result.error("请求体不能为空！");
        }
        System.out.println(universityTags);

        tagsService.update(universityTags,new QueryWrapper<UniversityTags>().eq("school_id",schoolId));
        return Result.success("更新成功");
    }

    /*
     * @Description: 获取单个的标签对象
     * @Date:   2024/4/7 20:59
     * @Param:  [schoolId]
     * @Return: com.zzuli.gaokao.common.Result
     */
    @GetMapping("/getTag")
    public Result getTag(Integer schoolId){
        UniversityTags one = tagsService.getOne(new QueryWrapper<UniversityTags>()
                .select("school_id","f985","f211","school_type_name","school_nature_name","dual_class_name","type_name")
                .eq("school_id", schoolId));
        HashMap<String, Object> map = new HashMap<>();
        map.put("universityTag",one);
        return Result.success(map);
    }

}
