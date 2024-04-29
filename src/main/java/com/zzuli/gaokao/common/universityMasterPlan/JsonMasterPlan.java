package com.zzuli.gaokao.common.universityMasterPlan;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class JsonMasterPlan {
    @JsonProperty("code")
    private Integer code;
    @JsonProperty("message")
    private String  message;
    @JsonProperty("data")
    private Map<String, Item> data;
}
