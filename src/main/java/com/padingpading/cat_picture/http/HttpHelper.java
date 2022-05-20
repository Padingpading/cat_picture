package com.padingpading.cat_picture.http;

import com.padingpading.cat_picture.util.AESUtils;
import com.padingpading.cat_picture.util.StringUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.*;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.cookie.ClientCookie;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.util.EntityUtils;
import org.springframework.util.StringUtils;

import java.io.Closeable;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * http请求帮助类，非线程安全
 *
 * @author King
 * @update 2019/1/8 17:37
 */
@Slf4j
public abstract class HttpHelper implements Closeable {
    /**
     * http headers设置类
     */
    @Getter
    private final HttpConfig config;

    protected volatile String token;
    /**
     * cookie管理对象
     */
    protected CookieStore cookieStore = new BasicCookieStore();
    /**
     * 当前http请求客户端实例
     */
    protected HttpClient httpClient;

    @Setter
    @Getter
    private IRetryCheck retryCheck;

    @Getter
    @Setter
    private volatile int resultHandleCode;

    private static final String CONNECTION_POOL_SHUT_DOWN = "Connection pool shut down";

    public HttpHelper() {
        config = new HttpConfig();
    }

    public HttpHelper(String token) {
        config = new HttpConfig();
        this.token = token;
    }

    public HttpHelper(HttpConfig httpConfig, String token) {
        this.config = httpConfig;
        this.token = token;
    }

    public HttpHelper(HttpConfig httpConfig, int resultHandleCode, String token) {
        this.config = httpConfig;
        this.resultHandleCode = resultHandleCode;
        this.token = token;
    }

    //获取编码方式
    private static Charset getCharset(String name) {
        if (StringUtils.isEmpty(name)) {
            return Charset.forName(Encodings.UTF8.getValue());
        } else {
            name = name.trim();
            return Charset.forName(Charset.isSupported(name) ? name : Encodings.UTF8.getValue());
        }
    }

    //获取请求对象
    private static HttpRequestBase getRequestBase(String url, HttpMethods method) {
        HttpEntityEnclosingRequestBase enclosingRequestBase = null;
        HttpRequestBase requestBase = null;
        switch (method) {
            case GET:
                requestBase = new HttpGet(url);
                break;
            case DELETE:
                requestBase = new HttpDelete(url);
                break;
            case HEAD:
                requestBase = new HttpHead(url);
                break;
            case OPTIONS:
                requestBase = new HttpOptions(url);
                break;
            case TRACE:
                requestBase = new HttpTrace(url);
                break;
            case POST:
                enclosingRequestBase = new HttpPost(url);
                break;
            case PUT:
                enclosingRequestBase = new HttpPut(url);
                break;
            case PATCH:
                enclosingRequestBase = new HttpPatch(url);
                break;
            default:
                throw new IllegalArgumentException("unknown http request method:" + method);
        }
        return StringUtils.isEmpty(requestBase) ? enclosingRequestBase : requestBase;
    }

    /**
     * 依据{@link Hashtable}参数获取表单形式的请求数据
     *
     * @param params {@link Hashtable}参数
     * @return 表单形式的请求数据字符串
     * @author King
     * @update 2019/1/9 9:25
     */
    public static String getFormParams(Hashtable<String, String> params) {
        if (StringUtils.isEmpty(params)) {
            return "";
        } else {
            StringBuilder formParams = new StringBuilder();
            for (Map.Entry<String, String> item : params.entrySet()) {
                formParams.append(item.getKey())
                        .append("=")
                        .append(StringUtils.isEmpty(item.getValue()) ? "" : item.getValue())
                        .append("&");
            }
            return formParams.length() < 1 ? "" : formParams.substring(0, formParams.length() - 1);
        }
    }

    /**
     * 获取http请求客户端对象，需要子类复写
     *
     * @author King
     * @update 2019/1/8 17:57
     */
    protected abstract HttpClient getClient();

    /**
     * 创建当前对象的副本
     *
     * @param useSameHttpRecords 是否使用相同的http记录缓存对象
     * @return HttpHelper
     * @author King
     * @update 2019/1/9 16:28
     */
    public abstract HttpHelper clone(boolean useSameHttpRecords);

    public abstract HttpHelper clone(boolean useSameHttpRecords, int resultHandleCode);

    /**
     * 获取http host
     */
    protected HttpHost getHttpHost() {
        return null;
    }

