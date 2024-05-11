package com.zzuli.gaokao.Utils;

import io.jsonwebtoken.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtUtil {


    public static final String SECRET = "gaokao-success";



    public static String getToken(String username){
        HashMap<String, Object> map = new HashMap<>();
        map.put("username",username);
        return Jwts.builder()
                .setClaims(map)
                // 设置过期时间为 1小时
                .setExpiration(new Date(System.currentTimeMillis() + CommonUtils.TOKEN_EXPIRED))
                .signWith(SignatureAlgorithm.HS256,SECRET)
                .compact();
    }

    public static String getToken(Map<String,Object> user){

        return Jwts.builder()
                .setClaims(user)
                // 设置过期时间为 1小时
                .setExpiration(new Date(System.currentTimeMillis() + CommonUtils.TOKEN_EXPIRED))
                .signWith(SignatureAlgorithm.HS256,SECRET)
                .compact();
    }


    /*
     * @Description: 用于在拦截器中验证jwt是否过期或者被篡改
     * @Date:   2024/3/29 17:09
     * @Param:  [token]
     * @Return: boolean
     */
    public static boolean verifyToken(String token){
        try{
         Jwts.parser()
                    .setSigningKey(SECRET)
                    .parse(token)
                    .getBody();
            return true;
        }catch (ExpiredJwtException e) {
            return false; // 标记为过期的JWT
        } catch (UnsupportedJwtException e) {
            return false; // 不支持的JWT类型
        } catch (MalformedJwtException e) {
            return false; // 格式错误的JWT
        } catch (SignatureException e) {
            return false; // 签名验证失败的JWT
        } catch (IllegalArgumentException e) {
            return false; // 其他非法情况
        }

    }

    /*
     * @Description: 通过token获取载荷中的用户名
     * @Date:   2024/3/30 16:24
     * @Param:  [token]
     * @Return: java.lang.String
     */
    public static String getSubject(String token){
        Object body = Jwts.parser()
                .setSigningKey(SECRET)
                .parse(token)
                .getBody();
        if (body instanceof  Map){
            Object username = ((Map<?, ?>) body).get("username");
            return username.toString();
        }
        return null;
    }
}
