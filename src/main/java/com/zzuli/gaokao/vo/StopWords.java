package com.zzuli.gaokao.vo;

import lombok.Data;

import java.util.HashMap;


@Data
public class StopWords {

    private HashMap<String,Boolean>  map;

    private Integer size;

    public StopWords (){
        this.map = new HashMap<>();
    }

    public Integer getSize(){
        return map.size();
    }

    public void put(String keywords){
        this.map.put(keywords,true);
    }


    public boolean isContainKey(String keywords){
       return this.map.containsKey(keywords);
    }
}
