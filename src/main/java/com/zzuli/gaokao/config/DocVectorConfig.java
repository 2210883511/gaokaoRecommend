package com.zzuli.gaokao.config;


import com.hankcs.hanlp.mining.word2vec.DocVectorModel;
import com.hankcs.hanlp.mining.word2vec.TrainingCallback;
import com.hankcs.hanlp.mining.word2vec.Word2VecTrainer;
import com.hankcs.hanlp.mining.word2vec.WordVectorModel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class DocVectorConfig {

    @Value("${gaokao.loadPath}")
    String path;
    private final static Log log = LogFactory.getLog(DocVectorConfig.class);

    /*
     * @Description: 装载数据集
     * @Date:   2024/3/28 20:16
     * @Param:  []
     * @Return: com.hankcs.hanlp.mining.word2vec.DocVectorModel
     */
    @Bean
    public DocVectorModel loadModel(){

        try {
            WordVectorModel wordVectorModel = new WordVectorModel(path);

            return new DocVectorModel(wordVectorModel);

        } catch (IOException e) {
            log.warn("训练集装载失败!" + e.getMessage());
            return null;
        }

    }



    public Word2VecTrainer trainer(){

        Word2VecTrainer trainer = new Word2VecTrainer();
        trainer.setLayerSize(100);
        trainer.setCallback(new TrainingCallback() {
            @Override
            public void corpusLoading(float v) {

            }

            @Override
            public void corpusLoaded(int i, int i1, int i2) {

            }

            @Override
            public void training(float v, float v1) {

            }
        });

        return trainer;
    }
}
