package com.zzuli.gaokao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zzuli.gaokao.bean.Master;
import com.zzuli.gaokao.bean.MasterType;
import com.zzuli.gaokao.bean.TypeDetail;
import com.zzuli.gaokao.service.Impl.MasterTypeServiceImpl;
import com.zzuli.gaokao.service.MasterService;
import com.zzuli.gaokao.service.MasterTypeService;
import com.zzuli.gaokao.service.TypeDetailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
public class TypeDetailTest {

    @Autowired
    private MasterTypeServiceImpl masterTypeService;

    @Autowired
    private TypeDetailService typeDetailService;

    @Autowired
    private MasterService masterService;

    @Test
    public void get(){

        List<MasterType> masterTypes = masterTypeService.list();

        masterTypes.forEach(masterType -> {

            QueryWrapper<Master> wrapper = new QueryWrapper<>();
            wrapper.select("distinct type_detail")
                    .eq("type",masterType.getType())
                    .eq("level1_name",masterType.getLevelName());
            List<String> collect = masterService.list(wrapper)
                    .stream()
                    .map(Master::getTypeDetail)
                    .collect(Collectors.toList());
            for (String s : collect) {
                TypeDetail typeDetail = new TypeDetail();
                typeDetail.setTypeDetail(s);
                typeDetail.setTypeId(masterType.getId());
                typeDetail.setStatus(1);
                typeDetail.setTypeName(masterType.getType());
                typeDetailService.save(typeDetail);
            }
            System.out.println(masterType.getType() + "   "  + masterType.getLevelName() +  "   "+ collect);
        });

    }


    @Test
    public void update(){

        List<TypeDetail> typeDetails = typeDetailService.list();

        List<Master> masters = masterService.list(new QueryWrapper<Master>()
                .select("type", "type_detail","id"));
        for (Master master : masters) {
            String type = master.getType();
            String typeDetail = master.getTypeDetail();
            for (TypeDetail detail : typeDetails) {
                if(type.equals(detail.getTypeName()) && typeDetail.equals(detail.getTypeDetail())){
                    master.setTypeDetailId(detail.getId());
                }
            }
        }
//        List<HashMap<String, Object>> list = masters.stream()
//                .map(master -> {
//                    HashMap<String, Object> map = new HashMap<>();
//                    map.put("type", master.getType());
//                    map.put("type_detail", master.getTypeDetail());
//                    map.put("type_detail_id", master.getTypeDetailId());
//                    return map;
//                })
//                .collect(Collectors.toList());
//        list.forEach(System.out::println);

        masterService.updateBatchById(masters);

    }

    @Test
    public void getTypeDetail(){


        for (TypeDetail typeDetail : typeDetailService.list()) {
            System.out.println(typeDetail);
        }



    }
}
