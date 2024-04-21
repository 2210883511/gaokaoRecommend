package com.zzuli.gaokao.service.Impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzuli.gaokao.bean.University;
import com.zzuli.gaokao.bean.UniversityImg;
import com.zzuli.gaokao.mapper.UniversityImgMapper;
import com.zzuli.gaokao.mapper.UniversityMapper;
import com.zzuli.gaokao.service.UniversityImgService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UniversityImgServiceImpl extends ServiceImpl<UniversityImgMapper, UniversityImg> implements UniversityImgService {

    @Autowired
    private UniversityImgMapper imgMapper;

    @Autowired
    private UniversityMapper universityMapper;

    @Override
    public Page<Map<String, Object>> findCustom(Page<Map<String, Object>> page, Integer provinceId, String schoolName) {


        Page<Map<String, Object>> mapPage = null;
        if(StringUtils.isBlank(schoolName)){
            mapPage = imgMapper.selectCustom(page, provinceId, null);
        }
        else {

           mapPage = imgMapper.selectCustom(page,provinceId,schoolName);
        }

        return mapPage;
    }
}
