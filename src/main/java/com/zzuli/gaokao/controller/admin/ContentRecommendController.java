package com.zzuli.gaokao.controller.admin;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hankcs.hanlp.HanLP;

import com.hankcs.hanlp.dictionary.CustomDictionary;
import com.hankcs.hanlp.mining.word.TfIdfCounter;
import com.hankcs.hanlp.mining.word2vec.DocVectorModel;
import com.hankcs.hanlp.mining.word2vec.Word2VecTrainer;
import com.hankcs.hanlp.mining.word2vec.WordVectorModel;
import com.hankcs.hanlp.seg.common.Term;
import com.zzuli.gaokao.bean.*;
import com.zzuli.gaokao.common.Result;
import com.zzuli.gaokao.mapper.UniversityImgMapper;
import com.zzuli.gaokao.service.ProvincesService;
import com.zzuli.gaokao.service.UniversityInfoService;
import com.zzuli.gaokao.service.UniversityService;
import com.zzuli.gaokao.service.UniversityTagsService;
import com.zzuli.gaokao.vo.StopWords;
import com.zzuli.gaokao.vo.TfIdfVo;
import com.zzuli.gaokao.vo.UniversityDataVo;
import com.zzuli.gaokao.vo.UniversityVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/content/recommend")
@Slf4j
public class ContentRecommendController {

    @Autowired
    private UniversityService universityService;

    @Autowired
    private UniversityTagsService tagsService;

    @Autowired
    private ProvincesService provincesService;

    @Autowired
    private UniversityInfoService infoService;

    @Autowired
    private UniversityImgMapper imgMapper;

    @Autowired
    private StopWords stopWords;

    @Autowired
    private DocVectorModel model;
    
    
    /*
     * @Description: 生成训练数据
     * @Date:   2024/4/26 20:23
     * @Param:  
     * @Return: 
     */
    @GetMapping("/getTrainData")
    public Result getTrainData(){
        // 动态增加
        CustomDictionary.add("985");
        CustomDictionary.add("211");
        CustomDictionary.add("双一流");
        FileWriter writer = null;
        BufferedWriter bufferedWriter = null;
        try {
            writer = new FileWriter("./src/main/resources/train.txt");
            bufferedWriter = new BufferedWriter(writer);
        } catch (IOException e) {
            log.error("文件读取失败{}",e.getMessage());
        }

        // 所有的大学 包含基本信息、详细信息、高校标签、省份
        List<University> universityList = universityService.list(
                new QueryWrapper<University>()
                        .select("school_id","school_name,province_id,city_name,town_name"));
        List<UniversityInfo> infoList = infoService.list(new QueryWrapper<UniversityInfo>()
                .select("school_id", "content", "belong"));
        List<UniversityTags> tagsList = tagsService.list(new QueryWrapper<UniversityTags>()
                .select("school_id", "school_type_name", "school_nature_name", "dual_class_name", "type_name",
                        "f985", "f211"));
        List<Provinces> provinces = provincesService.list();

        ArrayList<UniversityDataVo> list = new ArrayList<>();
        /* 构造vo对象开始*/
        for (University university : universityList) {
            for (UniversityTags tags : tagsList) {
                if(university.getSchoolId().equals(tags.getSchoolId())){
                    UniversityDataVo vo = new UniversityDataVo();
                    vo.setUniversity(university);
                    vo.setTags(tags);
                    list.add(vo);
                }
            }
        }
        for (UniversityDataVo vo : list) {
            for (UniversityInfo info : infoList) {
                if(vo.getSchoolId().equals(info.getSchoolId())){
                    vo.setInfo(info);
                }
            }
        }
        for (UniversityDataVo vo : list) {
            for (Provinces province : provinces) {
                if(vo.getProvinceId().equals(province.getId())){
                    vo.setProvinces(province);
                }
            }
        }
        /*构造vo对象结束*/


        TfIdfCounter counter = new TfIdfCounter();
        StringBuilder builder =  null;
        for (UniversityDataVo vo : list) {

            builder = vo.getKeyWordsList();
            String schoolName = vo.getSchoolName();
            String content = vo.getContent();
            // 单独对高校名称进行分词
            if(StringUtils.isNotBlank(schoolName)){
                for (Term term : HanLP.segment(schoolName)) {
                    if(!stopWords.isContainKey(term.word)){
                        builder.append(term.word).append(" ");
                    }
                }
            }
            // 单独对高校详情进行分词，然后去除停用词
            if(StringUtils.isNotBlank(content)){
                for (Term term : HanLP.segment(content)) {
                    if(!stopWords.isContainKey(term.word)){
                        builder.append(term.word).append(" ");
                    }
                }

            }
            // 添加文档到counter中
            counter.add(vo.getSchoolId(),builder.toString());
        }

         // 计算tf-idf值 并取出其中权重较大的前50个
        counter.compute();
        HashMap<Integer, Object> map = new HashMap<>();
        for (UniversityDataVo vo : list) {
            StringBuilder stringBuilder = vo.getKeyWordsList();
            List<Map.Entry<String, Double>> keywordsOf = counter.getKeywordsOf(vo.getSchoolId(),50);
            for (Map.Entry<String, Double> stringDoubleEntry : keywordsOf) {
                String key = stringDoubleEntry.getKey();
                stringBuilder.append(key).append(" ");
            }
            stringBuilder.append("\n");
            try {
                if (writer != null) {
                    writer.write(stringBuilder.toString());
                    writer.flush();
                }

            } catch (IOException e) {
                log.error("文件写入失败！{}",e.getMessage());
            }
        }
        return Result.success();
    }




