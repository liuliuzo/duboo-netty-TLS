package com.alibaba.dubbo.remoting.transport.netty4ssl;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;

public final class NettySSLContextFactory {
	
	private static final String PROTOCOL = "TLS";
	private static volatile SSLContext SERVER_CONTEXT;
	private static volatile SSLContext CLIENT_CONTEXT;
	
	public static SSLContext getServerContext(String pkPath) {
		if (SERVER_CONTEXT != null) {
			return SERVER_CONTEXT;	
		}
		InputStream in = null;
		try {
			KeyManagerFactory kmf = null;
			if (pkPath != null) {
				KeyStore ks = KeyStore.getInstance("JKS");
				in = new FileInputStream(pkPath);
				ks.load(in, "nettyDemo".toCharArray());
				kmf = KeyManagerFactory.getInstance("SunX509");
				kmf.init(ks, "nettyDemo".toCharArray());
			}
			SERVER_CONTEXT = SSLContext.getInstance(PROTOCOL);
			SERVER_CONTEXT.init(kmf.getKeyManagers(), null, null);

		} catch (Exception e) {
			throw new Error("Failed to initialize the server-side SSLContext", e);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return SERVER_CONTEXT;
	}

	public static SSLContext getClientContext(String caPath) {
		if (CLIENT_CONTEXT != null) {
			return CLIENT_CONTEXT;
		}
		InputStream tIN = null;
		try {
			TrustManagerFactory tf = null;
			if (caPath != null) {
				KeyStore tks = KeyStore.getInstance("JKS");
				tIN = new FileInputStream(caPath);
				tks.load(tIN, "nettyDemo".toCharArray());
				tf = TrustManagerFactory.getInstance("SunX509");
				tf.init(tks);
			}
			CLIENT_CONTEXT = SSLContext.getInstance(PROTOCOL);
			CLIENT_CONTEXT.init(null, tf == null ? null : tf.getTrustManagers(), null);
		} catch (Exception e) {
			throw new Error("Failed to initialize the client-side SSLContext");
		} finally {
			if (tIN != null) {
				try {
					tIN.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return CLIENT_CONTEXT;
	}
}
