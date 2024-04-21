package com.zzuli.gaokao.common.universitPprovinceSccore;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.zzuli.gaokao.bean.UniversityProvinceScore;
import lombok.Data;

@Data
public class Item {

    @JsonProperty("numFound")
    private Integer numFound;
    @JsonProperty("item")
    private UniversityProvinceScore[] universityProvinceScores;
}
