package com.zzuli.gaokao.vo;

import com.zzuli.gaokao.bean.University;
import com.zzuli.gaokao.bean.UniversityTags;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * @Description: 用于构建管理端的高校界面Vo
 * @Date:   2024/5/4 19:49
 * @Param:  
 * @Return: 
 */
@Data
@NoArgsConstructor
public class UniversityVo {

    private Integer schoolId;

    private String schoolName;

    private String headerUrl;

    private String cityName;

    private String townName;

    private String dualClassName;

    private String typeName;

    private Integer f985;

    private Integer f211;

    private String url;


    public void setUniversity(University university){

        this.setSchoolId(university.getSchoolId());
        this.setCityName(university.getCityName());
        this.setTownName(university.getTownName());
        this.setHeaderUrl(university.getHeaderUrl());
        this.setSchoolName(university.getSchoolName());

    }

    public void setTag(UniversityTags tag){
        this.setTypeName(tag.getTypeName());
        this.setF985(tag.getF985());
        this.setF211(tag.getF211());
        this.setDualClassName(tag.getDualClassName());
    }
}
