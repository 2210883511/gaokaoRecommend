package com.zzuli.gaokao.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.mining.word.TfIdfCounter;
import com.hankcs.hanlp.seg.common.Term;
import com.zzuli.gaokao.bean.Provinces;
import com.zzuli.gaokao.bean.University;
import com.zzuli.gaokao.bean.UniversityInfo;
import com.zzuli.gaokao.bean.UniversityTags;
import com.zzuli.gaokao.common.Result;
import com.zzuli.gaokao.mapper.*;
import com.zzuli.gaokao.vo.TfIdfVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class TFIDFController {
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



    @GetMapping("/getTfIdf")
    public Result getAllProfile() {
        List<UniversityTags> universityTags = mapper.selectList(null);
        StringBuilder tem = null;
        TfIdfCounter counter = new TfIdfCounter();
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
            String s = tem.toString().replaceAll("，", " ")
                    .replaceAll("。", " ")
                    .replaceAll("（", " ")
                    .replaceAll("）", " ")
                    .replaceAll("——", " ")
                    .replaceAll("；", " ");
            counter.add(universityTag.getSchoolId(),s);
        }
        List<Integer> schoolIds = universityTags.stream()
                .map(UniversityTags::getSchoolId)
                .collect(Collectors.toList());

        Map<Object, Map<String, Double>> compute = counter.compute();
        ArrayList<TfIdfVo> list = new ArrayList<>();
        List<Integer> collect = schoolIds.stream()
                .filter(x -> x <= 100)
                .collect(Collectors.toList());
        for (Integer schoolId : collect) {
            Map<String, Double> map = compute.get(schoolId);
            for (String s : map.keySet()) {
                TfIdfVo vo = new TfIdfVo();
                vo.setName(s);
                vo.setValue(map.get(s));
                list.add(vo);
            }
        }
        list.sort((o1, o2) -> (int) (o1.getValue() - o2.getValue()));
        HashMap<String, Object> map = new HashMap<>();
        map.put("list",list);
        return Result.success(map);


    }
}
