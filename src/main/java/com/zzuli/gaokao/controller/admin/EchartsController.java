package com.zzuli.gaokao.controller.admin;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zzuli.gaokao.bean.Provinces;
import com.zzuli.gaokao.bean.UniversityTags;
import com.zzuli.gaokao.common.Result;
import com.zzuli.gaokao.service.Impl.ProvincesServiceImpl;
import com.zzuli.gaokao.service.Impl.UniversityTagsServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/admin/echarts")
public class EchartsController {

    @Autowired
    private ProvincesServiceImpl provincesService;

    @Autowired
    private UniversityTagsServiceImpl tagsService;

    @GetMapping("/data")
    public Result getProvinces(){
        List<Provinces> list = provincesService.list(new QueryWrapper<Provinces>().select("name","id").orderByDesc("id"));
        List<UniversityTags> tagsList = tagsService.list();
        HashMap<Integer, Map<String,Integer>> hashMap = new HashMap<>();
        for (Provinces provinces : list) {
            HashMap<String, Integer> map = getStringIntegerHashMap(provinces, tagsList);
            hashMap.put(provinces.getId(),map);
        }
        ArrayList<Object> f_985 = new ArrayList<>();
        ArrayList<Object> f_211 = new ArrayList<>();
        ArrayList<Object> common = new ArrayList<>();
        ArrayList<Object> special = new ArrayList<>();
        for (Provinces provinces : list) {
            Integer id = provinces.getId();
            Map<String, Integer> integerMap = hashMap.get(id);
            Integer count_1 = integerMap.get("985");
            Integer count_2 = integerMap.get("211");
            Integer count_3 = integerMap.get("普通本科");
            Integer count_4 = integerMap.get("专科");
            f_985.add(count_1);
            f_211.add(count_2);
            common.add(count_3);
            special.add(count_4);
        }
        Object[] array = list.stream()
                .map(Provinces::getName)
                .toArray();
        HashMap<String, Object> map = new HashMap<>();

        map.put("f_985",f_985);
        map.put("f_211",f_211);
        map.put("common",common);
        map.put("special",special);
        map.put("provinces",array);
        return Result.success(map);
    }

    private static HashMap<String, Integer> getStringIntegerHashMap(Provinces provinces, List<UniversityTags> tagsList) {
        HashMap<String, Integer> map = new HashMap<>();
        map.put("普通本科",0);
        map.put("专科",0);
        map.put("985",0);
        map.put("211",0);
        Integer id = provinces.getId();
        for (UniversityTags tags : tagsList) {
            if(tags.getProvinceId().equals(id)){
                if(tags.getF985() != null){
                    if(tags.getF985() == 1){
                        Integer count = map.get("985");
                        map.put("985",++count);
                    }
                }
                if(tags.getF211() != null){
                    if(tags.getF211() == 1){
                        Integer count = map.get("211");
                        map.put("211",++count);
                    }
                }
                if(tags.getSchoolTypeName() != null){
                    if(tags.getSchoolTypeName().equals("普通本科")){
                        Integer count = map.get("普通本科");
                        map.put("普通本科",++count);
                    }
                    if(tags.getSchoolTypeName().equals("专科（高职）")){
                        Integer count = map.get("专科");
                        map.put("专科",++count);
                    }

                }
            }
        }
        return map;
    }
}
