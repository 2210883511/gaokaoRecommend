package com.zzuli.gaokao.common.universityMasterPlan;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.zzuli.gaokao.bean.UniversityMasterPlan;
import lombok.Data;

@Data
public class Item {
    @JsonProperty("numFound")
    private Integer numFound;
    @JsonProperty("item")
    private UniversityMasterPlan[] universityMasterPlans;
}
