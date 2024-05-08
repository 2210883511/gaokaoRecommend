package com.zzuli.gaokao.config;

import com.hankcs.hanlp.mining.word2vec.DocVectorModel;
import com.hankcs.hanlp.mining.word2vec.WordVectorModel;
import com.zzuli.gaokao.bean.University;
import com.zzuli.gaokao.bean.UniversityInfo;
import com.zzuli.gaokao.bean.UniversityTags;
import com.zzuli.gaokao.mapper.UniversityImgMapper;
import com.zzuli.gaokao.mapper.UniversityInfoMapper;
import com.zzuli.gaokao.mapper.UniversityMapper;
import com.zzuli.gaokao.mapper.UniversityTagsMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

@Configuration
@Slf4j
public class ContentRecommendConfig {

    @Autowired
    private UniversityMapper universityMapper;

    @Autowired
    private UniversityTagsMapper tagsMapper;

    @Autowired
    private UniversityInfoMapper infoMapper;

    @Bean
    public DocVectorModel getWordVectorModel(){
        WordVectorModel wordVectorModel = null;
        try {
            wordVectorModel = new WordVectorModel("./src/main/resources/ok.txt");
        } catch (IOException e) {
            log.error("内容数据模型加载失败！找不到./src/main/resources/ok.txt");
        }
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


        return model;
    }

}
