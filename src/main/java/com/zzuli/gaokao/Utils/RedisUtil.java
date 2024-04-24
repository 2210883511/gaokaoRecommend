package com.zzuli.gaokao.Utils;

public class RedisUtil {

    private static final String PREFIX_TOKEN ="token:user";
    private static final String SPLIT = ":";
    private static final String PREFIX_CAPTCHA = "verify";
    private static final String PREFIX_CF_RECOMMEND = "recommend:cf";



    //  token:user:1
    public static String getTokenKey(String username){
        return PREFIX_TOKEN + SPLIT + username;
    }
    public static String getCaptchaKey(String key){
        return PREFIX_CAPTCHA + SPLIT + key;
    }
    public static String getCfRecommendKey(Integer userId){return PREFIX_CF_RECOMMEND + SPLIT + userId;}
}
