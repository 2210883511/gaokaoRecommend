package com.zzuli.gaokao.service.Impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzuli.gaokao.bean.UniversityInfo;
import com.zzuli.gaokao.mapper.UniversityInfoMapper;
import com.zzuli.gaokao.service.UniversityInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UniversityInfoServiceImpl extends ServiceImpl<UniversityInfoMapper, UniversityInfo> implements UniversityInfoService {

    @Autowired
    private UniversityInfoMapper infoMapper;

    @Override
    public Page<Map<String, Object>> findCustom(Page<Map<String, Object>> page, String schoolName, Integer provinceId) {
       return infoMapper.getCustom(page,provinceId,schoolName);

    }
}
