package com.zzuli.gaokao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zzuli.gaokao.bean.UniversityProvinceScore;
import com.zzuli.gaokao.bean.User;
import com.zzuli.gaokao.bean.UserUniversityActions;
import com.zzuli.gaokao.common.universitPprovinceSccore.Item;
import com.zzuli.gaokao.common.universitPprovinceSccore.JsonTest;
import com.zzuli.gaokao.mapper.UniversityMapper;
import com.zzuli.gaokao.mapper.UserMapper;

import com.zzuli.gaokao.mapper.UserUniversityActionsMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.FSDirectory;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.eval.AverageAbsoluteDifferenceRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.eval.RMSRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.EuclideanDistanceSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.UncenteredCosineSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;


@Slf4j
@SpringBootTest
class GaokaoApplicationTests {

    @Autowired
    private UniversityMapper universityMapper;

    @Autowired
    private UserUniversityActionsMapper userUniversityActionsMapper;

    @Autowired
    WebClient webClient;

    @Autowired
    UserMapper userMapper;
    @Test
    void contextLoads() {

        List<User> userMappers = userMapper.selectList(null);
        System.out.println(userMappers);

    }


    @Test
    void WebClientSchoolInfo(){
        String id = "458";
        StringBuilder builder = new StringBuilder();
        Flux<String> mono = webClient.get().uri("/school/{id}/pc_special.json",id)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(String.class);
        Iterator<String> iterator = mono.toIterable().iterator();
        while(iterator.hasNext()){
            builder.append(iterator.next());
        }
//        System.out.println(builder);
        ObjectMapper objectMapper = new ObjectMapper();
        String json = builder.toString();
        try {
            JsonNode jsonNode = objectMapper.readTree(json);
            String s = jsonNode.get("data").get("1").toPrettyString();
            System.out.println(s);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }


    @Test
    void WebClientSchoolList(){
        StringBuilder builder = new StringBuilder();
        String json = webClient.get().uri("/info/linkage.json")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class)
                .block();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode jsonNode = objectMapper.readTree(json).get("data");
            System.out.println(jsonNode.toPrettyString());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

    }






