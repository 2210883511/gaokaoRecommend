package com.zzuli.gaokao;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GaokaoApplication {


    public static void main(String[] args) {
        SpringApplication.run(GaokaoApplication.class, args);
    }

}
