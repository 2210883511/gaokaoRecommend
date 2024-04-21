package com.zzuli.gaokao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@SpringBootTest
public class RedisTests {


    @Autowired
    private RedisTemplate<String,Object> redisTemplate;


    @Test
    public void testLock()  {
        ArrayList<String> list = new ArrayList<>();
        list.add("article:1:title");
        list.add("article:1:content");
        list.add("article:1:author");
        List<Object> objects = redisTemplate.opsForValue().multiGet(list);
        Long size = redisTemplate.opsForValue().size("article:1:author");
        System.out.println(size);
        System.out.println(objects);
    }
}
