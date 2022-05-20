package com.padingpading.cat_picture.crawler.upload.uploader;

import com.alibaba.fastjson.JSON;
import com.padingpading.cat_picture.crawler.upload.UploaderCrawler;
import com.padingpading.cat_picture.crawler.upload.entity.User;
import lombok.extern.slf4j.Slf4j;

/**
 * @author libin
 * @description
 * @date 2022-05-19
 */
@Slf4j
public class BilibiliUploader extends UploaderCrawler {
    
    @Override
    public Boolean doLogin(User user) {
        log.info("[[login]] 用戶登錄:{}", JSON.toJSONString(user));
        String  url  = "https://www.bilibili.com/";
        String s = getHttpHelper().sendGet(url);
        //发送短信
        String sendMail = "https://passport.bilibili.com/x/passport-login/web/sms/send";
        getHttpHelper().sepo(sendMail,)
        System.out.println(s);
        return null;
    }
}
