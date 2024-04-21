package com.zzuli.gaokao.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzuli.gaokao.bean.Dic;
import com.zzuli.gaokao.mapper.DicMapper;
import com.zzuli.gaokao.service.DicService;
import org.springframework.stereotype.Service;

@Service
public class DicServiceImpl extends ServiceImpl<DicMapper, Dic> implements DicService {
}
