package com.zzuli.gaokao.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zzuli.gaokao.bean.UniversityImg;

import java.util.Map;

public interface UniversityImgService extends IService<UniversityImg> {


    Page<Map<String,Object>> findCustom(Page<Map<String,Object>> page,Integer provinceId,String schoolName);

}
