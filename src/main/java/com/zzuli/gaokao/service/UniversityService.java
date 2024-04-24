package com.zzuli.gaokao.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zzuli.gaokao.bean.University;
import com.zzuli.gaokao.common.Result;
import com.zzuli.gaokao.vo.UniversityVo;

import java.util.List;

public interface UniversityService extends IService<University> {

    UniversityVo getUniversityVo(Integer schoolId);

    List<UniversityVo> getUniversityVoListById(List<Object> schoolIds);

    Result getUniversityVoList(Integer current, Integer size, String schoolName, Integer provinceId);



}
