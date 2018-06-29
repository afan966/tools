package com.afan.tool.http;

import java.io.File;
import java.io.FileInputStream;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import javax.net.ssl.SSLContext;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class SSLConnectionFactory {

	private SSLContext sslContext = null;
	private String certFile;
	private String keyStorePassword;

	public SSLConnectionFactory(String certFile, String keyStorePassword) {
		this.certFile = certFile;
		this.keyStorePassword = keyStorePassword;
		init();
	}

	public void init() {
		try {
			FileInputStream keyin = new FileInputStream(new File(certFile));
			KeyStore keystore = KeyStore.getInstance("PKCS12");
			keystore.load(keyin, keyStorePassword.toCharArray());
			keyin.close();
			final TrustStrategy trustStrategy = new TrustStrategy() {
				@Override
				public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
					return true;
				}
			};
			sslContext = SSLContexts.custom().loadTrustMaterial(trustStrategy).loadKeyMaterial(keystore, keyStorePassword.toCharArray()).build();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public SSLConnectionSocketFactory getSSLConnectionSocketFactory() {
		if (sslContext == null) {
			return null;
		}
		return new SSLConnectionSocketFactory(sslContext, SSLConnectionSocketFactory.getDefaultHostnameVerifier());
	}

}
