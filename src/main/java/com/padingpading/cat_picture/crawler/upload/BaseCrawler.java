package com.padingpading.cat_picture.crawler.upload;


import com.padingpading.cat_picture.crawler.upload.entity.User;
import com.padingpading.cat_picture.http.DefaultHttpHelper;
import com.padingpading.cat_picture.http.HttpHelper;

/**
 */
public abstract  class BaseCrawler {
    
    private HttpHelper httpHelper;
    
    
    public void  init(User user){
        httpHelper = new DefaultHttpHelper();
    }
    
    public HttpHelper getHttpHelper() {
        return httpHelper;
    }
}
