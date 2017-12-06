package com.example.nutzdemo.Util;

import java.util.HashMap;
import java.util.Map;

public class Toolkit {

    //获得请求失败的返回结果
    public static Map<String, Object> getFailResult(int ret, String msg){
        Map<String, Object>result = new HashMap<>();
        result.put("status", ret);
        result.put("msg", msg);
        return result;
    }
    //获得请求成功的返回结果
    public static Map<String, Object> getSuccessResult(Object o, String msg){
        Map<String,Object>result = new HashMap<>();
        result.put("status","1");
        if (msg==null)
            msg = "成功";
        result.put("msg",msg);
        if (o!=null)
            result.put("data",o);
        return result;
    }
}
