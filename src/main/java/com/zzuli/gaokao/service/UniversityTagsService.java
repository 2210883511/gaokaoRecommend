package com.zzuli.gaokao.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zzuli.gaokao.bean.UniversityTags;

import java.util.Map;

public interface UniversityTagsService extends IService<UniversityTags> {
    
    /*
     * @Description: 分页获取高校标签数据，同时可以添加多条件查询语句 省份id 和高校名称 模糊查询
     * @Date:   2024/4/6 17:36
     * @Param:  [page, provinceId, schoolName]
     * @Return: com.baomidou.mybatisplus.extension.plugins.pagination.Page<java.util.Map<java.lang.String,java.lang.Object>>
     */
    Page<Map<String,Object>> findTags(Page<Map<String,Object>> page,Integer provinceId, String schoolName);


}
