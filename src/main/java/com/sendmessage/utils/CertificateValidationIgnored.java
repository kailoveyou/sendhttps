package com.sendmessage.utils;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;


/**
 * 工具类：创建一个忽略用户证书验证的httpClient实例
 * @date 2015-02-13
 * @author Barry
 */
public class CertificateValidationIgnored {

    public static String sendHttpsPostjson(String url,String json, String charSet) throws Exception{
        HttpClient httpClient = null;
        HttpPost httpPost = null;
        String result = null;
        httpClient = new SSLClient();
        httpPost = new HttpPost(url);
        JSONObject map = JSON.parseObject(json);
        StringEntity entity = new StringEntity(map.toString(), charSet);
        entity.setContentEncoding(charSet);
        entity.setContentType("application/json");
        httpPost.setEntity(entity);
        HttpResponse response = httpClient.execute(httpPost);
        if(response != null){
            HttpEntity resEntity = response.getEntity();
            if(resEntity != null){
                result = EntityUtils.toString(resEntity,charSet);
            }
        }

        return result;
    }


    public static HttpClient getNoCertificateHttpClient(String url){
        return getCertificateValidationIgnoredHttpClient();
    }

    private static HttpClient getCertificateValidationIgnoredHttpClient() {
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore
                    .getDefaultType());
            trustStore.load(null, null);
            //核心代码，创建一个UnVerifySocketFactory对象，验证证书时总是返回true
            SSLSocketFactory sf = new UnVerifySocketFactory(trustStore);

            HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory
                    .getSocketFactory(), 80));
            registry.register(new Scheme("https", sf, 443));
            ClientConnectionManager ccm = new ThreadSafeClientConnManager(
                    params, registry);
            return new DefaultHttpClient(ccm, params);
        } catch (Exception e) {
            System.out.println("CertificateValidationIgnored创建忽略用户证书的HttpClient对象失败，尝试创建普通HttpClient对象");
            e.printStackTrace();
            return new DefaultHttpClient();
        }
    }

    /**
     * 核心类
     * UnVerifySocketFactory:一个验证证书时总是返回true的SSLSocketFactory的子类
     */
    private static X509HostnameVerifier ignoreVerifier;
    private static class UnVerifySocketFactory extends SSLSocketFactory {
        SSLContext sslContext = SSLContext.getInstance("TLS");

        public UnVerifySocketFactory(KeyStore truststore)
                throws NoSuchAlgorithmException, KeyManagementException,
                KeyStoreException, UnrecoverableKeyException {
            super(truststore);

            TrustManager tm = new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] chain,
                                               String authType) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] chain,
                                               String authType) throws CertificateException {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };

            sslContext.init(null, new TrustManager[] { tm }, null);
        }

        @Override
        public Socket createSocket(Socket socket, String host, int port,
                                   boolean autoClose) throws IOException, UnknownHostException {
            return sslContext.getSocketFactory().createSocket(socket, host,
                    port, autoClose);
        }

        //核心代码
        @Override
        public void setHostnameVerifier(X509HostnameVerifier hostnameVerifier) {
            // TODO Auto-generated method stub
            ignoreVerifier = new X509HostnameVerifier() {
                @Override
                public void verify(String arg0, String[] arg1, String[] arg2)
                        throws SSLException {
                }
                @Override
                public void verify(String arg0, X509Certificate arg1)
                        throws SSLException {
                }
                @Override
                public void verify(String arg0, SSLSocket arg1)
                        throws IOException {
                }

                //最最核心代码
                @Override
                public boolean verify(String arg0, SSLSession arg1) {
                    return true;
                }
            };
            super.setHostnameVerifier(ignoreVerifier);
        }

        @Override
        public X509HostnameVerifier getHostnameVerifier() {
            return ignoreVerifier;
        }

        @Override
        public Socket createSocket() throws IOException {
            return sslContext.getSocketFactory().createSocket();
        }
    }

}
