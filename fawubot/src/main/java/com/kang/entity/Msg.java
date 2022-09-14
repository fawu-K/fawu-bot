package com.kang.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @program: service
 * @description: 消息内容类
 * @author: K.faWu
 * @create: 2022-07-13 17:49
 **/
@Data
public class Msg implements Serializable {
    private String type;
    private String id;
    private String url;
    private String text;
    private String at;
    private String width;
    private String height;
    private String imageType;
    private String isEmoji;
}
