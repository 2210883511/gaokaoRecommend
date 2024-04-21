package com.zzuli.gaokao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zzuli.gaokao.bean.University;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface UniversityMapper extends BaseMapper<University> {


    List<Integer> selectMissId();


    Page<University> getPages(@Param("page") Page<University> page,@Param("id") Integer provinceId);

    
    Page<Map<String,Object>> test(@Param("page") Page<Map<String,Object>> page);



}
