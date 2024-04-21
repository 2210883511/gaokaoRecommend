package com.zzuli.gaokao;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zzuli.gaokao.bean.Provinces;
import com.zzuli.gaokao.bean.University;
import com.zzuli.gaokao.bean.UniversityProvinceScore;
import com.zzuli.gaokao.common.universitPprovinceSccore.Item;
import com.zzuli.gaokao.common.universitPprovinceSccore.JsonTest;
import com.zzuli.gaokao.mapper.ProvincesMapper;
import com.zzuli.gaokao.mapper.UniversityMapper;
import com.zzuli.gaokao.mapper.UniversityProvinceScoreMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Slf4j
@SpringBootTest
public class GetUniversityProvinceScoreTest {

    @Autowired
    private UniversityProvinceScoreMapper mapper;

    @Autowired
    private WebClient webClient;

    @Autowired
    private ProvincesMapper provincesMapper;

    @Autowired
    private UniversityMapper universityMapper;

    Integer[] years = {2022};


    @Test
    public void get() {
        List<Provinces> provinces = provincesMapper.selectList(new QueryWrapper<Provinces>().select("id"));

        List<University> schoolIds = universityMapper.selectList(new QueryWrapper<University>().select("school_id"));
        int count = 0;
        for (University university : schoolIds) {
            Integer schoolId = university.getSchoolId();
            for (Integer year : years){
                for (Provinces province : provinces){
                    Integer provinceId = province.getId();
                    if(count == 20)
                        try {
                            System.out.println("系统沉睡2s");
                            Thread.sleep(2000);
                            count = 0;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    String json = null;
                    JsonTest jsonTest = null;
                    try{
                         json = webClient.get()
                                .uri("/schoolprovincescore/{schoolId}/{year}/{provinceId}.json", schoolId, year, provinceId)
                                .accept(MediaType.APPLICATION_JSON)
                                .retrieve()
                                .bodyToMono(String.class)
                                .block();
                        count++;
                        ObjectMapper objectMapper = new ObjectMapper();
                        jsonTest = objectMapper.readValue(json, JsonTest.class);
                    }catch(Exception e){
                        log.error(e.getMessage());
                    }
                    if(jsonTest != null){
                        Map<String, Item> data = jsonTest.getData();
                        for (String s : data.keySet()) {
                            Item item = data.get(s);
                            for (UniversityProvinceScore universityProvinceScore : item.getUniversityProvinceScores()) {
                                System.out.println(universityProvinceScore);
                                universityProvinceScore.setStatus(1);
                                mapper.insert(universityProvinceScore);
                            }

                        }
                    }



                }
            }

        }




    }

    @Test
    public void get11(){
        String json = null;
        JsonTest jsonTest = null;
        try{
            json = webClient.get()
                    .uri("/schoolprovincescore/{schoolId}/{year}/{provinceId}.json", 140, 2023, 11)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            System.out.println(json);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(json);
            System.out.println(jsonNode.toPrettyString());
            jsonTest = objectMapper.readValue(json, JsonTest.class);
            System.out.println(jsonTest);
        }catch(Exception ignored){

        }
        if(jsonTest != null){
            Map<String, Item> data = jsonTest.getData();
            for (String s : data.keySet()) {
                Item item = data.get(s);
                for (UniversityProvinceScore universityProvinceScore : item.getUniversityProvinceScores()) {
                    System.out.println(universityProvinceScore);
                    universityProvinceScore.setStatus(1);
                    System.out.println(universityProvinceScore);
//                    mapper.insert(universityProvinceScore);
                }

            }
        }
    }

}
