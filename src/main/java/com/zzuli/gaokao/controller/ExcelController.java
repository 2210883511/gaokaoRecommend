package com.zzuli.gaokao.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.util.ListUtils;
import com.zzuli.gaokao.bean.excel.UniversityExcel;
import com.zzuli.gaokao.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@RestController
public class ExcelController {

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;



    @GetMapping("/excel/university")
    public Result getUniversityExcel(){


        // 注意 simpleWrite在数据量不大的情况下可以使用（5000以内，具体也要看实际情况），数据量大参照 重复多次写入

        // 写法1 JDK8+
        // since: 3.0.0-beta1
        String fileName = "D:/" + "simpleWrite" + System.currentTimeMillis() + ".xlsx";
        // 这里 需要指定写用哪个class去写，然后写到第一个sheet，名字为模板 然后文件流会自动关闭
        // 如果这里想使用03 则 传入excelType参数即可
        EasyExcel.write(fileName, UniversityExcel.class)
                .sheet("模板")
                .doWrite(new Supplier<Collection<?>>() {
                    @Override
                    public Collection<?> get() {
                        return null;
                    }
                });
        return Result.success();
    }


    private List<UniversityExcel> data() {
        List<UniversityExcel> list = ListUtils.newArrayList();
        for (int i = 0; i < 10; i++) {
            UniversityExcel data = new UniversityExcel();
            data.setString("字符串" + i);
            data.setDate(new Date());
            data.setDoubleData(0.56);
            list.add(data);
        }
        return list;
    }



}