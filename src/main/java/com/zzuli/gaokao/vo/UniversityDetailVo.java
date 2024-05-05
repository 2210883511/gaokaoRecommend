package com.zzuli.gaokao.vo;

import com.zzuli.gaokao.bean.University;
import com.zzuli.gaokao.bean.UniversityInfo;
import com.zzuli.gaokao.bean.UniversityTags;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UniversityDetailVo {

    private String provinceName;

    private Integer schoolId;

    private String schoolName;

    private String headerUrl;

    private String cityName;

    private String townName;

    private String phone;

    private String site;

    private String schoolSite;

    private String email;

    private String dualClassName;

    private String typeName;

    private String schoolTypeName;

    private String schoolNatureName;

    private Integer f985;

    private Integer f211;

    private String  content;

    private String createDate;

    private String area;

    private String belong;

    private String address;

    private Integer numMaster;

    private Integer numDoctor;

    private Integer numSubject;





    public void setUniversity(University university){

        this.setSchoolId(university.getSchoolId());
        this.setCityName(university.getCityName());
        this.setTownName(university.getTownName());
        this.setHeaderUrl(university.getHeaderUrl());
        this.setSchoolName(university.getSchoolName());
        this.setEmail(university.getEmail());
        this.setPhone(university.getPhone());
        this.setSite(university.getSite());
        this.setSchoolSite(university.getSchoolSite());
    }

    public void setTag(UniversityTags tag){
        this.setTypeName(tag.getTypeName());
        this.setF985(tag.getF985());
        this.setF211(tag.getF211());
        this.setDualClassName(tag.getDualClassName());
        this.setSchoolTypeName(tag.getSchoolTypeName());
        this.setSchoolNatureName(tag.getSchoolNatureName());
    }


    public void setInfo(UniversityInfo info){
        this.setAddress(info.getAddress());
        this.setContent(info.getContent());
        this.setBelong(info.getBelong());
        this.setArea(info.getArea());
        this.setCreateDate(info.getCreateDate());
        this.setNumMaster(info.getNumMaster());
        this.setNumDoctor(info.getNumDoctor());
        this.setNumSubject(info.getNumSubject());
    }
}
