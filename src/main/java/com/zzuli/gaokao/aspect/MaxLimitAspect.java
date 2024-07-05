package com.zzuli.gaokao.aspect;


import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;


@Aspect
@Component
public class MaxLimitAspect {
    
    /*
     * @Description: 参数校验，判断分页的size参数，如果size过大，则
     * @Date:   2024/6/29 19:37
     * @Param:  [joinPoint]
     * @Return: java.lang.Object
     */
    @Around("@annotation(com.zzuli.gaokao.annotation.MaxLimit)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] parameterNames = signature.getParameterNames();
        for (int i = 0; i < args.length; i++) {
            if("size".equals(parameterNames[i])){
                if(args[i] instanceof Integer ){
                   Integer size = (Integer) args[i];
                   if(size >= 30){
                       args[i] = 1;
                       break;
                   }
                }
            }
        }
        return joinPoint.proceed(args);
    }


}
