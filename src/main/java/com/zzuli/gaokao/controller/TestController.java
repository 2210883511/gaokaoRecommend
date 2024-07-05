package com.zzuli.gaokao.controller;



import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zzuli.gaokao.bean.Provinces;
import com.zzuli.gaokao.bean.University;
import com.zzuli.gaokao.bean.UniversityMasterScore;

import com.zzuli.gaokao.common.Result;
import com.zzuli.gaokao.common.WebSocketHandler;

import com.zzuli.gaokao.common.uvMasterScore.Item;
import com.zzuli.gaokao.common.uvMasterScore.JsonUvMasterScore;
import com.zzuli.gaokao.mapper.ProvincesMapper;
import com.zzuli.gaokao.mapper.UniversityMapper;
import com.zzuli.gaokao.mapper.UniversityMasterScoreMapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@RestController
@Slf4j
public class TestController {

    @Autowired
    private WebClient webClient;

    @Autowired
    private ProvincesMapper provincesMapper;

    @Autowired
    private UniversityMapper universityMapper;

    @Autowired
    private UniversityMasterScoreMapper scoreMapper;

    @Autowired
    private WebSocketHandler webSocketHandler;


    Integer[] years =  {2023};




    @GetMapping("/send")
    public Result sendMessage() throws Exception {

        Thread thread = new Thread(() -> {
            List<Provinces> provinces = provincesMapper.selectList(new QueryWrapper<Provinces>().select("id"));
            List<University> schoolIds = universityMapper.selectList(new QueryWrapper<University>().select("school_id").ge("school_id",76));
            int count = 0;
            for (University university : schoolIds) {
                Integer schoolId = university.getSchoolId();
                for (Integer year : years){
                    for (Provinces province : provinces){
                        Integer provinceId = province.getId();
                        if(count == 20)
                            try {
                                System.out.println("系统沉睡2s");
                                count = 0;
                                Thread.sleep(2000);

                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        String json = null;
                        JsonUvMasterScore jsonUvMasterScore = null;
                        try{
                            json = webClient.get()
                                    .uri("/schoolspecialscore/{schoolId}/{year}/{provinceId}.json", schoolId, year, provinceId)
                                    .accept(MediaType.APPLICATION_JSON)
                                    .retrieve()
                                    .bodyToMono(String.class)
                                    .block();
                            count++;
                            ObjectMapper objectMapper = new ObjectMapper();
                            jsonUvMasterScore = objectMapper.readValue(json, JsonUvMasterScore.class);

                        }catch(Exception ignored){
                            log.warn(ignored.getMessage());
                        }

                        if(jsonUvMasterScore != null){
                            Map<String, com.zzuli.gaokao.common.uvMasterScore.Item> map = jsonUvMasterScore.getMap();
                            for (String s : map.keySet()) {
                                Item item = map.get(s);
                                for (UniversityMasterScore masterScore : item.getMasterScores()) {
                                    try {
                                        if(!webSocketHandler.isClose()){
                                            webSocketHandler.sendMessage(masterScore.toString()+"\n");
                                        }else {
                                            Thread.currentThread().stop();
                                        }

                                    } catch (Exception e) {
                                        log.warn(e.getMessage());
                                    }

                                }



                            }
                        }




                    }
                }

            }
        });
        thread.start();

        return Result.success("开启成功！");

    }




}
