package com.zzuli.gaokao.service.Impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzuli.gaokao.bean.UniversityTags;
import com.zzuli.gaokao.mapper.UniversityMapper;
import com.zzuli.gaokao.mapper.UniversityTagsMapper;
import com.zzuli.gaokao.service.UniversityTagsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service
public class UniversityTagsServiceImpl extends ServiceImpl<UniversityTagsMapper, UniversityTags> implements UniversityTagsService {

    @Autowired
    private UniversityTagsMapper tagsMapper;

    @Override
    public Page<Map<String, Object>> findTags(Page<Map<String, Object>> page, Integer provinceId, String schoolName) {
        return tagsMapper.selectCustom(page, provinceId, schoolName);
    }
}
