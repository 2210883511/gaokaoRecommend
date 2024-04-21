package com.zzuli.gaokao.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zzuli.gaokao.Utils.CommonUtils;
import com.zzuli.gaokao.bean.Provinces;
import com.zzuli.gaokao.bean.University;
import com.zzuli.gaokao.bean.UniversityImg;
import com.zzuli.gaokao.common.Result;
import com.zzuli.gaokao.service.Impl.ProvincesServiceImpl;
import com.zzuli.gaokao.service.Impl.UniversityImgServiceImpl;
import com.zzuli.gaokao.service.Impl.UniversityServiceImpl;
import com.zzuli.gaokao.service.ProvincesService;
import it.unimi.dsi.fastutil.Hash;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;

import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/admin/images")
public class UniversityImgController {

    @Value("${gaokao.path.upload-path}")
    String uploadPath;


    @Value("${gaokao.path.domain}")
    String domain;


    @Autowired
    private UniversityImgServiceImpl imgService;

    @Autowired
    private UniversityServiceImpl universityService;

    @Autowired
    private ProvincesServiceImpl provincesService;

    @GetMapping("/getImages")
    public Result getImages(Integer current,Integer size,Integer provinceId,String schoolName){


        Page<University> page = new Page<>(current,size);
        QueryWrapper<University> wrapper = new QueryWrapper<>();
        if(!StringUtils.isBlank(schoolName)){
            wrapper.like("school_name",schoolName);
        }
        if(provinceId != null){
            wrapper.eq("province_id",provinceId);
        }
        //  查询省份
        List<Provinces> provinces = provincesService.list();

        /* 查询高校基本信息 */
        Page<University> universityPage = universityService.page(page, wrapper.select("school_id", "school_name", "header_url","city_name","town_name","province_id"));
        List<University> universityList = universityPage.getRecords();
        List<HashMap<String, Object>> list = universityList.stream()
                .map(university -> {
                    HashMap<String, Object> map = new HashMap<>();
                    String name = university.getSchoolName();
                    String headerUrl = university.getHeaderUrl();
                    Integer schoolId = university.getSchoolId();
                    Integer universityProvinceId = university.getProvinceId();
                    String cityName = university.getCityName();
                    String townName = university.getTownName();
                    for (Provinces province : provinces) {
                        if(universityProvinceId.equals(province.getId())){
                            map.put("provinceName",province.getName());
                        }
                    }
                    map.put("schoolName", name);
                    map.put("headerUrl", headerUrl);
                    map.put("schoolId", schoolId);
                    map.put("cityName",cityName);
                    map.put("townName",townName);
                    return map;
                })
                .collect(Collectors.toList());

        long total = universityPage.getTotal();


//        过滤出高校的id
        List<Object> ids = list.stream()
                .map(stringObjectHashMap -> stringObjectHashMap.get("schoolId"))
                .distinct()
                .collect(Collectors.toList());

        QueryWrapper<UniversityImg> imgQueryWrapper = new QueryWrapper<>();
        imgQueryWrapper.in("school_id",ids);
        List<UniversityImg> universityImages = imgService.list(imgQueryWrapper);
        Map<Integer, List<UniversityImg>> collect = universityImages.stream()
                .collect(Collectors.groupingBy(UniversityImg::getSchoolId));
        ArrayList<Map<String, Object>> data = new ArrayList<>();
        for (HashMap<String, Object> university:list) {
            HashMap<String, Object> map = new HashMap<>();
            Object id = university.get("schoolId");
            if(id instanceof Integer){
                List<UniversityImg> images = collect.get(id);
                map.put("university",university);
                map.put("images",images);
                data.add(map);
            }
        }

        HashMap<String, Object> map = new HashMap<>();
        map.put("universityVo",data);
        map.put("total",total);

        return Result.success(map);


    }


    @GetMapping("/getImage")
    public Result getImage(Integer id){
        UniversityImg image = imgService.getById(id);
        HashMap<String, Object> map = new HashMap<>();
        map.put("image",image);
        return Result.success(map);
    }


    @PostMapping("/upload")
    public Result uploadSchoolImage(MultipartFile schoolImage){
        if(schoolImage == null){
            return Result.error("图片不能为空！");
        }
        String filename = schoolImage.getOriginalFilename();
        String suffix = null;
        if (filename != null) {
            suffix = filename.substring(filename.lastIndexOf("."));
        }
        filename = CommonUtils.generateUUID() + suffix;
        File file = new File(uploadPath);
        if(!file.exists()){
            file.mkdirs();
        }
        try {
            schoolImage.transferTo(new File(uploadPath+ File.separator + filename));

            String url = domain + filename;
            HashMap<String, Object> map = new HashMap<>();
            map.put("url",url);
            return Result.success("图片上传成功！",map);
        } catch (IOException e) {
            log.error("文件上传失败!",e);
            return Result.error("文件上传失败!");
        }
    }


    @PostMapping("/addSchoolImage")
    public Result addSchoolImage(@RequestBody UniversityImg img){
        String title = img.getTitle();
        if(StringUtils.isBlank(title)){
            return Result.error("标题不能为空！");
        }
        if(StringUtils.isBlank(img.getUrl())){
            return Result.error("图片链接不能为空");
        }
        if(img.getRank() == null){
            return Result.error("排行不能为空！");
        }
        if(img.getSchoolId() == null){
            return  Result.error("高校id为空！");
        }
        img.setStatus(1);
        imgService.save(img);
        return Result.success("添加成功!");
    }

    @PostMapping("/updateSchoolImage")
    public Result updateSchoolImage(@RequestBody UniversityImg img) {
        if (StringUtils.isBlank(img.getTitle())) {
            return Result.error("标题不能为空！");
        }
        if (StringUtils.isBlank(img.getUrl())) {
            return Result.error("图片链接不能为空");
        }
        if (img.getRank() == null) {
            return Result.error("排行不能为空！");
        }
        if (img.getSchoolId() == null) {
            return Result.error("高校id为空！");
        }
        imgService.updateById(img);
        return Result.success("更新成功！");
    }

    @PostMapping("/delete/{id}")
    public Result deleteSchoolImage(@PathVariable("id") Integer id){
        imgService.removeById(id);
        return Result.success("删除成功！");
    }








}
