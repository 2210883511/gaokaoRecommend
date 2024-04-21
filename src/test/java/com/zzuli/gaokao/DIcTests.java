package com.zzuli.gaokao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zzuli.gaokao.bean.Dic;
import com.zzuli.gaokao.common.dic.JsonDic;
import com.zzuli.gaokao.mapper.DicMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@SpringBootTest
@Slf4j
public class DIcTests {

    @Autowired
    private DicMapper dicMapper;

    @Autowired
    private WebClient webClient;

    @Test
    public void getDic(){
        String json = null;
        JsonDic jsonDic = null;
        json = webClient.get()
                .uri("/config/dicprovince/dic.json")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class)
                .block();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
             jsonDic = objectMapper.readValue(json, JsonDic.class);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }
        if (jsonDic != null) {
            Map<Integer, String> data = jsonDic.getData();
            for (Integer i : data.keySet()) {
                String s = data.get(i);
                Dic dic = new Dic(i,s);
                dicMapper.insert(dic);
            }
        }


    }


}
