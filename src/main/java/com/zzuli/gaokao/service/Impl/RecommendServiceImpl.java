package com.zzuli.gaokao.service.Impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hankcs.hanlp.mining.word2vec.DocVectorModel;
import com.hankcs.hanlp.mining.word2vec.WordVectorModel;
import com.zzuli.gaokao.bean.University;
import com.zzuli.gaokao.bean.UniversityInfo;
import com.zzuli.gaokao.bean.UniversityTags;
import com.zzuli.gaokao.common.Result;
import com.zzuli.gaokao.mapper.UniversityInfoMapper;
import com.zzuli.gaokao.mapper.UniversityMapper;
import com.zzuli.gaokao.mapper.UniversityTagsMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

@Service
public class RecommendServiceImpl {

    @Autowired
    private UniversityMapper universityMapper;

    @Autowired
    private UniversityTagsMapper tagsMapper;

    @Autowired
    private UniversityInfoMapper infoMapper;

    
    public Result getRecommend(Integer id) throws IOException {
        WordVectorModel wordVectorModel = new WordVectorModel("./src/main/resources/ok.txt");
        DocVectorModel model = new DocVectorModel(wordVectorModel);
        List<University> universityList = universityMapper.selectList(null);
        List<UniversityTags> universityTagsList = tagsMapper.selectList(null);
        List<UniversityInfo> universityInfos = infoMapper.selectList(null);
        universityInfos.sort(Comparator.comparingInt(UniversityInfo::getSchoolId));
        universityList.sort(Comparator.comparingInt(University::getSchoolId));
        universityTagsList.sort((Comparator.comparingInt(UniversityTags::getSchoolId)));
        StringBuilder tem = null;
        for (int i = 0; i < universityList.size(); i++) {
            tem = new StringBuilder();
            University university = universityList.get(i);
            UniversityTags tags = universityTagsList.get(i);
            UniversityInfo info = universityInfos.get(i);
            String belong = info.getBelong();
            String content = info.getContent();
            String schoolName = university.getSchoolName();
            String cityName = university.getCityName();
            String schoolNatureName = tags.getSchoolNatureName(); // 公办
            String dualClassName = tags.getDualClassName(); // 双一流
            String schoolTypeName = tags.getSchoolTypeName();// 普通本科 专科高职
            String typeName = tags.getTypeName();   // 理工类
            Integer f211 = tags.getF211();
            Integer f985 = tags.getF985();
            if (!StringUtils.isBlank(schoolTypeName)) {
                tem.append(schoolTypeName).append(" ");
            }
            if (!StringUtils.isBlank(schoolNatureName)) {
                tem.append(schoolNatureName).append(" ");
            }

            if (!StringUtils.isBlank(dualClassName)) {
                tem.append(dualClassName).append(" ");
            }

            if (!StringUtils.isBlank(typeName)) {
                tem.append(typeName).append(" ");
            }
            if (f985 == 1) {
                tem.append("985").append(" ");
            }
            if (f211 == 1) {
                tem.append("211").append(" ");
            }
            if (!StringUtils.isBlank(cityName)) {
                tem.append(cityName).append(" ");
            }
            if (!StringUtils.isBlank(schoolName)) {
                tem.append(schoolName).append(" ");
            }

            if (!StringUtils.isBlank(belong)) {
                tem.append(belong).append(" ");
            }
            if (!StringUtils.isBlank(content)) {
                tem.append(content).append(" ");
            }
            model.addDocument(university.getSchoolId(),tem.toString());

        }
        List<Map.Entry<Integer, Float>> nearest = model.nearest(id,20);;
        ArrayList<Map<String,Object>> array = new ArrayList<>();
        for (Map.Entry<Integer, Float> integerFloatEntry : nearest) {
            HashMap<String, Object> map = new HashMap<>();
            Integer tempId = integerFloatEntry.getKey();
            Float value = integerFloatEntry.getValue();
            University university = universityMapper.selectOne(new QueryWrapper<University>().select("school_name").eq("school_id", tempId));
            map.put("学校名称",university.getSchoolName());
            map.put("高校id", tempId);
            map.put("相似度：",value);
            array.add(map);
        }
        HashMap<String, Object> data = new HashMap<>();
        data.put("data",array);
        return Result.success(data);
    }

    public Result getRecommend(String document) throws IOException {
        WordVectorModel wordVectorModel = new WordVectorModel("./src/main/resources/ok.txt");
        DocVectorModel model = new DocVectorModel(wordVectorModel);
        List<University> universityList = universityMapper.selectList(null);
        List<UniversityTags> universityTagsList = tagsMapper.selectList(null);
        List<UniversityInfo> universityInfos = infoMapper.selectList(null);
        universityInfos.sort(Comparator.comparingInt(UniversityInfo::getSchoolId));
        universityList.sort(Comparator.comparingInt(University::getSchoolId));
        universityTagsList.sort((Comparator.comparingInt(UniversityTags::getSchoolId)));
        StringBuilder tem = null;
        for (int i = 0; i < universityList.size(); i++) {
            tem = new StringBuilder();
            University university = universityList.get(i);
            UniversityTags tags = universityTagsList.get(i);
            UniversityInfo info = universityInfos.get(i);
            String belong = info.getBelong();
            String content = info.getContent();
            String schoolName = university.getSchoolName();
            String cityName = university.getCityName();
            String schoolNatureName = tags.getSchoolNatureName(); // 公办
            String dualClassName = tags.getDualClassName(); // 双一流
            String schoolTypeName = tags.getSchoolTypeName();// 普通本科 专科高职
            String typeName = tags.getTypeName();   // 理工类
            Integer f211 = tags.getF211();
            Integer f985 = tags.getF985();
            if (!StringUtils.isBlank(schoolTypeName)) {
                tem.append(schoolTypeName).append(" ");
            }
            if (!StringUtils.isBlank(schoolNatureName)) {
                tem.append(schoolNatureName).append(" ");
            }

            if (!StringUtils.isBlank(dualClassName)) {
                tem.append(dualClassName).append(" ");
            }

            if (!StringUtils.isBlank(typeName)) {
                tem.append(typeName).append(" ");
            }
            if (f985 == 1) {
                tem.append("985").append(" ");
            }
            if (f211 == 1) {
                tem.append("211").append(" ");
            }
            if (!StringUtils.isBlank(cityName)) {
                tem.append(cityName).append(" ");
            }
            if (!StringUtils.isBlank(schoolName)) {
                tem.append(schoolName).append(" ");
            }

            if (!StringUtils.isBlank(belong)) {
                tem.append(belong).append(" ");
            }
            if (!StringUtils.isBlank(content)) {
                tem.append(content).append(" ");
            }
            model.addDocument(university.getSchoolId(),tem.toString());

        }
        List<Map.Entry<Integer, Float>> nearest = model.nearest(document,20);
        ArrayList<Map<String,Object>> array = new ArrayList<>();
        for (Map.Entry<Integer, Float> integerFloatEntry : nearest) {
            HashMap<String, Object> map = new HashMap<>();
            Integer tempId = integerFloatEntry.getKey();
            Float value = integerFloatEntry.getValue();
            University university = universityMapper.selectOne(new QueryWrapper<University>().select("school_name").eq("school_id", tempId));
            map.put("学校名称",university.getSchoolName());
            map.put("高校id", tempId);
            map.put("相似度：",value);
            array.add(map);
        }
        HashMap<String, Object> data = new HashMap<>();
        data.put("data",array);
        return Result.success(data);
    }




}
