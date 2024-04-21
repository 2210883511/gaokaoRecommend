package com.zzuli.gaokao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zzuli.gaokao.bean.UserUniversityActions;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserUniversityActionsMapper extends BaseMapper<UserUniversityActions> {

        List<UserUniversityActions>  getUserPreference();

}
