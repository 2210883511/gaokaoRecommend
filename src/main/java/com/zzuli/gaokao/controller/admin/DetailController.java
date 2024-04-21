package com.zzuli.gaokao.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zzuli.gaokao.bean.UniversityInfo;
import com.zzuli.gaokao.common.Result;
import com.zzuli.gaokao.service.Impl.UniversityInfoServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/detail")
public class DetailController {

    @Autowired
    private UniversityInfoServiceImpl infoService;

    @GetMapping("/getInfos")
    public Result getInfo(Integer current,Integer size,Integer provinceId,String schoolName){
        Page<Map<String, Object>> custom = null;
        Page<Map<String, Object>> mapPage = new Page<>(current, size);
        if(StringUtils.isBlank(schoolName)){
            custom = infoService.findCustom(mapPage, null, provinceId);
        }
        else{
            custom = infoService.findCustom(mapPage,schoolName,provinceId);
        }

        List<Map<String, Object>> records = custom.getRecords();
        long total = custom.getTotal();
        HashMap<String, Object> map = new HashMap<>();
        map.put("details",records);
        map.put("total",total);
        return Result.success(map);
    }


    @GetMapping("/delete/{id}")
    public Result deleteInfo(@PathVariable("id") Integer id){
        infoService.removeById(id);
        return Result.success("删除成功！");
    }

    @PostMapping("/update")
    public Result updateInfo(@RequestBody UniversityInfo info){
        infoService.updateById(info);
        return Result.success("更新成功！");
    }

}
