package com.zzuli.gaokao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zzuli.gaokao.bean.Provinces;
import com.zzuli.gaokao.bean.University;


import com.zzuli.gaokao.bean.UniversityMasterPlan;
import com.zzuli.gaokao.common.universityMasterPlan.Item;
import com.zzuli.gaokao.common.universityMasterPlan.JsonMasterPlan;
import com.zzuli.gaokao.mapper.ProvincesMapper;
import com.zzuli.gaokao.mapper.UniversityMapper;
import com.zzuli.gaokao.mapper.UniversityMasterPlanMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@SpringBootTest
@Slf4j
public class MasterPlanTests {

    @Autowired
    private UniversityMasterPlanMapper masterPlanMapper;

    @Autowired
    private WebClient webClient;

    @Autowired
    private ProvincesMapper provincesMapper;

    @Autowired
    private UniversityMapper universityMapper;

    Integer[] years = {2023};

    @Test
    public void test(){
        List<Provinces> provinces = provincesMapper.selectList(new QueryWrapper<Provinces>().select("id"));

        List<University> schoolIds = universityMapper.selectList(new QueryWrapper<University>().select("school_id")
                .ge("school_id",776));
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
                    JsonMasterPlan jsonMasterPlan = null;
                    try{
                        json = webClient.get()
                                .uri("/schoolspecialplan/{schoolId}/{year}/{provinceId}.json", schoolId, year, provinceId)
                                .accept(MediaType.APPLICATION_JSON)
                                .retrieve()
                                .bodyToMono(String.class)
                                .block();
                        count++;
                        ObjectMapper objectMapper = new ObjectMapper();
                        jsonMasterPlan = objectMapper.readValue(json, JsonMasterPlan.class);
                    }catch(Exception e){
                        log.error(e.getMessage());
                    }
                    if(jsonMasterPlan != null){
                        Map<String, Item> data = jsonMasterPlan.getData();
                        for (String s : data.keySet()) {
                            Item item = data.get(s);
                            for (UniversityMasterPlan universityMasterPlan  : item.getUniversityMasterPlans()) {
                                System.out.println(universityMasterPlan);
                                universityMasterPlan.setStatus(1);
                                universityMasterPlan.setYear(year);
                                masterPlanMapper.insert(universityMasterPlan);
                            }

                        }
                    }



                }
            }

        }


    }
}
