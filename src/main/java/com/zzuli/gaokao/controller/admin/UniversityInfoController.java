package com.zzuli.gaokao.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zzuli.gaokao.bean.MasterDetail;
import com.zzuli.gaokao.bean.University;
import com.zzuli.gaokao.bean.UniversityInfo;
import com.zzuli.gaokao.common.Result;
import com.zzuli.gaokao.service.UniversityInfoService;
import com.zzuli.gaokao.service.UniversityService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/info")
public class UniversityInfoController {

    @Autowired
    private UniversityService universityService;

    @Autowired
    private UniversityInfoService infoService;


    @GetMapping("/getInfos")
    public Result getInfos(Integer current,Integer size,Integer provinceId,String schoolName){

        Page<Map<String, Object>> mapPage = new Page<>(current, size);
        Page<Map<String, Object>> custom = null;
        if(StringUtils.isBlank(schoolName)){
            custom = infoService.findCustom(mapPage, null, provinceId);
        }else {
            custom = infoService.findCustom(mapPage,schoolName,provinceId);
        }
        long total = custom.getTotal();
        List<Map<String, Object>> records = custom.getRecords();
        HashMap<String, Object> map = new HashMap<>();
        map.put("total",total);
        map.put("infos",records);
        return Result.success(map);
    }

    @PostMapping("/addInfo")
    public Result addInfo(@RequestBody UniversityInfo info){

        if(info.getSchoolId() == null){
            return Result.error("参数错误，高校id不能为空！");
        }
        if(StringUtils.isBlank(info.getContent())){
            return Result.error("简介不能为空！");
        }
        info.setStatus(1);
        infoService.save(info);

        return Result.success("添加成功！");

    }

    @PostMapping("/updateInfo")
    public Result updateInfo(@RequestBody UniversityInfo info){
        if(info.getId() == null){
            return Result.error("参数错误，id不能为空!");
        }
        if(StringUtils.isBlank(info.getContent())){
            return Result.error("简介不能为空！");
        }
        infoService.updateById(info);
        return Result.success("更新成功！");
    }


    @GetMapping("/getBelongs")
    public Result getBelong(){
        QueryWrapper<UniversityInfo> wrapper = new QueryWrapper<UniversityInfo>().select("distinct belong")
                .ne("belong", ' ');
        List<Map<String, Object>> maps = infoService.listMaps(wrapper);

        HashMap<String, Object> map = new HashMap<>();
        map.put("belongs",maps);
        return Result.success(map);
    }


    @PostMapping("/delete/{id}")
    public Result deleteInfo(@PathVariable("id") Integer id){
        infoService.removeById(id);
        return Result.success("删除成功！");
    }

    @GetMapping("/isAdd/{schoolId}")
    public Result isAddInfo(@PathVariable Integer schoolId){

        UniversityInfo one = infoService.getOne(new QueryWrapper<UniversityInfo>()
                .eq("school_id", schoolId));
        HashMap<String, Object> map = new HashMap<String, Object>();
        if(one != null){
            map.put("flag",true);
            return Result.success("已经添加过了！",map);
        }
        map.put("flag",false);
        return Result.success(map);
    }
}
