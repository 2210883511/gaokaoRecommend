package com.zzuli.gaokao.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzuli.gaokao.bean.UserAction;
import com.zzuli.gaokao.mapper.UserActionMapper;
import com.zzuli.gaokao.service.UserActionService;
import org.springframework.stereotype.Service;

@Service
public class UserActionServiceImpl extends ServiceImpl<UserActionMapper,UserAction> implements UserActionService {


}
