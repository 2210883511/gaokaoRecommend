package com.zzuli.gaokao;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zzuli.gaokao.bean.Provinces;
import com.zzuli.gaokao.bean.University;
import com.zzuli.gaokao.mapper.ProvincesMapper;
import com.zzuli.gaokao.mapper.UniversityMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class MybatisPlusPageTest {

    @Autowired
    private UniversityMapper universityMapper;

    @Autowired
    private ProvincesMapper provincesMapper;

    @Test
    public void page(){

        Page<University> page = new Page<>(2,10);
        universityMapper.selectPage(page,null);
        List<University> list = page.getRecords();
        System.out.println(page.getPages() );
        System.out.println(page.getTotal());

    }

    @Test
    public void get() throws JsonProcessingException {
        Page<University> page = new Page<>(1,200);
        Page<University> pages = universityMapper.getPages(page, 41);
        List<University> list = pages.getRecords();
        System.out.println(pages.getTotal());
        System.out.println(pages.getPages());
        list.forEach(System.out::println);
        String json = " {\n" +
                "  \"number\": 123,\n" +
                "  \"string\": \"sample data\"\n" +
                " \n" +
                "}";
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(json);
    }


    @Test
    public void getProvinces(){
        List<Provinces> provinces = provincesMapper.selectList(null);
        System.out.println(provinces);
    }
}
