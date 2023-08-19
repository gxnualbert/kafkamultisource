package com.tiger.config;


import lombok.extern.slf4j.Slf4j;
import org.apache.http.*;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestWrapper;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.net.URI;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

/**
 * 类描述...
 *
 * @author Tiger Chen
 * created on 2023/8/12 21:53
 */

@Configuration
@Slf4j
public class HttpClientConfig {
    private static final String[] supportProtocols = {"TLSv1.2"};
    private static final String[] supportCipherSuites = {"TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256", "TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384"};

    @Bean
    public CloseableHttpClient closeableHttpClient(PoolingHttpClientConnectionManager connectionManager) {
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(6000).setConnectionRequestTimeout(1000).setSocketTimeout(50000).build();

        return HttpClients.custom().disableCookieManagement().setRedirectStrategy(new CustomRedirectStrategy()).setConnectionManager(connectionManager).setDefaultRequestConfig(requestConfig).setKeepAliveStrategy(connectionKeepAliveStrategy()).build();
    }

    @Bean
    public PoolingHttpClientConnectionManager poolingHttpClientConnectionManager() {
        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create().register("http", PlainConnectionSocketFactory.INSTANCE).register("https", sslConnectionSocketFactory()).build();
        PoolingHttpClientConnectionManager poolingHttpClientConnectionManager = new PoolingHttpClientConnectionManager(registry);
        poolingHttpClientConnectionManager.setMaxTotal(1000);
        poolingHttpClientConnectionManager.setDefaultMaxPerRoute(1000);
        return poolingHttpClientConnectionManager;
    }


    private SSLConnectionSocketFactory sslConnectionSocketFactory() {
        SSLContext sslContext;
        try {
            sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(null, new TrustManager[]{new SelfX509TrustManager()}, SecureRandom.getInstanceStrong());
            return new SSLConnectionSocketFactory(sslContext, supportProtocols, supportCipherSuites, NoopHostnameVerifier.INSTANCE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return SSLConnectionSocketFactory.getSocketFactory();
    }

    private static class SelfX509TrustManager implements javax.net.ssl.X509TrustManager {
        @Override
        public void checkClientTrusted(java.security.cert.X509Certificate[] x509Certificates, String s) throws java.security.cert.CertificateException {

        }

        @Override
        public void checkServerTrusted(java.security.cert.X509Certificate[] x509Certificates, String authType) throws java.security.cert.CertificateException {
            if (StringUtils.hasText(authType)) {
                log.info("authType:{}", authType);
            }
        }

        @Override
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

    private ConnectionKeepAliveStrategy connectionKeepAliveStrategy() {

        return (response, context) -> {
            HeaderElementIterator it = new org.apache.http.message.BasicHeaderElementIterator(response.headerIterator("Keep-Alive"));
            while (it.hasNext()) {
                HeaderElement headerElement = it.nextElement();
                String value = headerElement.getValue();
                String name = headerElement.getName();
                if (StringUtils.hasText(name) && StringUtils.hasText(value)) {
                    log.info("name:{},value:{}", name, value);
                }
                if (value != null && name.equalsIgnoreCase("timeout")) {
                    return Long.parseLong(value) * 1000;
                }
            }
            return 60 * 1000;
        };
    }

    private static class CustomRedirectStrategy extends org.apache.http.impl.client.LaxRedirectStrategy {
        public static <T> T cast(Object object) {
            return (T) object;
        }

        public HttpUriRequest getRedirect(HttpRequest request, HttpResponse response, HttpContext context) throws ProtocolException {
            URI uri = getLocationURI(request, response, context);
            String methond = request.getRequestLine().getMethod();
            if (HttpPost.METHOD_NAME.equalsIgnoreCase(methond)) {
                HttpRequestWrapper httpRequestWrapper = cast(request);
                httpRequestWrapper.setURI(uri);
                httpRequestWrapper.removeHeaders(HttpHeaders.CONTENT_LENGTH);
                return httpRequestWrapper;
            } else {
                return new HttpGet(uri);
            }
        }
    }

}


