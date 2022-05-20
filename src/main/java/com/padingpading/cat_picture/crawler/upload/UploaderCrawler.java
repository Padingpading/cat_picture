package com.padingpading.cat_picture.crawler.upload;

import com.alibaba.fastjson.JSON;
import com.padingpading.cat_picture.crawler.upload.entity.User;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public abstract  class UploaderCrawler extends BaseCrawler {

    public Boolean login(User user){
       log.info("[[login]] 用戶登錄:{}", JSON.toJSONString(user));
       return doLogin(user);
    }
    
    public abstract  Boolean doLogin(User user);
 
    

}
