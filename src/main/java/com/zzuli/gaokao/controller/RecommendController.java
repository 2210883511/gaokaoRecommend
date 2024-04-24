package com.zzuli.gaokao.controller;


import com.zzuli.gaokao.common.Result;
import com.zzuli.gaokao.service.Impl.RecommendServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class RecommendController {


    @Autowired
    private RecommendServiceImpl recommendService;




    @GetMapping("/recommend")
    public Result getRecommend(Integer id) throws IOException {

        return recommendService.getRecommend(id);
    }



}
