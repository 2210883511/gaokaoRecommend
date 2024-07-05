package com.zzuli.gaokao.config;

import com.zzuli.gaokao.common.SensitiveWords;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.*;


@Configuration
@Slf4j
public class FilterWordsConfig {

    @Bean
    public SensitiveWords getFilterWords(){
        ClassPathResource resource = new ClassPathResource("/static/filter_words.txt");
        SensitiveWords sensitiveWords = null;
        try(InputStream inputStream = resource.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            String words = null;
            sensitiveWords = new SensitiveWords();
            while( (words= reader.readLine()) != null){
                sensitiveWords.add(words);
            }

        } catch (IOException e) {
            log.warn("敏感词加载失败！ 错误消息：{}",e.getMessage());
        }
        return sensitiveWords;
    }
}
