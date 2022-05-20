package com.padingpading.cat_picture.http;

import com.padingpading.cat_picture.util.DefaultConnectionPool;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.SocketConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;

import java.io.IOException;
import java.util.Arrays;

/**
 * httphelper默认实现
 */
@Slf4j
public class DefaultHttpHelper extends HttpHelper {
    public DefaultHttpHelper() {
        super();
        httpClient = getClient();
    }

    public DefaultHttpHelper(String token) {
        super(token);
        httpClient = getClient();
    }

    public DefaultHttpHelper(HttpConfig httpConfig, String token) {
        super(httpConfig, token);
        httpClient = getClient();
    }

    public DefaultHttpHelper(HttpConfig httpConfig, int resultHandleCode, String token) {
        super(httpConfig, resultHandleCode, token);
        httpClient = getClient();
    }

    @Override
    protected final CloseableHttpClient getClient() {
        RequestConfig defaultConfig = RequestConfig.custom()
                .setCookieSpec(CookieSpecs.NETSCAPE)
                .setExpectContinueEnabled(true)
                .setTargetPreferredAuthSchemes(Arrays.asList(AuthSchemes.NTLM, AuthSchemes.DIGEST))
                .setProxyPreferredAuthSchemes(Arrays.asList(AuthSchemes.BASIC))
                .setConnectionRequestTimeout(this.getConfig().getConnectionRequestTimeout())
                .setConnectTimeout(this.getConfig().getConnectTimeout())
                .setSocketTimeout(this.getConfig().getResponseTimeout())
                .setCircularRedirectsAllowed(this.getConfig().getCircularRedirectsAllowed())
                .setRedirectsEnabled(this.getConfig().getAutoRedirect())
                .setStaleConnectionCheckEnabled(true)
                .build();

        //TODO 强制通信协议
        System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2,SSLv3");


        //TODO test(socket异常挂起)
        SocketConfig socketConfig = SocketConfig.custom().setSoTimeout(this.getConfig().getResponseTimeout()).build();

        return HttpClients.custom().setDefaultSocketConfig(socketConfig).setConnectionManager(DefaultConnectionPool.INSTANCE.getInstance())
                .setDefaultRequestConfig(defaultConfig)
                .setDefaultCookieStore(this.cookieStore)
                .setRedirectStrategy(new LaxRedirectStrategy())
                .setProxy(this.getHttpHost())
                .setConnectionManagerShared(true)
                .build();
//        return HttpClients.custom().build();
    }

    /**
     * 创建当前对象副本：配置{@link HttpConfig}、{@link org.apache.http.client.CookieStore}以及请求缓存记录
     *
     * @param useSameHttpRecords 是否共用缓存记录
     * @return HttpHelper
     * @author King
     * @update 2019/1/9 16:42
     */
    @Override
    public final HttpHelper clone(boolean useSameHttpRecords) {
        HttpHelper helper;
//        if(this instanceof ProxyHttpHelper){
//            helper = new ProxyHttpHelper(this.getConfig().clone(), useSameHttpRecords ? this.getHttpRecordsPages() : null, this.token);
//            //把proxyObj传入
//            ((ProxyHttpHelper)helper).setProxyObj(((ProxyHttpHelper)this).proxyObj);
//        }else{
//            helper = new DefaultHttpHelper(this.getConfig().clone(), useSameHttpRecords ? this.getHttpRecordsPages() : null, this.token);
//        }
//        helper.setRetryCheck(this.getRetryCheck());
//
//        for (Cookie cookie : this.cookieStore.getCookies()) {
//            helper.cookieStore.addCookie(cookie);
//        }
        return null;
    }

    @Override
    public final HttpHelper clone(boolean useSameHttpRecords, int resultHandleCode) {
//        HttpHelper helper;
//        if(this instanceof ProxyHttpHelper){
//            helper = new ProxyHttpHelper(this.getConfig().clone(), useSameHttpRecords ? this.getHttpRecordsPages() : null, resultHandleCode, this.token);
//            //把proxyObj传入
//            ((ProxyHttpHelper)helper).setProxyObj(((ProxyHttpHelper)this).proxyObj);
//        }else{
//            helper = new DefaultHttpHelper(this.getConfig().clone(), useSameHttpRecords ? this.getHttpRecordsPages() : null, resultHandleCode, this.token);
//        }
//        helper.setRetryCheck(this.getRetryCheck());
//
//        for (Cookie cookie : this.cookieStore.getCookies()) {
//            helper.cookieStore.addCookie(cookie);
//        }
        return null;
    }

    @Override
    public void close() throws IOException {

    }
}