    /**
     * 添加cookie
     *
     * @param name   cookie名称
     * @param value  cookie值
     * @param path   cookie访问权限路径
     * @param domain cookie访问权限域名
     * @return void
     * @author King
     * @update 2019/1/8 18:07
     */
    public final void addCookie(String name, String value, String path, String domain) {
        BasicClientCookie basicClientCookie = new BasicClientCookie(name, value);
        basicClientCookie.setDomain(domain);
        basicClientCookie.setPath(path);
        basicClientCookie.setAttribute(ClientCookie.PATH_ATTR, path);
        basicClientCookie.setAttribute(ClientCookie.DOMAIN_ATTR, domain);
        this.cookieStore.addCookie(basicClientCookie);
    }

    /**
     * 添加cookie对象
     *
     * @param cookie 过期将添加失败
     * @return void
     * @author King
     * @update 2019/1/8 18:07
     */
    public final void addCookie(Cookie cookie) {
        this.cookieStore.addCookie(cookie);
    }

    /**
     * 清空当前CookieStore
     *
     * @author King
     * @update 2019/1/8 19:22
     */
    public final void clearCookieStore() {
        this.cookieStore.clear();
    }

    /**
     * 获取当前CookieStore中的Cookie集合
     *
     * @return java.util.List<org.apache.http.cookie.Cookie>
     * @author King
     * @update 2019/1/8 19:23
     */
    public final List<Cookie> getCookies() {
        return this.cookieStore.getCookies();
    }

    /**
     * 获取CookieStore的字符串形式（key1=value1,key2=value2...）
     *
     * @return java.lang.String
     * @author King
     * @update 2019/1/8 19:33
     */
    public final String getCookieStoreString() {
        StringBuilder cookieStr = new StringBuilder();
        for (Cookie cookie : getCookies()) {
            cookieStr.append(cookie.getName())
                    .append("=")
                    .append(cookie.getValue())
                    .append(";");
        }
        return cookieStr.length() < 1 ? "" : cookieStr.substring(0, cookieStr.length() - 1);
    }

    //#region sendPost

    /**
     * post请求
     *
     * @param url
     * @param paramStr
     * @return
     */
    public final String sendPost(String url, String paramStr) {
        return sendPost(url, paramStr, null);
    }

    /**
     * 发起post请求，请求和响应数据默认编码为{@link Encodings#UTF8}
     *
     * @param url      请求地址
     * @param paramStr 请求数据
     * @return String 返回请求响应数据，异常响应则为null
     * @author King
     * @update 2019/1/8 22:56
     */
    public final String sendPost(HttpRecordType httpRecordType, String url, String paramStr) {
        return sendPost(httpRecordType, url, paramStr, Encodings.UTF8.getValue(), Encodings.UTF8.getValue());
    }

    /**
     * post请求
     *
     * @param url
     * @param paramStr
     * @param reqEncoding
     * @return
     */
    public final String sendPost(String url, String paramStr, String reqEncoding) {
        return sendPost(url, paramStr, reqEncoding, null);
    }

    /**
     * 发起post请求，响应数据默认编码为{@link Encodings#UTF8}
     *
     * @param url         请求地址
     * @param paramStr    请求数据
     * @param reqEncoding 请求数据编码，设置无效则为{@link Encodings#UTF8}
     * @return String 返回请求响应数据，异常响应则为null
     * @author King
     * @update 2019/1/8 22:56
     */
    public final String sendPost(HttpRecordType httpRecordType, String url, String paramStr, String reqEncoding) {
        return sendPost(httpRecordType, url, paramStr, reqEncoding, Encodings.UTF8.getValue());
    }

    /**
     * post请求
     *
     * @param url
     * @param paramStr
     * @param reqEncoding
     * @param rspEncoding
     * @return
     */
    public final String sendPost(String url, String paramStr, String reqEncoding, String rspEncoding) {
        return sendPost(null, url, paramStr, reqEncoding, rspEncoding);
    }

    /**
     * 发起post请求
     *
     * @param url         请求地址
     * @param paramStr    请求数据
     * @param reqEncoding 请求数据编码，设置无效则为{@link Encodings#UTF8}
     * @param rspEncoding 响应数据编码，设置无效则为{@link Encodings#UTF8}
     * @return String 返回请求响应数据，异常响应则为null
     * @author King
     * @update 2019/1/8 22:56
     */
    public final String sendPost(HttpRecordType httpRecordType, String url, String paramStr, String reqEncoding, String rspEncoding) {
        return sendRequest(httpRecordType, url, paramStr, HttpMethods.POST, reqEncoding, rspEncoding);
    }
    //#endregion

