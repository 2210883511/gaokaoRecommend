package com.zzuli.gaokao;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zzuli.gaokao.bean.University;
import com.zzuli.gaokao.bean.UniversityTags;
import com.zzuli.gaokao.mapper.UniversityMapper;
import com.zzuli.gaokao.service.Impl.UniversityServiceImpl;
import com.zzuli.gaokao.service.Impl.UniversityTagsServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
@Deprecated
public class TagsTest {

    @Autowired
    UniversityServiceImpl universityService;

    @Autowired
    UniversityTagsServiceImpl tagsService;
    public void get(){
        List<UniversityTags> tagsList = tagsService.list(new QueryWrapper<UniversityTags>().select("id,school_id"));
        List<University> universityList = universityService.list(new QueryWrapper<University>()
                .select("school_id,province_id"));
        for (UniversityTags universityTag: tagsList) {
            Integer schoolId = universityTag.getSchoolId();
            for (University university : universityList) {
                    if(university.getSchoolId().equals(schoolId)){
                        universityTag.setProvinceId(university.getProvinceId());
                    }
            }
        }
        tagsService.updateBatchById(tagsList);


    }
}
