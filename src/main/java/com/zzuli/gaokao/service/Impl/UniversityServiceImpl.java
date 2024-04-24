package com.zzuli.gaokao.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzuli.gaokao.bean.University;
import com.zzuli.gaokao.bean.UniversityImg;
import com.zzuli.gaokao.bean.UniversityTags;
import com.zzuli.gaokao.common.Result;
import com.zzuli.gaokao.mapper.UniversityImgMapper;
import com.zzuli.gaokao.mapper.UniversityMapper;
import com.zzuli.gaokao.mapper.UniversityTagsMapper;
import com.zzuli.gaokao.service.UniversityService;
import com.zzuli.gaokao.vo.UniversityVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


@Service
public class UniversityServiceImpl extends ServiceImpl<UniversityMapper, University> implements UniversityService {

        @Autowired
        private UniversityMapper universityMapper;

        @Autowired
        private UniversityTagsMapper universityTagsMapper;

        @Autowired
        private UniversityImgMapper imgMapper;


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

        /*
         * @Description: 获取高校列表，包含基本信息、标签信息、校园风光
         * @Date:   2024/4/24 13:32
         * @Param:  [current, size, schoolName, provinceId]
         * @Return: java.util.List<com.zzuli.gaokao.vo.UniversityVo>
         */
        public Result getUniversityVoList(Integer current, Integer size, String schoolName, Integer provinceId){
            Page<University> page = new Page<>(current, size);
            QueryWrapper<University> wrapper = new QueryWrapper<>();
            wrapper.select("school_id","school_name","header_url","town_name","city_name");
            if(StringUtils.isNotBlank(schoolName)){
                wrapper.like("school_name",schoolName);
            }
            if(provinceId != null){
                wrapper.eq("province_Id",provinceId);
            }
            Page<University> universityPage =  universityMapper.selectPage(page,wrapper);
            List<University> uvList = universityPage.getRecords();
            long total = universityPage.getTotal();
            // 获取ids
            List<Integer> ids = uvList.stream()
                    .map(University::getSchoolId)
                    .collect(Collectors.toList());
            List<UniversityTags> tagsList = universityTagsMapper.selectList(new QueryWrapper<UniversityTags>()
                    .in("school_id", ids));
            List<UniversityImg> imgList = imgMapper.selectList(new QueryWrapper<UniversityImg>()
                    .select("school_id", "url")
                    .orderByDesc("rank")
                    .in("school_id", ids)
            );

            List<UniversityVo> voList = uvList.stream()
                    .map((university) -> {
                        UniversityVo vo = null;
                        for (UniversityTags tags : tagsList) {
                            if (university.getSchoolId().equals(tags.getSchoolId())) {
                                vo = new UniversityVo();
                                vo.setUniversity(university);
                                vo.setTag(tags);
                                break;
                            }
                        }
                        return vo;
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            for (UniversityVo vo : voList) {
                for (UniversityImg img : imgList) {
                    if(vo.getSchoolId().equals(img.getSchoolId())){
                        vo.setUrl(img.getUrl());
                        break;
                    }
                }
            }
            HashMap<String, Object> map = new HashMap<>();
            map.put("universityVoList",voList);
            map.put("total",total);
            return Result.success(map);
        }

        /*
         * @Description: 获取单个的universityVo
         * @Date:   2024/4/24 13:45
         * @Param:  
         * @Return: 
         */
        public UniversityVo getUniversityVo(Integer schoolId){
            University university = universityMapper.selectById(schoolId);
            UniversityTags tags = universityTagsMapper.selectOne(new QueryWrapper<UniversityTags>().eq("school_id", schoolId));
            UniversityVo vo = new UniversityVo();
            vo.setUniversity(university);
            vo.setTag(tags);
            return vo;
        }
        
        
        /*
         * @Description: 根据推荐高校ids来获取推荐高校列表，构建universityVo
         * @Date:   2024/4/24 13:56
         * @Param:  [schoolIds]
         * @Return: java.util.List<com.zzuli.gaokao.vo.UniversityVo>
         */
        public List<UniversityVo> getUniversityVoListById(List<Object> schoolIds){
            if(schoolIds == null){
                return null;
            }

            if(schoolIds.isEmpty()){
                return null;
            }
            List<University> uvList = universityMapper.selectList(new QueryWrapper<University>()
                .select("school_id", "school_name", "header_url", "town_name", "city_name")
                .in("school_id", schoolIds));
            List<UniversityTags> tagsList = universityTagsMapper.selectList(new QueryWrapper<UniversityTags>()
                .in("school_id", schoolIds));
            List<UniversityImg> imgList = imgMapper.selectList(new QueryWrapper<UniversityImg>()
                .select("school_id", "url")
                .orderByDesc("rank")
                .in("school_id", schoolIds));

        List<UniversityVo> voList = uvList.stream()
                .map((university) -> {
                    UniversityVo vo = null;
                    for (UniversityTags tags : tagsList) {
                        if (university.getSchoolId().equals(tags.getSchoolId())) {
                            vo = new UniversityVo();
                            vo.setUniversity(university);
                            vo.setTag(tags);
                            break;
                        }
                    }
                    return vo;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        for (UniversityVo vo : voList) {
            for (UniversityImg img : imgList) {
                if(vo.getSchoolId().equals(img.getSchoolId())){
                    vo.setUrl(img.getUrl());
                    break;
                }
            }
        }
        return voList;
    }




}