    //#region sendGet

    /**
     * get请求
     *
     * @param url
     * @return
     */
    public final String sendGet(String url) {
        return sendGet(url, null);
    }

    /**
     * 发起get请求，响应内容默认编码{@link Encodings#UTF8}
     *
     * @param url 请求地址
     * @return String 返回请求响应数据，异常响应则为null
     * @author King
     * @update 2019/1/8 22:56
     */
    public final String sendGet(HttpRecordType httpRecordType, String url) {
        return sendGet(httpRecordType, url, Encodings.UTF8.getValue());
    }

    /**
     * get请求
     *
     * @param url
     * @param rspEncoding
     * @return
     */
    public final String sendGet(String url, String rspEncoding) {
        return sendGet(null, url, rspEncoding);
    }

    /**
     * 发起get请求
     *
     * @param url         请求地址
     * @param rspEncoding 响应数据编码，设置无效则为{@link Encodings#UTF8}
     * @return String 返回请求响应数据，异常响应则为null
     * @author King
     * @update 2019/1/8 22:56
     */
    public final String sendGet(HttpRecordType httpRecordType, String url, String rspEncoding) {
        return sendRequest(httpRecordType, url, null, HttpMethods.GET, null, rspEncoding);
    }
    //#endregion

    //#region post and get by byte

    /**
     * 发起get请求
     *
     * @param url 请求地址
     * @return byte[] 返回请求响应数据，异常响应则为null
     * @author King
     * @update 2019/1/8 22:56
     */
    public final byte[] sendGetByte(String url) {
        return sendRequest(url, null, HttpMethods.GET, null);
    }

    /**
     * 发起get请求
     *
     * @param url
     * @return
     */
    public final byte[] sendGetByteResponse(String url) {
        return sendRequestResponse(null, url, null, HttpMethods.GET, null, null).getByteData();
    }

    /**
     * 发起get请求
     *
     * @param url 请求地址
     * @return byte[] 返回请求响应数据，异常响应则为null
     * @author yu_song
     * @update 2019/1/17 11:22
     */
    public final byte[] sendGetByteResponse(HttpRecordType httpRecordType, String url) {
        return sendRequestResponse(httpRecordType, url, null, HttpMethods.GET, null, null).getByteData();
    }

    /**
     * 发起post请求
     *
     * @param url         请求地址
     * @param paramStr    请求数据
     * @param reqEncoding 请求数据的编码方式
     * @return byte[] 返回请求响应数据，异常响应则为null
     * @author King
     * @update 2019/1/8 22:56
     */
    public final byte[] sendPostByte(String url, String paramStr, String reqEncoding) {
        return sendRequest(url, paramStr, HttpMethods.POST, reqEncoding);
    }

    /**
     * 发起post请求
     *
     * @param url         请求地址
     * @param paramStr    请求数据
     * @param reqEncoding 请求数据的编码方式
     * @return byte[] 返回请求响应数据，异常响应则为null
     * @author yu_song
     * @update 2019/1/17 11:22
     */
    public final byte[] sendPostByteResponse(String url, String paramStr, String reqEncoding) {
        return sendRequestResponse(null, url, paramStr, HttpMethods.POST, reqEncoding, null).getByteData();
    }

    /**
     * 发起post请求
     *
     * @param url         请求地址
     * @param paramStr    请求数据
     * @param reqEncoding 请求数据的编码方式
     * @return byte[] 返回请求响应数据，异常响应则为null
     * @author yu_song
     * @update 2019/1/17 11:22
     */
    public final byte[] sendPostByteResponse(HttpRecordType httpRecordType, String url, String paramStr, String reqEncoding) {
        return sendRequestResponse(httpRecordType, url, paramStr, HttpMethods.POST, reqEncoding, null).getByteData();
    }
    //#endregion

    //#region post and get basic request

