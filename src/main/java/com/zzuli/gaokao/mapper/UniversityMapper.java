package com.zzuli.gaokao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zzuli.gaokao.bean.University;
import com.zzuli.gaokao.vo.UniversityVo;
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

    Page<UniversityVo> selectCustom(@Param("page") Page<UniversityVo> page,
                                    @Param("f985") Integer f985, @Param("f211")Integer f211,
                                    @Param("dualClassName") String dualClassName,
                                    @Param("typeName")String typeName,
                                    @Param("schoolName")String schoolName,
                                    @Param("provinceId")Integer provinceId);



}
