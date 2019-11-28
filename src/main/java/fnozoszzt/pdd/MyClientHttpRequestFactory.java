//package com.netease.bdms.dsweb;
//
//import org.springframework.http.client.SimpleClientHttpRequestFactory;
//
//import javax.net.ssl.*;
//import java.io.IOException;
//import java.net.HttpURLConnection;
//import java.net.InetAddress;
//import java.net.Socket;
//import java.security.KeyManagementException;
//import java.security.NoSuchAlgorithmException;
//import java.security.cert.X509Certificate;
//
///**
// * 自定义ClientHttpRequestFactory来实现Spring RestTemplete访问Https接口
// * 参考链接：https://www.cnblogs.com/ssslinppp/p/8036603.html
// * 参考链接：https://stackoverflow.com/questions/17619871/access-https-rest-service-using-spring-resttemplate
// */
//public class MyClientHttpRequestFactory extends SimpleClientHttpRequestFactory {
//    @Override
//    protected void prepareConnection(HttpURLConnection connection, String httpMethod) throws IOException {
//        // 对HttpsURLConnection强行信任，即跳过验证
//        if(connection instanceof HttpsURLConnection) {
//            try {
//                HttpsURLConnection httpsConnection = (HttpsURLConnection) connection;
//                TrustManager[] trustAllCerts = new TrustManager[]{
//                        new X509TrustManager() {
//                            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
//                                return null;
//                            }
//
//                            public void checkClientTrusted(X509Certificate[] certs, String authType) {
//                            }
//
//                            public void checkServerTrusted(X509Certificate[] certs, String authType) {
//                            }
//
//                        }
//                };
//                SSLContext sslContext = SSLContext.getInstance("TLS");
//                sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
//                httpsConnection.setSSLSocketFactory(new MyCustomSSLSocketFactory(sslContext.getSocketFactory()));
//
//                httpsConnection.setHostnameVerifier(new HostnameVerifier() {
//                    @Override
//                    public boolean verify(String s, SSLSession sslSession) {
//                        return true;
//                    }
//                });
//
//                super.prepareConnection(httpsConnection, httpMethod);
//            }catch (NoSuchAlgorithmException e) {
//                e.printStackTrace();
//            }catch(KeyManagementException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    /**
//     * We need to invoke sslSocket.setEnabledProtocols(new String[] {"SSLv3"});
//     * see http://www.oracle.com/technetwork/java/javase/documentation/cve-2014-3566-2342133.html (Java 8 section)
//     */
//    private static class MyCustomSSLSocketFactory extends SSLSocketFactory {
//
//        private final SSLSocketFactory delegate;
//
//        public MyCustomSSLSocketFactory(SSLSocketFactory delegate) {
//            this.delegate = delegate;
//        }
//
//        @Override
//        public String[] getDefaultCipherSuites() {
//            return delegate.getDefaultCipherSuites();
//        }
//
//        @Override
//        public String[] getSupportedCipherSuites() {
//            return delegate.getSupportedCipherSuites();
//        }
//
//        @Override
//        public Socket createSocket(final Socket socket, final String host, final int port, final boolean autoClose) throws IOException {
//            final Socket underlyingSocket = delegate.createSocket(socket, host, port, autoClose);
//            return overrideProtocol(underlyingSocket);
//        }
//
//        @Override
//        public Socket createSocket(final String host, final int port) throws IOException {
//            final Socket underlyingSocket = delegate.createSocket(host, port);
//            return overrideProtocol(underlyingSocket);
//        }
//
//        @Override
//        public Socket createSocket(final String host, final int port, final InetAddress localAddress, final int localPort) throws
//                IOException {
//            final Socket underlyingSocket = delegate.createSocket(host, port, localAddress, localPort);
//            return overrideProtocol(underlyingSocket);
//        }
//
//        @Override
//        public Socket createSocket(final InetAddress host, final int port) throws IOException {
//            final Socket underlyingSocket = delegate.createSocket(host, port);
//            return overrideProtocol(underlyingSocket);
//        }
//
//        @Override
//        public Socket createSocket(final InetAddress host, final int port, final InetAddress localAddress, final int localPort) throws
//                IOException {
//            final Socket underlyingSocket = delegate.createSocket(host, port, localAddress, localPort);
//            return overrideProtocol(underlyingSocket);
//        }
//
//        private Socket overrideProtocol(final Socket socket) {
//            if(socket instanceof  SSLSocket) {
//                ((SSLSocket) socket).setEnabledProtocols(new String[]{"TLSv1","SSLv3"});
//            }
//            return socket;
//        }
//    }
//}