package com.zzuli.gaokao.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzuli.gaokao.bean.Tags;
import com.zzuli.gaokao.mapper.TagsMapper;
import com.zzuli.gaokao.service.TagsService;
import org.springframework.stereotype.Service;

@Service
public class TagsServiceImpl extends ServiceImpl<TagsMapper, Tags>  implements TagsService {


}
