package com.zzuli.gaokao.config;

import com.zzuli.gaokao.bean.CFRecommend;
import lombok.extern.slf4j.Slf4j;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.recommender.svd.ALSWRFactorizer;
import org.apache.mahout.cf.taste.impl.recommender.svd.SVDRecommender;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;

@Configuration
@Slf4j
public class CFRecommenderConfig {


    /*
     * @Description: 配置基于als的协同过滤
     * @Date:   2024/4/24 12:18
     * @Param:  []
     * @Return: com.zzuli.gaokao.bean.CFRecommend
     */
    @Bean
    public CFRecommend getCFRecommend(){

        ClassPathResource resource = new ClassPathResource("/cf.txt");
        File file = null;
        FileDataModel model = null;
        ALSWRFactorizer factorizer = null;
        SVDRecommender recommender = null;
        try {
            file = new File("/cf.txt");
            model = new FileDataModel(file);
            factorizer = new ALSWRFactorizer(model,5,0.001,100);
            recommender = new SVDRecommender(model, factorizer);

        } catch (IOException e) {
          log.error("协同过滤数据集加载失败！{}",e.getMessage());
        } catch (TasteException e) {
           log.error("迭代失败！{}",e.getMessage());
        }
        log.info("协同过滤模型加载成功！");
        return new CFRecommend(model,recommender);
    }


//    @Bean
//    public CFRecommend getCFRecommend(){
//
//        ClassPathResource resource = new ClassPathResource("/cf.txt");
//        File file = null;
//        FileDataModel model = null;
//        ALSWRFactorizer factorizer = null;
//        SVDRecommender recommender = null;
//        try {
//            file = resource.getFile();
//            model = new FileDataModel(file);
//            factorizer = new ALSWRFactorizer(model,5,0.001,100);
//            recommender = new SVDRecommender(model, factorizer);
//
//        } catch (IOException e) {
//            log.error("协同过滤数据集加载失败！{}",e.getMessage());
//        } catch (TasteException e) {
//            log.error("迭代失败！{}",e.getMessage());
//        }
//        log.info("协同过滤模型加载成功！");
//        return new CFRecommend(model,recommender);
//    }
}
