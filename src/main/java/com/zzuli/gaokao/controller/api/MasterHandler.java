package com.zzuli.gaokao.controller.api;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zzuli.gaokao.bean.Master;
import com.zzuli.gaokao.bean.MasterDetail;
import com.zzuli.gaokao.bean.MasterType;
import com.zzuli.gaokao.bean.TypeDetail;
import com.zzuli.gaokao.common.Result;
import com.zzuli.gaokao.service.MasterDetailService;
import com.zzuli.gaokao.service.MasterService;
import com.zzuli.gaokao.service.MasterTypeService;
import com.zzuli.gaokao.service.TypeDetailService;
import com.zzuli.gaokao.vo.MasterVo;
import lombok.AllArgsConstructor;
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


@RestController
@RequestMapping("/api/master")
public class MasterHandler {


    @Autowired
    private MasterService masterService;

    @Autowired
    private MasterDetailService detailService;

    @Autowired
    private MasterTypeService typeService;

    @Autowired
    private TypeDetailService typeDetailService;

    
    /*
     * @Description: 多条件查询专业
     * @Date:   2024/5/13 18:11
     * @Param:  [page, size, masterName, levelName, type, typeDetail]
     * @Return: com.zzuli.gaokao.common.Result
     */
    @GetMapping("/getMasterList")
    public Result getMasters(Integer page,Integer size,String masterName,String levelName,String type,String typeDetail){

        if(page == null || size == null){
            return Result.error("分页参数错误！");
        }
        Page<Master> masterPage = new Page<>(page, size);
        QueryWrapper<Master> wrapper = new QueryWrapper<>();

        if(StringUtils.isNotBlank(levelName)){
            wrapper.eq("level1_name",levelName);
        }

        if(StringUtils.isNotBlank(type)){
            wrapper.eq("type",type);
        }

        if(StringUtils.isNotBlank(typeDetail)){
            wrapper.eq("type_detail",typeDetail);
        }

        if(StringUtils.isNotBlank(masterName)){
            wrapper.like("name",masterName);
        }

        Page<Master> pageVo = masterService.page(masterPage, wrapper.select("id", "name", "code", "limit_year",
                "degree"));
        List<Master> masterList = pageVo.getRecords();
        long total = pageVo.getTotal();
        HashMap<String, Object> map = new HashMap<>();
        map.put("masterList",masterList);
        System.out.println(masterList);
        map.put("total",total);
        return Result.success(map);
    }


    @GetMapping("/getTypes")
    public Result getTypes(String levelName){
        if(StringUtils.isBlank(levelName)){
            return Result.error("专业层次参数错误！");
        }
        List<MasterType> list = typeService.list(new QueryWrapper<MasterType>()
                .eq("level_name", levelName)
                .select("type"));
        List<String> types = new ArrayList<>();
        for (MasterType type : list) {
            if(StringUtils.isNotBlank(type.getType())){
                types.add(type.getType());
            }
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("types",types);
        return Result.success(map);

    }
    @GetMapping("/getTypeDetails")
    public Result getTypeDetails(String type){
        if(StringUtils.isBlank(type)){
            return Result.error("专业门类参数错误!");
        }
        List<TypeDetail> list = typeDetailService.list(new QueryWrapper<TypeDetail>()
                .eq("type_name", type)
                .select("type_detail"));
        List<String> typeDetails = new ArrayList<>();
        for (TypeDetail typeDetail : list) {
            if(StringUtils.isNotBlank(typeDetail.getTypeDetail())){
                typeDetails.add(typeDetail.getTypeDetail());
            }
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("typeDetails",typeDetails);
        return Result.success(map);
    }


    @GetMapping("/getMasterVo")
    public Result getMasterVo(Integer masterId){

        if(masterId == null){
            return Result.error("专业id参数错误！");
        }
        Master master = masterService.getOne(new QueryWrapper<Master>()
                .eq("id", masterId));
        MasterDetail masterDetail = detailService.getOne(new QueryWrapper<MasterDetail>()
                .eq("master_id", masterId));
        MasterVo masterVo = new MasterVo();
        masterVo.setMaster(master);
        masterVo.setDetail(masterDetail);
        HashMap<String, Object> map = new HashMap<>();
        map.put("masterVo",masterVo);
        return Result.success(map);
    }

}