    /**
     * 发起请求
     *
     * @param url         请求地址
     * @param paramStr    请求数据
     * @param method      请求类型
     * @param reqEncoding 请求数据的编码方式
     * @return byte[] 返回请求响应数据，异常响应则为null
     * @author King
     * @update 2019/1/8 22:46
     */
    private byte[] sendRequest(String url, String paramStr, final HttpMethods method, String reqEncoding) {
        SimpleHttpResponse response = null;
        for (int i = 0; i < config.getMaxRetryTimes(); i++) {
            response = sendRequestBaseResponse(url, paramStr, method, reqEncoding, null);
            byte[] result = response.getByteData();
            if (result == null) {
                result = new byte[0];
            }
            if (retryCheck != null) {
                if (!(retryCheck.retryCheck(response.getStatusCode()) || retryCheck.retryCheck(new String(result, getCharset(Encodings.UTF8.getValue()))))) {
                    break;
                }
            } else {
                if (!retryCheck(response.getStatusCode())) {
                    break;
                }
            }
        }
        return response.getByteData();
    }

    /**
     * 发起请求
     *
     * @param url         请求地址
     * @param paramStr    请求数据
     * @param method      请求类型
     * @param reqEncoding 请求数据的编码方式
     * @param rspEncoding 响应数据编码，设置无效则为{@link Encodings#UTF8}
     * @return String 返回请求响应数据，异常响应则为null
     * @author King
     * @update 2019/1/8 22:46
     */
    private String sendRequest(HttpRecordType httpRecordType, String url, String paramStr, final HttpMethods method, String reqEncoding, String rspEncoding) {
        String rspString = null;
        SimpleHttpResponse response;
        int retryTimes = 0;
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < config.getMaxRetryTimes(); i++) {
            rspString = null;//重置
            retryTimes++;
            response = sendRequestBaseResponse(url, paramStr, method, reqEncoding, null);
            if (response.getByteData() != null) {
                rspString = new String(response.getByteData(), response.getCharset() == null ? getCharset(rspEncoding) : getCharset(response.getCharset()));
                //结果处理
                if (retryCheck != null) {
                    rspString = retryCheck.resultHandle(rspString);
                }
            }

            if (retryCheck != null) {
                if (!(retryCheck.retryCheck(response.getStatusCode()) || retryCheck.retryCheck(rspString))) {
                    break;
                }
            } else {
                if (!retryCheck(response.getStatusCode())) {
                    break;
                }
            }
        }

