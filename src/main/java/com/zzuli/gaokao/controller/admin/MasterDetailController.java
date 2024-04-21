package com.zzuli.gaokao.controller.admin;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zzuli.gaokao.bean.MasterDetail;
import com.zzuli.gaokao.common.Result;
import com.zzuli.gaokao.service.MasterDetailService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/masterDetail")
public class MasterDetailController {

    @Autowired
    private MasterDetailService detailService;

    /*
     * @Description: 分页获取专业详情列表
     * @Date:   2024/4/16 12:11
     * @Param:  [current, size, masterName, type, typeDetail, levelName]
     * @Return: com.zzuli.gaokao.common.Result
     */
    @GetMapping("/getDetails")
    public Result getDetails(Integer current,Integer size,String masterName,String type,String typeDetail,String levelName ){

        Page<Map<String, Object>> page = new Page<>(current, size);
        if(StringUtils.isBlank(masterName)){
            masterName = null;
        }
        if(StringUtils.isBlank(type) || StringUtils.isBlank(typeDetail) || StringUtils.isBlank(levelName)){
            type = null;
            typeDetail = null;
            levelName = null;
        }
        Page<Map<String, Object>> custom = detailService.findCustom(page, masterName, type, typeDetail, levelName);
        List<Map<String, Object>> list = custom.getRecords();
        long total = custom.getTotal();
        HashMap<String, Object> map = new HashMap<>();
        map.put("masterDetails",list);
        map.put("total",total);
        return Result.success(map);
    }

    
    /*
     * @Description: 判断是否已经添加了专业详情
     * @Date:   2024/4/16 12:11
     * @Param:  [masterId]
     * @Return: com.zzuli.gaokao.common.Result
     */
    @GetMapping("/{masterId}")
    public Result isAddDetail(@PathVariable("masterId") Integer masterId){
        MasterDetail one = detailService.getOne(new QueryWrapper<MasterDetail>().eq("master_id", masterId));
        HashMap<String, Object> map = new HashMap<String, Object>();
        if(one != null){
            map.put("flag",true);
            return Result.success("已经添加过了！",map);
        }
        map.put("flag",false);
        return Result.success(map);
    }
    
    
    
    /*
     * @Description: 删除专业详情
     * @Date:   2024/4/16 12:11
     * @Param:  [id]
     * @Return: com.zzuli.gaokao.common.Result
     */
    @PostMapping("/delete/{id}")
    public Result deleteMasterDetail(@PathVariable("id") Integer id){

        detailService.removeById(id);
        return Result.success("删除成功！");

    }
    
    /*
     * @Description: 更新专业详情
     * @Date:   2024/4/16 12:11
     * @Param:  [detail]
     * @Return: com.zzuli.gaokao.common.Result
     */
    @PostMapping("/updateDetail")
    public Result updateMasterDetail(@RequestBody MasterDetail detail){
       if(detail.getId() == null){
            return Result.error("参数错误,id不能为空!");
        }
        String content = detail.getContent();
        if(StringUtils.isBlank(content)){
            return Result.error("简介不能为空!");
        }
        detailService.updateById(detail);

        return Result.success("更新成功！");
    }
    
    /*
     * @Description: 添加专业详情
     * @Date:   2024/4/16 12:12
     * @Param:  [detail]
     * @Return: com.zzuli.gaokao.common.Result
     */
    @PostMapping("/addDetail")
    public Result addMasterDetail(@RequestBody MasterDetail detail){

        String content = detail.getContent();
        if(StringUtils.isBlank(content)){
            return Result.error("内容不能为空！");
        }
        detailService.save(detail);
        return Result.success("添加成功！");
    }


}
