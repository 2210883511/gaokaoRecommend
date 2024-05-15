package com.zzuli.gaokao.controller;

import com.zzuli.gaokao.bean.Provinces;
import com.zzuli.gaokao.common.Result;
import com.zzuli.gaokao.service.Impl.ProvincesServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;

@RestController
public class ProvinceController {

    @Autowired
    private ProvincesServiceImpl provincesService;


    @GetMapping("/getProvinces")
    public Result getProvince(){
        List<Provinces> list = provincesService.list();
        HashMap<String, Object> map = new HashMap<>();
        map.put("provinces",list);
        return Result.success(map);
    }
}
