package com.zzuli.gaokao.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zzuli.gaokao.bean.UniversityInfo;

import java.util.Map;

public interface UniversityInfoService extends IService<UniversityInfo> {

    Page<Map<String,Object>> findCustom(Page<Map<String,Object>> page,String schoolName,Integer provinceId);
}
