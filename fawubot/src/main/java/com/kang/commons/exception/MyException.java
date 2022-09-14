package com.kang.commons.exception;


import com.alibaba.fastjson.JSONObject;

/**
 * @program: photo_fawu
 * @description: 自定义异常
 * @author: K.faWu
 * @create: 2021-11-05 14:10
 **/

public class MyException extends RuntimeException {


    private String content;


    public MyException() {
        super();
    }

    public MyException(int code, String msg) {
        super(msg);
        JSONObject returnJson = new JSONObject();
        returnJson.put("code", code);
        returnJson.put("msg", msg);
        this.setContent(returnJson.toString());
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}


