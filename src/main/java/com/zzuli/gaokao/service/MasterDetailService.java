package com.zzuli.gaokao.service;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zzuli.gaokao.bean.MasterDetail;

import java.util.Map;

public interface MasterDetailService extends IService<MasterDetail> {

    Page<Map<String,Object>> findCustom(Page<Map<String,Object>> page,String masterName,String type,String typeDetail,String levelName);
}
