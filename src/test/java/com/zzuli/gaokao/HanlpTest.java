package com.zzuli.gaokao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hankcs.hanlp.mining.word.WordInfo;
import com.hankcs.hanlp.mining.word2vec.DocVectorModel;
import com.hankcs.hanlp.mining.word2vec.Word2VecTrainer;
import com.hankcs.hanlp.mining.word2vec.WordVectorModel;
import com.hankcs.hanlp.seg.Segment;
import com.zzuli.gaokao.bean.*;
import com.zzuli.gaokao.mapper.*;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.classification.features.TfIdfFeatureWeighter;
import com.hankcs.hanlp.mining.word.TfIdf;
import com.hankcs.hanlp.mining.word.TfIdfCounter;

import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.tokenizer.StandardTokenizer;
import org.apache.commons.lang3.StringUtils;
import org.apache.mahout.math.SequentialAccessSparseVector;
import org.apache.mahout.vectorizer.TFIDF;
import org.apache.mahout.vectorizer.tfidf.TFIDFPartialVectorReducer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;


@SpringBootTest
public class HanlpTest {


    @Autowired
    private UniversityTagsMapper mapper;

    @Autowired
    private UniversityMapper universityMapper;

    @Autowired
    private ProvincesMapper provincesMapper;

    @Autowired
    private UniversityInfoMapper infoMapper;

    @Autowired
    private UniversityProfileMapper profileMapper;

    @Test
    public void get  () throws IOException {


        List<UniversityTags> universityTags = mapper.selectList(null);
        StringBuilder tem  = null;
        TfIdfCounter counter = new TfIdfCounter();
        FileWriter writer = new FileWriter("./src/main/resources/train.txt");
        BufferedWriter bufferedWriter = new BufferedWriter(writer);

        for (UniversityTags universityTag : universityTags) {
            University university = universityMapper.selectById(universityTag.getSchoolId());
            Provinces provinces = provincesMapper.selectById(university.getProvinceId());
            UniversityInfo info = infoMapper.selectOne(new QueryWrapper<UniversityInfo>().select("belong,content").eq("school_id", universityTag.getSchoolId()));
            String name = provinces.getName();
            String belong = info.getBelong();
            String content = info.getContent();
            String schoolName = university.getSchoolName();
            String cityName = university.getCityName();
            String schoolNatureName = universityTag.getSchoolNatureName(); // 公办
            String dualClassName = universityTag.getDualClassName(); // 双一流
            String schoolTypeName = universityTag.getSchoolTypeName();// 普通本科 专科高职
            String typeName = universityTag.getTypeName();   // 理工类
            Integer f211 = universityTag.getF211();
            Integer f985 = universityTag.getF985();

            tem = new StringBuilder();
            tem.append(universityTag.getSchoolId()).append(" ");
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
            if (!StringUtils.isBlank(name)) {

                tem.append(name).append(" ");
            }
            if (!StringUtils.isBlank(schoolName)) {
                for (Term term : HanLP.segment(schoolName)) {
                    tem.append(term.word).append(" ");
                }


            }

            if (!StringUtils.isBlank(belong)) {
                tem.append(belong).append(" ");
            }
            if (!StringUtils.isBlank(content)) {
                for (Term term : HanLP.segment(content)) {
                    tem.append(term.word).append(" ");
                }
            }
            String s = tem.toString().replaceAll("，"," ")
                    .replaceAll("。"," ")
                    .replaceAll("（"," ")
                    .replaceAll("）"," ")
                    .replaceAll("——"," ")
                    .replaceAll("；"," ");
            bufferedWriter.write(s+"\n");
            writer.flush();
        }



    }



    @Test
    public void test(){
        TFIDF tfidf = new TFIDF();
        TFIDFPartialVectorReducer vectorReducer = new TFIDFPartialVectorReducer();
        SequentialAccessSparseVector vector = new SequentialAccessSparseVector();
    }


/*
 * @Description: 将得到tf-idf标签和权重存储到数据库，为高校画像
 * @Date:   2024/3/27 20:07
 * @Param:  [list, tfidf, profileMapper]
 * @Return: void
 */
    public static void insertInfo(List<University> list,Map<Object,Map<String,Double>> tfidf,UniversityProfileMapper profileMapper){
//         用于将标签和权重添加到数据库中
        int i = 0;
        for (Map<String, Double> value : tfidf.values()) {
            ObjectMapper objectMapper = new ObjectMapper();
            University university = list.get(i);
            try {
                String json = objectMapper.writeValueAsString(value);
                UniversityProfile profile = new UniversityProfile();
                profile.setSchoolId(university.getSchoolId());
                profile.setSchoolName(university.getSchoolName());
                profile.setTags(json);
                profileMapper.insert(profile);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            i++;
        }
    }

}
