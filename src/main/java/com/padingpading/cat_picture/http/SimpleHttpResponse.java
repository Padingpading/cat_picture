package com.padingpading.cat_picture.http;

import lombok.Getter;
import lombok.Setter;
import org.apache.http.Header;

/**
 * @author: yu_song
 * @update: 2019/1/17 10:51
 */
@Setter
@Getter
public class SimpleHttpResponse {

    private Integer statusCode = null;

    private byte[] byteData = null;

    private String stringData = null;

    private Header[] headers = null;

    private String charset = null;
}
