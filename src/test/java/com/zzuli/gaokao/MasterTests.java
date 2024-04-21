package com.zzuli.gaokao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zzuli.gaokao.bean.Master;
import com.zzuli.gaokao.bean.MasterType;
import com.zzuli.gaokao.mapper.MasterMapper;
import com.zzuli.gaokao.service.Impl.MasterServiceImpl;
import com.zzuli.gaokao.service.Impl.MasterTypeServiceImpl;
import com.zzuli.gaokao.service.MasterService;
import com.zzuli.gaokao.service.MasterTypeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
public class MasterTests {

    @Autowired
    private MasterServiceImpl masterService;

    @Autowired
    private MasterTypeServiceImpl typeService;


    @Test
    public void get(){

        QueryWrapper<Master> wrapper = new QueryWrapper<>();
        wrapper.select("DISTINCT type","level1_name").eq("level1_name","专科（高职）");
        List<Master> list = masterService.list(wrapper);
        List<MasterType> collect = list.stream()
                .map(master -> {
                    MasterType masterType = new MasterType();
                    String type = master.getType();
                    String levelName = master.getLevel1Name();
                    masterType.setType(type);
                    masterType.setLevelName(levelName);
                    masterType.setStatus(1);
                    return masterType;
                })
                .collect(Collectors.toList());
        typeService.saveBatch(collect);

    }
}
