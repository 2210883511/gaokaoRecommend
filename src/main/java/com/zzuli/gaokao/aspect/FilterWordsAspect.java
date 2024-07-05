package com.zzuli.gaokao.aspect;

import com.zzuli.gaokao.common.SensitiveWords;
import com.zzuli.gaokao.vo.TfIdfVo;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Aspect
@Component
public class FilterWordsAspect {


    @Autowired
    private SensitiveWords sensitiveWords;



    @Around("@annotation(com.zzuli.gaokao.annotation.FilterWords)")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        Object[] args = pjp.getArgs();

        MethodSignature signature = (MethodSignature) pjp.getSignature();
        String[] parameterNames = signature.getParameterNames();
        for (int i = 0; i < args.length; i++){
            if("tfIdfVos".equals(parameterNames[i])){
                if(args[i] instanceof ArrayList){
                    ArrayList<Object> tfIdfVos = (ArrayList)args[i];
                    for (Object tfIdfVo : tfIdfVos) {
                        if(tfIdfVo instanceof TfIdfVo){
                            TfIdfVo tmp = (TfIdfVo) tfIdfVo;
                            String words = tmp.getName();
                            if(sensitiveWords.isContains(words)){
                                tmp.setName("***");
                            }
                        }
                    }

                }
            }
        }
        return pjp.proceed(args);
    }

}
