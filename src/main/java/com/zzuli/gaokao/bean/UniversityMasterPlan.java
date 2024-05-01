package com.zzuli.gaokao.bean;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UniversityMasterPlan {

    @TableId
    private Integer id;
//    @JsonProperty("spe_id")
    private Integer specialId;
//    @JsonProperty("province")
    private Integer provinceId;
//    @JsonProperty("school_id")
    private Integer schoolId;
    private Integer type;
    private String num;
    private String tuition;
    private String length;
    private String spname;
//    @JsonProperty("local_batch_name")
    private String localBatchName;
    private Integer year;
    private String sgInfo;
    @TableLogic(delval = "2",value = "1")
    private Integer status;
}
