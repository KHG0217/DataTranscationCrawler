package com.tapacross.data.transcation.util;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.jsoup.Connection;
import org.jsoup.Jsoup;


public class JsoupUtil {
	public static final String ACCEPT = "Accept";
	public static final String ACCEPT_LANGUANGE = "Accept-Language";
	public static final String ACCEPT_ENCODING = "Accept-Encoding";
	public static final String COOKIE = "cookie";
	
	public static final String ACCEPT_VALUE = "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8";
	public static final String ACCEPT_LANGUAGE_VALUE = "ko-KR,ko;q=0.8,en-US;q=0.6,en;q=0.4";
	public static final String ACCEPT_ENCODING_VALUE = "gzip,deflate,sdch";
	
	
	public static Connection getJsoupSecureConnection(String url) {
		return Jsoup
				.connect(url)
				.userAgent(getDesktopUserAgent())
				.header(ACCEPT, ACCEPT_VALUE)
				.header(ACCEPT_LANGUANGE, ACCEPT_LANGUAGE_VALUE)
				.header(ACCEPT_ENCODING, ACCEPT_ENCODING_VALUE)
				.sslSocketFactory(JsoupUtil.socketFactory());
	}
	
	public static String getDesktopUserAgent() {
		return "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.130 Safari/537.36";
	}
	
	public static SSLSocketFactory socketFactory() {
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        }};

        try {
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            SSLSocketFactory result = sslContext.getSocketFactory();

            return result;
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new RuntimeException("Failed to create a SSL socket factory", e);
        }
    }
}
