package com.padingpading.cat_picture.util;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StringUtil {
    public static String getRespCharset(String contentType) {
        try {
            if (contentType == null) return null;
            if (contentType.toLowerCase().contains("charset")) {
                String[] items = contentType.split(";");
                for (String item : items) {
                    if (item != null && item.toLowerCase().contains("charset")) {
                        return item.split("=")[1];
                    }
                }
            }
        } catch (Exception e) {
            log.error("获取请求响应编码异常error:{}", e);
            return null;
        }
        return null;
    }

}
