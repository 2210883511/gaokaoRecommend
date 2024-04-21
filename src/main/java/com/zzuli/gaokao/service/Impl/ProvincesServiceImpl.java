package com.zzuli.gaokao.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzuli.gaokao.bean.Provinces;
import com.zzuli.gaokao.mapper.ProvincesMapper;
import com.zzuli.gaokao.service.ProvincesService;
import org.springframework.stereotype.Service;

@Service
public class ProvincesServiceImpl extends ServiceImpl<ProvincesMapper, Provinces>  implements ProvincesService {
}
