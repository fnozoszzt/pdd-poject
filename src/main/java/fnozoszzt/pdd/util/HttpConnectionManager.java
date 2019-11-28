package fnozoszzt.pdd.util;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.apache.log4j.Logger;

import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;


public class HttpConnectionManager {

    private static final Logger log = Logger.getLogger(HttpConnectionManager.class);
    private static CloseableHttpClient httpClient;

    private static Integer maxConnection = 1000;
    private static Integer maxPerRoute = 500;
    private static Integer connectTimeout = 60 * 1000;
    private static Integer socketTimeout = 60 * 1000;
    private static Long defaultKeepliveTimeout = 60 * 1000l;

    public static synchronized CloseableHttpClient initConnectionPool() {
        try {
            if (httpClient != null) {
                return httpClient;
            }

            ConnectionKeepAliveStrategy keepAliveStrat = new DefaultConnectionKeepAliveStrategy() {

                @Override
                public long getKeepAliveDuration(
                        HttpResponse response,
                        HttpContext context) {
                    long keepAlive = super.getKeepAliveDuration(response, context);
                    if (keepAlive == -1){
                        keepAlive = defaultKeepliveTimeout;
                    }

                    return keepAlive;
                }
            };

            RegistryBuilder<ConnectionSocketFactory> registryBuilder = RegistryBuilder.<ConnectionSocketFactory>create();
            ConnectionSocketFactory plainSF = new PlainConnectionSocketFactory();
            registryBuilder.register("http", plainSF);

            //指定信任密钥存储对象和连接套接字工厂
            try {
                KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
                SSLContext sslContext = SSLContexts
                        .custom()
                        .useTLS()
                        .loadTrustMaterial(trustStore, new TrustStrategy() {

                        //信任所有
                        public boolean isTrusted(X509Certificate[] chain,
                                         String authType) throws CertificateException {
                        return true;
                    }
                }).build();

                LayeredConnectionSocketFactory sslSF = new SSLConnectionSocketFactory(
                    sslContext, SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
                registryBuilder.register("https", sslSF);
            } catch (KeyStoreException e) {
                throw new RuntimeException(e);
            } catch (KeyManagementException e) {
                throw new RuntimeException(e);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }

            Registry<ConnectionSocketFactory> registry = registryBuilder.build();
            PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(registry);
            cm.setMaxTotal(maxConnection);          //连接池最大并发连接数
            cm.setDefaultMaxPerRoute(maxPerRoute);  //单路由最大并发数

            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(connectTimeout).
                    setSocketTimeout(socketTimeout).setCookieSpec(CookieSpecs.IGNORE_COOKIES)
                    .build();

            httpClient = HttpClients.custom()
                    .setKeepAliveStrategy(keepAliveStrat)
                    .setConnectionManager(cm)
                    .setDefaultRequestConfig(requestConfig)
                    .setConnectionReuseStrategy(DefaultConnectionReuseStrategy.INSTANCE)
                    .build();

            return httpClient;
        } catch (Exception e) {
            log.error("init http connection manager failed.", e);
        }

        return null;
    }

    public static HttpClient getHttpClient() {
        if (httpClient == null) {
            httpClient = initConnectionPool();
        }
        return httpClient;
    }
}
