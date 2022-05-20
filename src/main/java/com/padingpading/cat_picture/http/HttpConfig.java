package com.padingpading.cat_picture.http;

import lombok.Getter;
import lombok.Setter;
import org.apache.http.Header;
import org.apache.http.HttpVersion;
import org.apache.http.message.BasicHeader;

import java.util.LinkedList;
import java.util.List;

/**
 * http 配置类，线程安全
 *
 * @author King
 * @update 2019/1/8 15:39
 */
public final class HttpConfig implements Cloneable {
    private static final String ACCEPT = "Accept";
    private static final String DEFAULT_ACCEPT_VALUE = "*/*";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CACHE_CONTROL = "Cache-Control";
    private static final String DEFAULT_CACHE_CONTROL_VALUE = "no-cache";
    private static final String PRAGMA = "Pragma";
    private static final String USERAGENT = "User-Agent";
    private static final String DEFAULT_USERAGENT_VALUE = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.101 Safari/537.36";
    private final List<Header> headers;
    /**
     * 开启请求记录缓存保存
     */
    public Boolean isSaveHttpRecords = true;

    @Setter
    @Getter
    private HttpVersion httpVersion;
    /**
     * 循环重定向，默认false
     */
    @Getter
    @Setter
    private Boolean circularRedirectsAllowed = false;
    /**
     * 自动重定向，默认true
     */
    @Getter
    @Setter
    private Boolean autoRedirect = true;
    /**
     * 最大重试次数，默认2
     */
    @Getter
    private int maxRetryTimes = 2;
    /**
     * 连接池获取连接的超时时间，单位：毫秒，默认20000
     */
    @Getter
    private int connectionRequestTimeout = 20000;
    /**
     * 客户端和服务器建立连接的超时时间，单位：毫秒，默认20000
     */
    @Getter
    private int connectTimeout = 20000;
    /**
     * 客户端从服务器读取数据的超时时间，单位：毫秒，默认20000
     */
    @Getter
    private int responseTimeout = 20000;
    /**
     * 请求前休眠时间(ms)
     */
    @Getter
    @Setter
    private Integer sleepMs = 0;

    /**
     * 构造函数
     * <li>默认使用http 1.1版本</li>
     * <li>默认头如下：</li>
     * <li>Accept：*\/*</li>
     * <li>Content-Type：{@link ContentTypes#FORM}</li>
     * <li>Cache-Control：no-cache</li>
     * <li>Pragma：no-cache</li>
     *
     * @author King
     * @update 20190108
     */
    public HttpConfig() {
        httpVersion = HttpVersion.HTTP_1_1;//默认使用1.1版本
        headers = new LinkedList<>();
        headers.add(new BasicHeader(ACCEPT, DEFAULT_ACCEPT_VALUE));
        //headers.add(new BasicHeader(CONTENT_TYPE, ContentTypes.FORM.getValue()));//form表单
        headers.add(new BasicHeader(USERAGENT, DEFAULT_USERAGENT_VALUE));//默认代理客户端头
        //禁止服务器使用缓存
        headers.add(new BasicHeader(CACHE_CONTROL, DEFAULT_CACHE_CONTROL_VALUE));
        headers.add(new BasicHeader(PRAGMA, DEFAULT_CACHE_CONTROL_VALUE));
    }

    /**
     * 设置最大重试次数
     *
     * @param maxRetryTimes 最大重试次数，在0和50之间
     * @return void
     * @author King
     * @update 2019/1/8 17:46
     */
    public void setMaxRetryTimes(int maxRetryTimes) throws IllegalArgumentException {
        if (maxRetryTimes < 1 || maxRetryTimes > 50) {
            throw new IllegalArgumentException("http最大重试次数必须在0和50之间！");
        }
        this.maxRetryTimes = maxRetryTimes;
    }

