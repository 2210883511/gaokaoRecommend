package com.zzuli.gaokao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zzuli.gaokao.bean.MasterDetail;
import com.zzuli.gaokao.vo.MasterDetailVo;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

@Mapper
public interface MasterDetailMapper extends BaseMapper<MasterDetail> {

    @MapKey("id")
    Page<Map<String,Object>> selectCustom(@Param("page")  Page<Map<String,Object>>page, @Param("masterName") String masterName,
                                      @Param("type") String type,
                                      @Param("typeDetail") String typeDetail,
                                      @Param("levelName") String levelName);


}
