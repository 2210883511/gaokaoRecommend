package com.zzuli.gaokao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zzuli.gaokao.bean.UniversityMaster;
import com.zzuli.gaokao.vo.UniversityMasterVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UniversityMasterMapper extends BaseMapper<UniversityMaster> {


}