    /*
     * @Description:
     * @Date:   2024/3/13 17:06
     * @Param:  基于预测的模型评估
     * @Return:
     **/
    @Test
    public void recommend() throws TasteException {
        File file = null;
        try {
//            RandomUtils.useTestSeed();
            AverageAbsoluteDifferenceRecommenderEvaluator evaluator = new AverageAbsoluteDifferenceRecommenderEvaluator();
            RMSRecommenderEvaluator rmsRecommenderEvaluator = new RMSRecommenderEvaluator();
            file = new ClassPathResource("/test.txt").getFile();
            FileDataModel model = new FileDataModel(file);
            RecommenderBuilder recommenderBuilder = new RecommenderBuilder() {
                @Override
                public Recommender buildRecommender(DataModel model) throws TasteException {

                    // 计算相似度用于找到邻居

                    // 欧几里得距离相似度
                    EuclideanDistanceSimilarity similarity = new EuclideanDistanceSimilarity(model);

                    // 皮尔逊系数相似度
                    PearsonCorrelationSimilarity pearsonCorrelationSimilarity = new PearsonCorrelationSimilarity(model);

                    // 余弦相似度
                    UncenteredCosineSimilarity cosineSimilarity = new UncenteredCosineSimilarity(model);

                    // 找到前100个相似的邻居

                    //计算最近邻域，邻居有两种算法，基于固定数量的邻居和基于相似度的邻居，这里使用基于固定数量的邻居
                    NearestNUserNeighborhood neighborhood = new NearestNUserNeighborhood(20, cosineSimilarity, model);
                    ThresholdUserNeighborhood thresholdUserNeighborhood = new ThresholdUserNeighborhood(0, pearsonCorrelationSimilarity, model);
                    return new GenericUserBasedRecommender(model, neighborhood, similarity);
                }
            };
            try {
                double score1 = evaluator.evaluate(recommenderBuilder, null, model, 0.7, 1);
//                double score2 = rmsRecommenderEvaluator.evaluate(recommenderBuilder, null, model, 0.7, 1);
                System.out.println(score1);  // 0.81  用户实际5分  5 - 0.81 = 4.19 我们预测的评分是4.19 这里有0.81的误差
//                System.out.println(score2);
                Recommender recommender = recommenderBuilder.buildRecommender(model);
//                //给用户ID等于1的用户推荐20部电影
//                List<RecommendedItem> list = recommender.recommend(1, 20);
                LongPrimitiveIterator userIDs = model.getUserIDs();
                FileWriter writer = new FileWriter("./src/main/resources/recommend.txt");
                BufferedWriter bufferedWriter = new BufferedWriter(writer);
                while (userIDs.hasNext()){
                    Long id = userIDs.next();
                    List<RecommendedItem> recommend = recommender.recommend(id, 20);

                    StringBuilder builder = new StringBuilder();
                    builder.append("给用户" + id +"推荐的大学有：\n");
                    System.out.println("给用户" + id +"推荐的大学有：\n");
                    for (RecommendedItem item : recommend) {
                        long itemID = item.getItemID();
                        float predict = item.getValue();
                        String schoolName = universityMapper.selectById(itemID).getSchoolName();
                        builder.append(schoolName + "\t预测喜欢值：\t" + predict + "\n");
                        System.out.println(schoolName + "\t预测喜欢值：\t" + predict + "\n");
                    }
                    bufferedWriter.write(builder.toString());
                    writer.flush();
                }
                bufferedWriter.close();

            } catch (TasteException e) {
                e.printStackTrace();
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testRecommend(){

        List<UserUniversityActions> userPreference = userUniversityActionsMapper.getUserPreference();

        try(BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("./src/main/resources/test.txt"));) {

            for (UserUniversityActions preference : userPreference) {
                    String line = preference.getUserId()+ "," + preference.getUniversityId()+","
                            + preference.getValue()+ "\n";
                    bufferedWriter.write(line);
                    bufferedWriter.flush();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    @Test
    public void select(){
        List<Integer> list = universityMapper.selectMissId();
        System.out.println(list);
    }

    @Test
    public void test() throws IOException {
        FSDirectory directory = FSDirectory.open(Paths.get("./src/main/resources/recommend") );
        DirectoryReader reader = DirectoryReader.open(directory);
        IndexSearcher searcher = new IndexSearcher(reader);  // 定义查询词项
        String termText = "大学"; // 替换为你的查询词项

        // 构建查询
        TermQuery query = new TermQuery(new Term("content", termText));

        // 执行查询
        ScoreDoc[] hits = searcher.search(query, 10).scoreDocs;

        // 输出查询结果
        System.out.println("Top 10 documents containing the term '" + termText + "':");
        for (int i = 0; i < hits.length; i++) {
            int docId = hits[i].doc;
            System.out.println("Document ID: " + docId + ", Score: " + hits[i].score);
        }

        // 关闭 IndexReader
        reader.close();

    }


    @Test
    public void JsonTest() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String json = "{\n" +
                "  \"number\": 123,\n" +
                "  \"test_a\": \"sample data\",\n" +
                "  \"data\": {\n" +
                "    \"1\": {},\n" +
                "    \"2\": {}\n" +
                "  }\n" +
                "}";
        JsonTest jsonTest = objectMapper.readValue(json, JsonTest.class);
        System.out.println(jsonTest);
    }
    @Test
    public void JsonTest1() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String json = "{\n" +
                "  \"code\": \"0000\",\n" +
                "  \"message\": \"成功\",\n" +
                "  \"data\": {\n" +
                "    \"3\": {\n" +
                "      \"numFound\": 3,\n" +
                "      \"item\": [\n" +
                "        {\n" +
                "          \"school_id\": \"458\",\n" +
                "          \"province_id\": \"11\",\n" +
                "          \"type\": \"3\",\n" +
                "          \"batch\": \"14\",\n" +
                "          \"zslx\": \"0\",\n" +
                "          \"xclevel\": \"0\",\n" +
                "          \"max\": \"-\",\n" +
                "          \"min_section\": \"31831\",\n" +
                "          \"min\": \"500\",\n" +
                "          \"average\": \"-\",\n" +
                "          \"filing\": \"500\",\n" +
                "          \"special_group\": \"108314\",\n" +
                "          \"first_km\": \"0\",\n" +
                "          \"local_batch_id\": \"14\",\n" +
                "          \"local_batch_name\": \"本科批\",\n" +
                "          \"zslx_name\": \"普通类\",\n" +
                "          \"xclevel_name\": \"-\",\n" +
                "          \"zslx_rank\": 200,\n" +
                "          \"sg_fxk\": \"\",\n" +
                "          \"sg_sxk\": \"70000,70001,70002\",\n" +
                "          \"sg_type\": \"80002\",\n" +
                "          \"sg_name\": \"（03）\",\n" +
                "          \"sg_info\": \"物/化/生(3选1)\",\n" +
                "          \"proscore\": \"448\",\n" +
                "          \"year\": \"2023\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"school_id\": \"458\",\n" +
                "          \"province_id\": \"11\",\n" +
                "          \"type\": \"3\",\n" +
                "          \"batch\": \"14\",\n" +
                "          \"zslx\": \"0\",\n" +
                "          \"xclevel\": \"0\",\n" +
                "          \"max\": \"-\",\n" +
                "          \"min_section\": \"36227\",\n" +
                "          \"min\": \"480\",\n" +
                "          \"average\": \"-\",\n" +
                "          \"filing\": \"480\",\n" +
                "          \"special_group\": \"108315\",\n" +
                "          \"first_km\": \"0\",\n" +
                "          \"local_batch_id\": \"14\",\n" +
                "          \"local_batch_name\": \"本科批\",\n" +
                "          \"zslx_name\": \"普通类\",\n" +
                "          \"xclevel_name\": \"-\",\n" +
                "          \"zslx_rank\": 200,\n" +
                "          \"sg_fxk\": \"\",\n" +
                "          \"sg_sxk\": \"70000\",\n" +
                "          \"sg_type\": \"80003\",\n" +
                "          \"sg_name\": \"（02）\",\n" +
                "          \"sg_info\": \"物理必选\",\n" +
                "          \"proscore\": \"448\",\n" +
                "          \"year\": \"2023\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"school_id\": \"458\",\n" +
                "          \"province_id\": \"11\",\n" +
                "          \"type\": \"3\",\n" +
                "          \"batch\": \"14\",\n" +
                "          \"zslx\": \"0\",\n" +
                "          \"xclevel\": \"0\",\n" +
                "          \"max\": \"-\",\n" +
                "          \"min_section\": \"36227\",\n" +
                "          \"min\": \"480\",\n" +
                "          \"average\": \"-\",\n" +
                "          \"filing\": \"480\",\n" +
                "          \"special_group\": \"108316\",\n" +
                "          \"first_km\": \"0\",\n" +
                "          \"local_batch_id\": \"14\",\n" +
                "          \"local_batch_name\": \"本科批\",\n" +
                "          \"zslx_name\": \"普通类\",\n" +
                "          \"xclevel_name\": \"-\",\n" +
                "          \"zslx_rank\": 200,\n" +
                "          \"sg_fxk\": \"\",\n" +
                "          \"sg_sxk\": \"70008\",\n" +
                "          \"sg_type\": \"80001\",\n" +
                "          \"sg_name\": \"（01）\",\n" +
                "          \"sg_info\": \"不限\",\n" +
                "          \"proscore\": \"448\",\n" +
                "          \"year\": \"2023\"\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  },\n" +
                "  \"md5\": \"8264fed2430369186c23bb907a216cf2\",\n" +
                "  \"time\": \"2024-03-14 19:03:08\"\n" +
                "}";
        JsonTest jsonTest = objectMapper.readValue(json, JsonTest.class);
        Map<String, Item> map = jsonTest.getData();
        for (String s : map.keySet()) {
            Item item = map.get(s);
            for (UniversityProvinceScore universityProvinceScore : item.getUniversityProvinceScores()) {
                System.out.println(universityProvinceScore);
            }

        }

    }





}
