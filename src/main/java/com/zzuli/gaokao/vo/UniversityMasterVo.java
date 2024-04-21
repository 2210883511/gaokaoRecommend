package com.zzuli.gaokao.vo;

import com.zzuli.gaokao.bean.Master;
import com.zzuli.gaokao.bean.UniversityMaster;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UniversityMasterVo {


    private Integer id;


    private Integer schoolId;


    private String xuekeRankScore;


    private Integer specialId;


    private Integer nationFeature;



    private Integer provinceFeature;



    private String specialName;

    private String type;

    private String typeDetail;

    private String level1Name;

    private String limitYear;


    public void setUniversityMaster(UniversityMaster uvMaster){
        this.id = uvMaster.getId();
        this.schoolId = uvMaster.getSchoolId();
        this.specialId = uvMaster.getSpecialId();
        this.provinceFeature = uvMaster.getProvinceFeature();
        this.nationFeature = uvMaster.getNationFeature();
        this.xuekeRankScore = uvMaster.getXuekeRankScore();
        this.specialName = uvMaster.getSpecialName();
    }

    public void setMaster(Master master){
        this.type = master.getType();
        this.typeDetail = master.getTypeDetail();
        this.level1Name = master.getLevel1Name();
        this.limitYear = master.getLimitYear();
    }

}
