package com.zzuli.gaokao.common;


import lombok.Data;

import java.util.ArrayList;
import java.util.List;


@Data
public class SensitiveWords {

    private List<String> list;


    public SensitiveWords(){
        this.list = new ArrayList<>();
    }

    public boolean add(String sensitiveWords){
        return  list.add(sensitiveWords);
    }

    public boolean isContains(String word){
       return list.contains(word);
    }



}
