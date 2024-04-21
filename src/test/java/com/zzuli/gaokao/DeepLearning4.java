package com.zzuli.gaokao;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.corpus.dictionary.TFDictionary;
import com.hankcs.hanlp.corpus.document.sentence.word.Word;
import com.hankcs.hanlp.mining.word.TfIdf;
import com.hankcs.hanlp.mining.word.TfIdfCounter;
import com.hankcs.hanlp.mining.word2vec.*;

import com.hankcs.hanlp.seg.common.Term;
import com.zzuli.gaokao.bean.University;
import com.zzuli.gaokao.bean.UniversityInfo;
import com.zzuli.gaokao.bean.UniversityTags;
import com.zzuli.gaokao.mapper.ProvincesMapper;
import com.zzuli.gaokao.mapper.UniversityInfoMapper;
import com.zzuli.gaokao.mapper.UniversityMapper;
import com.zzuli.gaokao.mapper.UniversityTagsMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

@SpringBootTest
public class DeepLearning4 {

    @Autowired
    private UniversityTagsMapper tagsMapper;
    @Autowired
    private UniversityMapper universityMapper;
    @Autowired
    private UniversityInfoMapper infoMapper;

    @Autowired
    private ProvincesMapper provincesMapper;

    Log log = LogFactory.getLog(DeepLearning4.class);

    @Test
    public void get(){

        List<Term> segment = HanLP.segment("郑州轻工业大学创建于1977年，原隶属于国家轻工业部，1998年转隶河南省人民政府。学校是河南省人民政府和国家烟草专卖局共建高校、河南省特色骨干大学建设高校。现有科学校区、东风校区和禹州实习实训基地，占地面积2200余亩。现有全日制本科生、研究生30000余人。有各类中外文纸质图书243万余册，电子图书860万余册。是河南省智慧校园建设示范学校。建校以来，学校牢");
        for (Term term : segment) {
            System.out.println(term.word);
        }
        System.out.println();
        TfIdfCounter counter = new TfIdfCounter();
        for (String keyword : counter.getKeywords(segment, 100)) {
            System.out.println(keyword);
        }
        Word2VecTrainer trainer = new Word2VecTrainer();
        trainer.setLayerSize(100);
        WordVectorModel train = trainer.train("./src/main/resources/train.txt","./src/main/resources/ok.txt");
        DocVectorModel model = new DocVectorModel(train);
        System.out.println(model);
    }

    @Test
    public void load() throws IOException {
        FileWriter writer = new FileWriter("./src/main/resources/university.txt");
        BufferedWriter bufferedWriter = new BufferedWriter(writer);

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
            System.out.println(tem.toString());
            model.addDocument(university.getSchoolId(),tem.toString());

        }
        Integer schoolId = 460;
        List<Map.Entry<Integer, Float>> nearest = model.nearest(schoolId,20);
        University one = universityMapper.selectOne(new QueryWrapper<University>().select("school_name").eq("school_id", schoolId));
        System.out.println("与" + one.getSchoolName() + "相似的大学有：");
        for (Map.Entry<Integer, Float> integerFloatEntry : nearest) {
            Integer id = integerFloatEntry.getKey();
            Float value = integerFloatEntry.getValue();
            University university = universityMapper.selectOne(new QueryWrapper<University>().select("school_name").eq("school_id", id));
            System.out.println("高校id: " + id + " 高校名称： " + university.getSchoolName() +  " 相似度: " + value);
        }
    }

}