        //添加到记录缓存
        String recordCache = rspString;
        paramStr = AESUtils.paramStrAESEncrypt(paramStr);
        url = AESUtils.urlAESEncrypt(url);
        if (StringUtils.isEmpty(httpRecordType)) {
            recordCache = "<!--" + System.lineSeparator() + "type:" + method.name() + System.lineSeparator() + "retry_times:" + retryTimes + System.lineSeparator() +
                    "take_time:" + (System.currentTimeMillis() - startTime) + "ms" + System.lineSeparator() + "url:" + url + System.lineSeparator() +
                    "param:" + paramStr + System.lineSeparator() + "-->" + System.lineSeparator() + rspString;
        }
        addHttpRecord(httpRecordType, recordCache);
        return rspString;
    }
    //#endregion

    //#region post and get by SimpleHttpResponse

    /**
     * get请求
     *
     * @param url
     * @param rspEcoding
     * @return
     */
    public final SimpleHttpResponse sendGetResponse(String url, String rspEcoding) {
        return sendGetResponse(null, url, rspEcoding);
    }

    /**
     * 发起get请求
     *
     * @param url         请求地址
     * @param rspEncoding 响应数据编码，设置无效则为{@link Encodings#UTF8}
     * @return String 返回请求响应数据，异常响应则为null
     * @author yu_song
     * @update 2019/1/17 11:33
     */
    public final SimpleHttpResponse sendGetResponse(HttpRecordType httpRecordType, String url, String rspEncoding) {
        return sendRequestResponse(httpRecordType, url, null, HttpMethods.GET, null, rspEncoding);
    }

    /**
     * post请求
     *
     * @param url
     * @param paramStr
     * @param reqEncoding
     * @param rspEncoding
     * @return
     */
    public final SimpleHttpResponse sendPostResponse(String url, String paramStr, String reqEncoding, String rspEncoding) {
        return sendPostResponse(null, url, paramStr, reqEncoding, rspEncoding);
    }

    /**
     * 发起post请求
     *
     * @param url         请求地址
     * @param paramStr    请求数据
     * @param reqEncoding 请求数据编码，设置无效则为{@link Encodings#UTF8}
     * @param rspEncoding 响应数据编码，设置无效则为{@link Encodings#UTF8}
     * @return String 返回请求响应数据，异常响应则为null
     * @author yu_song
     * @update 2019/1/17 11:22
     */
    public final SimpleHttpResponse sendPostResponse(HttpRecordType httpRecordType, String url, String paramStr, String reqEncoding, String rspEncoding) {
        return sendRequestResponse(httpRecordType, url, paramStr, HttpMethods.POST, reqEncoding, rspEncoding);
    }
    //#endregion

    /**
     * 请求基方法
     *
     * @param httpRecordType 抓取记录枚举
     * @param url            请求地址
     * @param paramStr       请求数据
     * @param method         请求类型
     * @param reqEncoding    请求数据的编码方式
     * @param rspEncoding    响应数据编码，设置无效则为{@link Encodings#UTF8}
     * @return SimpleHttpResponse 返回请求响应数据，异常响应则为null
     * @author yu_song
     * @update 2019/1/17 11:22
     */
    private SimpleHttpResponse sendRequestResponse(HttpRecordType httpRecordType, String url, String paramStr, final HttpMethods method, String reqEncoding, String rspEncoding) {
        SimpleHttpResponse response = null;
        int retryTimes = 0;
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < config.getMaxRetryTimes(); i++) {
            retryTimes++;
            response = sendRequestBaseResponse(url, paramStr, method, reqEncoding, rspEncoding);
            if (retryCheck != null) {
                if (!(retryCheck.retryCheck(response.getStatusCode()) || retryCheck.retryCheck(response.getStringData()))) {
                    break;
                }
            } else {
                if (!retryCheck(response.getStatusCode())) {
                    break;
                }
            }
        }

        //添加到记录缓存
        String recordCache = response.getStringData();
        paramStr = AESUtils.paramStrAESEncrypt(paramStr);
        url = AESUtils.urlAESEncrypt(url);
        if (StringUtils.isEmpty(httpRecordType)) {
            recordCache = "<!--" + System.lineSeparator() + "type:" + method.name() + System.lineSeparator() + "retry_times:" + retryTimes + System.lineSeparator() +
                    "take_time:" + (System.currentTimeMillis() - startTime) + "ms" + System.lineSeparator() + "url:" + url + System.lineSeparator() +
                    "param:" + paramStr + System.lineSeparator() + "-->" + System.lineSeparator() + response.getStringData();
        }
        addHttpRecord(httpRecordType, recordCache);
        return response;
    }

    /**
     * 请求基方法，返回SimpleHttpResponse
     *
     * @param url
     * @param paramStr
     * @param method
     * @param reqEncoding
     * @param rspEncoding
     * @return
     */
    private SimpleHttpResponse sendRequestBaseResponse(String url, String paramStr, final HttpMethods method, String reqEncoding, String rspEncoding) {
        //参数简单预处理
        if (null != url) {
            url = url.trim();
        }
        if (null != paramStr) {
            paramStr = paramStr.trim();
        }
        if (null != reqEncoding) {
            reqEncoding = reqEncoding.trim();
        }
        if (null != rspEncoding) {
            rspEncoding = rspEncoding.trim();
        }
        HttpResponse httpResponse = null;
        SimpleHttpResponse simpleHttpResponse = new SimpleHttpResponse();
        try {
            sleepBeforeRequest();//请求前休眠
            StringEntity stringEntity = new StringEntity(paramStr == null ? "" : paramStr, getCharset(reqEncoding));
            httpResponse = sendRequestBase(url, stringEntity, method);
            if (httpResponse != null) {
                StatusLine statusLine = httpResponse.getStatusLine();
                simpleHttpResponse.setStatusCode(statusLine == null ? null : statusLine.getStatusCode());
                HttpEntity httpEntity = httpResponse.getEntity();
                if (!StringUtils.isEmpty(httpEntity)) {
                    byte[] data = EntityUtils.toByteArray(httpEntity);
                    simpleHttpResponse.setByteData(data);
                    simpleHttpResponse.setStringData(new String(data, getCharset(rspEncoding)));
                }
                simpleHttpResponse.setHeaders(httpResponse.getAllHeaders());
                Header contentType = httpResponse.getFirstHeader("Content-Type");
                if (contentType != null) {
                    String respCharset = StringUtil.getRespCharset(contentType.toString());
                    simpleHttpResponse.setCharset(respCharset);
                }

            }
        } catch (Exception e) {
            log.error("{} 发起http请求异常：{}|url:{}", token, e, AESUtils.paramStrAESEncrypt(url));
        } finally {
            if (httpResponse instanceof Closeable) {
                try {
                    ((CloseableHttpResponse) httpResponse).close();
                } catch (IOException e) {
                    log.error("{} HttpResponse释放异常：{}|url:{}", token, e, AESUtils.paramStrAESEncrypt(url));
                }
            }
        }
        return simpleHttpResponse;
    }

    //请求基方法
    private HttpResponse sendRequestBase(String url, final AbstractHttpEntity entity, final HttpMethods method) {
        HttpResponse httpResponse = null;
        try {
            Object requestBase = getRequestBase(url, method);

            RequestConfig requestConfig = RequestConfig.custom()
                    .setProxy(this.getHttpHost())
                    .setConnectTimeout(config.getConnectTimeout())
                    .setSocketTimeout(config.getResponseTimeout())
                    .setConnectionRequestTimeout(config.getConnectionRequestTimeout())
                    .setRedirectsEnabled(config.getAutoRedirect())
                    .setCircularRedirectsAllowed(config.getCircularRedirectsAllowed())
                    .build();

            if (requestBase instanceof HttpEntityEnclosingRequestBase) {
                HttpEntityEnclosingRequestBase httpRequestBase = (HttpEntityEnclosingRequestBase) requestBase;
                httpRequestBase.setProtocolVersion(this.config.getHttpVersion());
                httpRequestBase.setHeaders(this.config.getHeadersCopy());
                httpRequestBase.setConfig(requestConfig);
                httpRequestBase.getParams().setParameter("http.connection-manager.timeout", 30 * 1000);//TODO test(socket异常挂起)
                Header contentTypeHeader = httpRequestBase.getFirstHeader("Content-Type");
                if (contentTypeHeader != null) {
                    entity.setContentType(contentTypeHeader);
                }
                httpRequestBase.setEntity(entity);
                httpResponse = this.httpClient.execute(httpRequestBase);
            } else {
                HttpRequestBase httpRequestBase = (HttpRequestBase) requestBase;
                httpRequestBase.setProtocolVersion(this.config.getHttpVersion());
                httpRequestBase.setHeaders(this.config.getHeadersCopy());
                httpRequestBase.setConfig(requestConfig);
                httpRequestBase.getParams().setParameter("http.connection-manager.timeout", 30 * 1000);//TODO test(socket异常挂起)
                httpResponse = this.httpClient.execute(httpRequestBase);
            }
        } catch (Exception e) {
            if (e.toString().contains(CONNECTION_POOL_SHUT_DOWN)) {
                //String content = "如题 应用:" + IPUtil.getLocalIP() + " 结果:" + "Connection pool shut down :" + e + " | url: " + AESUtils.paramStrAESEncrypt(url);
                //MailUtil.sendQuantityControlledMail("告警：qz-crawler Connection pool shut down", content, MailMessageType.EXCEPTION);
            }
            log.error("{} 请求发送异常：{}|url:{}", token, e, AESUtils.paramStrAESEncrypt(url));
        }
        return httpResponse;
    }

    /**
     * 每次发起请求前休眠，默认是100毫秒内随机
     *
     * @return void
     * @author King
     * @update 2019/1/9 12:55
     */
    private void sleepBeforeRequest() {
        try {
            Thread.sleep(config.getSleepMs());
        } catch (InterruptedException e) {
            log.error("线程休眠异常", e);
        }
    }

    /**
     * 重试检测，默认 无响应或者50x错误 需要重试
     *
     * @param statusCode 请求响应码
     * @return boolean 返回true表示需要重试
     * @author King
     * @update 2019/1/9 9:52
     */
    public boolean retryCheck(Integer statusCode) {
        //无响应或者50x错误 需要重试
        return StringUtils.isEmpty(statusCode) || statusCode.toString().startsWith("5");
    }

    /**
     * 添加缓存及解析页面
     *
     * @param httpRecord
     */
    private void addHttpRecord(HttpRecordType type, String httpRecord) {
        if (StringUtils.isEmpty(type)) {
            if (config.isSaveHttpRecords) {
                //httpRecordsPages.addRecord(httpRecord);
            }
        } else {
//            httpRecordsPages.addParsedRecord(type, httpRecord);
            //PageUtils.saveParsedRecord(type, httpRecord, token);
        }
    }
}
