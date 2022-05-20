package com.padingpading.cat_picture.http;

/**
 * @author: yu_song
 * @update: 2019/3/20 17:40
 * @Desc: 线程安全
 */
public interface IRetryCheck {

    boolean retryCheck(Integer statusCode);

    boolean retryCheck(String result);

    String resultHandle(String result);
}
