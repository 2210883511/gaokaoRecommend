package com.zzuli.gaokao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zzuli.gaokao.bean.Tags;
import com.zzuli.gaokao.bean.UniversityInfo;
import com.zzuli.gaokao.bean.UniversityTags;
import com.zzuli.gaokao.mapper.TagsMapper;
import com.zzuli.gaokao.mapper.UniversityInfoMapper;
import com.zzuli.gaokao.mapper.UniversityTagsMapper;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
public class TagsTests2 {

    @Autowired
    private UniversityTagsMapper mapper;

    @Autowired
    private TagsMapper tagsMapper;

    @Autowired
    private UniversityInfoMapper infoMapper;

    @Test
    public void test(){
        List<UniversityTags> universityTags = mapper.selectList(new QueryWrapper<UniversityTags>().select("DISTINCT school_nature_name"));
        List<String> collect1 = universityTags
                .stream()
                .map(UniversityTags::getSchoolNatureName)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toList());
        System.out.println(collect1);
        for (String s : collect1) {
            Tags tags = new Tags();
            tags.setName(s);
            tags.setStatus(1);
            tags.setType(2);
            tagsMapper.insert(tags);
        }
    }

    @Test
    public void test2(){
        List<UniversityInfo> infos = infoMapper.selectList(new QueryWrapper<UniversityInfo>()
                .select("distinct belong")
                .ne("belong", " "));
        List<String> belongs = infos.stream()
                .map(UniversityInfo::getBelong)
                .collect(Collectors.toList());
        System.out.println(belongs );
        for (String belong : belongs) {
            int i = belong.lastIndexOf("大学");
            if(i < 0){
                Tags tag = new Tags();
                tag.setName(belong);
                tag.setStatus(1);
                tag.setType(4);
                tagsMapper.insert(tag);
            }
        }

    }


}
