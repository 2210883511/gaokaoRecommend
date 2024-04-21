package com.zzuli.gaokao.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzuli.gaokao.bean.University;
import com.zzuli.gaokao.bean.UniversityTags;
import com.zzuli.gaokao.mapper.UniversityMapper;
import com.zzuli.gaokao.mapper.UniversityTagsMapper;
import com.zzuli.gaokao.service.UniversityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;
import java.util.Map;


@Service
public class UniversityServiceImpl extends ServiceImpl<UniversityMapper, University> implements UniversityService {

        @Autowired
        private UniversityMapper universityMapper;

        @Autowired
        private UniversityTagsMapper universityTagsMapper;


        @Transactional
        public List<Map<String,Object>> findUniversityInfo(Page<Map<String,Object>> page){
            QueryWrapper<University> wrapper = new QueryWrapper<>();
            wrapper.select("school_id","school_name","province_id","city_name","town_name","header_url");
            Page<Map<String, Object>> mapPage = universityMapper.selectMapsPage(page, wrapper);
            List<Map<String, Object>> records = mapPage.getRecords();
            for (Map<String, Object> record : records) {
                Object school_id = record.get("school_id");
                UniversityTags tags = universityTagsMapper.selectOne(new QueryWrapper<UniversityTags>().eq("school_id", school_id));
                record.put("tags",tags);
            }
            return records;
        }


        public List<Map<String,Object>> find(Page<Map<String,Object>> page){
            Page<Map<String, Object>> test = universityMapper.test(page);
            return test.getRecords();
        }

}
