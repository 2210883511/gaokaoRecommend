package com.zzuli.gaokao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zzuli.gaokao.bean.*;
import com.zzuli.gaokao.mapper.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SpringBootTest
public class WebClientTests {

    @Autowired
    private WebClient webClient;

    @Autowired
    private UniversityMapper universityMapper;

    @Autowired
    private UniversityInfoMapper universityInfoMapper;

    @Autowired
    private UniversityTagsMapper universityTagsMapper;

    @Autowired
    private DualClassMapper dualClassMapper;


    @Autowired
    private UniversityMasterMapper universityMasterMapper;


    @Autowired
    private MasterMapper masterMapper;

    @Autowired
    private UniversityRankMapper rankMapper;

    @Autowired
    private UniversityImgMapper universityImgMapper;


    StringBuilder builder = null;

    @Test
    public void insertUniversity(){
        University university = null;
        String json = webClient.get().uri("/info/linkage.json")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class)
                .block();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode jsonNode = objectMapper.readTree(json).get("data").get("school");
            System.out.println(jsonNode.toPrettyString());
            for (JsonNode node : jsonNode) {
                university = new University();
                int school_id = node.get("school_id").asInt();
                String name = node.get("name").asText();
                university.setSchoolId(school_id);
                university.setSchoolName(name);
                universityMapper.insert(university);

            }

//            try {
//                Thread.sleep(15000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }


        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }


    }


    @Test
    public void insertUniversityDetail(){
        QueryWrapper<University> j = new QueryWrapper<University>();
        j.select("school_id");
        List<University> list = universityMapper.selectList(j);
        int count = 0;
        for (University university : list) {
            if(count == 300){
                try {
                    System.out.println("沉睡3s");
                    count = 0;
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            String json = webClient.get().uri("/school/{id}/info.json",university.getSchoolId())
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            count++;
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                JsonNode jsonNode = objectMapper.readTree(json).get("data");
                int province_id = jsonNode.get("province_id").asInt();
                String city_name = jsonNode.get("city_name").asText();
                String town_name = jsonNode.get("town_name").asText();
                String phone = jsonNode.get("phone").asText();
                String site = jsonNode.get("site").asText();
                String school_site = jsonNode.get("school_site").asText();
                String email = jsonNode.get("email").asText();
                university.setCityName(city_name);
                university.setEmail(email);
                university.setProvinceId(province_id);
                university.setTownName(town_name);
                university.setPhone(phone);
                university.setSite(site);
                university.setSchoolSite(school_site);
                universityMapper.updateById(university);
                String content = jsonNode.get("content").asText();
                String area = jsonNode.get("area").asText();
                String create_date = jsonNode.get("create_date").asText();
                String belong = jsonNode.get("belong").asText();
                String address = jsonNode.get("address").asText();
                int num_master = jsonNode.get("num_master").asInt();
                int num_doctor = jsonNode.get("num_doctor").asInt();
                int num_subject = jsonNode.get("num_subject").asInt();
                UniversityInfo universityInfo = new UniversityInfo();
                universityInfo.setSchoolId(university.getSchoolId());
                universityInfo.setAddress(address);
                universityInfo.setAddTime(new Date());
                universityInfo.setArea(area);
                universityInfo.setBelong(belong);
                universityInfo.setNumDoctor(num_doctor);
                universityInfo.setNumMaster(num_master);
                universityInfo.setNumSubject(num_subject);
                universityInfo.setContent(content);
                universityInfo.setCreateDate(create_date);
                universityInfoMapper.insert(universityInfo);
                int f985 = jsonNode.get("f985").asInt();
                int f211 = jsonNode.get("f211").asInt();
                String school_type_name = jsonNode.get("school_type_name").asText();
                String school_nature_name = jsonNode.get("school_nature_name").asText();
                String dual_class_name = jsonNode.get("dual_class_name").asText();
                String type_name = jsonNode.get("type_name").asText();
                UniversityTags universityTags = new UniversityTags();
                universityTags.setSchoolId(university.getSchoolId());
                universityTags.setF211(f211);
                universityTags.setF985(f985);
                universityTags.setDualClassName(dual_class_name);
                universityTags.setSchoolNatureName(school_nature_name);
                universityTags.setSchoolTypeName(school_type_name);
                universityTags.setTypeName(type_name);
                universityTagsMapper.insert(universityTags);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

        }


    }


    @Test
    public void insertDualClass(){
        QueryWrapper<University> wrapper = new QueryWrapper<>();
        wrapper.gt("school_id",1006);
        wrapper.select("school_id");
        List<University> list = universityMapper.selectList(wrapper);
        int count = 0;
        for (University university : list) {
            if(count == 300){
                try {
                    System.out.println("沉睡3s");
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            String json = webClient.get().uri("/school/{id}/info.json",university.getSchoolId())
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            count++;
            System.out.println(json);
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                JsonNode jsonNode = objectMapper.readTree(json).get("data").get("dualclass");
                if(!jsonNode.isNull()){
                    for (JsonNode node : jsonNode) {
                        int id = node.get("id").asInt();
                        int school_id = node.get("school_id").asInt();
                        String aClass = node.get("class").asText();
                        DualClass dualClass = new DualClass();
                        dualClass.setId(id);
                        dualClass.setSchoolId(school_id);
                        dualClass.setSubject(aClass);
                        dualClassMapper.insert(dualClass);
                    }
                }


            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
    }


    @Test
    public void insertMaster(){
        Master master = null;
        String json = webClient.get().uri("/info/linkage.json")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class)
                .block();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode jsonNode = objectMapper.readTree(json).get("data").get("special");
            System.out.println(jsonNode.toPrettyString());
            for (JsonNode node : jsonNode) {
                master = new Master();
                int id = node.get("id").asInt();
                String name = node.get("name").asText();
                master.setId(id);
                master.setName(name);
                masterMapper.insert(master);
            }



        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

    }



    @Test
    public void insertMasterInfo(){
        QueryWrapper<Master> queryWrapper = new QueryWrapper<>();
        queryWrapper.gt("id",8369)
                .select("id");

        List<Master> list = masterMapper.selectList(queryWrapper);
        System.out.println(list.size());
        int count = 0;
        for (Master master : list) {

            if(count == 150){
                try {
                    System.out.println("系统沉睡3s");
                    Thread.sleep(3000);
                    count = 0;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            String json = webClient.get()
                    .uri("/special/{id}/pc_special_detail.json", master.getId())
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            count++;
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                JsonNode data = objectMapper.readTree(json).get("data");
                String code = data.get("code").asText();
                String job = data.get("job").asText();
                String learn_what = data.get("learn_what").asText();
                String is_what = data.get("is_what").asText();
                String do_what = data.get("do_what").asText();
                String limit_year = data.get("limit_year").asText();
                String course = data.get("course").asText();
                String type = data.get("type").asText();
                String type_detail = data.get("type_detail").asText();
                String level1_name = data.get("level1_name").asText();
                String content = data.get("content").asText();
                String degree = data.get("degree").asText();
//                master.setCode(code);
//                master.setContent(content);
//                master.setAddTime(new Date());
//                master.setCourse(course);
//                master.setDegree(degree);
//                master.setDoWhat(do_what);
//                master.setIsWhat(is_what);
//                master.setLimitYear(limit_year);
//                master.setType(type);
//                master.setTypeDetail(type_detail);
//                master.setLevel1Name(level1_name);
//                master.setLearnWhat(learn_what);
//                master.setJob(job);
                master.setStatus(1);
                System.out.println(master);
                masterMapper.updateById(master);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

        }


    }



    @Test
    public void insertRank(){

        QueryWrapper<University> wrapper = new QueryWrapper<>();
        wrapper.select("school_id");
        List<University> list = universityMapper.selectList(wrapper);
        int count =0;
        for (University university : list) {
            if(count == 150){
                try {
                    System.out.println("系统沉睡3s");
                    Thread.sleep(3000);
                    count = 0;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            String json = webClient.get()
                    .uri("/school/{id}/rank.json", university.getSchoolId())
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            count++;
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                JsonNode data = objectMapper.readTree(json).get("data");
                System.out.println(data.toPrettyString());
                for (JsonNode node : data) {
                    int rank = node.get("rank").asInt();
                    String rank_type = node.get("rank_type").asText();
                    String rank_name = node.get("rank_name").asText();
                    UniversityRank universityRank = new UniversityRank();
                    universityRank.setRank(rank);
                    universityRank.setRankType(rank_type);
                    universityRank.setRankName(rank_name);
                    universityRank.setSchoolId(university.getSchoolId());
                    rankMapper.insert(universityRank);
                }

            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }



    }


    @Test
    public void insertUvMaster(){
        QueryWrapper<University> wrapper = new QueryWrapper<>();
        wrapper.select("school_id");
        wrapper.gt("school_id",1269);
        List<University> list = universityMapper.selectList(wrapper);
        int count = 0;
        for (University university : list) {
            if(count ==30)
                try {
                    System.out.println("系统沉睡3s");
                    Thread.sleep(3000);
                    count = 0;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            String json = webClient.get().uri("/school/{id}/pc_special.json", university.getSchoolId())
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            count++;
            ObjectMapper objectMapper = new ObjectMapper();

            try {
                JsonNode data = objectMapper.readTree(json).get("data").get("1");
                if(data != null){
                    for (JsonNode node : data) {
                        String xueke_rank_score = node.get("xueke_rank_score").asText();
                        int special_id = node.get("special_id").asInt();
                        UniversityMaster universityMaster = new UniversityMaster();
                        universityMaster.setSchoolId(university.getSchoolId());
                        universityMaster.setSpecialId(special_id);
                        universityMaster.setXuekeRankScore(xueke_rank_score);
                        System.out.println(universityMaster);
                        universityMasterMapper.insert(universityMaster);

                    }
                }
                JsonNode data2 = objectMapper.readTree(json).get("data").get("2");
                if(data2 != null){
                    for (JsonNode node : data) {
                        String xueke_rank_score = node.get("xueke_rank_score").asText();
                        int special_id = node.get("special_id").asInt();
                        UniversityMaster universityMaster = new UniversityMaster();
                        universityMaster.setSchoolId(university.getSchoolId());
                        universityMaster.setSpecialId(special_id);
                        universityMaster.setXuekeRankScore(xueke_rank_score);
                        System.out.println(universityMaster);
                        universityMasterMapper.insert(universityMaster);
                    }
                }





            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }


    }





    @Test
    public void updateUniversityHeader(){
        String baseUrl = "https://static-data.gaokao.cn/upload/logo/";
        QueryWrapper<University> wrapper = new QueryWrapper<>();
        wrapper.select("school_id");
        List<University> list = universityMapper.selectList(wrapper);
        for (University university : list) {
            UpdateWrapper<University> updateWrapper = new UpdateWrapper<>();
            updateWrapper.set("header_url",baseUrl + university.getSchoolId()+".jpg");
            updateWrapper.eq("school_id",university.getSchoolId());
            universityMapper.update(null,updateWrapper);
        }

    }



    @Test
    public void insertUniversityImg() throws JsonProcessingException {

        QueryWrapper<University> wrapper = new QueryWrapper<>();
        wrapper.select("school_id");
        List<University> list = universityMapper.selectList(wrapper);
        int count = 0;
        for (University university : list) {
            if(count == 10)
                try {
                    System.out.println("系统沉睡3s");
                    Thread.sleep(2000);
                    count = 0;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            Integer schoolId = university.getSchoolId();
            String json = webClient.get()
                    .uri("/school/image/{schoolId}.json", schoolId)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            count++;
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(json).get("data").get("schoolImg");
            System.out.println(jsonNode.toPrettyString());
            for (JsonNode node : jsonNode) {
                UniversityImg img = new UniversityImg();
                String title = node.get("title").asText();
                int rank = node.get("rank").asInt();
                String url = node.get("url").asText();
                img.setSchoolId(university.getSchoolId());
                img.setTitle(title);
                img.setRank(rank);
                img.setUrl("https://static-data.gaokao.cn" + url);
                universityImgMapper.insert(img);
            }

        }


    }


}
