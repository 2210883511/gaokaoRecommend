package com.zzuli.gaokao.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zzuli.gaokao.bean.UniversityRank;
import com.zzuli.gaokao.mapper.UniversityRankMapper;

import java.util.Map;

public interface UniversityRankService extends IService<UniversityRank> {


    Page<Map<String,Object>> findCustom(Page<Map<String,Object>>page,Integer provinceId,String schoolName);
}
