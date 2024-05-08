package com.zzuli.gaokao;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zzuli.gaokao.service.UniversityService;
import com.zzuli.gaokao.vo.UniversityVo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class UniversityServiceTests {


    @Autowired
    private UniversityService universityService;

    @Test
    public void get() throws JsonProcessingException {
        Page<UniversityVo> universityVoPage = universityService.selectCustom();
        ObjectMapper objectMapper = new ObjectMapper();
        String s = objectMapper.writeValueAsString(universityVoPage);
        System.out.println(s);
    }

}
