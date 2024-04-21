package com.zzuli.gaokao.common.universitPprovinceSccore;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.zzuli.gaokao.bean.UniversityProvinceScore;
import lombok.Data;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class JsonTest {

    @JsonProperty("code")
    private Integer code;
    @JsonProperty("message")
    private String  message;
    @JsonProperty("data")
    private Map<String, Item> data;

}