    /**
     * 设置连接池获取连接的超时时间，单位：毫秒
     *
     * @param ms 连接池获取连接的超时时间
     * @return void
     * @author King
     * @update 2019/1/8 17:46
     */
    public void setConnectionRequestTimeout(int ms) throws IllegalArgumentException {
        if (ms < 1) {
            throw new IllegalArgumentException("连接池获取连接的超时时间必须大于0！");
        }
        connectionRequestTimeout = ms;
    }

    /**
     * 设置客户端和服务器建立连接的超时时间，单位：毫秒
     *
     * @param ms 客户端和服务器建立连接的超时时间
     * @return void
     * @author King
     * @update 2019/1/8 17:46
     */
    public void setConnectTimeout(int ms) throws IllegalArgumentException {
        if (ms < 1) {
            throw new IllegalArgumentException("客户端和服务器建立连接的超时时间必须大于0！");
        }
        connectTimeout = ms;
    }

    /**
     * 设置客户端从服务器读取数据的超时时间，单位：毫秒
     *
     * @param ms 客户端从服务器读取数据的超时时间
     * @return void
     * @author King
     * @update 2019/1/8 17:46
     */
    public void setResponseTimeout(int ms) throws IllegalArgumentException {
        if (ms < 1) {
            throw new IllegalArgumentException("客户端从服务器读取数据的超时时间必须大于0！");
        }
        responseTimeout = ms;
    }

    /**
     * 更新请求头信息，无则添加，有则更新。
     * 头信息格式参考
     *
     * @param name  头信息名称
     * @param value 头信息值
     * @return HttpConfig
     * @author King
     * @update 2019/1/8 15:12
     * @see <a href="http://tools.jb51.net/table/http_header">http://tools.jb51.net/table/http_header</a>
     */
    public synchronized HttpConfig updateHeader(String name, String value) {
        Header desHeader = findHeader(name);
        if (null == desHeader) {
            headers.add(new BasicHeader(name, value));
        } else {
            headers.remove(desHeader);
            headers.add(new BasicHeader(name, value));
        }
        return this;
    }

    /**
     * 根据name删除头信息
     *
     * @param name 头信息name
     * @return HttpConfig
     * @author King
     * @update 2019/1/8 16:02
     */
    public synchronized HttpConfig removeHeader(String name) {
        Header desHeader = findHeader(name);
        if (null != desHeader) {
            headers.remove(desHeader);
        }
        return this;
    }

    /**
     * 删除所有头信息
     *
     * @return HttpConfig
     * @author King
     * @update 2019/1/8 16:02
     */
    public synchronized HttpConfig removeAllHeader() {
        headers.clear();
        return this;
    }

    //查找headers里指定name的Header对象，忽略name大小写
    private Header findHeader(String name) {
        for (Header header : headers) {
            if (header.getName().equalsIgnoreCase(name)) {
                return header;
            }
        }
        return null;
    }

    /**
     * 获取头信息集合副本
     *
     * @return java.util.Collection<org.apache.http.Header>
     * @author King
     * @update 2019/1/8 16:19
     */
    public synchronized Header[] getHeadersCopy() {
        Header[] desHeaders = new Header[headers.size()];
        return headers.toArray(desHeaders);
    }

    /**
     * 获取当前对象副本
     *
     * @return java.util.Collection<org.apache.http.Header>
     * @author King
     * @update 2019/1/8 16:19
     */
    @Override
    public synchronized HttpConfig clone() {
        HttpConfig config = new HttpConfig();
        config.removeAllHeader();
        config.httpVersion = this.httpVersion;
        config.isSaveHttpRecords = isSaveHttpRecords;
        config.circularRedirectsAllowed = circularRedirectsAllowed;
        config.autoRedirect = autoRedirect;
        config.maxRetryTimes = maxRetryTimes;
        config.connectionRequestTimeout = connectionRequestTimeout;
        config.connectTimeout = connectTimeout;
        config.responseTimeout = responseTimeout;
        config.sleepMs = sleepMs;
        for (Header header : headers) {
            config.updateHeader(header.getName(), header.getValue());
        }
        return config;
    }
}
