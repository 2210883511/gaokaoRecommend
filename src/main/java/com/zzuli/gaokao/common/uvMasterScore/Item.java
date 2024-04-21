package com.zzuli.gaokao.common.uvMasterScore;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.zzuli.gaokao.bean.UniversityMasterScore;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Item{

    @JsonProperty("item")
    private UniversityMasterScore[] masterScores;

}