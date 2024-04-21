package com.zzuli.gaokao.Utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class CommonUtils {

    public static final Long TOKEN_EXPIRED = 3600 * 24 * 1000L;

    public static final Long CAPTCHA_EXPIRED = 2L;


    /*
     * @Description: 生成随机的uuid
     * @Date:   2024/3/25 16:02
     * @Param:  []
     * @Return: java.lang.String
     */
    public static String generateUUID(){

        return UUID.randomUUID().toString().replaceAll("-", "");

    }



    /*
     * @Description: 对字符串进行MD5加密
     * @Date:   2024/3/25 16:02
     * @Param:  [key]
     * @Return: java.lang.String
     */
    public static String md5(String key){
        if(StringUtils.isBlank(key)){
            return null;
        }

        return DigestUtils.md5DigestAsHex(key.getBytes(StandardCharsets.UTF_8));
    }



}
