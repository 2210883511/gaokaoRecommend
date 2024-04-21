package com.zzuli.gaokao;


import com.zzuli.gaokao.bean.Master;
import com.zzuli.gaokao.bean.MasterDetail;
import com.zzuli.gaokao.service.MasterDetailService;
import com.zzuli.gaokao.service.MasterService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
public class MasterDetailTests {

    @Autowired
    private MasterDetailService masterDetailService;

    @Autowired
    private MasterService masterService;

    @Test
    public void get(){
        List<Master> masterList = masterService.list();
        List<MasterDetail> collect = masterList.stream()
                .map(master -> {
                    MasterDetail detail = new MasterDetail();
                    detail.setMasterId(master.getId());
                    return detail;
                })
                .collect(Collectors.toList());
        masterDetailService.saveBatch(collect);

    }
}
