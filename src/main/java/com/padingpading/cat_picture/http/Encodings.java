package com.padingpading.cat_picture.http;

/**
 * 编码方式
 *
 * @author King
 * @createDate 2019/1/8 21:07
 */
public enum Encodings {
    UTF8("UTF-8"), GBK("GBK"), ISO88591("ISO-8859-1"), GB2312("GB2312"),;
    private String value;

    Encodings(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
