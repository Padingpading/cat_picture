package com.padingpading.cat_picture.uploader;

import com.padingpading.cat_picture.CatPictureApplication;
import com.padingpading.cat_picture.crawler.upload.entity.User;
import com.padingpading.cat_picture.crawler.upload.uploader.BilibiliUploader;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = CatPictureApplication.class)
public class BilibiliUploaderTest {
    
    private BilibiliUploader bilibiliUploader;
    private User user;

    @Before
    public void setUp() {
        user = new User();
        user.setAccount("13083965390");
        user.setPassword("www.libin.com");
        bilibiliUploader = new BilibiliUploader();
        bilibiliUploader.init(user);
    }
    @Test
    public void contextLoads() {
        bilibiliUploader.login(user);
    }
    
}
