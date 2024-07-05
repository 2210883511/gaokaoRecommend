package com.zzuli.gaokao;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zzuli.gaokao.bean.Provinces;
import com.zzuli.gaokao.bean.University;
import com.zzuli.gaokao.bean.UniversityMasterScore;
import com.zzuli.gaokao.bean.UniversityProvinceScore;
import com.zzuli.gaokao.common.WebSocketHandler;
import com.zzuli.gaokao.common.universitPprovinceSccore.JsonTest;
import com.zzuli.gaokao.common.uvMasterScore.Item;
import com.zzuli.gaokao.common.uvMasterScore.JsonUvMasterScore;
import com.zzuli.gaokao.mapper.ProvincesMapper;
import com.zzuli.gaokao.mapper.UniversityMapper;
import com.zzuli.gaokao.mapper.UniversityMasterScoreMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@SpringBootTest
public class GetUvMaScoreTests {

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

    @Test
    public void test1(){
        Integer id =458;


        String json = webClient.get()
                .uri("/schoolspecialscore/458/2023/11.json")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            JsonUvMasterScore jsonUvMasterScore = objectMapper.readValue(json, JsonUvMasterScore.class);
            System.out.println(jsonUvMasterScore);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }


    }



    @Test
    public void get() {
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
                        System.out.println(json);
                        jsonUvMasterScore = objectMapper.readValue(json, JsonUvMasterScore.class);

                    }catch(Exception ignored){

                    }

                    if(jsonUvMasterScore != null){
                        Map<String, Item> map = jsonUvMasterScore.getMap();
                        for (String s : map.keySet()) {
                            Item item = map.get(s);
                            for (UniversityMasterScore masterScore : item.getMasterScores()) {
                                masterScore.setYear(year);
                                masterScore.setStatus(1);
                                System.out.println(masterScore);
                                scoreMapper.insert(masterScore);
                            }

                        }
                    }




                }
            }

        }




    }
}
