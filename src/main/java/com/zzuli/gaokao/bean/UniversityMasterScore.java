package com.zzuli.gaokao.bean;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UniversityMasterScore {


    @TableId(type=IdType.AUTO)
    private Integer id;

    @JsonProperty("school_id")
    private Integer schoolId;

    @JsonProperty("spe_id")
    private Integer specialId;


    @JsonProperty("province")
    private Integer provinceId;

    @JsonProperty("type")
    private Integer type;

    @JsonProperty("min")
    private String min;

    @JsonProperty("min_section")
    private String minSection;


    private Integer year;


//    @JsonProperty("spname")
    private String spname;


//    @JsonProperty("local_batch_name")
    private String localBatchName;

//    @JsonProperty("sg_name")
    private String sgName;


//    @JsonProperty("sg_info")
    private String sgInfo;


    @TableLogic(delval = "2",value = "1")
    private Integer status;


}
