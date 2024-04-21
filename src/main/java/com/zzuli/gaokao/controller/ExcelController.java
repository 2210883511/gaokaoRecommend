package com.zzuli.gaokao.controller;

import com.zzuli.gaokao.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@RestController
public class ExcelController {
    Integer count = 0;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @GetMapping("/thread")
    public Result test(){
//        Thread thread1 =  new Thread(() -> {
//            Date date = new Date(System.currentTimeMillis() + 1000*  20);
//            while(date.after(new Date())){
//                Boolean result = redisTemplate.opsForValue().setIfAbsent("lock", 1,10, TimeUnit.SECONDS);
//                System.out.println(result);
//                if(Boolean.TRUE.equals(result)){
//                    System.out.println(Thread.currentThread().getName() + "   获取锁成功！");
//                }else {
//                    System.out.println(Thread.currentThread().getName() + "   获取失败！");
//                }
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//
//        });
//        thread1.setName("线程1");
        System.out.println(Thread.currentThread().getName());
        count++;
        System.out.println("想发疯顺风顺水");
        System.out.println(Thread.currentThread().getName());
        System.out.println(count);
        return Result.success("heello",count);
    }



}