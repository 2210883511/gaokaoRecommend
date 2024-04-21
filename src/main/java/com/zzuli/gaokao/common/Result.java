package com.zzuli.gaokao.common;

import java.util.HashMap;
import java.util.Map;

public class Result extends HashMap<String,Object> {

    private Object data;

    public Result(){
        this.put("code",200);
        this.put("msg","success");
        this.put("data",data);
    }

    public static Result success(){
        return new Result();
    }

    public static Result success(String msg){
        return success(msg,null);
    }

    public static Result success(Object data){

        return success("success",data);
    }
    public static Result success(String msg,Object data){
        Result result = new Result();
        result.put("data",data);
        result.put("msg",msg);
        return result;
    }

    public static Result error(Integer code, String msg){
        Result result = new Result();
        result.put("code",code);
        result.put("msg",msg);
        return result;
    }

    public static Result error(String msg){
        return error(500,msg);
    }

    public static Result error(Integer code){
        return error(code,"服务器发生错误");
    }


    @Override
    public Object put(String s, Object o) {
        super.put(s, o);
        return this;
    }
}