    @GetMapping("/trainModel")
    public Result train(){
        Word2VecTrainer trainer = new Word2VecTrainer();
        trainer.setLayerSize(100);
        WordVectorModel train = trainer.train("./src/main/resources/train.txt","./src/main/resources/ok.txt");
        DocVectorModel model = new DocVectorModel(train);
        System.out.println(model);
        return Result.success("训练成功！");
    }


    @GetMapping("/getTags")
    public Result getTags(){
        // 动态增加
        CustomDictionary.add("985");
        CustomDictionary.add("211");
        CustomDictionary.add("双一流");
        // 所有的大学 包含基本信息、详细信息、高校标签、省份
        List<University> universityList = universityService.list(
                new QueryWrapper<University>()
                        .select("school_id","school_name,province_id,city_name,town_name"));
        List<UniversityInfo> infoList = infoService.list(new QueryWrapper<UniversityInfo>()
                .select("school_id", "content", "belong"));
        List<UniversityTags> tagsList = tagsService.list(new QueryWrapper<UniversityTags>()
                .select("school_id", "school_type_name", "school_nature_name", "dual_class_name", "type_name",
                        "f985", "f211"));
        List<Provinces> provinces = provincesService.list();

        ArrayList<UniversityDataVo> list = new ArrayList<>();
        /* 构造vo对象开始*/
        for (University university : universityList) {
            for (UniversityTags tags : tagsList) {
                if(university.getSchoolId().equals(tags.getSchoolId())){
                    UniversityDataVo vo = new UniversityDataVo();
                    vo.setUniversity(university);
                    vo.setTags(tags);
                    list.add(vo);
                }
            }
        }
        for (UniversityDataVo vo : list) {
            for (UniversityInfo info : infoList) {
                if(vo.getSchoolId().equals(info.getSchoolId())){
                    vo.setInfo(info);
                }
            }
        }
        for (UniversityDataVo vo : list) {
            for (Provinces province : provinces) {
                if(vo.getProvinceId().equals(province.getId())){
                    vo.setProvinces(province);
                }
            }
        }
        /*构造vo对象结束*/


        TfIdfCounter counter = new TfIdfCounter();
        StringBuilder builder =  null;
        for (UniversityDataVo vo : list) {

            builder = vo.getKeyWordsList();
            String schoolName = vo.getSchoolName();
//            // 单独对高校名称进行分词
//            if(StringUtils.isNotBlank(schoolName)){
//                for (Term term : HanLP.segment(schoolName)) {
//                    if(!stopWords.isContainKey(term.word)){
//                        builder.append(term.word).append(" ");
//                    }
//                }
//            }
            // 添加文档到counter中
            counter.add(vo.getSchoolId(),builder.toString());
        }
        counter.compute();
        ArrayList<TfIdfVo> tfIdfVos = new ArrayList<>();
        for (UniversityDataVo vo : list) {
            List<Map.Entry<String, Double>> keywordsOf = counter.getKeywordsOf(vo.getSchoolId(), 30);
            System.out.println(keywordsOf);
            for (Map.Entry<String, Double> stringDoubleEntry : keywordsOf) {
                 TfIdfVo tf = new TfIdfVo();
                 tf.setName(stringDoubleEntry.getKey());
                 tf.setValue(stringDoubleEntry.getValue());
                 tfIdfVos.add(tf);
            }
        }
        List<TfIdfVo> voList = new ArrayList<>(tfIdfVos.stream()
                .collect(Collectors.toMap(TfIdfVo::getName, tfIdfVo -> tfIdfVo, (t1, t2) -> t1))
                .values());
        HashMap<String, Object> map = new HashMap<>();
        map.put("tfIdfVos",voList);
        return Result.success(map);

    }


    @GetMapping("/recommendList")
    public Result getRecommend(Integer schoolId){

        List<Map.Entry<Integer, Float>> nearest = model.nearest(schoolId, 30);
        ArrayList<Integer> ids = new ArrayList<>();
        for (Map.Entry<Integer, Float> integerFloatEntry : nearest) {
            Integer key = integerFloatEntry.getKey();
            ids.add(key);
        }
        ArrayList<UniversityVo> voList = new ArrayList<>();
        if(!ids.isEmpty()){
            List<UniversityTags> tagsList = tagsService.list(new QueryWrapper<UniversityTags>()
                    .in("school_id", ids));
            List<University> universityList = universityService.list(new QueryWrapper<University>()
                    .in("school_id", ids));
            List<UniversityImg> imgList = imgMapper.selectList(new QueryWrapper<UniversityImg>()
                    .select("school_id", "url")
                    .orderByDesc("rank")
                    .in("school_id", ids));
            for (University university : universityList) {
                for (UniversityTags tags : tagsList) {
                    if(university.getSchoolId().equals(tags.getSchoolId())){
                        UniversityVo vo = new UniversityVo();
                        vo.setUniversity(university);
                        vo.setTag(tags);
                        voList.add(vo);
                        break;
                    }
                }
            }
            for (UniversityVo vo : voList) {
                for (UniversityImg img : imgList) {
                    if(vo.getSchoolId().equals(img.getSchoolId())){
                        vo.setUrl(img.getUrl());
                        break;
                    }
                }
            }
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("recommendList",voList);
        return Result.success(map);
    }






}
