package com.zzuli.gaokao.controller.admin;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.zzuli.gaokao.Utils.CommonUtils;
import com.zzuli.gaokao.bean.*;
import com.zzuli.gaokao.common.Result;
import com.zzuli.gaokao.service.Impl.UniversityInfoServiceImpl;
import com.zzuli.gaokao.service.Impl.UniversityRankServiceImpl;
import com.zzuli.gaokao.service.Impl.UniversityServiceImpl;
import com.zzuli.gaokao.service.Impl.UniversityTagsServiceImpl;
import com.zzuli.gaokao.service.UniversityImgService;
import com.zzuli.gaokao.service.UniversityTagsService;
import com.zzuli.gaokao.vo.UniversityVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Slf4j
@RestController
@RequestMapping("/admin/university")
public class UniversityController {

    @Autowired
    private UniversityServiceImpl universityService;

    @Autowired
    private UniversityTagsServiceImpl universityTagsService;

    @Autowired
    private UniversityTagsService tagsService;

    @Autowired
    private UniversityImgService imgService;


    @Value("${gaokao.path.upload-path}")
    String uploadPath;


    @Value("${gaokao.path.domain}")
    String domain;


    @GetMapping("/getPagination")
    public Result getPage(Integer page,Integer size, String id,String universityName){

        Page<University> pages = new Page<>(page, size);
        QueryWrapper<University> wrapper = new QueryWrapper<>();
        if(!StringUtils.isBlank(id)){
            wrapper.eq("province_id",Integer.valueOf(id));
        }
        if(!StringUtils.isBlank(universityName)) {
            wrapper.like("school_name", universityName);
        }

        Page<University> universityPage = universityService.page(pages, wrapper);
        List<University> list = universityPage.getRecords();
        long total = universityPage.getTotal();
        HashMap<String, Object> map = new HashMap<>();
        map.put("list",list);
        map.put("total",total);
        return Result.success(map);
    }


    @PostMapping("/upload")
    public Result upload(MultipartFile headerImg){
        if(headerImg == null){
            return Result.error("图片不能为空！");
        }
        String filename = headerImg.getOriginalFilename();
        String suffix = filename.substring(filename.lastIndexOf("."));
        filename = CommonUtils.generateUUID() + suffix;
        File file = new File(uploadPath);
        if(!file.exists()){
            file.mkdirs();
        }
        try {
            headerImg.transferTo(new File(uploadPath + File.separator + filename));
            String headerUrl = domain + filename;
            HashMap<String, String> map = new HashMap<>();
            map.put("headerUrl",headerUrl);
            return Result.success(map);
        } catch (IOException e) {
            return Result.error("文件上传出错！");
        }


    }



    /*
     * @Description: 高校添加功能，名称、邮箱不能重复、不能为空
     * @Date:   2024/4/4 20:12
     * @Param:  [university]
     * @Return: com.zzuli.gaokao.common.Result
     */
    @PostMapping("/add")
    @Transactional(isolation = Isolation.READ_COMMITTED,propagation = Propagation.REQUIRED)
    public Result saveUniversity(@RequestBody University university) {
        if (StringUtils.isBlank(university.getSchoolName())) {
            return Result.error("高校名称不能为空！");
        }
        if (StringUtils.isBlank(university.getEmail())) {
            return Result.error("高校邮箱不能为空！");
        }
        if (university.getProvinceId() == null) {
            return Result.error("省份不能为空！");
        }
        University one = null;
        one = universityService.getOne(new QueryWrapper<University>().eq("school_name", university.getSchoolName()));
        if (one != null) {
            return Result.error("高校名称已经存在了，请再换一个!");
        }
        one = universityService.getOne(new QueryWrapper<University>().eq("email", university.getEmail()));
        if (one != null) {
            return Result.error("邮箱已经重复了，请再换一个！");
        }
        university.setStatus(1);
        universityService.save(university);
        UniversityTags tags = new UniversityTags();
        tags.setSchoolId(university.getSchoolId());
        tags.setProvinceId(university.getProvinceId());
        tags.setStatus(1);
        universityTagsService.save(tags);
        return Result.success("添加成功！");
    }


    /*
     * @Description: 更新高校信息，这里更新只需更新信息，不需要更新其他相关表的数据
     * @Date:   2024/4/4 20:59
     * @Param:  [university]
     * @Return: com.zzuli.gaokao.common.Result
     */
    @PostMapping("/update")
    public Result updateUniversity(@RequestBody University university){
        String schoolName = university.getSchoolName();
        String email = university.getEmail();
        Integer schoolId = university.getSchoolId();
        University one = null;
        one = universityService.getOne(new QueryWrapper<University>().eq("school_name", schoolName));
        if(one != null && !one.getSchoolId().equals(schoolId)){
            return Result.error("高校名称重复！");
        }
        one = universityService.getOne(new QueryWrapper<University>().eq("email", email));
        if(one != null && !one.getSchoolId().equals(schoolId)){
            return Result.error("邮箱已经重复了");
        }
        if(StringUtils.isBlank(university.getSite())){
            university.setSite(null);
        }
        if(StringUtils.isBlank(university.getPhone())){
            university.setPhone(null);
        }
        if(StringUtils.isBlank(university.getCityName())){
            university.setCityName(null);
        }
        if(StringUtils.isBlank(university.getTownName())){
            university.setTownName(null);
        }
        if(StringUtils.isBlank(university.getSchoolName())){
            university.setSchoolName(null);
        }
        if(StringUtils.isBlank(university.getEmail())){
            university.setEmail(null);
        }
        if(StringUtils.isBlank(university.getSchoolSite())){
            university.setSchoolSite(null);
        }
        if(StringUtils.isBlank(university.getHeaderUrl())){
            university.setHeaderUrl(null);
        }
        universityService.updateById(university);
        return Result.success("更新成功！");

    }


    /*
     * @Description: 删除高校时，删除信息、基本信息、标签、排行
     * @Date:   2024/4/4 23:12
     * @Param:  [id]
     * @Return: com.zzuli.gaokao.common.Result
     */
    @GetMapping("/delete/{id}")
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public Result remove(@PathVariable Integer id) {
        universityService.removeById(id);
        universityTagsService.remove(new QueryWrapper<UniversityTags>().eq("school_id",id));
        return Result.success("删除成功！");
    }


    @GetMapping("/{id}")
    public Result getUniversity(@PathVariable("id") Integer id){

        University one = universityService.getOne(new QueryWrapper<University>().eq("school_id", id));
        HashMap<String, Object> map = new HashMap<>();
        map.put("university",one);
        return Result.success(map);
    }

    @GetMapping("/getUniversityVo/{schoolId}")
    public Result getUniversityVo(@PathVariable Integer schoolId){
        if(schoolId == null){
            return Result.error("高校id为空！");
        }
        UniversityVo vo = universityService.getUniversityVo(schoolId);
        HashMap<String, Object> map = new HashMap<>();
        map.put("universityVo",vo);
        return Result.success(map);
    }

    /*
     * @Description: 获取高校列表 包括高校基本信息和高校标签，以及高校校园风光
     * @Date:   2024/4/24 13:23
     * @Param:  [current, size, provinceId, schoolName]
     * @Return: com.zzuli.gaokao.common.Result
     */
    @GetMapping("/getUniversityVoList")
    public Result getUniversityVoList(Integer current,Integer size,Integer provinceId,String schoolName){
        if(current == null || size == null){
            return Result.error("参数错误，current或size为空！");
        }
       return universityService.getUniversityVoList(current, size, schoolName, provinceId);
    }


}
