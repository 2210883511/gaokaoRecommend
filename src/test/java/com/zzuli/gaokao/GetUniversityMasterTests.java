package com.zzuli.gaokao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zzuli.gaokao.bean.University;
import com.zzuli.gaokao.bean.UniversityMaster;
import com.zzuli.gaokao.common.universityMaster.JsonUvMaster;
import com.zzuli.gaokao.mapper.UniversityMapper;
import com.zzuli.gaokao.mapper.UniversityMasterMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@SpringBootTest
public class GetUniversityMasterTests {

    @Autowired
    private UniversityMasterMapper usMapper;

    @Autowired
    private UniversityMapper uvMapper;

    @Autowired
    private WebClient webClient;

    @Test
    public void test(){

        List<University> schoolId = uvMapper.selectList(new QueryWrapper<University>().select("school_id"));
        int count =0;
        for (University university : schoolId) {
            if(count == 20){
                try {
                    count=0;
                    System.out.println("系统沉睡2s");
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            String json = webClient.get()
                    .uri("/school/{id}/pc_special.json", university.getSchoolId())
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            count++;
            ObjectMapper objectMapper = new ObjectMapper();
            JsonUvMaster jsonUvMaster = null;
            try {
                jsonUvMaster = objectMapper.readValue(json, JsonUvMaster.class);
                System.out.println(jsonUvMaster);
                for (UniversityMaster master : jsonUvMaster.getMaster1()) {
                    usMapper.insert(master);
                }
                for (UniversityMaster master : jsonUvMaster.getMaster2()) {
                    usMapper.insert(master);
                }

            } catch (Exception ignored) {

            }

        }


    }




}
