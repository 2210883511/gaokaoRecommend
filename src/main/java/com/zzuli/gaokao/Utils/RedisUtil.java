package com.zzuli.gaokao.Utils;

public class RedisUtil {

    private static final String PREFIX_ADMIN_TOKEN ="token:admin";
    private static final String PREFIX_USER_TOKEN =  "token:user";
    private static final String SPLIT = ":";
    private static final String PREFIX_ADMIN_CAPTCHA = "verify:admin";
    private static final String PREFIX_CF_RECOMMEND = "recommend:cf";
    private static final String PREFIX_USER_CAPTCHA = "verify:user";



    //  token:user:1
    public static String getTokenKey(String username){
        return PREFIX_ADMIN_TOKEN + SPLIT + username;
    }

    public static String getUserTokenKey(String username){
        return PREFIX_USER_TOKEN + SPLIT + username;
    }

    public static String getCaptchaKey(String key){
        return PREFIX_ADMIN_CAPTCHA + SPLIT + key;
    }
    public static String getCfRecommendKey(Integer userId){return PREFIX_CF_RECOMMEND + SPLIT + userId;}

    public static String getUserCaptchaKey(String key){
        return PREFIX_USER_CAPTCHA + SPLIT + key;
    }
}
