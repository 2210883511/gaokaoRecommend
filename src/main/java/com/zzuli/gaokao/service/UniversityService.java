package com.zzuli.gaokao.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zzuli.gaokao.bean.University;
import com.zzuli.gaokao.common.Result;
import com.zzuli.gaokao.vo.UniversityVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UniversityService extends IService<University> {

    UniversityVo getUniversityVo(Integer schoolId);

    List<UniversityVo> getUniversityVoListById(List<Object> schoolIds);

    Result getUniversityVoList(Integer current, Integer size, String schoolName, Integer provinceId);

    /*
     * @Description: 多条件查询高校列表 用户端
     * @Date:   2024/5/7 16:48
     * @Param:  [page, f985, f211, dualClassName, typeName, schoolName, provinceId]
     * @Return: com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.zzuli.gaokao.vo.UniversityVo>
     */
    Page<UniversityVo> selectCustom(Page<UniversityVo> page,Integer f985,Integer f211,
                                    String dualClassName,
                                    String typeName,
                                    String schoolName,
                                    Integer provinceId);

}
