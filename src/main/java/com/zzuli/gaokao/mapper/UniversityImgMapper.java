package com.zzuli.gaokao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zzuli.gaokao.bean.UniversityImg;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

@Mapper
public interface UniversityImgMapper extends BaseMapper<UniversityImg> {

    @MapKey("")
    Page<Map<String,Object>> selectCustom(@Param("page") Page<Map<String,Object>> page,
                                          @Param("provinceId") Integer provinceId,
                                          @Param("schoolName") String schoolName);

}
