package com.zzuli.gaokao.config;

import com.zzuli.gaokao.common.StopWords;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.*;

@Configuration
@Slf4j
public class StopWordsConfig {


    @Bean
    public StopWords getStopWords(){

        ClassPathResource resource = new ClassPathResource("/static/cn_stopwords.txt");
        BufferedReader bufferedReader = null;
        StopWords words = null;
        try {
            InputStream inputStream = resource.getInputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            words = new StopWords();
            String line = null;
            while((line = bufferedReader.readLine() )!= null){
                words.put(line);
            }
        } catch (IOException e) {
            log.warn("停用词文件读取异常！{}",e.getMessage());
        }finally{
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (IOException e) {
                log.warn("缓冲流关闭失败！{}",e.getMessage());
            }
        }

        return words;

    }


}
