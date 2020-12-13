package com.car.util;

import java.util.HashMap;

//工具类方式 Response 对象
public class RStatic {
    private Integer code;
    private String msg;
    private HashMap<Object,Object> data = null;

    public RStatic() {
    }

    private RStatic(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    //处理正常
    public static RStatic ok(String message){
        return new RStatic(ResultCode.SUCCESS,message);
    }
    //处理异常
    public static RStatic error(String message){
        return new RStatic(ResultCode.ERROR,message);
    }


    public RStatic data(Object k, Object v){
        if (null == data){
            data = new HashMap<>();
        }
        data.put(k, v);
        return this;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public HashMap<Object, Object> getData() {
        return data;
    }

    public void setData(HashMap<Object, Object> data) {
        this.data = data;
    }
}
