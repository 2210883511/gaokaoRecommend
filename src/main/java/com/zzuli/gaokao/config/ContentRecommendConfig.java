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
import com.zzuli.gaokao.vo.UniversityDataVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.ArrayList;
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

            wordVectorModel = new WordVectorModel("./src/main/resources/static/ok.txt");
        } catch (IOException e) {
            log.error("内容数据模型加载失败！找不到./src/main/resources/ok.txt");
        }
        DocVectorModel model = new DocVectorModel(wordVectorModel);
        List<University> universityList = universityMapper.selectList(null);
        List<UniversityTags> universityTagsList = tagsMapper.selectList(null);
        List<UniversityInfo> universityInfos = infoMapper.selectList(null);
        ArrayList<UniversityDataVo> list = new ArrayList<>();
        for (University university : universityList) {
            for (UniversityTags tags : universityTagsList) {
                if(university.getSchoolId().equals(tags.getSchoolId())){
                    UniversityDataVo vo = new UniversityDataVo();
                    vo.setUniversity(university);
                    vo.setTags(tags);
                    list.add(vo);
                    break;
                }
            }
        }
        for (UniversityDataVo vo : list) {
            for (UniversityInfo info : universityInfos) {
                if(vo.getSchoolId().equals(info.getSchoolId())){
                    vo.setInfo(info);
                    break;
                }
            }
        }
        StringBuilder tem = null;
        for (UniversityDataVo vo : list) {
            tem = new StringBuilder();
            String belong = vo.getBelong();
            String content = vo.getContent();
            String schoolName = vo.getSchoolName();
            String cityName = vo.getCityName();
            String schoolNatureName = vo.getSchoolNatureName(); // 公办
            String dualClassName = vo.getDualClassName(); // 双一流
            String schoolTypeName = vo.getSchoolTypeName();// 普通本科 专科高职
            String typeName = vo.getTypeName();   // 理工类
            Integer f211 = vo.getF211();
            Integer f985 = vo.getF985();
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
            if(f985 != null){
                if (f985 == 1) {
                    tem.append("985").append(" ");
                }
            }
            if(f211 != null){
                if (f211 == 1) {
                    tem.append("211").append(" ");
                }
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
            model.addDocument(vo.getSchoolId(),tem.toString());

        }

        return model;
    }

    // 这里是部署服务器用的
////    @Bean
////    public DocVectorModel getWordVectorModel(){
////        WordVectorModel wordVectorModel = null;
////        try {
////
////            wordVectorModel = new WordVectorModel("/opt/ok.txt");
//        } catch (IOException e) {
//            log.error("内容数据模型加载失败！找不到/opt/ok.txt");
//        }
//        DocVectorModel model = new DocVectorModel(wordVectorModel);
//        List<University> universityList = universityMapper.selectList(null);
//        List<UniversityTags> universityTagsList = tagsMapper.selectList(null);
//        List<UniversityInfo> universityInfos = infoMapper.selectList(null);
//        ArrayList<UniversityDataVo> list = new ArrayList<>();
//        for (University university : universityList) {
//            for (UniversityTags tags : universityTagsList) {
//                if(university.getSchoolId().equals(tags.getSchoolId())){
//                    UniversityDataVo vo = new UniversityDataVo();
//                    vo.setUniversity(university);
//                    vo.setTags(tags);
//                    list.add(vo);
//                    break;
//                }
//            }
//        }
//        for (UniversityDataVo vo : list) {
//            for (UniversityInfo info : universityInfos) {
//                if(vo.getSchoolId().equals(info.getSchoolId())){
//                    vo.setInfo(info);
//                    break;
//                }
//            }
//        }
//        StringBuilder tem = null;
//        for (UniversityDataVo vo : list) {
//            tem = new StringBuilder();
//            String belong = vo.getBelong();
//            String content = vo.getContent();
//            String schoolName = vo.getSchoolName();
//            String cityName = vo.getCityName();
//            String schoolNatureName = vo.getSchoolNatureName(); // 公办
//            String dualClassName = vo.getDualClassName(); // 双一流
//            String schoolTypeName = vo.getSchoolTypeName();// 普通本科 专科高职
//            String typeName = vo.getTypeName();   // 理工类
//            Integer f211 = vo.getF211();
//            Integer f985 = vo.getF985();
//            if (!StringUtils.isBlank(schoolTypeName)) {
//                tem.append(schoolTypeName).append(" ");
//            }
//            if (!StringUtils.isBlank(schoolNatureName)) {
//                tem.append(schoolNatureName).append(" ");
//            }
//
//            if (!StringUtils.isBlank(dualClassName)) {
//                tem.append(dualClassName).append(" ");
//            }
//
//            if (!StringUtils.isBlank(typeName)) {
//                tem.append(typeName).append(" ");
//            }
//            if(f985 != null){
//                if (f985 == 1) {
//                    tem.append("985").append(" ");
//                }
//            }
//            if(f211 != null){
//                if (f211 == 1) {
//                    tem.append("211").append(" ");
//                }
//            }
//            if (!StringUtils.isBlank(cityName)) {
//                tem.append(cityName).append(" ");
//            }
//            if (!StringUtils.isBlank(schoolName)) {
//                tem.append(schoolName).append(" ");
//            }
//
//            if (!StringUtils.isBlank(belong)) {
//                tem.append(belong).append(" ");
//            }
//            if (!StringUtils.isBlank(content)) {
//                tem.append(content).append(" ");
//            }
//            model.addDocument(vo.getSchoolId(),tem.toString());
//
//        }
//
//        return model;
//    }

}
