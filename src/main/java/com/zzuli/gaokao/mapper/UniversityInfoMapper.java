package com.zzuli.gaokao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zzuli.gaokao.bean.UniversityInfo;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

@Mapper
public interface UniversityInfoMapper extends BaseMapper<UniversityInfo> {

    @MapKey("schoolId")
    Page<Map<String,Object>> getCustom(@Param("page") Page<Map<String,Object>> page,@Param("provinceId")Integer provinceId,@Param("schoolName") String schoolName);


}
