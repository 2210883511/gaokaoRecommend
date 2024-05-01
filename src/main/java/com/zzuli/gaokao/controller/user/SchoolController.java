package com.zzuli.gaokao.controller.user;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zzuli.gaokao.bean.University;
import com.zzuli.gaokao.bean.UniversityTags;
import com.zzuli.gaokao.common.Result;
import com.zzuli.gaokao.service.Impl.UniversityServiceImpl;
import com.zzuli.gaokao.service.Impl.UniversityTagsServiceImpl;
import com.zzuli.gaokao.vo.UniversityVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@RestController
@RequestMapping("/api")
public class SchoolController {

    @Autowired
    private UniversityServiceImpl universityService;

    @Autowired
    private UniversityTagsServiceImpl universityTagsService;

    /*
     * @Description: 获取高校列表 可以根据 985 || 211 || 双一流进行查询 或者 高校类型 军事类 || 农林类等
     * @Date:   2024/5/1 16:59
     * @Param:  [page, size, f985, f211, dualClassName, typeName]
     * @Return: com.zzuli.gaokao.common.Result
     */
    @GetMapping("/schoolList")
    public Result getSchoolList(Integer page, Integer size,Integer f985,Integer f211,String dualClassName,String typeName){


        Page<UniversityTags> pageTags = new Page<>(page,size);
        QueryWrapper<UniversityTags> tagsQueryWrapper = new QueryWrapper<>();
        if(f985 != null){
            tagsQueryWrapper.eq("f985",f985);
        }
        if(f211 != null){
            tagsQueryWrapper.eq("f211",f211);
        }
        if(StringUtils.isNotBlank(dualClassName)){
            tagsQueryWrapper.eq("dual_class_name",dualClassName);
        }
        if(StringUtils.isNotBlank(typeName)){
            tagsQueryWrapper.eq("type_name",typeName);
        }
        Page<UniversityTags> tagsPage = universityTagsService.page(pageTags, tagsQueryWrapper);
        List<UniversityTags> tagsList = tagsPage.getRecords();
        long total = tagsPage.getTotal();
        List<Integer> ids = tagsList.stream()
                .map(UniversityTags::getSchoolId)
                .collect(Collectors.toList());

        if(ids.isEmpty()){
            return Result.error("未查询到数据！");
        }
        List<University> universityList = universityService.list(new QueryWrapper<University>()
                .select("school_id","school_name","city_name","town_name","header_url")
                .in("school_id", ids));
        ArrayList<UniversityVo> list = new ArrayList<>();
        for (UniversityTags tags : tagsList) {
            for (University university : universityList) {
                if (university.getSchoolId().equals(tags.getSchoolId())){
                    UniversityVo vo = new UniversityVo();
                    vo.setUniversity(university);
                    vo.setTag(tags);
                    list.add(vo);
                    break;
                }
            }
        }
        HashMap<String, Object> data = new HashMap<>();
        data.put("list",list);
        data.put("total",total);
        return  Result.success(data);
    }

}
