package com.zzuli.gaokao.controller.api;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zzuli.gaokao.bean.Master;
import com.zzuli.gaokao.bean.UniversityMaster;
import com.zzuli.gaokao.common.Result;
import com.zzuli.gaokao.service.*;
import com.zzuli.gaokao.service.Impl.UniversityServiceImpl;
import com.zzuli.gaokao.service.Impl.UniversityTagsServiceImpl;
import com.zzuli.gaokao.vo.UniversityMasterVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/uvMaster")
public class SchoolMasterHandler {

    @Autowired
    private UniversityService universityService;

    @Autowired
    private UniversityTagsService tagsService;

    @Autowired
    private UniversityMasterService uvMasterService;

    @Autowired
    private MasterService masterService;
    /*
     * @Description: 获取高校专业详情列表
     * @Date:   2024/4/18 11:10
     * @Param:  []
     * @Return: com.zzuli.gaokao.common.Result
     */
    @GetMapping("/getUvMasterVoList")
    public Result getUvMasterVo(Integer current, Integer size, Integer schoolId, String masterName, String level1Name, String type, String typeDetail){

        QueryWrapper<UniversityMaster> wrapper = new QueryWrapper<UniversityMaster>()
                .eq("school_id", schoolId);
        if(StringUtils.isNotBlank(masterName)){
            wrapper.like("special_name",masterName);
        }
        List<UniversityMaster> uvList = uvMasterService.list(wrapper);
        if(uvList.isEmpty())
            return Result.error("未查询到相关高校的专业信息");
        List<Integer> masterIds = uvList.stream()
                .map(UniversityMaster::getSpecialId)
                .collect(Collectors.toList());
        List<Master> masterList = masterService.list(new QueryWrapper<Master>()
                .select("type","type_detail","level1_name","limit_year","id","name")
                .in("id", masterIds));
        List<UniversityMasterVo> universityMasterVoList = uvList.stream()
                .map(uvMaster -> {
                    UniversityMasterVo universityMasterVo = null;
                    for (Master master : masterList) {

                        if (uvMaster.getSpecialId().equals(master.getId())) {
                            universityMasterVo = new UniversityMasterVo();
                            universityMasterVo.setUniversityMaster(uvMaster);
                            universityMasterVo.setMaster(master);
                        }
                    }
                    return universityMasterVo;
                })
                .filter(Objects::nonNull)
                .sorted(Comparator.comparingInt(UniversityMasterVo::getNationFeature))
                .collect(Collectors.toList());

        Integer total = null;
        List<UniversityMasterVo> realVoList = null;
        if(StringUtils.isBlank(type) && StringUtils.isBlank(typeDetail)  && StringUtils.isBlank(level1Name)){
            total = universityMasterVoList.size();
            realVoList = universityMasterVoList
                    .stream()
                    .skip((long) (current - 1) * size)
                    .limit(size)
                    .sorted(Comparator.comparingInt(UniversityMasterVo::getNationFeature))
                    .collect(Collectors.toList());
        }else{
            List<UniversityMasterVo> tmpList = universityMasterVoList
                    .stream()
                    .filter(vo -> vo.getLevel1Name().equals(level1Name)
                            && vo.getType().equals(type)
                            && vo.getTypeDetail().equals(typeDetail))
                    .sorted(Comparator.comparingInt(UniversityMasterVo::getNationFeature))
                    .collect(Collectors.toList());
            total  = tmpList.size();
            realVoList = tmpList.stream()
                    .skip((long) (current - 1) * size)
                    .limit(size)
                    .collect(Collectors.toList());
        }

        HashMap<String, Object> map = new HashMap<>();
        map.put("total",total);
        map.put("universityMasterVoList",realVoList);
        return Result.success(map);

    }

}
