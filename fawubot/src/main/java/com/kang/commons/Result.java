package com.kang.commons;

//import com.github.pagehelper.PageInfo;
import com.kang.commons.exception.MyException;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * @author K.faWu
 * @program fawu-bot
 * @description: 返回结果
 * @create 2022-09-13 11:26
 **/

public class Result {

    /**
     * 成功，但无返回结果
     */
    public static <T> ResponseEntity<T> ok() {
        return ResponseEntity.ok().build();
    }

    /**
     * 成功，返回列表
     */
    public static <T> ResponseEntity<List<T>> ok(List<T> list) {
        return ResponseEntity.ok().body(list);
    }

    /**
     * 成功，返回分页列表
     */
/*    public static <T> ResponseEntity<PageInfo<T>> ok(PageInfo<T> page) {
        return ResponseEntity.ok().body(page);
    }*/

    /**
     * 成功，返回单条数据
     */
    public static <T> ResponseEntity<T> ok(T data) {
        return ResponseEntity.ok().body(data);
    }

    /**
     * 失败。展示失败原因
     */
    public static void error(String msg){
        throw new MyException(500,msg);
    }
}
