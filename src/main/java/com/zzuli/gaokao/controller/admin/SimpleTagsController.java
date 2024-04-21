package com.zzuli.gaokao.controller.admin;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zzuli.gaokao.bean.Tags;
import com.zzuli.gaokao.common.Result;
import com.zzuli.gaokao.service.Impl.TagsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/admin/simpleTags")
public class SimpleTagsController {

    @Autowired
    private TagsServiceImpl tagsService;

    @GetMapping("/getTags")
    public Result get(Integer type){
        if(type == null){
            return  Result.error("标签类型不能为空！");
        }
        List<Tags> list = tagsService.list(new QueryWrapper<Tags>()
                .select("id", "name")
                .eq("type", type));
        HashMap<String, Object> map = new HashMap<>();
        map.put("tags",list);
        return Result.success(map);

    }




}
