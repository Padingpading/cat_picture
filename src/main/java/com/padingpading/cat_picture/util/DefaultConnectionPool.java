package com.padingpading.cat_picture.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

/**
 * @author: yu_song
 * @update: 2019/4/9 9:56
 */
@Slf4j
public enum DefaultConnectionPool {

     INSTANCE;
    private PoolingHttpClientConnectionManager pcm;
    private IdleConnectionMonitorThread idleThread = null;

    DefaultConnectionPool() {
        //TODO 初始化连接池
        try {
            //TODO 强制通信协议
            System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2,SSLv3");

            X509TrustManager xtm = new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] arg0, String arg1)
                        throws CertificateException {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] arg0, String arg1)
                        throws CertificateException {
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[]{};
                }
            };

            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, new TrustManager[]{xtm}, null);
            SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(context, NoopHostnameVerifier.INSTANCE);
            Registry<ConnectionSocketFactory> sfr = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.INSTANCE)
                    .register("https", sslConnectionSocketFactory).build();
            pcm = new PoolingHttpClientConnectionManager(sfr, null, null, null, 3, TimeUnit.SECONDS);
            pcm.setDefaultMaxPerRoute(30);
            pcm.setMaxTotal(600);

            SocketConfig socketConfig = SocketConfig.custom().setSoTimeout(30 * 1000).build();//TODO test(socket超时挂起)
            pcm.setDefaultSocketConfig(socketConfig);
            pcm.setValidateAfterInactivity(2500);

            idleThread = new IdleConnectionMonitorThread(pcm);
            idleThread.start();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
    }

    public PoolingHttpClientConnectionManager getInstance() {
        return pcm;
    }

    private class IdleConnectionMonitorThread extends Thread {
        private final HttpClientConnectionManager connMgr;
        private volatile boolean exitFlag = false;

        public IdleConnectionMonitorThread(HttpClientConnectionManager connMgr) {
            this.connMgr = connMgr;
            setDaemon(true);
        }

        @Override
        public void run() {
            while (!this.exitFlag) {
                synchronized (this) {
                    try {
                        this.wait(2000);
                    } catch (InterruptedException e) {
                        log.error("this daemon thread wait error : {}", e);
                    }
                }
                connMgr.closeExpiredConnections();
                connMgr.closeIdleConnections(30, TimeUnit.SECONDS);
            }
        }

        public void shutdown() {
            this.exitFlag = true;
            synchronized (this) {
                notify();
            }
        }
    }
}
