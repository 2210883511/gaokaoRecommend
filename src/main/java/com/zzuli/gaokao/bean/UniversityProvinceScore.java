package com.zzuli.gaokao.bean;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
//@JsonIgnoreProperties(ignoreUnknown = true)
public class UniversityProvinceScore {

    @TableId(type = IdType.AUTO)
    private Integer id;
//    @JsonProperty("school_id")
    private Integer schoolId;
//    @JsonProperty("province_id")
    private Integer provinceId;
    private Integer type;
    private String min;
//    @JsonProperty("min_section")
    private String minSection;

    private String proscore;
    private Integer year;
//    @JsonProperty("zslx_name")
    private String zslxName;
//    @JsonProperty("sg_name")
    private String sgName;
//    @JsonProperty("sg_info")
    private String sgInfo;
//    @JsonProperty("local_batch_name")
    private String localBatchName;
    @TableLogic(delval = "2",value = "1")
    private Integer status;

}
