package com.zzuli.gaokao.service.Impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzuli.gaokao.bean.UniversityRank;
import com.zzuli.gaokao.mapper.UniversityRankMapper;
import com.zzuli.gaokao.service.UniversityRankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UniversityRankServiceImpl extends ServiceImpl<UniversityRankMapper, UniversityRank> implements UniversityRankService {

    @Autowired
    private UniversityRankMapper rankMapper;

    @Override
    public Page<Map<String, Object>> findCustom(Page<Map<String, Object>> page, Integer provinceId, String schoolName) {

        return rankMapper.getCustom(page, provinceId, schoolName);
    }
}
