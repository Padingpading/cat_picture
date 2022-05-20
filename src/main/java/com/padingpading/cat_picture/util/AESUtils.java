package com.padingpading.cat_picture.util;

import org.springframework.util.StringUtils;

/**
 * @author: yu_song
 * @update: 2019/4/17 16:31
 */
public class AESUtils {

    /**
     * 解密测试工具
     *
     * @param args
     */
    public static void main(String[] args) throws Exception {
//        String paramStr = "goGQHEPNTtvvbvkJqrR0NiBh3j7rSD7afoTQVv9fVoo=";
//        paramStr = AESUtil.decrypt(paramStr, Constants.CRAWLER_AES_ENCRYPT_KEY);
//        System.out.println(paramStr);
//
//        String user = "O4pdlAFF0AZpTAOn9Tq2AJOvnki52AZgwHcoDP8JrBm2Yl37tB7w/Ww6VLSvKrvkceDxWFDP8mguQgyo0RBW7ofr65ZTOX1gtJVN+tuzE6d8xq3xDZfaF/35DCgSdd6LimmmUwX0Kh/yAP5kZz9n2tVLM1w1wAebd1j7iBptbczVN6x63aCuJdODDWof3hwYgjMv6PS2Gms2SuSNSFQC/GxdS/znuoUvcxV5CunHS/ZhAvH8ANF3H77D0VzzxBlwsDtQiIRGRaIG4nMRdhNSSbwfRzJ6uS8qh+vrllM5fWC+Fh3V6zXPyyUvzGwX2EEENZGsqNrEsitkPAM9Zj1sZRt/izMmOShuo2oxdSWs/BP5Q12RYaMPNfA1E7wuON6IIL/e/zcryYjpZNNUZorvmKePqzBnxamqQafduhNr08TU/CH/ZtmsrbBtER9aBfXH7g2oxDXi378HVbJ8brycTW5D/QdEMcKapUclhVoNpcqX3d5Uw+BwFyvJZ/Htcw+mr/OTXgXreC0Euns34doN1fGspcx/nlGhkpzv0mHCRUFxYV1GQerWACHNiJdfFWqrFYo0XAt365lK2Sip8/u1QYKzx4bcoqItj2YGtS4q321XwVy85VezbLUY8aZJXSqmX3EukWEe04eXvvlYPZ3z9+2N7Ob2BR2a8wIqpMmZvHy3xZ2HZ81oVewG+WCVi6KyJHIop6pzWDV7izl0ptDX/+XkJEmyb1dlDpS+gbmr5lRs2G2X/auBqcYGVLxKWvk1WK3JkmIKcW/DPDucKx/j5DtJcedIq/RSIWsZBmmNspNLxidLudFaDWMpOvIVelxF/Hw372HWpHVIoQUsCc1053KZZhKpscpXTK8kVVnrbjV3jDS3tu9dULBtER9aBfXH7g2oxDXi378HVbJ8brycTW5D/QdEMcKapUclhVoNpcqX3d5Uw+BwF8PG6dGbwm1qED1Q8/vGgdWLvFYOopwqITw+LZzJDyV9Y70rxnMCCTK80JPbO/co+bbMaIjO/qencxIcJtJrfXox1jH3j9xsLE/WFT7kIKHT/3oFiIW20DZGX+qZOuhaoew/DM/c8dYoiVXk7bb4GkMXfh/0QJVPBtG3DxYouBmQ";
//        user = ThreeDESUtil.decryptThreeDESECB(user, ThreeDESUtil.CRAWLER_KEY);
//        System.out.println(user);

   
    }

    public static String paramStrAESEncrypt(String paramStr) {
        return paramStr;
    }

    public static String urlAESEncrypt(String url) {
        if (url.contains("?")) {
            String param = url.substring(url.indexOf("?") + 1, url.length());
            if (!StringUtils.isEmpty(param)) {
                param = paramStrAESEncrypt(param);
                url = url.substring(0, url.indexOf("?") + 1) + param;
            }
        }
        return url;
    }
}
