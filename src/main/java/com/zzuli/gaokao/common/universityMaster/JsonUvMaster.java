package com.zzuli.gaokao.common.universityMaster;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.zzuli.gaokao.bean.UniversityMaster;
import lombok.Data;
import lombok.Getter;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class JsonUvMaster {

    @JsonProperty("code")
    private Integer code;
    @JsonProperty("message")
    private String message;
    @JsonProperty("data")
    private DataJson item;

    public UniversityMaster[] getMaster1(){

        return this.getItem().getSpecialDetail().getMasters1();
    }
    public UniversityMaster[] getMaster2(){
        return  this.getItem().getSpecialDetail().getMasters2();
    }

}

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
class DataJson{

    @JsonProperty("special_detail")
    public SpecialDetail specialDetail;

}

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
class SpecialDetail{

    @JsonProperty("1")
    public  UniversityMaster[] masters1;

    @JsonProperty("2")
    public UniversityMaster[] masters2;
}

