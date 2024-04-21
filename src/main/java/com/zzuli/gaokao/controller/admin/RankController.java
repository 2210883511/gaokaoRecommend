package com.zzuli.gaokao.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zzuli.gaokao.bean.UniversityRank;
import com.zzuli.gaokao.common.Result;
import com.zzuli.gaokao.service.Impl.UniversityRankServiceImpl;
import it.unimi.dsi.fastutil.Hash;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/rank")
public class RankController {

    @Autowired
    private UniversityRankServiceImpl rankService;

    @GetMapping("/getRanks")
    public Result getRanks(Integer current,Integer size,Integer provinceId,String schoolName){
        Page<Map<String, Object>> custom;
        Page<Map<String, Object>> mapPage = new Page<Map<String, Object>>(current,size);
        if(StringUtils.isBlank(schoolName)){
            custom = rankService.findCustom(mapPage, provinceId, null);
        }else {
            custom = rankService.findCustom(mapPage,provinceId,schoolName);
        }
        List<Map<String, Object>> records = custom.getRecords();
        long total = custom.getTotal();
        HashMap<String, Object> map = new HashMap<>();
        map.put("ranks",records);
        map.put("total",total);
        return Result.success(map);
    }

    @GetMapping("/delete/{id}")
    public Result deleteRank(@PathVariable("id") Integer id){
        rankService.removeById(id);
        return Result.success("删除成功!");
    }

    @PostMapping("/update")
    public Result updateRank(@RequestBody UniversityRank rank){

        rankService.updateById(rank);
        return Result.success("更新成功！");
    }









}
