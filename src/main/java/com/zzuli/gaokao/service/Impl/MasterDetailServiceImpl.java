package com.zzuli.gaokao.service.Impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzuli.gaokao.bean.MasterDetail;
import com.zzuli.gaokao.mapper.MasterDetailMapper;
import com.zzuli.gaokao.service.MasterDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class MasterDetailServiceImpl extends ServiceImpl<MasterDetailMapper, MasterDetail> implements MasterDetailService {

    @Autowired
    private MasterDetailMapper detailMapper;

    @Override
    public Page<Map<String, Object>> findCustom(Page<Map<String,Object>> page,String masterName, String type, String typeDetail, String levelName) {

        return detailMapper.selectCustom(page, masterName, type, typeDetail, levelName);
    }
}
