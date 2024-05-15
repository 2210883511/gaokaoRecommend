package com.zzuli.gaokao.vo;


import com.zzuli.gaokao.bean.Provinces;
import com.zzuli.gaokao.bean.University;
import com.zzuli.gaokao.bean.UniversityInfo;
import com.zzuli.gaokao.bean.UniversityTags;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@Data
@NoArgsConstructor
public class UniversityDataVo {

    private Integer schoolId;
    private String schoolName;
    private Integer provinceId;
    private String cityName;
    private String townName;

    private Integer f985;
    private Integer f211;
    private String schoolTypeName;
    private String schoolNatureName;
    private String dualClassName;
    private String typeName;  // 综合类大学

    private String content;
    private String belong;
    private String provinceName;


    public void setUniversity(University university){
        this.schoolId = university.getSchoolId();
        this.schoolName = university.getSchoolName();
        this.provinceId = university.getProvinceId();
        this.cityName = university.getCityName();
        this.townName = university.getTownName();
    }

    public void setTags(UniversityTags tags){
        this.f985 = tags.getF985();
        this.f211 = tags.getF211();
        this.typeName = tags.getTypeName();
        this.dualClassName = tags.getDualClassName();
        this.schoolTypeName = tags.getSchoolTypeName();
        this.schoolNatureName = tags.getSchoolNatureName();
    }

    public void setInfo(UniversityInfo info){
        this.content = info.getContent();
        this.belong = info.getBelong();
    }

    public void setProvinces(Provinces provinces) {
        this.provinceName = provinces.getName();
    }


    public StringBuilder getKeyWordsList(){
        StringBuilder builder = new StringBuilder();
        if(StringUtils.isNotBlank(this.schoolTypeName)){
            builder.append(this.schoolTypeName).append(" ");
        }
        if(StringUtils.isNotBlank(this.schoolNatureName)){
            builder.append(this.schoolNatureName).append(" ");
        }
        if(StringUtils.isNotBlank(this.dualClassName)){
            builder.append(this.dualClassName).append(" ");
        }
        if(StringUtils.isNotBlank(this.typeName)){
            builder.append(this.typeName).append(" ");
        }

        if(this.f985 != null){
            if(this.f985 == 1){
                builder.append("985").append(" ");
            }
        }
        if(this.f211 != null){
            if(this.f211 == 1){
                builder.append("211").append(" ");
            }
        }

        if(StringUtils.isNotBlank(this.belong)){
            builder.append(this.belong).append(" ");
        }

        return builder;
    }




}
